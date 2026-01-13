package it.gov.pagopa.payhub.ionotification.service.notify;

import it.gov.pagopa.payhub.ionotification.connector.io.IORestConnector;
import it.gov.pagopa.payhub.ionotification.connector.organization.OrganizationService;
import it.gov.pagopa.payhub.ionotification.dto.*;
import it.gov.pagopa.payhub.ionotification.dto.generated.MessageResponseDTO;
import it.gov.pagopa.payhub.ionotification.dto.generated.NotificationRequestDTO;
import it.gov.pagopa.payhub.ionotification.dto.mapper.IONotificationMapper;
import it.gov.pagopa.payhub.ionotification.enums.NotificationStatus;
import it.gov.pagopa.payhub.ionotification.exception.custom.SenderNotAllowedException;
import it.gov.pagopa.payhub.ionotification.model.IONotification;
import it.gov.pagopa.payhub.ionotification.repository.IONotificationRepository;
import it.gov.pagopa.payhub.ionotification.service.UserIdObfuscatorService;
import it.gov.pagopa.pu.organization.dto.generated.OrganizationApiKeyType;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.regex.Pattern;

import static it.gov.pagopa.payhub.ionotification.enums.NotificationStatus.KO_SENDER_NOT_ALLOWED;
import static it.gov.pagopa.payhub.ionotification.enums.NotificationStatus.OK;

@Service
@Slf4j
public class IONotificationServiceImpl implements IONotificationService {

    private final IONotificationRepository ioNotificationRepository;
    private final IORestConnector connector;
    private final IONotificationMapper ioNotificationMapper;
    private final UserIdObfuscatorService obfuscatorService;
    private final OrganizationService organizationService;
    private final Long timeToLive;

    @SuppressWarnings("java:S5843")
    private static final String CF_VALIDITY_REGEX = "^(?:[A-Z][AEIOUX][AEIOUX]|[B-DF-HJ-NP-TV-Z]{2}[A-Z]){2}(?:[\\dLMNP-V]{2}(?:[A-EHLMPR-T](?:[04LQ][1-9MNP-V]|[15MR][\\dLMNP-V]|[26NS][0-8LMNP-U])|[DHPS][37PT][0L]|[ACELMRT][37PT][01LM]|[AC-EHLMPR-T][26NS][9V])|(?:[02468LNQSU][048LQU]|[13579MPRTV][26NS])B[26NS][9V])(?:[A-MZ][1-9MNP-V][\\dLMNP-V]{2}|[A-M][0L](?:[1-9MNP-V][\\dLMNP-V]|[0L][1-9MNP-V]))[A-Z]$";
    private static final Pattern CF_PATTERN = Pattern.compile(CF_VALIDITY_REGEX);

    public IONotificationServiceImpl(IONotificationRepository ioNotificationRepository,
                                     IORestConnector connector,
                                     IONotificationMapper ioNotificationMapper,
                                     UserIdObfuscatorService obfuscatorService, OrganizationService organizationService,
                                     @Value("${rest.backend-io-manage.notification.ttl}") Long timeToLive) {
        this.ioNotificationRepository = ioNotificationRepository;
        this.connector = connector;
        this.ioNotificationMapper = ioNotificationMapper;
        this.obfuscatorService = obfuscatorService;
        this.organizationService = organizationService;
        this.timeToLive = timeToLive;
    }

    @Override
    public MessageResponseDTO sendMessage(String accessToken, NotificationRequestDTO notificationRequestDTO) {
        log.info("Sending notification to organizationId {} and debtPositionTypeOrgId {}",
                notificationRequestDTO.getOrgId(), notificationRequestDTO.getDebtPositionTypeOrgId());
        String apiKey = organizationService.getOrganizationApiKey(accessToken, notificationRequestDTO.getOrgId(), OrganizationApiKeyType.IO);
        if (apiKey != null) {
            String token = retrieveTokenIO(notificationRequestDTO.getServiceId(), apiKey);
            if (isSenderAllowed(notificationRequestDTO, token)) {
                String notificationId = sendNotification(notificationRequestDTO, token);
                return MessageResponseDTO.builder().notificationId(notificationId).build();
            }
        } else {
            log.info("No ApiKey configured for IO service on organization {}", notificationRequestDTO.getOrgId());
        }
        return null;
    }

    @Override
    public void deleteNotification(String notificationId) {
        Optional<IONotification> ioNotification = ioNotificationRepository.findByNotificationId(notificationId);

        if (ioNotification.isPresent()) {
            log.info("Deleting notification {}", notificationId);
            ioNotificationRepository.delete(ioNotification.get());
        }
    }

    private String retrieveTokenIO(String serviceId, String apiKey) {
        log.info("Retrieve token from IO for service {}", serviceId);
        KeysDTO keys = connector.getServiceKeys(serviceId, apiKey);
        return keys.getPrimaryKey();
    }

    private boolean isSenderAllowed(NotificationRequestDTO notificationRequestDTO, String token) {
        FiscalCodeDTO fiscalCode = ioNotificationMapper.mapToGetProfile(notificationRequestDTO);

        if (!CF_PATTERN.matcher(fiscalCode.getFiscalCode()).matches()) {
            log.warn("Fiscal code is not a valid CF or is null. Blocking io-notification.");
            return handleSenderNotAllowed(notificationRequestDTO);
        }

        try {
            log.info("Verify if user is allowed to receive notification");
            ProfileResource profileResource = connector.getProfile(fiscalCode, token);
            if (!profileResource.isSenderAllowed()) {
                return handleSenderNotAllowed(notificationRequestDTO);
            }
        } catch (SenderNotAllowedException e) {
            return handleSenderNotAllowed(notificationRequestDTO);
        }
        return true;
    }

    private boolean handleSenderNotAllowed(NotificationRequestDTO notificationRequestDTO) {
        log.info("The user is not enabled to receive notifications");
        saveNotification(notificationRequestDTO, null, KO_SENDER_NOT_ALLOWED);
        return false;
    }

    private String sendNotification(NotificationRequestDTO notificationRequestDTO, String token) {

        NotificationDTO notificationDTO = ioNotificationMapper
                .map(timeToLive, notificationRequestDTO);

        log.info("Sending notification to IO");
        NotificationResource notificationResource = connector.sendNotification(notificationDTO, token);
        saveNotification(notificationRequestDTO, notificationResource.getId(), OK);

        return notificationResource.getId();
    }

    private void saveNotification(NotificationRequestDTO notificationRequestDTO, String notificationId, NotificationStatus status) {
        IONotification ioNotification = ioNotificationMapper
                .mapToSaveNotification(notificationRequestDTO, status, encryptFiscalCode(notificationRequestDTO.getFiscalCode()));

        if (notificationId != null) {
            ioNotification.setNotificationId(notificationId);
        }

        log.info("Saving notification with status {}", status);
        ioNotificationRepository.save(ioNotification);
    }

    private String encryptFiscalCode(String fiscalCode) {
        return obfuscatorService.obfuscate(fiscalCode);
    }
}

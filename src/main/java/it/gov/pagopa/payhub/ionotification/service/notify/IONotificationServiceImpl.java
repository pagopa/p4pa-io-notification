package it.gov.pagopa.payhub.ionotification.service.notify;

import it.gov.pagopa.payhub.ionotification.connector.IORestConnector;
import it.gov.pagopa.payhub.ionotification.dto.*;
import it.gov.pagopa.payhub.ionotification.dto.generated.MessageResponseDTO;
import it.gov.pagopa.payhub.ionotification.dto.generated.NotificationRequestDTO;
import it.gov.pagopa.payhub.ionotification.dto.mapper.IONotificationMapper;
import it.gov.pagopa.payhub.ionotification.enums.NotificationStatus;
import it.gov.pagopa.payhub.ionotification.exception.custom.SenderNotAllowedException;
import it.gov.pagopa.payhub.ionotification.model.IONotification;
import it.gov.pagopa.payhub.ionotification.repository.IONotificationRepository;
import it.gov.pagopa.payhub.ionotification.service.UserIdObfuscatorService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

import static it.gov.pagopa.payhub.ionotification.enums.NotificationStatus.KO_SENDER_NOT_ALLOWED;
import static it.gov.pagopa.payhub.ionotification.enums.NotificationStatus.OK;

@Service
@Slf4j
public class IONotificationServiceImpl implements IONotificationService {

    private final IONotificationRepository ioNotificationRepository;
    private final IORestConnector connector;
    private final IONotificationMapper ioNotificationMapper;
    private final UserIdObfuscatorService obfuscatorService;
    private final Long timeToLive;

    public IONotificationServiceImpl(IONotificationRepository ioNotificationRepository,
                                     IORestConnector connector,
                                     IONotificationMapper ioNotificationMapper,
                                     UserIdObfuscatorService obfuscatorService,
                                     @Value("${rest-client.backend-io-manage.notification.ttl}") Long timeToLive) {
        this.ioNotificationRepository = ioNotificationRepository;
        this.connector = connector;
        this.ioNotificationMapper = ioNotificationMapper;
        this.obfuscatorService = obfuscatorService;
        this.timeToLive = timeToLive;
    }

    @Override
    public MessageResponseDTO sendMessage(NotificationRequestDTO notificationRequestDTO) {
        log.info("Sending message to notify of type {}", notificationRequestDTO.getOperationType());
        String token = retrieveTokenIO(notificationRequestDTO.getServiceId());
        if (isSenderAllowed(notificationRequestDTO, token)) {
            String notificationId = sendNotification(notificationRequestDTO, token);
            return MessageResponseDTO.builder().notificationId(notificationId).build();
        }
        return null;
    }

    @Override
    public void deleteNotification(String userId, Long orgId, Long debtPositionTypeOrgId) {
        Optional<IONotification> ioNotification = ioNotificationRepository
                .findByUserIdAndOrgIdAndDebtPositionTypeOrgId(userId, orgId, debtPositionTypeOrgId);

        if (ioNotification.isPresent()) {
            log.info("Deleting notification {}", ioNotification.get().getNotificationId());
            ioNotificationRepository.delete(ioNotification.get());
        }

    }

    private String retrieveTokenIO(String serviceId) {
        log.info("Retrieve token from IO for service {}", serviceId);
        KeysDTO keys = connector.getServiceKeys(serviceId);
        return keys.getPrimaryKey();
    }

    private boolean isSenderAllowed(NotificationRequestDTO notificationRequestDTO, String token) {
        FiscalCodeDTO fiscalCode = ioNotificationMapper.mapToGetProfile(notificationRequestDTO);
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
        Map<String, String> placeholders = Map.of(
                "%importoDovuto%", notificationRequestDTO.getAmount(),
                "%dataEsecuzionePagamento%", notificationRequestDTO.getDueDate(),
                "%codIUV%", notificationRequestDTO.getIuv(),
                "%causaleVersamento%", notificationRequestDTO.getPaymentReason()
        );

        String customMarkdown = replacePlaceholders(notificationRequestDTO.getMarkdown(), placeholders);

        NotificationDTO notificationDTO = ioNotificationMapper
                .map(notificationRequestDTO.getFiscalCode(), timeToLive, notificationRequestDTO.getSubject(), customMarkdown);

        log.info("Sending notification to IO");
        NotificationResource notificationResource = connector.sendNotification(notificationDTO, token);
        saveNotification(notificationRequestDTO, notificationResource.getId(), OK);

        return notificationResource.getId();
    }

    private String replacePlaceholders(String template, Map<String, String> placeholders) {
        for (Map.Entry<String, String> entry : placeholders.entrySet()) {
            template = template.replace(entry.getKey(), entry.getValue() != null ? entry.getValue() : "");
        }
        return template.replaceAll("\\s{2,}", " ").trim();
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

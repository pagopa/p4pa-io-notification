package it.gov.pagopa.payhub.ionotification.service.notify;

import it.gov.pagopa.payhub.ionotification.connector.IORestConnector;
import it.gov.pagopa.payhub.ionotification.enums.NotificationStatus;
import it.gov.pagopa.payhub.ionotification.dto.*;
import it.gov.pagopa.payhub.ionotification.dto.mapper.IONotificationMapper;
import it.gov.pagopa.payhub.ionotification.event.producer.IONotificationProducer;
import it.gov.pagopa.payhub.ionotification.exception.custom.SenderNotAllowedException;
import it.gov.pagopa.payhub.ionotification.model.IONotification;
import it.gov.pagopa.payhub.ionotification.model.IOService;
import it.gov.pagopa.payhub.ionotification.repository.IONotificationRepository;
import it.gov.pagopa.payhub.ionotification.repository.IOServiceRepository;
import it.gov.pagopa.payhub.model.generated.NotificationQueueDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static it.gov.pagopa.payhub.ionotification.enums.NotificationStatus.*;

@Service
@Slf4j
public class IONotificationServiceImpl implements IONotificationService {

    private final IONotificationRepository ioNotificationRepository;
    private final IORestConnector connector;
    private final IONotificationProducer ioNotificationProducer;
    private final IONotificationMapper ioNotificationMapper;
    private final IOServiceRepository ioServiceRepository;
    private final Long timeToLive;
    private final String subject;
    private final String markdown;
    public IONotificationServiceImpl(IONotificationRepository ioNotificationRepository,
                                     IORestConnector connector,
                                     IONotificationProducer ioNotificationProducer,
                                     IONotificationMapper ioNotificationMapper,
                                     IOServiceRepository ioServiceRepository,
                                     @Value("${rest-client.backend-io-manage.notification.ttl}") Long timeToLive,
                                     @Value("${rest-client.backend-io-manage.notification.subject}") String subject,
                                     @Value("${rest-client.backend-io-manage.notification.markdown}") String markdown) {
        this.ioNotificationRepository = ioNotificationRepository;
        this.connector = connector;
        this.ioNotificationProducer = ioNotificationProducer;
        this.ioNotificationMapper = ioNotificationMapper;
        this.ioServiceRepository = ioServiceRepository;
        this.timeToLive = timeToLive;
        this.subject = subject;
        this.markdown = markdown;
    }

    @Override
    public void sendMessage(NotificationQueueDTO notificationQueueDTO) {
        log.info("Sending message to notify of type {}", notificationQueueDTO.getOperationType());
        ioNotificationProducer.sendNotification(notificationQueueDTO);
    }

    @Override
    public void sendNotification(NotificationQueueDTO notificationQueueDTO) {
        Optional<IOService> ioService = retrieveIOService(notificationQueueDTO);
        if (ioService.isPresent()) {
            String token = retrieveTokenIO(ioService.get());
            if (isSenderAllowed(notificationQueueDTO, ioService.get(), token)) {
                sendNotification(notificationQueueDTO, ioService.get(), token);
            }
        }
    }

    @Override
    public void deleteNotification(String userId, Long enteId, Long tipoDovutoId) {
        Optional<IONotification> ioNotification = ioNotificationRepository.findByUserIdAndEnteIdAndTipoDovutoId(userId, enteId, tipoDovutoId);

        if (ioNotification.isPresent()) {
            log.info("Deleting notification {}", ioNotification.get().getNotificationId());
            ioNotificationRepository.delete(ioNotification.get());
        }

    }

    private Optional<IOService> retrieveIOService(NotificationQueueDTO notificationQueueDTO) {
        log.info("Search service for {} and {}", notificationQueueDTO.getEnteId(), notificationQueueDTO.getTipoDovutoId());
        Optional<IOService> ioService = ioServiceRepository
                .findByEnteIdAndTipoDovutoId(notificationQueueDTO.getEnteId(), notificationQueueDTO.getTipoDovutoId());

        if (ioService.isEmpty()) {
            log.error("There is no service for organizationId {} and tipoDovutoId {}",
                    notificationQueueDTO.getEnteId(), notificationQueueDTO.getTipoDovutoId());

            saveNotification(notificationQueueDTO, null, null, KO_SERVICE_NOT_FOUND);
        }
        return ioService;
    }

    private String retrieveTokenIO(IOService ioService) {
        log.info("Retrieve token from IO for service {}", ioService.getServiceId());
        KeysDTO keys = connector.getServiceKeys(ioService.getServiceId());
        return keys.getPrimaryKey();
    }

    private boolean isSenderAllowed(NotificationQueueDTO notificationQueueDTO, IOService ioService, String token) {
        FiscalCodeDTO fiscalCode = ioNotificationMapper.mapToGetProfile(notificationQueueDTO);
        try{
            log.info("Verify if is user is allowed to receive notification");
            ProfileResource profileResource = connector.getProfile(fiscalCode, token);
            if (!profileResource.isSenderAllowed()) {
                return handleSenderNotAllowed(notificationQueueDTO, ioService);
            }
        } catch (SenderNotAllowedException e) {
            return handleSenderNotAllowed(notificationQueueDTO, ioService);
        }
        return true;
    }

    private boolean handleSenderNotAllowed(NotificationQueueDTO notificationQueueDTO, IOService ioService) {
        log.info("The user is not enabled to receive notifications");
        saveNotification(notificationQueueDTO, ioService, null, KO_SENDER_NOT_ALLOWED);
        return false;
    }

    private void sendNotification(NotificationQueueDTO notificationQueueDTO, IOService ioService, String token) {
        String customSubject = subject.replace("%tipoDovutoName%", ioService.getServiceName());
        String customMarkdown = markdown
                .replace("%amount%", notificationQueueDTO.getAmount())
                .replace("%paymentDate%", notificationQueueDTO.getPaymentDate())
                .replace("%iuv%", notificationQueueDTO.getIuv())
                .replace("%paymentReason%", notificationQueueDTO.getPaymentReason());

        NotificationDTO notificationDTO = ioNotificationMapper
                .mapToQueue(notificationQueueDTO.getFiscalCode(), timeToLive, customSubject, customMarkdown);

        log.info("Sending notification to IO");
        NotificationResource notificationResource = connector.sendNotification(notificationDTO, token);
        saveNotification(notificationQueueDTO, ioService, notificationResource.getId(), OK);
    }
    private void saveNotification(NotificationQueueDTO notificationQueueDTO, IOService ioService, String notificationId, NotificationStatus status) {
        IONotification ioNotification = ioNotificationMapper.mapToSaveNotification(notificationQueueDTO, status);

        if (notificationId != null) {
            ioNotification.setNotificationId(notificationId);
        }

        if (ioService != null) {
            ioNotification.setEnteName(ioService.getOrganizationName());
            ioNotification.setTipoDovutoName(ioService.getServiceName());
        }

        log.info("Saving notification with status {}", status);
        ioNotificationRepository.save(ioNotification);
    }
}

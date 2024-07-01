package it.gov.pagopa.payhub.ionotification.service.notify;

import it.gov.pagopa.payhub.ionotification.connector.IORestConnector;
import it.gov.pagopa.payhub.ionotification.constants.IONotificationConstants;
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

@Service
@Slf4j
public class IONotificationServiceImpl implements IONotificationService {

    private final IONotificationRepository ioNotificationRepository;
    private final IORestConnector connector;
    private final IONotificationProducer ioNotificationProducer;
    private final IONotificationMapper ioNotificationMapper;
    private final IOServiceRepository ioServiceRepository;
    @Value("${rest-client.backend-io-manage.notification.ttl}")
    private Long timeToLive;
    @Value("${rest-client.backend-io-manage.notification.subject}")
    private String subject;

    @Value("${rest-client.backend-io-manage.notification.markdown}")
    private String markdown;

    public IONotificationServiceImpl(IONotificationRepository ioNotificationRepository,
                                     IORestConnector connector,
                                     IONotificationProducer ioNotificationProducer,
                                     IONotificationMapper ioNotificationMapper,
                                     IOServiceRepository ioServiceRepository) {
        this.ioNotificationRepository = ioNotificationRepository;
        this.connector = connector;
        this.ioNotificationProducer = ioNotificationProducer;
        this.ioNotificationMapper = ioNotificationMapper;
        this.ioServiceRepository = ioServiceRepository;
    }

    @Override
    public void sendMessage(NotificationQueueDTO notificationQueueDTO) {
        log.info("Sending message to notify of type {}", notificationQueueDTO.getOperationType());
        ioNotificationProducer.sendNotification(notificationQueueDTO);
    }

    @Override
    public void sendNotification(NotificationQueueDTO notificationQueueDTO) {
        Optional<String> token = retrieveTokenIO(notificationQueueDTO);
        if (token.isPresent() && isSenderAllowed(notificationQueueDTO, token.get())) {
            sendNotification(notificationQueueDTO, token.get());
        }
    }

    private Optional<String> retrieveTokenIO(NotificationQueueDTO notificationQueueDTO) {
        log.info("Search service for {} and {}", notificationQueueDTO.getEnteId(), notificationQueueDTO.getTipoDovutoId());
        Optional<IOService> ioService = ioServiceRepository.
                findByEnteIdAndTipoDovutoId(notificationQueueDTO.getEnteId(), notificationQueueDTO.getTipoDovutoId());

        if (ioService.isEmpty()) {
            saveNotification(notificationQueueDTO, null, IONotificationConstants.NOTIFICATION_STATUS_KO_SERVICE_NOT_FOUND);
            log.error("There is no service for organizationId {} and tipoDovutoId {}", notificationQueueDTO.getEnteId(), notificationQueueDTO.getTipoDovutoId());
            return Optional.empty();
        }

        log.info("Retrieve token from IO for service {}", ioService.get().getServiceId() );
        KeysDTO keys = connector.getServiceKeys(ioService.get().getServiceId());
        return Optional.of(keys.getPrimaryKey());
    }

    private boolean isSenderAllowed(NotificationQueueDTO notificationQueueDTO, String token) {
        FiscalCodeDTO fiscalCode = ioNotificationMapper.mapToGetProfile(notificationQueueDTO);
        try{
            log.info("Verify if is user is allowed to receive notification");
            ProfileResource profileResource = connector.getProfile(fiscalCode, token);
            if (!profileResource.isSenderAllowed()) {
                return handleSenderNotAllowed(notificationQueueDTO);
            }
        } catch (SenderNotAllowedException e) {
            return handleSenderNotAllowed(notificationQueueDTO);
        }
        return true;
    }

    private boolean handleSenderNotAllowed(NotificationQueueDTO notificationQueueDTO) {
        log.error("The user is not enabled to receive notifications");
        saveNotification(notificationQueueDTO, null, IONotificationConstants.NOTIFICATION_STATUS_KO_SENDER_NOT_ALLOWED);
        return false;
    }

    private void sendNotification(NotificationQueueDTO notificationQueueDTO, String token) {
        String customSubject = subject.replace("%tipoDovutoName%", notificationQueueDTO.getTipoDovutoName());
        NotificationDTO notificationDTO = ioNotificationMapper
                .mapToQueue(notificationQueueDTO.getFiscalCode(), timeToLive, customSubject, markdown);

        log.info("Sending notification to IO");
        NotificationResource notificationResource = connector.sendNotification(notificationDTO, token);
        saveNotification(notificationQueueDTO, notificationResource.getId(), IONotificationConstants.NOTIFICATION_STATUS_OK);
    }

    private void saveNotification(NotificationQueueDTO notificationQueueDTO, String notificationId, String status) {
        IONotification ioNotification = ioNotificationMapper.mapToSaveNotification(notificationQueueDTO, status);

        if (notificationId != null) {
            ioNotification.setNotificationId(notificationId);
        }

        ioNotificationRepository.save(ioNotification);
    }
}

package it.gov.pagopa.payhub.ionotification.service.notify;

import it.gov.pagopa.payhub.ionotification.connector.IORestConnector;
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

import static it.gov.pagopa.payhub.ionotification.constants.IONotificationConstants.*;

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
            Optional<String> token = retrieveTokenIO(ioService.get());
            if (token.isPresent() && isSenderAllowed(notificationQueueDTO, ioService.get(), token.get())) {
                sendNotification(notificationQueueDTO, ioService.get(), token.get());
            }
        }
    }

    private Optional<IOService> retrieveIOService(NotificationQueueDTO notificationQueueDTO) {
        log.info("Search service for {} and {}", notificationQueueDTO.getEnteId(), notificationQueueDTO.getTipoDovutoId());
        Optional<IOService> ioService = ioServiceRepository
                .findByEnteIdAndTipoDovutoId(notificationQueueDTO.getEnteId(), notificationQueueDTO.getTipoDovutoId());

        if (ioService.isEmpty()) {
            saveNotification(notificationQueueDTO, null, null, NOTIFICATION_STATUS_KO_SERVICE_NOT_FOUND);
            log.error("There is no service for organizationId {} and tipoDovutoId {}", notificationQueueDTO.getEnteId(), notificationQueueDTO.getTipoDovutoId());
        }
        return ioService;
    }

    private Optional<String> retrieveTokenIO(IOService ioService) {
        log.info("Retrieve token from IO for service {}", ioService.getServiceId());
        KeysDTO keys = connector.getServiceKeys(ioService.getServiceId());
        return Optional.of(keys.getPrimaryKey());
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
        log.error("The user is not enabled to receive notifications");
        saveNotification(notificationQueueDTO, ioService, null, NOTIFICATION_STATUS_KO_SENDER_NOT_ALLOWED);
        return false;
    }

    private void sendNotification(NotificationQueueDTO notificationQueueDTO, IOService ioService, String token) {
        String customSubject = subject.replace("%tipoDovutoName%", ioService.getServiceName());
        NotificationDTO notificationDTO = ioNotificationMapper
                .mapToQueue(notificationQueueDTO.getFiscalCode(), timeToLive, customSubject, markdown);

        log.info("Sending notification to IO");
        NotificationResource notificationResource = connector.sendNotification(notificationDTO, token);
        saveNotification(notificationQueueDTO, ioService, notificationResource.getId(), NOTIFICATION_STATUS_OK);
    }
    private void saveNotification(NotificationQueueDTO notificationQueueDTO, IOService ioService, String notificationId, String status) {
        IONotification ioNotification = ioNotificationMapper.mapToSaveNotification(notificationQueueDTO, status);

        Optional.ofNullable(notificationId).ifPresent(ioNotification::setNotificationId);

        Optional.ofNullable(ioService).ifPresent(service -> {
            ioNotification.setEnteName(service.getOrganizationName());
            ioNotification.setTipoDovutoName(service.getServiceName());
        });

        ioNotificationRepository.save(ioNotification);
    }
}

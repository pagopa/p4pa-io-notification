package it.gov.pagopa.payhub.ionotification.dto.mapper;

import it.gov.pagopa.payhub.ionotification.dto.FiscalCodeDTO;
import it.gov.pagopa.payhub.ionotification.dto.MessageContent;
import it.gov.pagopa.payhub.ionotification.dto.NotificationDTO;
import it.gov.pagopa.payhub.ionotification.enums.NotificationStatus;
import it.gov.pagopa.payhub.ionotification.model.IONotification;
import it.gov.pagopa.payhub.ionotification.dto.generated.NotificationQueueDTO;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class IONotificationMapper {
    public NotificationDTO mapToQueue(String fiscalCode, Long timeToLive, String subject, String markdown){
        MessageContent messageContent = MessageContent.builder()
                .markdown(markdown)
                .subject(subject)
                .build();
        return NotificationDTO.builder()
                .timeToLive(timeToLive)
                .content(messageContent)
                .fiscalCode(fiscalCode)
                .build();
    }

    public FiscalCodeDTO mapToGetProfile(NotificationQueueDTO notificationQueueDTO) {
        return FiscalCodeDTO.builder()
                .fiscalCode(notificationQueueDTO.getFiscalCode())
                .build();
    }

    public IONotification mapToSaveNotification(NotificationQueueDTO notificationQueueDTO, NotificationStatus status, String userId){
        return IONotification.builder()
                .notificationDate(LocalDateTime.now())
                .userId(userId)
                .notificationStatus(status)
                .tipoDovutoId(notificationQueueDTO.getTipoDovutoId())
                .enteId(notificationQueueDTO.getEnteId())
                .build();
    }
}

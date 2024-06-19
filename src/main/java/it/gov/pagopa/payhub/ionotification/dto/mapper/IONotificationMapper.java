package it.gov.pagopa.payhub.ionotification.dto.mapper;

import it.gov.pagopa.payhub.ionotification.dto.MessageContent;
import it.gov.pagopa.payhub.ionotification.dto.NotificationDTO;
import org.springframework.stereotype.Service;

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
}

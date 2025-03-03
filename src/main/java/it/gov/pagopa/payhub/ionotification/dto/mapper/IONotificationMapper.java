package it.gov.pagopa.payhub.ionotification.dto.mapper;

import it.gov.pagopa.payhub.ionotification.dto.FiscalCodeDTO;
import it.gov.pagopa.payhub.ionotification.dto.MessageContent;
import it.gov.pagopa.payhub.ionotification.dto.NotificationDTO;
import it.gov.pagopa.payhub.ionotification.dto.generated.NotificationRequestDTO;
import it.gov.pagopa.payhub.ionotification.enums.NotificationStatus;
import it.gov.pagopa.payhub.ionotification.model.IONotification;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class IONotificationMapper {
    public NotificationDTO map(String fiscalCode, Long timeToLive, String subject, String markdown){
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

    public FiscalCodeDTO mapToGetProfile(NotificationRequestDTO notificationRequestDTO) {
        return FiscalCodeDTO.builder()
                .fiscalCode(notificationRequestDTO.getFiscalCode())
                .build();
    }

    public IONotification mapToSaveNotification(NotificationRequestDTO notificationRequestDTO, NotificationStatus status, String userId){
        return IONotification.builder()
                .notificationDate(LocalDateTime.now())
                .userId(userId)
                .notificationStatus(status)
                .debtPositionTypeOrgId(notificationRequestDTO.getDebtPositionTypeOrgId())
                .orgId(notificationRequestDTO.getOrgId())
                .operationType(notificationRequestDTO.getOperationType())
                .build();
    }
}

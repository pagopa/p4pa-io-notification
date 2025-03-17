package it.gov.pagopa.payhub.ionotification.dto.mapper;

import it.gov.pagopa.payhub.ionotification.dto.*;
import it.gov.pagopa.payhub.ionotification.dto.generated.NotificationRequestDTO;
import it.gov.pagopa.payhub.ionotification.enums.NotificationStatus;
import it.gov.pagopa.payhub.ionotification.model.IONotification;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class IONotificationMapper {
    public NotificationDTO map(Long timeToLive, NotificationRequestDTO notificationRequestDTO){
        MessageContent messageContent = MessageContent.builder()
                .markdown(notificationRequestDTO.getMarkdown())
                .subject(notificationRequestDTO.getSubject())
                .build();
        Payee payee = Payee.builder()
                .fiscalCode(notificationRequestDTO.getFiscalCode())
                .build();
        PaymentData paymentData = PaymentData.builder()
                .payee(payee)
                .noticeNumber(notificationRequestDTO.getNav())
                .invalidAfterDueDate(false)
                .amount(notificationRequestDTO.getAmount())
                .build();
        return NotificationDTO.builder()
                .timeToLive(timeToLive)
                .content(messageContent)
                .fiscalCode(notificationRequestDTO.getFiscalCode())
                .paymentData(paymentData)
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
                .build();
    }
}

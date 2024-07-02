package it.gov.pagopa.payhub.ionotification.model;

import it.gov.pagopa.payhub.ionotification.enums.NotificationStatus;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldNameConstants;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@FieldNameConstants
@Document(collection = "io_notification")
@Builder
public class IONotification {

    @Id
    private String id;
    private String userId;
    private String notificationId;
    private NotificationStatus notificationStatus;
    private Long enteId;
    private String enteName;
    private Long tipoDovutoId;
    private String tipoDovutoName;
    private LocalDateTime notificationDate;
    private LocalDateTime retryDate;

}

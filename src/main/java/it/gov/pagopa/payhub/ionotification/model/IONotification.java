package it.gov.pagopa.payhub.ionotification.model;

import lombok.Data;
import lombok.experimental.FieldNameConstants;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@FieldNameConstants
@Document(collection = "io_notification")
public class IONotification {

    @Id
    private String id;
    private String userId;
    private String notificationId;
    private String notificationStatus;
    private String enteId;
    private String tipoDovutoId;
    private LocalDateTime notificationDate;
    private Integer retry;
    private LocalDateTime retryDate;

}

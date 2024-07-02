package it.gov.pagopa.payhub.ionotification.model;

import it.gov.pagopa.payhub.ionotification.enums.ServiceStatus;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldNameConstants;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@FieldNameConstants
@Document(collection = "io_service")
@Builder
public class IOService {

    @Id
    private String id;
    private String serviceId;
    private Long enteId;
    private Long tipoDovutoId;
    private ServiceStatus status;
    private String serviceName;
    private String serviceDescription;
    private String organizationName;
    private String organizationDepartmentName;
    private String organizationFiscalCode;
    private LocalDateTime creationRequestDate;
    private LocalDateTime creationServiceDate;

}

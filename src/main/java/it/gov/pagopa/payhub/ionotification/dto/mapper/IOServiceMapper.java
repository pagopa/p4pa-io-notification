package it.gov.pagopa.payhub.ionotification.dto.mapper;

import it.gov.pagopa.payhub.ionotification.model.IOService;
import it.gov.pagopa.payhub.ionotification.dto.generated.ServiceDTO;
import it.gov.pagopa.payhub.ionotification.dto.generated.ServiceRequestDTO;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class IOServiceMapper {

    public IOService apply(Long enteId, Long tipoDovutoId, ServiceRequestDTO serviceRequestDTO) {
        return IOService.builder()
                .enteId(enteId)
                .tipoDovutoId(tipoDovutoId)
                .serviceName(serviceRequestDTO.getName())
                .serviceDescription(serviceRequestDTO.getDescription())
                .organizationName(serviceRequestDTO.getOrganization().getName())
                .organizationDepartmentName(serviceRequestDTO.getOrganization().getDepartmentName())
                .organizationFiscalCode(serviceRequestDTO.getOrganization().getFiscalCode())
                .creationRequestDate(LocalDateTime.now())
                .build();

    }

    public ServiceDTO mapService(IOService service){
        String creationServiceDate = service.getCreationServiceDate() != null ? service.getCreationServiceDate().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) : null;
        return ServiceDTO.builder()
                .serviceId(service.getServiceId())
                .serviceName(service.getServiceName())
                .status(service.getStatus().name())
                .serviceDescription(service.getServiceDescription())
                .organizationName(service.getOrganizationName())
                .organizationDepartmentName(service.getOrganizationDepartmentName())
                .organizationFiscalCode(service.getOrganizationFiscalCode())
                .creationRequestDate(service.getCreationRequestDate().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                .creationServiceDate(creationServiceDate)
                .build();
    }
}

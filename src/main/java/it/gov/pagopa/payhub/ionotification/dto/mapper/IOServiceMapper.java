package it.gov.pagopa.payhub.ionotification.dto.mapper;

import it.gov.pagopa.payhub.ionotification.model.IOService;
import it.gov.pagopa.payhub.model.generated.ServiceDTO;
import it.gov.pagopa.payhub.model.generated.ServiceRequestDTO;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class IOServiceMapper {

    public IOService apply(String enteId, String tipoDovutoId, ServiceRequestDTO serviceRequestDTO) {
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
        return ServiceDTO.builder()
                .serviceId(service.getServiceId() != null ? service.getServiceId() : null)
                .serviceName(service.getServiceName())
                .status(service.getStatus())
                .serviceDescription(service.getServiceDescription())
                .organizationName(service.getOrganizationName())
                .organizationDepartmentName(service.getOrganizationDepartmentName())
                .organizationFiscalCode(service.getOrganizationFiscalCode())
                .creationRequestDate(String.valueOf(service.getCreationRequestDate()))
                .creationServiceDate(service.getCreationServiceDate() != null ? String.valueOf(service.getCreationServiceDate()) : null)
                .build();
    }
}

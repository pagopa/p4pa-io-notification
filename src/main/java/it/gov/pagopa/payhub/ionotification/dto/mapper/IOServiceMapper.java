package it.gov.pagopa.payhub.ionotification.dto.mapper;

import it.gov.pagopa.payhub.ionotification.model.IOService;
import it.gov.pagopa.payhub.model.generated.ServiceRequestDTO;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class IOServiceMapper {

    public IOService apply(String enteId, String tipoDovutoId, ServiceRequestDTO serviceRequestDTO) {
        return IOService.builder()
                .enteId(enteId)
                .tipoDovutoId(tipoDovutoId)
                .scope(serviceRequestDTO.getMetadata().getScope())
                .topicId(serviceRequestDTO.getMetadata().getTopicId())
                .serviceName(serviceRequestDTO.getName())
                .serviceDescription(serviceRequestDTO.getDescription())
                .organizationName(serviceRequestDTO.getOrganization().getName())
                .organizationDepartmentName(serviceRequestDTO.getOrganization().getDepartmentName())
                .organizationFiscalCode(serviceRequestDTO.getOrganization().getFiscalCode())
                .creationDate(LocalDateTime.now())
                .build();

    }
}

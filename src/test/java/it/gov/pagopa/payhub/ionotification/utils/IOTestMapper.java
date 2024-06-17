package it.gov.pagopa.payhub.ionotification.utils;

import it.gov.pagopa.payhub.ionotification.dto.*;
import it.gov.pagopa.payhub.ionotification.model.IOService;
import it.gov.pagopa.payhub.model.generated.OrganizationRequestDTO;
import it.gov.pagopa.payhub.model.generated.ServiceRequestDTO;
import it.gov.pagopa.payhub.model.generated.ServiceRequestMetadataDTO;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class IOTestMapper {

    public static IOService mapIoService(ServiceRequestDTO serviceRequestDTO) {
        return IOService.builder()
                .enteId("ENTE_ID")
                .tipoDovutoId("TIPO_DOVUTO_ID")
                .scope(serviceRequestDTO.getMetadata().getScope())
                .topicId(0)
                .serviceName(serviceRequestDTO.getName())
                .serviceDescription(serviceRequestDTO.getDescription())
                .organizationName(serviceRequestDTO.getOrganization().getName())
                .organizationDepartmentName(serviceRequestDTO.getOrganization().getDepartmentName())
                .organizationFiscalCode(serviceRequestDTO.getOrganization().getFiscalCode())
                .creationDate(LocalDateTime.now())
                .build();
    }

    public static ServiceRequestDTO createServiceRequestDTO() {
        return ServiceRequestDTO.builder()
                .name("SERVICE_NAME")
                .description("DESCRIPTION")
                .organization(createOrganizationRequestDTO())
                .metadata(createServiceRequestMetadataDTO())
                .build();
    }

    private static ServiceRequestMetadataDTO createServiceRequestMetadataDTO() {
        return ServiceRequestMetadataDTO.builder()
                .email("EMAIL")
                .phone("PHONE")
                .supportUrl("SUPPORT_URL")
                .privacyUrl("PRIVACY_URL")
                .tosUrl("TOS_URL")
                .scope("SCOPE")
                .topicId(0)
                .build();
    }

    private static OrganizationRequestDTO createOrganizationRequestDTO() {
        return OrganizationRequestDTO.builder()
                .departmentName("PRODUCT_DEPARTMENT_NAME")
                .name("ORGANIZATION_NAME")
                .fiscalCode("ORGANIZATION_VAT")
                .build();
    }

    private static OrganizationResponseDTO createOrganizationResponseDTO() {
        return OrganizationResponseDTO.builder()
                .departmentName("PRODUCT_DEPARTMENT_NAME")
                .organizationName("ORGANIZATION_NAME")
                .organizationFiscalCode("ORGANIZATION_VAT")
                .build();
    }

    public static ServiceResponseDTO createServiceResponseDTO() {
        ServiceResponseMetadataDTO serviceMetadataDTO = createServiceResponseMetadataDTO();
        return ServiceResponseDTO.builder()
                .id("SERVICE_ID")
                .serviceName("SERVICE_NAME")
                .organization(createOrganizationResponseDTO())
                .serviceMetadata(serviceMetadataDTO)
                .build();
    }

    private static ServiceResponseMetadataDTO createServiceResponseMetadataDTO() {
        return ServiceResponseMetadataDTO.builder()
                .email("EMAIL")
                .phone("PHONE")
                .supportUrl("SUPPORT_URL")
                .privacyUrl("PRIVACY_URL")
                .tosUrl("TOS_URL")
                .scope("SCOPE")
                .topic(new TopicDTO(0,"Altro"))
                .build();
    }

    public static KeysDTO getTokenIOResponse() {
        return KeysDTO.builder()
                .primaryKey("PRIMARY_KEY")
                .secondaryKey("SECONDARY_KEY")
                .build();
    }

    public static ProfileResource getUserProfileResponse(){
        return ProfileResource.builder()
                .senderAllowed(true)
                .preferredLanguages(new ArrayList<>())
                .build();
    }

    public static FiscalCodeDTO getUserProfileRequest(){
        return FiscalCodeDTO.builder()
                .fiscalCode("FISCAL_CODE")
                .build();
    }

    public static NotificationDTO sendNotificationRequest(){
        MessageContent messageContent = MessageContent.builder()
                .subject("SUBJECT")
                .markdown("MARKDOWN")
                .build();
        return NotificationDTO.builder()
                .content(messageContent)
                .timeToLive(10L)
                .fiscalCode("FISCAL_CODE")
                .build();
    }

    public static ServicesListDTO getAllServicesResponse(){
        ServicePaginatedResponseDTO serviceList = ServicePaginatedResponseDTO.builder()
                .status(new StatusDTO("VALUE", "REASON"))
                .lastUpdate("DATE")
                .id("SERVICE_ID")
                .serviceName("SERVICE_NAME")
                .organization(createOrganizationResponseDTO())
                .build();
        return ServicesListDTO.builder()
                .serviceList(List.of(serviceList))
                .pagination(new PaginationDTO(0,0,0))
                .build();
    }
}

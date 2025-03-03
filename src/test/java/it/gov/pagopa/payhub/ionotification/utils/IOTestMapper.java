package it.gov.pagopa.payhub.ionotification.utils;

import it.gov.pagopa.payhub.ionotification.dto.NotificationDTO;
import it.gov.pagopa.payhub.ionotification.dto.*;
import it.gov.pagopa.payhub.ionotification.dto.generated.*;
import it.gov.pagopa.payhub.ionotification.model.IONotification;
import it.gov.pagopa.payhub.ionotification.model.IOService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static it.gov.pagopa.payhub.ionotification.enums.NotificationStatus.OK;
import static it.gov.pagopa.payhub.ionotification.enums.ServiceStatus.CREATED;

public class IOTestMapper {

    public static final Long DEBT_POSITION_TYPE_ORG_ID = 456L;
    public static final Long ORG_ID = 123L;
    public static final String SERVICE_NAME = "SERVICE_NAME";
    public static final String SERVICE_ID = "SERVICE_ID";
    public static final String DATE = "DATE";
    public static final String VALUE = "VALUE";
    public static final String REASON = "REASON";
    public static final String FISCAL_CODE = "FISCAL_CODE";
    public static final String MARKDOWN = "This is a markdown %causaleVersamento%";
    public static final String SUBJECT = "Test Subject";
    public static final String SECONDARY_KEY = "SECONDARY_KEY";
    public static final String PRIMARY_KEY = "PRIMARY_KEY";
    public static final String DESCRIPTION = "DESCRIPTION";
    public static final String EMAIL = "EMAIL";
    public static final String SCOPE = "SCOPE";
    public static final String PHONE = "PHONE";
    public static final String SUPPORT_URL = "SUPPORT_URL";
    public static final String PRIVACY_URL = "PRIVACY_URL";
    public static final String TOS_URL = "TOS_URL";
    public static final String PRODUCT_DEPARTMENT_NAME = "PRODUCT_DEPARTMENT_NAME";
    public static final String ORGANIZATION_NAME = "ORGANIZATION_NAME";
    public static final String ORGANIZATION_VAT = "ORGANIZATION_VAT";
    public static final String USER_ID = "USER_ID";

    public static IOService mapIoService(ServiceRequestDTO serviceRequestDTO) {
        return IOService.builder()
                .enteId(ORG_ID)
                .tipoDovutoId(DEBT_POSITION_TYPE_ORG_ID)
                .serviceName(serviceRequestDTO.getName())
                .serviceDescription(serviceRequestDTO.getDescription())
                .organizationName(serviceRequestDTO.getOrganization().getName())
                .organizationDepartmentName(serviceRequestDTO.getOrganization().getDepartmentName())
                .organizationFiscalCode(serviceRequestDTO.getOrganization().getFiscalCode())
                .creationRequestDate(LocalDateTime.now())
                .creationServiceDate(LocalDateTime.now())
                .status(CREATED)
                .build();
    }

    public static ServiceRequestDTO createServiceRequestDTO() {
        return ServiceRequestDTO.builder()
                .name(SERVICE_NAME)
                .description(DESCRIPTION)
                .organization(createOrganizationRequestDTO())
                .metadata(createServiceRequestMetadataDTO())
                .build();
    }

    private static ServiceRequestMetadataDTO createServiceRequestMetadataDTO() {
        return ServiceRequestMetadataDTO.builder()
                .email(EMAIL)
                .phone(PHONE)
                .supportUrl(SUPPORT_URL)
                .privacyUrl(PRIVACY_URL)
                .tosUrl(TOS_URL)
                .scope(SCOPE)
                .topicId(BigDecimal.valueOf(0))
                .build();
    }

    private static OrganizationRequestDTO createOrganizationRequestDTO() {
        return OrganizationRequestDTO.builder()
                .departmentName(PRODUCT_DEPARTMENT_NAME)
                .name(ORGANIZATION_NAME)
                .fiscalCode(ORGANIZATION_VAT)
                .build();
    }

    private static OrganizationResponseDTO createOrganizationResponseDTO() {
        return OrganizationResponseDTO.builder()
                .departmentName(PRODUCT_DEPARTMENT_NAME)
                .organizationName(ORGANIZATION_NAME)
                .organizationFiscalCode(ORGANIZATION_VAT)
                .build();
    }

    public static ServiceResponseDTO createServiceResponseDTO() {
        ServiceResponseMetadataDTO serviceMetadataDTO = createServiceResponseMetadataDTO();
        return ServiceResponseDTO.builder()
                .id(SERVICE_ID)
                .serviceName(SERVICE_NAME)
                .organization(createOrganizationResponseDTO())
                .serviceMetadata(serviceMetadataDTO)
                .build();
    }

    private static ServiceResponseMetadataDTO createServiceResponseMetadataDTO() {
        return ServiceResponseMetadataDTO.builder()
                .email(EMAIL)
                .phone(PHONE)
                .supportUrl(SUPPORT_URL)
                .privacyUrl(PRIVACY_URL)
                .tosUrl(TOS_URL)
                .scope(SCOPE)
                .topic(new TopicDTO(0,"Altro"))
                .build();
    }

    public static KeysDTO getTokenIOResponse() {
        return KeysDTO.builder()
                .primaryKey(PRIMARY_KEY)
                .secondaryKey(SECONDARY_KEY)
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
                .fiscalCode(FISCAL_CODE)
                .build();
    }

    public static NotificationDTO sendNotificationRequest(){
        MessageContent messageContent = MessageContent.builder()
                .subject(SUBJECT)
                .markdown(MARKDOWN)
                .build();
        return NotificationDTO.builder()
                .content(messageContent)
                .timeToLive(3600L)
                .fiscalCode(FISCAL_CODE)
                .build();
    }

    public static ServicesListDTO getAllServicesResponse(){
        List<ServicePaginatedResponseDTO>  serviceList = new ArrayList<>();
        ServicePaginatedResponseDTO serviceList1 = ServicePaginatedResponseDTO.builder()
                .status(new StatusDTO(VALUE, REASON))
                .lastUpdate(DATE)
                .id(SERVICE_ID)
                .serviceName(SERVICE_NAME)
                .organization(createOrganizationResponseDTO())
                .build();
        serviceList.add(serviceList1);
        for (int i = 2; i <= 10; i++) {
            ServicePaginatedResponseDTO serviceListN = ServicePaginatedResponseDTO.builder()
                    .status(new StatusDTO("deleted", "reason " + i))
                    .lastUpdate(DATE)
                    .id(SERVICE_ID + (char) ('M' + i))
                    .serviceName(SERVICE_NAME + i)
                    .organization(new OrganizationResponseDTO("Organization" + i, "description " + i, "department " + i))
                    .build();
            serviceList.add(serviceListN);
        }
        return ServicesListDTO.builder()
                .serviceList(serviceList)
                .pagination(new PaginationDTO(0,0,0))
                .build();
    }

    public static ServicesListDTO getAllServicesResponseFeign(){
        ServicePaginatedResponseDTO serviceList = ServicePaginatedResponseDTO.builder()
                .status(new StatusDTO(VALUE, REASON))
                .lastUpdate(DATE)
                .id(SERVICE_ID)
                .serviceName(SERVICE_NAME)
                .organization(createOrganizationResponseDTO())
                .build();
        return ServicesListDTO.builder()
                .serviceList(List.of(serviceList))
                .pagination(new PaginationDTO(0,0,0))
                .build();
    }

    public static NotificationRequestDTO buildNotificationRequestDTO(){
        return NotificationRequestDTO.builder()
                .orgId(ORG_ID)
                .debtPositionTypeOrgId(DEBT_POSITION_TYPE_ORG_ID)
                .serviceId(SERVICE_ID)
                .subject(SUBJECT)
                .markdown(MARKDOWN)
                .fiscalCode(FISCAL_CODE)
                .amount("AMOUNT")
                .iuv("IUV")
                .dueDate("DATE")
                .paymentReason("REASON")
                .operationType("OPERATION_TYPE")
                .build();
    }

    public static ServiceDTO getServiceResponse(){
        return ServiceDTO.builder()
                .serviceId(SERVICE_ID)
                .status(CREATED.name())
                .serviceName(SERVICE_NAME)
                .organizationName(ORGANIZATION_NAME)
                .build();
    }

    public static IONotification mapIONotification(){
        return IONotification.builder()
                .userId(USER_ID)
                .debtPositionTypeOrgId(DEBT_POSITION_TYPE_ORG_ID)
                .orgId(ORG_ID)
                .notificationStatus(OK)
                .build();
    }
}

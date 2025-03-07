package it.gov.pagopa.payhub.ionotification.connector;

import it.gov.pagopa.payhub.ionotification.dto.*;
import it.gov.pagopa.payhub.ionotification.dto.generated.ServiceRequestDTO;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@FeignClient(
        name = "backend-io-manage",
        url = "${rest-client.backend-io-manage.service.base-url}")
public interface IOFeignRestClient {

    @PostMapping(
            value = "/manage/services",
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @ResponseStatus(HttpStatus.CREATED)
    ServiceResponseDTO createService(
            @RequestBody @Valid ServiceRequestDTO serviceRequestDTO,
            @RequestHeader("Ocp-Apim-Subscription-Key") String subscriptionKey);

    @GetMapping(
            value = "/manage/services/{serviceId}/keys",
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    KeysDTO getServiceKeys(
            @PathVariable("serviceId") String serviceId,
            @RequestHeader("Ocp-Apim-Subscription-Key") String subscriptionKey);

    @PostMapping(
            value = "/profiles",
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    ProfileResource getProfile(
            @RequestBody @Valid FiscalCodeDTO fiscalCode,
            @RequestHeader("Ocp-Apim-Subscription-Key") String subscriptionKey);

    @PostMapping(
            value = "/messages",
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @ResponseStatus(HttpStatus.CREATED)
    NotificationResource sendNotification(
            @RequestBody @Valid NotificationDTO notificationDTO,
            @RequestHeader("Ocp-Apim-Subscription-Key") String subscriptionKey);

    @GetMapping(
            value = "/manage/services",
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    ServicesListDTO getAllServices(
            @RequestParam(required = false) Integer limit,
            @RequestParam(required = false) Integer offset,
            @RequestHeader("Ocp-Apim-Subscription-Key") String subscriptionKey);

    @DeleteMapping(
            value = "/manage/services/{serviceId}",
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteService(
            @PathVariable("serviceId") String serviceId,
            @RequestHeader("Ocp-Apim-Subscription-Key") String subscriptionKey);
}

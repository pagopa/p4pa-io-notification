package it.gov.pagopa.payhub.ionotification.connector;

import it.gov.pagopa.payhub.ionotification.dto.KeysDTO;
import it.gov.pagopa.payhub.ionotification.dto.ServiceRequestDTO;
import it.gov.pagopa.payhub.ionotification.dto.ServiceResponseDTO;
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
    @ResponseStatus(HttpStatus.OK)
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


}

package it.gov.pagopa.payhub.ionotification.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.gov.pagopa.payhub.ionotification.service.IOService;
import it.gov.pagopa.payhub.model.generated.NotificationQueueDTO;
import it.gov.pagopa.payhub.model.generated.ServiceDTO;
import it.gov.pagopa.payhub.model.generated.ServiceRequestDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static it.gov.pagopa.payhub.ionotification.utils.IOTestMapper.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(IONotificationControllerImpl.class)
class IONotificationControllerImplTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private IOService ioService;

    @Test
    void givenCreateServiceThenSuccess() throws Exception {
        ServiceRequestDTO serviceRequestDTO = createServiceRequestDTO();
        doNothing().when(ioService)
                .createService("enteId", "tipoDovutoId", serviceRequestDTO);

        mockMvc.perform(
                post("/notification/create/service/enteId/tipoDovutoId")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(serviceRequestDTO)))
                .andExpect(status().is2xxSuccessful())
                .andReturn();
    }

    @Test
    void givenSendNotificationThenSuccess() throws Exception {
        NotificationQueueDTO notificationQueueDTO = mapToSendMessageToQueue();
        doNothing().when(ioService)
                .sendMessage(notificationQueueDTO);

        mockMvc.perform(
                        post("/notification/send/message")
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .accept(MediaType.APPLICATION_JSON_VALUE)
                                .content(objectMapper.writeValueAsString(notificationQueueDTO)))
                .andExpect(status().is2xxSuccessful())
                .andReturn();
    }

    @Test
    void givenGetServiceThenSuccess() throws Exception {
        ServiceDTO serviceDTO = getServiceResponse();
        when(ioService.getService("enteId", "tipoDovutoId")).thenReturn(serviceDTO);

        MvcResult result = mockMvc.perform(
                        get("/notification/service/enteId/tipoDovutoId")
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andReturn();

        ServiceDTO resultResponse = objectMapper.readValue(result.getResponse().getContentAsString(), ServiceDTO.class);
        assertEquals(serviceDTO, resultResponse);
    }
}

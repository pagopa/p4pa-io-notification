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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(IONotificationControllerImpl.class)
class IONotificationControllerImplTest {

    public static final Long TIPO_DOVUTO_ID = 456L;
    public static final Long ENTE_ID = 123L;

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
                .createService(ENTE_ID, TIPO_DOVUTO_ID, serviceRequestDTO);

        mockMvc.perform(
                post("/ionotification/service/"+ENTE_ID+"/"+TIPO_DOVUTO_ID)
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
                        post("/ionotification/send/message")
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .accept(MediaType.APPLICATION_JSON_VALUE)
                                .content(objectMapper.writeValueAsString(notificationQueueDTO)))
                .andExpect(status().is2xxSuccessful())
                .andReturn();
    }

    @Test
    void givenGetServiceThenSuccess() throws Exception {
        ServiceDTO serviceDTO = getServiceResponse();
        when(ioService.getService(ENTE_ID, TIPO_DOVUTO_ID)).thenReturn(serviceDTO);

        MvcResult result = mockMvc.perform(
                        get("/ionotification/service/"+ENTE_ID+"/"+TIPO_DOVUTO_ID)
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andReturn();

        ServiceDTO resultResponse = objectMapper.readValue(result.getResponse().getContentAsString(), ServiceDTO.class);
        assertEquals(serviceDTO, resultResponse);
    }

    @Test
    void givenDeleteServiceThenSuccess() throws Exception {
        doNothing().when(ioService)
                .deleteService("serviceId");

        mockMvc.perform(put("/ionotification/service/serviceId")
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().is2xxSuccessful())
                .andReturn();
    }
}

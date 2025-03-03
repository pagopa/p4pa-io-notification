package it.gov.pagopa.payhub.ionotification.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.gov.pagopa.payhub.ionotification.dto.generated.*;
import it.gov.pagopa.payhub.ionotification.service.IOService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static it.gov.pagopa.payhub.ionotification.utils.IOTestMapper.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(IONotificationControllerImpl.class)
@AutoConfigureMockMvc(addFilters = false)
class IONotificationControllerImplTest {

    public static final Long TIPO_DOVUTO_ID = 456L;
    public static final Long ENTE_ID = 123L;
    public static final String USERID = "USERID";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
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
    void givenSendMessageThenSuccess() throws Exception {
        NotificationRequestDTO notificationRequestDTO = buildNotificationRequestDTO();
        MessageResponseDTO messageResponseDTO = MessageResponseDTO.builder().notificationId("id").build();

        Mockito.when(ioService.sendMessage(notificationRequestDTO))
                .thenReturn(messageResponseDTO);

        MvcResult result = mockMvc.perform(
                        post("/ionotification/message")
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .accept(MediaType.APPLICATION_JSON_VALUE)
                                .content(objectMapper.writeValueAsString(notificationRequestDTO)))
                .andExpect(status().is2xxSuccessful())
                .andReturn();

        MessageResponseDTO resultResponse = objectMapper.readValue(result.getResponse().getContentAsString(), MessageResponseDTO.class);
        assertEquals(messageResponseDTO, resultResponse);
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

        mockMvc.perform(delete("/ionotification/service/serviceId")
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().is2xxSuccessful())
                .andReturn();
    }

    @Test
    void givenDeleteNotificationThenSuccess() throws Exception {
        doNothing().when(ioService)
                .deleteNotification(USERID, ENTE_ID, TIPO_DOVUTO_ID);

        mockMvc.perform(delete("/ionotification/message/"+USERID+"/"+ENTE_ID+"/"+TIPO_DOVUTO_ID)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().is2xxSuccessful())
                .andReturn();
    }
}

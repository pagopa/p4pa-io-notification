package it.gov.pagopa.payhub.ionotification.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.gov.pagopa.payhub.ionotification.service.IONotificationService;
import it.gov.pagopa.payhub.model.generated.ServiceRequestDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static it.gov.pagopa.payhub.ionotification.utils.IOTestMapper.createServiceRequestDTO;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(IONotificationControllerImpl.class)
class IONotificationControllerImplTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private IONotificationService ioNotificationService;

    @Test
    void givenCreateServiceThenSuccess() throws Exception {
        ServiceRequestDTO serviceRequestDTO = createServiceRequestDTO();
        doNothing().when(ioNotificationService)
                .createService("enteId", "tipoDovutoId", serviceRequestDTO);

        mockMvc.perform(
                post("/notification/create/service/enteId/tipoDovutoId")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(serviceRequestDTO)))
                .andExpect(status().is2xxSuccessful())
                .andReturn();
    }
}

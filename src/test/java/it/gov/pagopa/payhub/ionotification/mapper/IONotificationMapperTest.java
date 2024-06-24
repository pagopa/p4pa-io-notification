package it.gov.pagopa.payhub.ionotification.mapper;

import it.gov.pagopa.payhub.ionotification.dto.NotificationDTO;
import it.gov.pagopa.payhub.ionotification.dto.mapper.IONotificationMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = IONotificationMapper.class)
class IONotificationMapperTest {

    @Autowired
    IONotificationMapper ioNotificationMapper;

    @Test
    void whenMapToQueueThenSuccess(){
        NotificationDTO notificationDTO = ioNotificationMapper
                .mapToQueue("FISCAL_CODE", 1L, "SUBJECT", "MARKDOWN");
        assertNotNull(notificationDTO);
    }
}

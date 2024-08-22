package it.gov.pagopa.payhub.ionotification.mapper;

import it.gov.pagopa.payhub.ionotification.dto.FiscalCodeDTO;
import it.gov.pagopa.payhub.ionotification.dto.NotificationDTO;
import it.gov.pagopa.payhub.ionotification.dto.mapper.IONotificationMapper;
import it.gov.pagopa.payhub.ionotification.model.IONotification;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static it.gov.pagopa.payhub.ionotification.enums.NotificationStatus.OK;
import static it.gov.pagopa.payhub.ionotification.utils.IOTestMapper.USER_ID;
import static it.gov.pagopa.payhub.ionotification.utils.IOTestMapper.mapToSendMessageToQueue;
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

    @Test
    void whenMapToGetProfileThenSuccess(){
        FiscalCodeDTO fiscalCodeDTO = ioNotificationMapper
                .mapToGetProfile(mapToSendMessageToQueue());
        assertNotNull(fiscalCodeDTO);
    }

    @Test
    void whenMapToSaveNotificationThenSuccess(){
        IONotification ioNotification = ioNotificationMapper
                .mapToSaveNotification(mapToSendMessageToQueue(), OK, USER_ID.getBytes());
        assertNotNull(ioNotification);
    }
}

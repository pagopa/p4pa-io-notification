package it.gov.pagopa.payhub.ionotification.mapper;

import it.gov.pagopa.payhub.ionotification.dto.FiscalCodeDTO;
import it.gov.pagopa.payhub.ionotification.dto.NotificationDTO;
import it.gov.pagopa.payhub.ionotification.dto.generated.NotificationRequestDTO;
import it.gov.pagopa.payhub.ionotification.dto.mapper.IONotificationMapper;
import it.gov.pagopa.payhub.ionotification.model.IONotification;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static it.gov.pagopa.payhub.ionotification.enums.NotificationStatus.OK;
import static it.gov.pagopa.payhub.ionotification.utils.IOTestMapper.USER_ID;
import static it.gov.pagopa.payhub.ionotification.utils.IOTestMapper.buildNotificationRequestDTO;
import static it.gov.pagopa.payhub.ionotification.utils.TestUtils.checkNotNullFields;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = IONotificationMapper.class)
class IONotificationMapperTest {

    @Autowired
    IONotificationMapper ioNotificationMapper;

    @Test
    void whenMapThenSuccess(){
        NotificationDTO notificationDTO = ioNotificationMapper
                .map("FISCAL_CODE", 1L, "SUBJECT", "MARKDOWN", "NAV", 1L);

        assertNotNull(notificationDTO);
        checkNotNullFields(notificationDTO);
    }

    @Test
    void whenMapToGetProfileThenSuccess(){
        FiscalCodeDTO fiscalCodeDTO = ioNotificationMapper
                .mapToGetProfile(buildNotificationRequestDTO());

        assertNotNull(fiscalCodeDTO);
        checkNotNullFields(fiscalCodeDTO);
    }

    @Test
    void whenMapToSaveNotificationThenSuccess(){
        NotificationRequestDTO notificationRequestDTO = buildNotificationRequestDTO();

        IONotification ioNotification = ioNotificationMapper
                .mapToSaveNotification(notificationRequestDTO, OK, USER_ID);

        assertNotNull(ioNotification);
        // the notificationId is set only when notification was sent successfully
        checkNotNullFields(ioNotification, "id", "notificationId");
    }
}

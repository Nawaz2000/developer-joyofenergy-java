package uk.tw.energy.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.tw.energy.service.impl.MeterReadingServiceImpl;

class MeterReadingServiceTest {

    private MeterReadingServiceImpl meterReadingServiceImpl;

    @BeforeEach
    void setUp() {
        meterReadingServiceImpl = new MeterReadingServiceImpl(new HashMap<>());
    }

    @Test
    void givenMeterIdThatDoesNotExistShouldReturnNull() {
        assertThat(meterReadingServiceImpl.getReadings("unknown-id")).isEqualTo(Optional.empty());
    }

    @Test
    void givenMeterReadingThatExistsShouldReturnMeterReadings() {
        meterReadingServiceImpl.storeReadings("random-id", new ArrayList<>());
        assertThat(meterReadingServiceImpl.getReadings("random-id")).isEqualTo(Optional.of(new ArrayList<>()));
    }
}

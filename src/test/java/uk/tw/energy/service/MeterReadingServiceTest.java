package uk.tw.energy.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.tw.energy.domain.ElectricityReading;
import uk.tw.energy.generator.ElectricityReadingsGenerator;
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
        final ElectricityReadingsGenerator electricityReadingsGenerator = new ElectricityReadingsGenerator();
        List<ElectricityReading> readings = electricityReadingsGenerator.generate(20);
        meterReadingServiceImpl.storeReadings("random-id", readings);
        assertThat(meterReadingServiceImpl.getReadings("random-id").get()).isEqualTo(readings);
    }
}

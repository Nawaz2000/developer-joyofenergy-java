package uk.tw.energy.generator;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

import lombok.extern.slf4j.Slf4j;
import uk.tw.energy.domain.ElectricityReading;

/**
 * This class generates a list of electricity readings with random values.
 */
@Slf4j
public class ElectricityReadingsGenerator {

    private final Random readingRandomiser = new Random();

    /**
     * Generates a list of electricity readings with random values.
     *
     * @param number The number of electricity readings to generate.
     * @return A list of electricity readings, sorted by time in ascending order.
     */
    public List<ElectricityReading> generate(int number) {
        log.info("Generated {} electricity readings", number);
        List<ElectricityReading> readings = new ArrayList<>();
        Instant now = Instant.now();

        for (int i = 0; i < number; i++) {
            double positiveRandomValue = Math.abs(readingRandomiser.nextGaussian());
            BigDecimal randomReading = BigDecimal.valueOf(positiveRandomValue).setScale(4, RoundingMode.CEILING);
            ElectricityReading electricityReading = new ElectricityReading(now.minusSeconds(i * 10L), randomReading);
            readings.add(electricityReading);
        }

        readings.sort(Comparator.comparing(ElectricityReading::time));
        readings.forEach(reading -> log.info("Reading: {} - {}", reading.time(), reading.reading()));

        return readings;
    }
}

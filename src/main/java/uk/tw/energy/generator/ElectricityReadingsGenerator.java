package uk.tw.energy.generator;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.tw.energy.domain.ElectricityReading;

/**
 * This class generates a list of electricity readings with random values.
 */
@Slf4j
public class ElectricityReadingsGenerator {

    private static final Logger logger = LoggerFactory.getLogger(ElectricityReadingsGenerator.class);

    /**
     * Generates a list of electricity readings with random values.
     *
     * @param number The number of electricity readings to generate.
     * @return A list of electricity readings, sorted by time in ascending order.
     */
    public List<ElectricityReading> generate(int number) {
        logger.info("Generated {} electricity readings", number);
        List<ElectricityReading> readings = new ArrayList<>();
        Instant now = Instant.now();

        Random readingRandomiser = new Random();
        for (int i = 0; i < number; i++) {
            double positiveRandomValue = Math.abs(readingRandomiser.nextGaussian());
            BigDecimal randomReading = BigDecimal.valueOf(positiveRandomValue).setScale(4, RoundingMode.CEILING);
            ElectricityReading electricityReading = new ElectricityReading(now.minusSeconds(i * 10L), randomReading);
            readings.add(electricityReading);
        }

        readings.sort(Comparator.comparing(ElectricityReading::time));
        readings.forEach(reading -> logger.info("Reading: {} - {}", reading.time(), reading.reading()));

        return readings;
    }
}

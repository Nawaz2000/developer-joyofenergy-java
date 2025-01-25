package uk.tw.energy.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.tw.energy.domain.ElectricityReading;
import uk.tw.energy.domain.PricePlan;

@Slf4j
public class ElectricityUtil {

    private static final Logger logger = LoggerFactory.getLogger(ElectricityUtil.class);

    private ElectricityUtil() {
        // Private constructor to prevent instantiation
    }

    public static boolean isMeterReadingsValid(String smartMeterId, List<ElectricityReading> electricityReadings) {
        return smartMeterId != null
                && !smartMeterId.isEmpty()
                && electricityReadings != null
                && !electricityReadings.isEmpty();
    }

    public static BigDecimal calculateCost(List<ElectricityReading> electricityReadings, PricePlan pricePlan) {
        BigDecimal average = calculateAverageReading(electricityReadings);
        BigDecimal timeElapsed = calculateTimeElapsed(electricityReadings);
        BigDecimal averagedCost = average.divide(timeElapsed, RoundingMode.HALF_UP);
        BigDecimal result = averagedCost.multiply(pricePlan.getUnitRate());

        logger.info("Cost calculation: {}", result);

        return result;
    }

    public static BigDecimal calculateAverageReading(List<ElectricityReading> electricityReadings) {
        if (electricityReadings.isEmpty()) {
            logger.warn("Electricity readings list is empty, average calculation may result in an error");
        }

        BigDecimal summedReadings = electricityReadings.stream()
                .map(ElectricityReading::reading)
                .reduce(BigDecimal.ZERO, (reading, accumulator) -> reading.add(accumulator));

        return summedReadings.divide(BigDecimal.valueOf(electricityReadings.size()), RoundingMode.HALF_UP);
    }

    public static BigDecimal calculateTimeElapsed(List<ElectricityReading> electricityReadings) {
        Optional<ElectricityReading> firstOptional =
                electricityReadings.stream().min(Comparator.comparing(ElectricityReading::time));

        Optional<ElectricityReading> lastOptional =
                electricityReadings.stream().max(Comparator.comparing(ElectricityReading::time));

        if (firstOptional.isPresent() && lastOptional.isPresent()) {
            ElectricityReading first = firstOptional.get();
            ElectricityReading last = lastOptional.get();

            return BigDecimal.valueOf(
                    Duration.between(first.time(), last.time()).getSeconds() / 3600.0);
        } else {
            logger.warn("Electricity readings list does not contain both first and last readings, time elapsed calculation may result in an error");

            return BigDecimal.ZERO;
        }
    }
}

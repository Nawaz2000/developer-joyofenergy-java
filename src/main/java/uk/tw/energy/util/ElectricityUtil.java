package uk.tw.energy.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import uk.tw.energy.domain.ElectricityReading;
import uk.tw.energy.domain.PricePlan;

/**
 * Utility class for electricity-related calculations and validations.
 *
 * This class provides static methods for various operations related to electricity readings,
 * including validation of meter readings, cost calculation, average reading calculation,
 * and time elapsed calculation between readings.
 *
 * The class uses BigDecimal for precise calculations and includes logging for important
 * information and warnings.
 *
 * This class cannot be instantiated as it only contains static utility methods.
 */
@Slf4j
public class ElectricityUtil {

    private ElectricityUtil() {
        // Private constructor to prevent instantiation
    }

    /**
     * Validates the meter readings by checking if the smart meter ID is valid and the electricity readings list is not empty.
     *
     * @param smartMeterId        The ID of the smart meter.
     * @param electricityReadings The list of electricity readings.
     * @return true if the meter readings are valid, false otherwise.
     */
    public static boolean isMeterReadingsValid(String smartMeterId, List<ElectricityReading> electricityReadings) {
        return smartMeterId != null
                && !smartMeterId.isEmpty()
                && electricityReadings != null
                && !electricityReadings.isEmpty();
    }

    /**
     * Calculates the cost of electricity consumption based on the provided readings and price plan.
     *
     * @param electricityReadings The list of electricity readings.
     * @param pricePlan           The price plan to be used for cost calculation.
     * @return The calculated cost as a BigDecimal.
     */
    public static BigDecimal calculateCost(List<ElectricityReading> electricityReadings, PricePlan pricePlan) {
        BigDecimal average = calculateAverageReading(electricityReadings);
        BigDecimal timeElapsed = calculateTimeElapsed(electricityReadings);
        BigDecimal averagedCost = average.divide(timeElapsed, RoundingMode.HALF_UP);
        BigDecimal result = averagedCost.multiply(pricePlan.getUnitRate());

        log.info("Cost calculation: {}", result);

        return result;
    }

    /**
     * Calculates the average electricity reading from a list of readings.
     *
     * @param electricityReadings The list of electricity readings.
     * @return The average reading as a BigDecimal.
     */
    private static BigDecimal calculateAverageReading(List<ElectricityReading> electricityReadings) {
        if (electricityReadings.isEmpty()) {
            log.warn("Electricity readings list is empty, average calculation may result in an error");
        }

        BigDecimal summedReadings = electricityReadings.stream()
                .map(ElectricityReading::reading)
                .reduce(BigDecimal.ZERO, (reading, accumulator) -> reading.add(accumulator));

        return summedReadings.divide(BigDecimal.valueOf(electricityReadings.size()), RoundingMode.HALF_UP);
    }

    /**
     * Calculates the time elapsed between the first and last electricity readings.
     *
     * @param electricityReadings The list of electricity readings.
     * @return The time elapsed in hours as a BigDecimal. Returns BigDecimal.ZERO if the list doesn't contain both first and last readings.
     */
    private static BigDecimal calculateTimeElapsed(List<ElectricityReading> electricityReadings) {
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
            log.warn(
                    "Electricity readings list does not contain both first and last readings, time elapsed calculation may result in an error");

            return BigDecimal.ZERO;
        }
    }
}

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

@Slf4j
public class ElectricityUtil {

    private ElectricityUtil() {
        // Private constructor to prevent instantiation
    }

    /**
     * Validates the smart meter ID and electricity readings list.
     *
     * @param smartMeterId The smart meter ID to validate.
     * @param electricityReadings The list of electricity readings to validate.
     * @return True if the smart meter ID and electricity readings list are valid; false otherwise.
     */
    public static boolean isMeterReadingsValid(String smartMeterId, List<ElectricityReading> electricityReadings) {
        return smartMeterId != null
                && !smartMeterId.isEmpty()
                && electricityReadings != null
                && !electricityReadings.isEmpty();
    }

    /**
     * Calculates the cost based on the average electricity reading and the time elapsed between readings,
     * using the provided price plan's unit rate.
     *
     * @param electricityReadings The list of electricity readings to calculate the cost from.
     * @param pricePlan The price plan to apply to the cost calculation.
     * @return The calculated cost based on the given electricity readings and price plan.
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
     * Calculates the average electricity reading from a list of electricity readings.
     *
     * @param electricityReadings The list of electricity readings to calculate the average from.
     * @return The average electricity reading as a BigDecimal. If the electricity readings list is empty,
     * a warning message will be logged, and the function will return 0.
     */
    public static BigDecimal calculateAverageReading(List<ElectricityReading> electricityReadings) {
        if (electricityReadings.isEmpty()) {
            log.warn("Electricity readings list is empty, average calculation may result in an error");
        }

        BigDecimal summedReadings = electricityReadings.stream()
                .map(ElectricityReading::reading)
                .reduce(BigDecimal.ZERO, (reading, accumulator) -> reading.add(accumulator));

        return summedReadings.divide(BigDecimal.valueOf(electricityReadings.size()), RoundingMode.HALF_UP);
    }

    /**
     * Calculates the time elapsed between the first and last electricity readings in the list,
     * expressed in hours as a BigDecimal.
     *
     * @param electricityReadings The list of electricity readings to calculate the time elapsed from.
     * @return The time elapsed between the first and last electricity readings in hours, as a BigDecimal.
     * If the electricity readings list does not contain both first and last readings, a warning message
     * will be logged, and the function will return 0.
     */
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
            log.warn("Electricity readings list does not contain both first and last readings, time elapsed calculation may result in an error");

            return BigDecimal.ZERO;
        }
    }
}

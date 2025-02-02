package uk.tw.energy.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import uk.tw.energy.domain.ElectricityReading;

public interface MeterReadingService {

    /**
     * Returns the electricity readings associated with the given smart meter ID.
     *
     * @param smartMeterId the ID of the smart meter
     * @return an Optional containing the list of electricity readings or an empty Optional if no readings found
     */
    Optional<List<ElectricityReading>> getReadings(String smartMeterId);

    /**
     * Stores the given electricity readings for the given smart meter ID.
     * Throws an IllegalArgumentException if the meter readings are invalid.
     *
     * @param smartMeterId the ID of the smart meter
     * @param electricityReadings the list of electricity readings to be stored
     */
    void storeReadings(String smartMeterId, List<ElectricityReading> electricityReadings);

    /**
     * Calculates the daily energy usage for a given smart meter.
     *
     * @param smartMeterId the identifier of the smart meter for which the daily energy usage is to be calculated.
     * @return an Optional containing a map where the keys are dates (as strings) and the values are the total energy
     *         usage (as BigDecimal) for each day. If there are no readings for the given smart meter, an empty Optional is returned.
     */
    Optional<Map<String, BigDecimal>> getDailyEnergyUsage(String smartMeterId);

    Optional<Map<String, Map<String, BigDecimal>>> compareUsage(String smartMeterId);
}

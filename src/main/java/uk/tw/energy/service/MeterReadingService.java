package uk.tw.energy.service;

import java.util.List;
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
     * Calculates the usage cost for a specific smart meter over a given number of days.
     *
     * @param smartMeterId the ID of the smart meter for which the usage cost is to be calculated
     * @param days the number of days for which the usage cost is to be calculated
     * @return an Optional containing the calculated usage cost as a double, or an empty Optional if the price plan for the smart meter ID is not found
     */
    Optional<Double> getUsageCostForRequiredDays(String smartMeterId, String days);
}

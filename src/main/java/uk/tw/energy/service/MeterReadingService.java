package uk.tw.energy.service;

import java.util.List;
import java.util.Optional;
import uk.tw.energy.domain.ElectricityReading;

public interface MeterReadingService {

    Optional<List<ElectricityReading>> getReadings(String smartMeterId);

    void storeReadings(String smartMeterId, List<ElectricityReading> electricityReadings);
}

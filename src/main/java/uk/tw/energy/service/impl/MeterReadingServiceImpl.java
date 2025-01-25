package uk.tw.energy.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.stereotype.Service;
import uk.tw.energy.domain.ElectricityReading;
import uk.tw.energy.service.MeterReadingService;

@Service
public class MeterReadingServiceImpl implements MeterReadingService {

    private final Map<String, List<ElectricityReading>> meterAssociatedReadings;

    public MeterReadingServiceImpl(Map<String, List<ElectricityReading>> meterAssociatedReadings) {
        this.meterAssociatedReadings = meterAssociatedReadings;
    }

    @Override
    public Optional<List<ElectricityReading>> getReadings(String smartMeterId) {
        return Optional.ofNullable(meterAssociatedReadings.get(smartMeterId));
    }

    @Override
    public void storeReadings(String smartMeterId, List<ElectricityReading> electricityReadings) {
        if (!isMeterReadingsValid(smartMeterId, electricityReadings))
            throw new IllegalArgumentException("Invalid meter readings provided");

        meterAssociatedReadings.computeIfAbsent(smartMeterId, k -> new ArrayList<>()).addAll(electricityReadings);
    }

    private boolean isMeterReadingsValid(String smartMeterId, List<ElectricityReading> electricityReadings) {
        return smartMeterId != null
                && !smartMeterId.isEmpty()
                && electricityReadings != null
                && !electricityReadings.isEmpty();
    }
}

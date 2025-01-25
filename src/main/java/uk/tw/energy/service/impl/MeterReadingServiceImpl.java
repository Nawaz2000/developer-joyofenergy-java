package uk.tw.energy.service.impl;

import static uk.tw.energy.util.ElectricityUtil.isMeterReadingsValid;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.stereotype.Service;
import uk.tw.energy.domain.ElectricityReading;
import uk.tw.energy.service.MeterReadingService;

@Service
public class MeterReadingServiceImpl implements MeterReadingService {

    /**
     * A map to store electricity readings associated with smart meters.
     */
    private final Map<String, List<ElectricityReading>> meterAssociatedReadings;

    /**
     * Constructor for MeterReadingServiceImpl.
     *
     * @param meterAssociatedReadings a map to store electricity readings associated with smart meters
     */
    public MeterReadingServiceImpl(Map<String, List<ElectricityReading>> meterAssociatedReadings) {
        this.meterAssociatedReadings = meterAssociatedReadings;
    }

    /**
     * @inheritDoc
     */
    @Override
    public Optional<List<ElectricityReading>> getReadings(String smartMeterId) {
        return Optional.ofNullable(meterAssociatedReadings.get(smartMeterId));
    }

    /**
     * @inheritDoc
     */
    @Override
    public void storeReadings(String smartMeterId, List<ElectricityReading> electricityReadings) {
        if (!isMeterReadingsValid(smartMeterId, electricityReadings))
            throw new IllegalArgumentException("Invalid meter readings provided");

        meterAssociatedReadings
                .computeIfAbsent(smartMeterId, k -> new ArrayList<>())
                .addAll(electricityReadings);
    }
}

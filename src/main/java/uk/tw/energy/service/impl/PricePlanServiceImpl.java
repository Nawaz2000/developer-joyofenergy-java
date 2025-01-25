package uk.tw.energy.service.impl;

import static uk.tw.energy.util.ElectricityUtil.calculateCost;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import uk.tw.energy.domain.ElectricityReading;
import uk.tw.energy.domain.PricePlan;
import uk.tw.energy.service.PricePlanService;

@Service
public class PricePlanServiceImpl implements PricePlanService {

    private final List<PricePlan> pricePlans;
    private final MeterReadingServiceImpl meterReadingServiceImpl;

    /**
     * Constructs a new instance of {@link PricePlanServiceImpl}.
     *
     * @param pricePlans A list of available price plans.
     * @param meterReadingServiceImpl An instance of {@link MeterReadingServiceImpl} to retrieve electricity readings.
     */
    public PricePlanServiceImpl(List<PricePlan> pricePlans, MeterReadingServiceImpl meterReadingServiceImpl) {
        this.pricePlans = pricePlans;
        this.meterReadingServiceImpl = meterReadingServiceImpl;
    }

    /**
     * @inheritDoc
     */
    @Override
    public Optional<Map<String, BigDecimal>> getConsumptionCostOfElectricityReadingsForEachPricePlan(
            String smartMeterId) {
        Optional<List<ElectricityReading>> electricityReadings = meterReadingServiceImpl.getReadings(smartMeterId);

        return electricityReadings.map(readings ->
                pricePlans.stream().collect(Collectors.toMap(PricePlan::getPlanName, t -> calculateCost(readings, t))));
    }
}

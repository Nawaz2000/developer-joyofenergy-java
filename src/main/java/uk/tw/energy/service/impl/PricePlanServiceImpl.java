package uk.tw.energy.service.impl;

import static uk.tw.energy.util.ElectricityUtil.calculateCost;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import uk.tw.energy.domain.ElectricityReading;
import uk.tw.energy.domain.PricePlan;
import uk.tw.energy.service.PricePlanService;

@Service
public class PricePlanServiceImpl implements PricePlanService {

    private static final Logger logger = LoggerFactory.getLogger(PricePlanServiceImpl.class);

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
        logger.info("PricePlanServiceImpl initialized with {} price plans", pricePlans.size());
    }

    /**
     * @inheritDoc
     */
    @Override
    public Optional<Map<String, BigDecimal>> getConsumptionCostOfElectricityReadingsForEachPricePlan(
            String smartMeterId) {
        logger.info("Calculating consumption cost for smart meter ID: {}", smartMeterId);
        Optional<List<ElectricityReading>> electricityReadings = meterReadingServiceImpl.getReadings(smartMeterId);

        return electricityReadings.map(readings -> {
            logger.debug("Found {} electricity readings for smart meter ID: {}", readings.size(), smartMeterId);
            return pricePlans.stream()
                    .collect(Collectors.toMap(PricePlan::getPlanName, t -> calculateCost(readings, t)));
        });
    }
}

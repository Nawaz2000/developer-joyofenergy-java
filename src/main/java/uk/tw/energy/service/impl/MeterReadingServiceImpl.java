package uk.tw.energy.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.tw.energy.domain.ElectricityReading;
import uk.tw.energy.domain.PricePlan;
import uk.tw.energy.service.AccountService;
import uk.tw.energy.service.MeterReadingService;
import uk.tw.energy.util.ElectricityUtil;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static uk.tw.energy.util.ElectricityUtil.isMeterReadingsValid;

@Slf4j
@Service
public class MeterReadingServiceImpl implements MeterReadingService {

    /**
     * A map to store electricity readings associated with smart meters.
     */
    private final Map<String, List<ElectricityReading>> meterAssociatedReadings;

    @Autowired
    private AccountService accountService;

    @Autowired
    private List<PricePlan> pricePlans;

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
        log.debug("Getting readings for smart meter: {}", smartMeterId);
        return Optional.ofNullable(meterAssociatedReadings.get(smartMeterId));
    }

    /**
     * @inheritDoc
     */
    @Override
    public void storeReadings(String smartMeterId, List<ElectricityReading> electricityReadings) {
        log.debug("Storing readings for smart meter: {}", smartMeterId);
        if (!isMeterReadingsValid(smartMeterId, electricityReadings)) {
            log.error("Invalid meter readings provided for smart meter: {}", smartMeterId);
            throw new IllegalArgumentException("Invalid meter readings provided");
        }

        meterAssociatedReadings
                .computeIfAbsent(smartMeterId, k -> new ArrayList<>())
                .addAll(electricityReadings);
    }

    /**
     * Calculates the usage cost for a specific smart meter over a given number of days.
     *
     * @param smartMeterId the ID of the smart meter for which the usage cost is to be calculated
     * @param days the number of days for which the usage cost is to be calculated
     * @return an Optional containing the calculated usage cost as a double, or an empty Optional if the price plan for the smart meter ID is not found
     */
    public Optional<Double> getUsageCostForRequiredDays(String smartMeterId, String days) {
        log.info("Calculating usage cost for smart meter ID: {}", smartMeterId);
        Optional<List<ElectricityReading>> electricityReadings = getReadings(smartMeterId);
        String pricePlanIdForSmartMeterId = accountService.getPricePlanIdForSmartMeterId(smartMeterId);

        Optional<PricePlan> pricePlan = pricePlans.stream()
                .filter(currPlan -> currPlan.getPlanName().equals(pricePlanIdForSmartMeterId))
                .findAny();

        if (pricePlan.isEmpty())
            return Optional.empty();

        List<ElectricityReading> filteredReadings = electricityReadings
                .orElse(List.of())
                .stream()
                .filter(reading -> reading.time().isAfter(Instant.now().minus(Duration.ofDays(Long.parseLong(days)))))
                .collect(Collectors.toList());

        BigDecimal cost = ElectricityUtil.calculateCost(filteredReadings, pricePlan.get());

        log.info("Usage cost calculation: {}", cost);

        return Optional.of(cost.doubleValue());
    }
}

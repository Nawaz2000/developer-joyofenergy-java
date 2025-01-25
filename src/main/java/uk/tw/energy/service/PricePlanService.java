package uk.tw.energy.service;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;

public interface PricePlanService {

    /**
     * Retrieves the consumption cost of electricity readings for each available price plan.
     *
     * @param smartMeterId The unique identifier of the smart meter for which the readings are to be retrieved.
     * @return An {@link Optional} containing a {@link Map} where the keys are the names of the price plans and the values are the
     * calculated costs for the electricity readings associated with the respective price plans. If no readings are found for the
     * given smart meter ID, the {@link Optional} will be empty.
     */
    Optional<Map<String, BigDecimal>> getConsumptionCostOfElectricityReadingsForEachPricePlan(String smartMeterId);
}

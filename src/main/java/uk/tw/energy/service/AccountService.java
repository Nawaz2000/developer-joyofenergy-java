package uk.tw.energy.service;

import java.util.Map;
import org.springframework.stereotype.Service;

/**
 * This class provides services related to account management.
 */
@Service
public class AccountService {

    /**
     * A map that associates smart meter IDs with their corresponding price plan IDs.
     */
    private final Map<String, String> smartMeterToPricePlanAccounts;

    /**
     * Constructs a new instance of AccountService.
     *
     * @param smartMeterToPricePlanAccounts a map associating smart meter IDs with their corresponding price plan IDs
     */
    public AccountService(Map<String, String> smartMeterToPricePlanAccounts) {
        this.smartMeterToPricePlanAccounts = smartMeterToPricePlanAccounts;
    }

    /**
     * Retrieves the price plan ID associated with a given smart meter ID.
     *
     * @param smartMeterId the ID of the smart meter
     * @return the price plan ID associated with the given smart meter ID, or null if no such association exists
     */
    public String getPricePlanIdForSmartMeterId(String smartMeterId) {
        return smartMeterToPricePlanAccounts.get(smartMeterId);
    }
}

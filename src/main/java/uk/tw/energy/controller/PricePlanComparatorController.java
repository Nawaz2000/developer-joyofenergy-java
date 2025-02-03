package uk.tw.energy.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uk.tw.energy.service.AccountService;
import uk.tw.energy.service.impl.PricePlanServiceImpl;

@RestController
@RequestMapping("/price-plans/v1")
@Tag(name = "price-plans/v1")
public class PricePlanComparatorController {

    public static final String PRICE_PLAN_ID_KEY = "pricePlanId";
    public static final String PRICE_PLAN_COMPARISONS_KEY = "pricePlanComparisons";
    private final PricePlanServiceImpl pricePlanServiceImpl;
    private final AccountService accountService;

    public PricePlanComparatorController(PricePlanServiceImpl pricePlanServiceImpl, AccountService accountService) {
        this.pricePlanServiceImpl = pricePlanServiceImpl;
        this.accountService = accountService;
    }

    @Operation(
            summary = "Calculate the cost for each price plan for a given smart meter ID",
            description = "This API calculates the cost for each price plan associated with the given smart meter ID.")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Successfully calculated costs for each price plan",
                        content = {@Content(mediaType = "application/json")}),
                @ApiResponse(
                        responseCode = "404",
                        description = "Smart meter ID not found",
                        content = {@Content(mediaType = "application/json")}),
                @ApiResponse(
                        responseCode = "500",
                        description = "Internal server error",
                        content = {@Content(mediaType = "application/json")})
            })
    @GetMapping("/compare-all/{smartMeterId}")
    public ResponseEntity<Map<String, Object>> calculatedCostForEachPricePlan(@PathVariable String smartMeterId) {
        String pricePlanId = accountService.getPricePlanIdForSmartMeterId(smartMeterId);
        Optional<Map<String, BigDecimal>> consumptionsForPricePlans =
                pricePlanServiceImpl.getConsumptionCostOfElectricityReadingsForEachPricePlan(smartMeterId);

        if (consumptionsForPricePlans.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Map<String, Object> pricePlanComparisons = new HashMap<>();
        pricePlanComparisons.put(PRICE_PLAN_ID_KEY, pricePlanId);
        pricePlanComparisons.put(PRICE_PLAN_COMPARISONS_KEY, consumptionsForPricePlans.get());

        return ResponseEntity.ok(pricePlanComparisons);
    }

    @Operation(
            summary = "Recommend the cheapest price plans for a given smart meter ID",
            description = "This API recommends the cheapest price plans associated with the given smart meter ID. "
                    + "The number of recommended plans can be limited using the 'limit' query parameter.")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Successfully recommended cheapest price plans",
                        content = {@Content(mediaType = "application/json")}),
                @ApiResponse(
                        responseCode = "404",
                        description = "Smart meter ID not found",
                        content = {@Content(mediaType = "application/json")}),
                @ApiResponse(
                        responseCode = "500",
                        description = "Internal server error",
                        content = {@Content(mediaType = "application/json")})
            })
    @GetMapping("/recommend/{smartMeterId}")
    public ResponseEntity<List<Map.Entry<String, BigDecimal>>> recommendCheapestPricePlans(
            @PathVariable String smartMeterId,
            @Parameter(
                            description =
                                    "Limit the number of recommended price plans. If not provided, all available plans will be returned.")
                    @RequestParam(value = "limit", required = false)
                    Integer limit) {
        Optional<Map<String, BigDecimal>> consumptionsForPricePlans =
                pricePlanServiceImpl.getConsumptionCostOfElectricityReadingsForEachPricePlan(smartMeterId);

        if (consumptionsForPricePlans.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        List<Map.Entry<String, BigDecimal>> recommendations =
                new ArrayList<>(consumptionsForPricePlans.get().entrySet());
        recommendations.sort(Comparator.comparing(Map.Entry::getValue));

        if (limit != null && limit < recommendations.size()) {
            recommendations = recommendations.subList(0, limit);
        }

        return ResponseEntity.ok(recommendations);
    }
}

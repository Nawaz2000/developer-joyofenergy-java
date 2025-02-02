package uk.tw.energy;

import static java.util.Collections.emptyList;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import uk.tw.energy.domain.ElectricityReading;
import uk.tw.energy.domain.PricePlan;
import uk.tw.energy.generator.ElectricityReadingsGenerator;

@Slf4j
@Configuration
public class SeedingApplicationDataConfiguration {

    private static final String MOST_EVIL_PRICE_PLAN_ID = "price-plan-0";
    private static final String RENEWABLES_PRICE_PLAN_ID = "price-plan-1";
    private static final String STANDARD_PRICE_PLAN_ID = "price-plan-2";

    /**
     * This method generates a list of predefined price plans for the energy application.
     *
     * @return A list of PricePlan objects, each representing a unique energy price plan.
     */
    @Bean
    public List<PricePlan> pricePlans() {
        log.info("Generating price plans");
        final List<PricePlan> pricePlans = new ArrayList<>();
        pricePlans.add(new PricePlan(MOST_EVIL_PRICE_PLAN_ID, "Dr Evil's Dark Energy", BigDecimal.TEN, emptyList()));
        pricePlans.add(new PricePlan(RENEWABLES_PRICE_PLAN_ID, "The Green Eco", BigDecimal.valueOf(2), emptyList()));
        pricePlans.add(new PricePlan(STANDARD_PRICE_PLAN_ID, "Power for Everyone", BigDecimal.ONE, emptyList()));
        log.info("Generated {} price plans", pricePlans.size());

        return pricePlans;
    }

    /**
     * This method generates a map of electricity readings per smart meter.
     *
     * @return A map where the keys are smart meter IDs and the values are lists of ElectricityReading objects.
     *         Each list contains 20 randomly generated ElectricityReading objects for the corresponding smart meter.
     *
     * @see ElectricityReading
     * @see ElectricityReadingsGenerator
     */
    @Bean
    public Map<String, List<ElectricityReading>> perMeterElectricityReadings() {
        log.info("Generating electricity readings");
        final Map<String, List<ElectricityReading>> readings = new HashMap<>();
        final ElectricityReadingsGenerator electricityReadingsGenerator = new ElectricityReadingsGenerator();
        smartMeterToPricePlanAccounts()
                .keySet()
                .forEach(smartMeterId -> readings.put(smartMeterId, electricityReadingsGenerator.generate(20)));
        return readings;
    }

    /**
     * This method generates a mapping between smart meter IDs and their corresponding price plan IDs.
     *
     * @return A map where the keys are smart meter IDs and the values are the corresponding price plan IDs.
     *         The map contains predefined associations between smart meters and price plans.
     *
     * @see PricePlan
     */
    @Bean
    public Map<String, String> smartMeterToPricePlanAccounts() {
        log.info("Generating smart meter to price plan associations");
        final Map<String, String> smartMeterToPricePlanAccounts = new HashMap<>();
        smartMeterToPricePlanAccounts.put("smart-meter-0", MOST_EVIL_PRICE_PLAN_ID);
        smartMeterToPricePlanAccounts.put("smart-meter-1", RENEWABLES_PRICE_PLAN_ID);
        smartMeterToPricePlanAccounts.put("smart-meter-2", MOST_EVIL_PRICE_PLAN_ID);
        smartMeterToPricePlanAccounts.put("smart-meter-3", STANDARD_PRICE_PLAN_ID);
        smartMeterToPricePlanAccounts.put("smart-meter-4", RENEWABLES_PRICE_PLAN_ID);
        log.info("Generated {} smart meter to price plan associations", smartMeterToPricePlanAccounts.size());

        return smartMeterToPricePlanAccounts;
    }

    /**
     * This method provides a configured ObjectMapper instance for JSON serialization and deserialization.
     * The ObjectMapper instance is configured to not write dates as timestamps.
     *
     * @param builder The Jackson2ObjectMapperBuilder instance used to create the ObjectMapper.
     * @return A configured ObjectMapper instance for JSON serialization and deserialization.
     *
     * @see ObjectMapper
     * @see Jackson2ObjectMapperBuilder
     * @see SerializationFeature#WRITE_DATES_AS_TIMESTAMPS
     */
    @Bean
    @Primary
    public ObjectMapper objectMapper(Jackson2ObjectMapperBuilder builder) {
        log.info("Configuring ObjectMapper");
        ObjectMapper objectMapper = builder.createXmlMapper(false).build();
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        log.info("Configured ObjectMapper");

        return objectMapper;
    }
}

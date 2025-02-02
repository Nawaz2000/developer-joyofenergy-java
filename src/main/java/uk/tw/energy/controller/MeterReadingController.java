package uk.tw.energy.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.tw.energy.domain.ElectricityReading;
import uk.tw.energy.domain.MeterReadings;
import uk.tw.energy.service.MeterReadingService;

@RestController
@RequestMapping("readings/v1")
@Slf4j
@Tag(name = "readings/v1", description = "API for managing meter readings")
public class MeterReadingController {

    private final MeterReadingService meterReadingService;

    public MeterReadingController(MeterReadingService meterReadingService) {
        this.meterReadingService = meterReadingService;
    }

    /**
     * This function is responsible for storing electricity readings for a specific meter.
     *
     * @param meterReadings The object containing the smart meter ID and a list of electricity readings.
     *                     The meterReadings object is expected to be valid and not null.
     *
     * @return A ResponseEntity object with a status code of 201 (CREATED) and a body containing the message
     *         "Meter readings stored successfully". If any error occurs during the storage process,
     *         a ResponseEntity object with a status code of 400 (BAD_REQUEST) or 500 (INTERNAL_SERVER_ERROR)
     *         is returned.
     */
    @Operation(summary = "Store meter readings", description = "Stores electricity readings for a specific meter")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "201",
                        description = "Meter reading added successfully",
                        content = {@Content(mediaType = "application/json")}),
                @ApiResponse(
                        responseCode = "400",
                        description = "Invalid input",
                        content = {@Content(mediaType = "application/json")}),
                @ApiResponse(
                        responseCode = "500",
                        description = "Internal server error",
                        content = {@Content(mediaType = "application/json")})
            })
    @PostMapping("/store")
    public ResponseEntity<String> storeReadings(@RequestBody @Valid MeterReadings meterReadings) {
        log.info("Received meter readings for meter: {}", meterReadings.smartMeterId());
        meterReadingService.storeReadings(meterReadings.smartMeterId(), meterReadings.electricityReadings());

        return ResponseEntity.status(HttpStatus.CREATED).body("Meter readings stored successfully");
    }

    /**
     * This function retrieves electricity readings for a specific meter.
     *
     * @param smartMeterId The unique identifier of the meter for which readings are to be fetched.
     *                     This parameter is expected to be a non-null, non-empty string.
     *
     * @return A ResponseEntity object containing a list of ElectricityReading objects.
     *         If readings are found for the specified meter, the status code will be 200 (OK)
     *         and the list of readings will be present in the response body.
     *         If no readings are found for the specified meter, the status code will be 404 (NOT_FOUND)
     *         and the response body will be empty.
     *         If any error occurs during the retrieval process, the status code will be 500 (INTERNAL_SERVER_ERROR)
     *         and the response body will contain an error message.
     */
    @Operation(summary = "Fetch meter readings", description = "Fetches electricity readings for a specific meter")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Fetches reading for a particular meter id",
                        content = {@Content(mediaType = "application/json")}),
                @ApiResponse(
                        responseCode = "400",
                        description = "Input validation error",
                        content = {@Content(mediaType = "application/json")}),
                @ApiResponse(
                        responseCode = "404",
                        description = "Meter not found",
                        content = {@Content(mediaType = "application/json")}),
                @ApiResponse(
                        responseCode = "500",
                        description = "Internal server error",
                        content = {@Content(mediaType = "application/json")})
            })
    @GetMapping("/read/{smartMeterId}")
    public ResponseEntity<List<ElectricityReading>> readReadings(@PathVariable String smartMeterId) {

        log.info("Fetching readings for meter: {}", smartMeterId);
        Optional<List<ElectricityReading>> readings = meterReadingService.getReadings(smartMeterId);

        return readings.map(ResponseEntity::ok).orElseGet(() -> {
            log.warn("No readings found for meter: {}", smartMeterId);
            return ResponseEntity.notFound().build();
        });
    }

    /**
     * Retrieves daily energy usage readings for a specific meter.
     *
     * @param smartMeterId The unique identifier of the meter for which daily energy usage readings are to be fetched.
     *                     This parameter is expected to be a non-null, non-empty string.
     *
     * @return A ResponseEntity object containing a map of daily energy usage readings.
     *         If readings are found for the specified meter, the status code will be 200 (OK)
     *         and the map of readings will be present in the response body.
     *         If no readings are found for the specified meter, the status code will be 404 (NOT_FOUND)
     *         and the response body will be empty.
     *         If any error occurs during the retrieval process, the status code will be 500 (INTERNAL_SERVER_ERROR)
     *         and the response body will contain an error message.
     */
    @Operation(
            summary = "Get daily energy usage",
            description = "Fetches daily energy usage readings for a specific meter")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Daily energy usage readings retrieved successfully",
                        content = {@Content(mediaType = "application/json")}),
                @ApiResponse(
                        responseCode = "404",
                        description = "No daily energy usage readings found for the specified meter",
                        content = {@Content(mediaType = "application/json")}),
                @ApiResponse(
                        responseCode = "500",
                        description = "Internal server error",
                        content = {@Content(mediaType = "application/json")})
            })
    @GetMapping("/daily-energy-usage/{smartMeterId}")
    public ResponseEntity<Map<String, BigDecimal>> getDailyEnergyUsageForMeter(@PathVariable String smartMeterId) {
        log.info("Fetching daily energy usage readings for meter: {}", smartMeterId);

        Optional<Map<String, BigDecimal>> dailyEnergyUsage = meterReadingService.getDailyEnergyUsage(smartMeterId);

        return dailyEnergyUsage.map(ResponseEntity::ok).orElseGet(() -> {
            log.warn("No daily energy usage readings found for meter: {}", smartMeterId);
            return ResponseEntity.notFound().build();
        });
    }
}

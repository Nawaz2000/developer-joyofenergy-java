package uk.tw.energy.controller;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import uk.tw.energy.service.impl.MeterReadingServiceImpl;

@RestController
@RequestMapping("readings/v1")
@Slf4j
@Tag(name = "readings/v1")
public class MeterReadingController {

    private final MeterReadingServiceImpl meterReadingServiceImpl;

    private final Logger logger = LoggerFactory.getLogger(MeterReadingController.class);

    public MeterReadingController(MeterReadingServiceImpl meterReadingService) {
        this.meterReadingServiceImpl = meterReadingService;
    }

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
    public ResponseEntity<String> storeReadings(@RequestBody MeterReadings meterReadings) {
        logger.info("Received meter readings for meter: {}", meterReadings.smartMeterId());
        meterReadingServiceImpl.storeReadings(meterReadings.smartMeterId(), meterReadings.electricityReadings());

        return ResponseEntity.status(HttpStatus.CREATED).body("Meter readings stored successfully");
    }

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
                        responseCode = "500",
                        description = "Internal server error",
                        content = {@Content(mediaType = "application/json")})
            })
    @GetMapping("/read/{smartMeterId}")
    public ResponseEntity<List<ElectricityReading>> readReadings(@PathVariable String smartMeterId) {

        logger.info("Fetching readings for meter: {}", smartMeterId);
        Optional<List<ElectricityReading>> readings = meterReadingServiceImpl.getReadings(smartMeterId);

        return readings.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}

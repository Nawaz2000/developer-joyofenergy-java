package uk.tw.energy.controller;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
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
@Tag(name = "readings/v1")
public class MeterReadingController {

    private final MeterReadingService meterReadingService;

    public MeterReadingController(MeterReadingService meterReadingService) {
        this.meterReadingService = meterReadingService;
    }

    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Adds a meter reading",
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
    @PostMapping("/store")
    public ResponseEntity storeReadings(@RequestBody MeterReadings meterReadings) {
        if (!isMeterReadingsValid(meterReadings)) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
        meterReadingService.storeReadings(meterReadings.smartMeterId(), meterReadings.electricityReadings());
        return ResponseEntity.ok().build();
    }

    private boolean isMeterReadingsValid(MeterReadings meterReadings) {
        String smartMeterId = meterReadings.smartMeterId();
        List<ElectricityReading> electricityReadings = meterReadings.electricityReadings();
        return smartMeterId != null
                && !smartMeterId.isEmpty()
                && electricityReadings != null
                && !electricityReadings.isEmpty();
    }

    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Feteches reading for a particular meter id",
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
    public ResponseEntity readReadings(@PathVariable String smartMeterId) {
        Optional<List<ElectricityReading>> readings = meterReadingService.getReadings(smartMeterId);
        return readings.isPresent()
                ? ResponseEntity.ok(readings.get())
                : ResponseEntity.notFound().build();
    }
}

package iot.data.platform.position_redis;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api")
public class PositionRedisController {

    private final TripPositionService service;

    public PositionRedisController(TripPositionService service) {
        this.service = service;
    }

    @GetMapping("/position_redis/{tripId}")
    public ResponseEntity<?> getPosition(@PathVariable String tripId,
                                         @RequestParam(value = "freshSeconds", required = false) Long freshSeconds) {
        Optional<TripPositionDto> dto = service.findFreshByTripId(tripId, freshSeconds);
        return dto.<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}

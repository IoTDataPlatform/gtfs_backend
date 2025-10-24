package com.example.gtfs.trips;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/trips")
public class TripsController {

    private final TripsDao dao;

    public TripsController(TripsDao dao) {
        this.dao = dao;
    }

    @GetMapping("/{tripId}/shape")
    public TripShapeResponse getTripShape(@PathVariable String tripId) {
        return dao.getTripShape(tripId);
    }

    @GetMapping("/{tripId}/stops")
    public TripStopsResponse getTripStops(@PathVariable String tripId) {
        return dao.getTripStops(tripId);
    }
}

package com.example.gtfs.trips;

import java.util.List;

public record TripShapeResponse(
        String tripId,
        String routeId,
        String shapeId,
        List<LatLon> points
) {}

package iot.data.platform.trips;

import java.util.List;

public record TripShapeResponse(
        String tripId,
        String routeId,
        String shapeId,
        List<LatLon> points
) {}

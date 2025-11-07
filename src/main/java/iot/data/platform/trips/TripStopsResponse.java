package iot.data.platform.trips;

import java.util.List;

public record TripStopsResponse(
        String tripId,
        String routeId,
        List<TripStopDto> stops
) {}
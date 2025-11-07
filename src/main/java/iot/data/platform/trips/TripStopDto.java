package iot.data.platform.trips;

public record TripStopDto(
        String stopId,
        String stopName,
        double lat,
        double lon,
        int sequence,
        String arrivalTime,
        String departureTime
) {}

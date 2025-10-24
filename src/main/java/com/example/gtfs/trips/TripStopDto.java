package com.example.gtfs.trips;

public record TripStopDto(
        String stopId,
        String stopName,
        double lat,
        double lon,
        int sequence,
        String arrivalTime,
        String departureTime
) {}

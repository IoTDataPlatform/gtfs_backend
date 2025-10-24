package com.example.gtfs.trips;

public record LatLon(
        double lat,
        double lon,
        int sequence
) {}
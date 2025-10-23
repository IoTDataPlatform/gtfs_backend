package com.example.gtfs.stops;

public record StopDto(
        String id,
        String name,
        Double lat,
        Double lon
) {}
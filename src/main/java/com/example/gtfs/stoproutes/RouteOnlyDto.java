package com.example.gtfs.stoproutes;

public record RouteOnlyDto(
        String routeId,
        String shortName,
        String longName,
        Integer routeType
) {}

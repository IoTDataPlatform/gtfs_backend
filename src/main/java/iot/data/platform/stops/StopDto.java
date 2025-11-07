package iot.data.platform.stops;

public record StopDto(
        String id,
        String name,
        Double lat,
        Double lon
) {}
package iot.data.platform.stoproutes;

public record RouteOnlyDto(
        String routeId,
        String shortName,
        String longName,
        Integer routeType
) {}

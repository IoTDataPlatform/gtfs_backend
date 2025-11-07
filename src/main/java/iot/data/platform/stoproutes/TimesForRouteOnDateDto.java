package iot.data.platform.stoproutes;

import java.util.List;

public record TimesForRouteOnDateDto(
        String stopId,
        String routeId,
        String date,
        String shortName,
        String longName,
        Integer routeType,
        List<String> times
) {}

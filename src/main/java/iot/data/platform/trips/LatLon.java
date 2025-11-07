package iot.data.platform.trips;

public record LatLon(
        double lat,
        double lon,
        int sequence
) {}
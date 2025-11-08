package iot.data.platform.routes;

public record TripDto(
        String  tripId,
        String  serviceId,
        String  headsign,
        Integer directionId,
        String  shapeId,
        String  shortName,
        String  blockId
) {}
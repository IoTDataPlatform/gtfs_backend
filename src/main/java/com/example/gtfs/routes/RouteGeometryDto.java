package com.example.gtfs.routes;

import com.example.gtfs.stops.StopDto;
import java.util.List;

public record RouteGeometryDto(
        String routeId,
        List<StopDto> stops,
        List<ShapeDto> shapes
) {}

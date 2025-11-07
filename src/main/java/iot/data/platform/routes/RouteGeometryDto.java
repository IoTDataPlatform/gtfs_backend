package iot.data.platform.routes;

import iot.data.platform.stops.StopDto;
import java.util.List;

public record RouteGeometryDto(
        String routeId,
        List<StopDto> stops,
        List<ShapeDto> shapes
) {}

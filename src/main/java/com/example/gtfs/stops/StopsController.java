package com.example.gtfs.stops;

import com.example.gtfs.util.GeoUtil;
import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/stops")
@Validated
public class StopsController {

    private final StopsDao dao;

    public StopsController(StopsDao dao) {
        this.dao = dao;
    }

    @GetMapping("/in-rect")
    public List<StopDto> inRect(
            @RequestParam @NotNull Double topLeftLat,
            @RequestParam @NotNull Double topLeftLon,
            @RequestParam @NotNull Double bottomRightLat,
            @RequestParam @NotNull Double bottomRightLon
    ) {
        double tlLat = GeoUtil.clampLat(topLeftLat);
        double tlLon = GeoUtil.clampLon(topLeftLon);
        double brLat = GeoUtil.clampLat(bottomRightLat);
        double brLon = GeoUtil.clampLon(bottomRightLon);

        double north = Math.max(tlLat, brLat);
        double south = Math.min(tlLat, brLat);
        double west  = tlLon;
        double east  = brLon;

        return dao.findInRect(north, south, west, east);
    }
}
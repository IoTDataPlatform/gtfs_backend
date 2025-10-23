package com.example.gtfs.util;



public final class GeoUtil {
    private GeoUtil() {}


    public static double clampLat(double v) {
        return Math.max(-90.0, Math.min(90.0, v));
    }
    public static double clampLon(double v) {
        return Math.max(-180.0, Math.min(180.0, v));
    }
}

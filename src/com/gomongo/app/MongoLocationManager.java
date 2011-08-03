package com.gomongo.app;

public interface MongoLocationManager {
    void centerMapAndLoadMarkers( double latitude, double longitude );
    void resetLocationsState();
}

package com.example.thetrempiada.driverActivities;

import java.io.Serializable;

public class LanLat implements Serializable {
    private Double latitude;
    private Double longitude;

    public LanLat(Double latitude, Double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public LanLat(){

    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }
}

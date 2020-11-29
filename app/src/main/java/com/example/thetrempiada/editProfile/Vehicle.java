package com.example.thetrempiada.editProfile;

import java.io.Serializable;

public class Vehicle implements Serializable {
    private int vehicleId;
    private String name;

    public Vehicle(int vehicleId, String name) {
        this.vehicleId = vehicleId;
        this.name = name;
    }

    public Vehicle(){}

    public int getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(int vehicleId) {
        this.vehicleId = vehicleId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String toString(){
        return this.vehicleId + ":" + this.name;
    }
}

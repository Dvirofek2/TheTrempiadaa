package com.example.thetrempiada.users;

import com.example.thetrempiada.UserType;
import com.example.thetrempiada.editProfile.Vehicle;

import java.util.ArrayList;
import java.util.Arrays;

public class DriverUser extends User{
    protected ArrayList<Vehicle> vehicleIds;

    public DriverUser(String firstName, String lastName, String id, ArrayList<Vehicle> vehicleIds, long phone) {
        this.firstName = firstName;
        this.type = UserType.DRIVER;
        this.lastName = lastName;
        this.id = id;
        this.phone = phone;
        this.vehicleIds = vehicleIds;
    }

    public DriverUser(){
        this.type = UserType.DRIVER;
        this.vehicleIds = new ArrayList<>();
    }


    public ArrayList<Vehicle> getVehicleIds() {
        return vehicleIds;
    }

    public void setVehicleIds(ArrayList<Vehicle> vehicleIds) {
        this.vehicleIds = vehicleIds;
    }

    @Override
    public String toString(){
        String s = super.toString();
        s+="\nVehicle id: "+ Arrays.toString(vehicleIds.toArray());
        return s;
    }
}

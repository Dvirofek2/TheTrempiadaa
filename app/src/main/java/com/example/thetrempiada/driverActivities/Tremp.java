package com.example.thetrempiada.driverActivities;

import com.example.thetrempiada.FirebaseDB;
import com.example.thetrempiada.SimpleCallback;
import com.example.thetrempiada.editProfile.Vehicle;
import com.example.thetrempiada.users.User;
import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;

public class Tremp implements Serializable {
    protected DtaeAndTime dateTime;
    protected String driverId;
    protected String trempId;
    protected int numOfPeople;
    protected Vehicle vehicle;
    protected LanLat src,dst;
    protected String driver_lname;
    protected String driver_fname;
    protected String driver_phone;

    public Tremp(DtaeAndTime dateTime, String driverId, String trempId, int numOfPeople, Vehicle vehicle, LanLat src, LanLat dst, String driver_lname, String driver_fname, String driver_phone) {
        this.dateTime = dateTime;
        this.driverId = driverId;
        this.trempId = trempId;
        this.numOfPeople = numOfPeople;
        this.vehicle = vehicle;
        this.src = src;
        this.dst = dst;
        this.driver_lname = driver_lname;
        this.driver_fname = driver_fname;
        this.driver_phone = driver_phone;
    }

    public Tremp(){}

    public Tremp(Tremp t) {
        this.dateTime = t.dateTime;
        this.driverId = t.driverId;
        this.trempId = t.trempId;
        this.numOfPeople = t.numOfPeople;
        this.vehicle = t.vehicle;
        this.src = t.src;
        this.dst = t.dst;
        this.driver_lname = t.driver_lname;
        this.driver_fname = t.driver_fname;
        this.driver_phone = t.driver_phone;

    }

    public String getDriver_lname() {
        return driver_lname;
    }

    public void setDriver_lname(String driver_lname) {
        this.driver_lname = driver_lname;
    }

    public String getDriver_fname() {
        return driver_fname;
    }

    public void setDriver_fname(String driver_fname) {
        this.driver_fname = driver_fname;
    }

    public String getDriver_phone() {
        return driver_phone;
    }

    public void setDriver_phone(String driver_phone) {
        this.driver_phone = driver_phone;
    }

    public DtaeAndTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(DtaeAndTime dateTime) {
        this.dateTime = dateTime;
    }

    public String getDriverId() {
        return driverId;
    }

    public void setDriverId(String driverId) {
        this.driverId = driverId;
    }

    public String getTrempId() {
        return trempId;
    }

    public void setTrempId(String trempId) {
        this.trempId = trempId;
    }

    public int getNumOfPeople() {
        return numOfPeople;
    }

    public void setNumOfPeople(int numOfPeople) {
        this.numOfPeople = numOfPeople;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public void setVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
    }

    public LanLat getSrc() {
        return src;
    }

    public void setSrc(LanLat src) {
        this.src =  src;
    }

    public LanLat getDst() {
        return dst;
    }

    public void setDst(LanLat dst) {
        this.dst = dst;
    }

    @Override
    public String toString(){
        String s = this.dateTime.toString();
        s+="\n";
        s+="Driver name: "+driver_fname+" "+driver_lname+"\n" +
                "Driver phone: "+driver_phone;
        s+="\n"+"Vehicle: "+vehicle.toString();
        s+="\n"+"Number of seats: "+numOfPeople;
        return s;
    }


}

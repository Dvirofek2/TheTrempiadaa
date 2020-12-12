package com.example.thetrempiada.trempistActivities;

import com.example.thetrempiada.driverActivities.Tremp;

import java.io.Serializable;
import java.util.ArrayList;

public class TrempistTremp implements Serializable {
    protected String uid,firstName,lastName;
    protected ArrayList<Tremp> tremps;
    protected long phone;

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public long getPhone() {
        return phone;
    }

    public void setPhone(long phone) {
        this.phone = phone;
    }

    public TrempistTremp(String uid, String firstName, String lastName, long phone, ArrayList<Tremp> tremps) {
        this.uid = uid;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
        this.tremps = tremps;
    }

    public TrempistTremp() {

        this.tremps = new ArrayList<>();
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public void setTremps(ArrayList<Tremp> tremps) {
        this.tremps = tremps;
    }

    public String getUid() {
        return uid;
    }

    public ArrayList<Tremp> getTremps() {
        return tremps;
    }
}
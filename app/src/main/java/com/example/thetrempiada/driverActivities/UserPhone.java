package com.example.thetrempiada.driverActivities;

import java.io.Serializable;

public class UserPhone implements Serializable {
    String firstName;
    String lastName;
    Long phone;

    public UserPhone(String firstName, String lastName, Long phone) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
    }

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

    public Long getPhone() {
        return phone;
    }

    public void setPhone(Long phone) {
        this.phone = phone;
    }



    public String toString(){
        String s = "";
        s+="Name: "+this.firstName+" "+this.lastName+"\n";
        s+="Phone: "+this.phone;
        return s;
    }
}

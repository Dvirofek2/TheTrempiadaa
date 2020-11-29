package com.example.thetrempiada.users;

import com.example.thetrempiada.UserType;

public class TrempistUser extends User {

    public TrempistUser(String firstName, String lastName, String id,long phone) {
        this.firstName = firstName;
        this.type = UserType.TREMPIST;
        this.lastName = lastName;
        this.id = id;
        this.phone = phone;
    }

    public TrempistUser(){this.type = UserType.TREMPIST;}

}

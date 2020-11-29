package com.example.thetrempiada.users;

import com.example.thetrempiada.UserType;

public class UserFactory {
    public static User getNewUser(UserType type){
        switch (type){
            case DRIVER: {
                return new DriverUser();
            }

            case TREMPIST:{
                return new TrempistUser();
            }

            default:{return null;}
        }
    }
}

package com.example.thetrempiada;

import android.content.Intent;

public enum UserType {
    DRIVER("0"), TREMPIST("1");
    private String value;

    UserType(String value){this.value = value;}

    public String getValue() {
        return this.value;
    }
    public static String getName(String value){
        if(value == "DRIVER")
            return "Driver";
        return "Trempist";
    }

}

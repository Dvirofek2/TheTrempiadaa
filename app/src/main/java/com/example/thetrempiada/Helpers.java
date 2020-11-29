package com.example.thetrempiada;

import android.content.Intent;
import android.os.Parcelable;
import android.util.Pair;

public class Helpers {
    public static void sendParametersToActivity(Intent i, Pair<String, Parcelable>... args){
        for (Pair<String, Parcelable> arg : args) {
            i.putExtra(arg.first, arg.second);
        }
    }
}

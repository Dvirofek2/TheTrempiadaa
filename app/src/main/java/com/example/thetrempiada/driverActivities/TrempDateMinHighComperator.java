package com.example.thetrempiada.driverActivities;

import java.util.Comparator;

public class TrempDateMinHighComperator implements Comparator<Tremp> {


    @Override
    public int compare(Tremp o1, Tremp o2) {
        return o1.dateTime.compareTo(o2.dateTime);
    }
}

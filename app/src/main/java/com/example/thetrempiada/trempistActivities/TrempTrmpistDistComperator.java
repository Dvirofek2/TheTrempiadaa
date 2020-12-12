package com.example.thetrempiada.trempistActivities;

import com.example.thetrempiada.FirebaseDB;
import com.example.thetrempiada.driverActivities.LanLat;
import com.example.thetrempiada.driverActivities.Tremp;

import java.util.Comparator;

public class TrempTrmpistDistComperator implements Comparator<Tremp> {
    static LanLat source;
    FirebaseDB db;

    TrempTrmpistDistComperator(FirebaseDB f,LanLat s){
        source = s;
        db = f;
    }

    @Override
    public int compare(Tremp t1, Tremp t2) {

        if(db.distance(t1.getSrc(),source) < db.distance(t2.getSrc(),source) ){
            return 1;
        }else {
            return -1;
        }
    }

}

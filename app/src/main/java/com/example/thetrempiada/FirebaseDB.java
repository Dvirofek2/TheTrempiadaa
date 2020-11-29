package com.example.thetrempiada;

import android.app.Activity;
import android.icu.lang.UScript;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.thetrempiada.driverActivities.DriverTremp;
import com.example.thetrempiada.driverActivities.LanLat;
import com.example.thetrempiada.driverActivities.Tremp;
import com.example.thetrempiada.editProfile.Vehicle;
import com.example.thetrempiada.trempistActivities.SearchQuery;
import com.example.thetrempiada.users.DriverUser;
import com.example.thetrempiada.users.TrempistUser;
import com.example.thetrempiada.users.User;
import com.google.android.gms.tasks.SuccessContinuation;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;

public class FirebaseDB {
    private static FirebaseDB instance;
    public FirebaseDatabase database;
    private DatabaseReference ref;

    private FirebaseDB(){
        database = FirebaseDatabase.getInstance();
        ref = database.getReference();
    }


    public static FirebaseDB getInstance(){
        if(instance == null){
            synchronized (GoogleLogin.class){
                if(instance == null)
                    instance = new FirebaseDB();
            }
        }
        return instance;
    }

    public void writeUser(User user,SimpleCallback<Boolean> call){
        this.checkIfUserExists(user.getId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!snapshot.exists())
                {
                    ref.child("users").child(user.getId()).setValue(user);
                    if(user.getType()==UserType.DRIVER)
                        writeInUserVehicles(user.getId());
                    call.callback(true,null);
                }
                else
                {
                    if(getUserFromDb(snapshot).getType() !=user.getType()) {
                        call.callback(false,new Exception("User already exists with different type"));
                    }
                    else
                        call.callback(true,null);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                call.callback(false,new Exception(error.getMessage()));
            }
        });

    }

    public Query checkIfUserExists(String id){
        return ref.child("users").child(id);
    }

    private User getUserFromDb(DataSnapshot snapshot){
        if(snapshot.child("type").getValue().toString() == UserType.TREMPIST.toString())
            return snapshot.getValue(TrempistUser.class);
        return snapshot.getValue(DriverUser.class);
    }

    public void getUserById(String id,SimpleCallback<User> call,UserType type){

        ref.child("users").orderByChild("id").equalTo(id).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (type == UserType.DRIVER) {
                    call.callback(dataSnapshot.getValue(DriverUser.class), null);
                } else {
                    call.callback(dataSnapshot.getValue(TrempistUser.class), null);
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                call.callback(null,new Exception(error.getMessage()));
            }

        });

    }

    public void writeInUserVehicles(String id1){
        Object myobj = new Object() {
            public String id = id1;
            public ArrayList<Integer> vehiclesId = new ArrayList<>();
        };
        ref.child("users-vehicles").child(id1).setValue(myobj);
    }

    public Task<Void> updateUser(User user){
        if(user.getType() == UserType.DRIVER){
            DriverUser driver = (DriverUser)user;
            ref.child("users").child(user.getId()).setValue(driver);
            Object myobj = new Object() {
                public String id = user.getId();
                public ArrayList<Vehicle> vehiclesId = driver.getVehicleIds();
            };
            return ref.child("users-vehicles").child(user.getId()).setValue(myobj);
        }
        else {
            return ref.child("users").child(user.getId()).setValue(user);
        }
    }

    public Task<Void> writeNewTremp(Tremp t){
        String id = ref.child("tremps").push().getKey();
        t.setTrempId(id);
        return ref.child("tremps").child(id).setValue(t);
    }

    public void writeUserTremp(Tremp t, String uid, SimpleCallback<Boolean> callback){
        ref.child("drivers-tremps").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    DriverTremp driverT = snapshot.getValue(DriverTremp.class);
                    driverT.getTremps().add(t);
                    Task<Void> task = ref.child("drivers-tremps").child(uid).setValue(driverT);
                    task.addOnFailureListener((x -> callback.callback(false, new Exception(x.getMessage()))));
                    task.addOnSuccessListener((x -> callback.callback(true, null)));
                }
                else
                {
                    DriverTremp driverT = new DriverTremp(uid,new ArrayList<>());
                    driverT.getTremps().add(t);
                    Task<Void> task= ref.child("drivers-tremps").child(uid).setValue(driverT);
                    task.addOnFailureListener((x->callback.callback(false,new Exception(x.getMessage()))));
                    task.addOnSuccessListener((x->callback.callback(true,null)));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.callback(null,new Exception(error.getMessage()));
            }

        });
    }

    public void solveSearchQuery(SearchQuery query,SimpleCallback<ArrayList<Tremp>> callback){

        ref.child("tremps")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        ArrayList<Tremp> tremps = new ArrayList<>();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            Tremp tremp = snapshot.getValue(Tremp.class);
                            if(passQuery(query,tremp))
                                tremps.add(tremp);
                        }
                        callback.callback(tremps,null);
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        callback.callback(null,new Exception(databaseError.getMessage()));
                    }
                });
    }

    private boolean passQuery(SearchQuery query, Tremp tremp) {
        //same date
        if(query.getDateTime().getDay() == tremp.getDateTime().getDay() && query.getDateTime().getMonth()== tremp.getDateTime().getMonth() && query.getDateTime().getYear() == tremp.getDateTime().getYear()){
            if(query.getDateTime().getHour() < tremp.getDateTime().getHour() ||(query.getDateTime().getHour() == tremp.getDateTime().getHour() && query.getDateTime().getMin() <= tremp.getDateTime().getMin()) ){
                if(Math.abs(distance(tremp.getSrc(),query.getSrc()))<=query.getRangeSrc() && Math.abs(distance(tremp.getDst(),query.getDst()))<=query.getRangeDst()){
                    return true;
                }
            }
        }

        return false;
    }


    private double distance(LanLat p1, LanLat p2) {
        double lat1 = p1.getLatitude();
        double lat2 = p2.getLatitude();
        double lon1 = p1.getLongitude();
        double lon2 = p2.getLongitude();
        double theta = lon1 - lon2;
        char unit = 'K';
        double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        if (unit == 'K') {
            dist = dist * 1.609344;
        } else if (unit == 'N') {
            dist = dist * 0.8684;
        }
        return (dist*1000);
    }

    /*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
    /*::  This function converts decimal degrees to radians             :*/
    /*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    /*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
    /*::  This function converts radians to decimal degrees             :*/
    /*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }

}

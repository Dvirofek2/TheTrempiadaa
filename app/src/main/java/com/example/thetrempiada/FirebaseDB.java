package com.example.thetrempiada;

import android.app.Activity;
import android.icu.lang.UScript;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.thetrempiada.driverActivities.DriverTremp;
import com.example.thetrempiada.driverActivities.LanLat;
import com.example.thetrempiada.driverActivities.MyTrempObj;
import com.example.thetrempiada.driverActivities.Tremp;
import com.example.thetrempiada.driverActivities.UserPhone;
import com.example.thetrempiada.editProfile.Vehicle;
import com.example.thetrempiada.trempistActivities.SearchQuery;
import com.example.thetrempiada.trempistActivities.TrempistTremp;
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
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.core.utilities.Tree;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class FirebaseDB {
    private static FirebaseDB instance;
    public FirebaseDatabase database;
    private DatabaseReference ref;

    private FirebaseDB() {
        database = FirebaseDatabase.getInstance();
        ref = database.getReference();
    }


    public static FirebaseDB getInstance() {
        if (instance == null) {
            synchronized (GoogleLogin.class) {
                if (instance == null)
                    instance = new FirebaseDB();
            }
        }
        return instance;
    }

    public void writeUser(User user, SimpleCallback<Boolean> call) {
        this.checkIfUserExists(user.getId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    ref.child("users").child(user.getId()).setValue(user);
                    if (user.getType() == UserType.DRIVER)
                        writeInUserVehicles(user.getId());
                    call.callback(true, null);
                } else {
                    if (getUserFromDb(snapshot).getType() != user.getType()) {
                        call.callback(false, new Exception("User already exists with different type"));
                    } else
                        call.callback(true, null);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                call.callback(false, new Exception(error.getMessage()));
            }
        });

    }

    public Query checkIfUserExists(String id) {
        return ref.child("users").child(id);
    }

    private User getUserFromDb(DataSnapshot snapshot) {
        if (snapshot.child("type").getValue().toString() == UserType.TREMPIST.toString())
            return snapshot.getValue(TrempistUser.class);
        return snapshot.getValue(DriverUser.class);
    }

    public void getUserById(String id, SimpleCallback<User> call, UserType type) {

        ref.child("users").orderByChild("id").equalTo(id).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (type == UserType.DRIVER) {
                    call.callback(dataSnapshot.getValue(DriverUser.class), null);
                } else if (type == UserType.TREMPIST) {
                    call.callback(dataSnapshot.getValue(TrempistUser.class), null);
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
                call.callback(null, new Exception(error.getMessage()));
            }

        });

    }

    public void writeInUserVehicles(String id1) {
        Object myobj = new Object() {
            public String id = id1;
            public ArrayList<Integer> vehiclesId = new ArrayList<>();
        };
        ref.child("users-vehicles").child(id1).setValue(myobj);
    }

    public Task<Void> updateUser(User user) {
        if (user.getType() == UserType.DRIVER) {
            DriverUser driver = (DriverUser) user;
            ref.child("users").child(user.getId()).setValue(driver);
            Object myobj = new Object() {
                public String id = user.getId();
                public ArrayList<Vehicle> vehiclesId = driver.getVehicleIds();
            };

            ref.child("tremps").orderByChild("trempId").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for(DataSnapshot cur : snapshot.getChildren()){
                        Tremp current = cur.getValue(Tremp.class);
                        if(current.getDriverId().equals(user.getId())){
                            current.setDriver_fname(user.getFirstName());
                            current.setDriver_lname(user.getLastName());
                            current.setDriver_phone(String.valueOf(user.getPhone()));
                            ref.child("tremps").child(current.getTrempId()).setValue(current);
                        }
                    }
                }



                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            getDriverTremp(user.getId(), new SimpleCallback<DriverTremp>() {
                @Override
                public void callback(DriverTremp data, Exception error) {

                    ArrayList<Tremp> myTremps = new ArrayList<>();
                    for(Tremp x:data.getTremps()){
                        x.setDriver_fname(user.getFirstName());
                        x.setDriver_phone(String.valueOf(user.getPhone()));
                        x.setDriver_lname(user.getLastName());
                        myTremps.add(x);
                        ref.child("trempist-tremp").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for(DataSnapshot ds: snapshot.getChildren()){
                                    TrempistTremp tp = ds.getValue(TrempistTremp.class);
                                    ArrayList<Tremp> updatedTremps = new ArrayList<>();
                                    for(Tremp t: tp.getTremps()){
                                        if(t.getDriverId().equals(user.getId())){
                                            t.setDriver_lname(user.getLastName());
                                            t.setDriver_fname(user.getLastName());
                                            t.setDriver_phone(String.valueOf(user.getPhone()));
                                        }
                                        updatedTremps.add(t);
                                    }
                                    tp.setTremps(updatedTremps);
                                    try {
                                        ref.child("trempist-tremp").child(tp.getUid()).setValue(tp);
                                    }
                                    catch (NullPointerException e){

                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                    data.setTremps(myTremps);
                    ref.child("drivers-tremps").child(user.getId()).setValue(data);
                }
            });


            return ref.child("users-vehicles").child(user.getId()).setValue(myobj);
        } else {
            ref.child("trempist-tremp").orderByChild("uid").equalTo(user.getId()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists()){
                        TrempistTremp tp = snapshot.getValue(TrempistTremp.class);
                        tp.setFirstName(user.getFirstName());
                        tp.setLastName(user.getLastName());
                        tp.setPhone(user.getPhone());
                        ref.child("trempist-tremp").child(user.getId()).setValue(tp);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            return ref.child("users").child(user.getId()).setValue(user);
        }
    }

    public Task<Void> writeNewTremp(Tremp t) {
        String id = ref.child("tremps").push().getKey();
        t.setTrempId(id);
        return ref.child("tremps").child(id).setValue(t);
    }

    public void updateTremp(Tremp t) {
        ref.child("tremps").child(t.getTrempId()).setValue(t);

        getDriverTremp(t.getDriverId(), new SimpleCallback<DriverTremp>() {
            @Override
            public void callback(DriverTremp data, Exception error) {

                ArrayList<Tremp> updatedT = new ArrayList<>();
                boolean update = false;

                for (Tremp tremp : data.getTremps()) {

                    if (tremp.getTrempId().equals(t.getTrempId())) {
                        updatedT.add(t);

                        update = true;
                    } else
                        updatedT.add(tremp);
                }
                data.setTremps(updatedT);
                if (update) {

                    ref = database.getReference();
                    ref.child("drivers-tremps").child(data.getId()).setValue(data);
                }
                ref = database.getReference();
                ref.child("trempist-tremp")
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                    TrempistTremp trempistT = snapshot.getValue(TrempistTremp.class);
                                    boolean update = false;
                                    ArrayList<Tremp> updatedT = new ArrayList<>();
                                    for (Tremp cur : trempistT.getTremps()) {

                                        if (cur.getTrempId().equals(t.getTrempId())) {

                                            updatedT.add(t);
                                            update = true;
                                        } else
                                            updatedT.add(cur);
                                    }
                                    trempistT.setTremps(updatedT);
                                    if (update) {

                                        ref = database.getReference();
                                        ref.child("trempist-tremp").child(trempistT.getUid()).setValue(trempistT);

                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }


                        });
            }
        });

    }

    public void writeUserTremp(Tremp t, String uid, SimpleCallback<Boolean> callback) {
        ref.child("drivers-tremps").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    DriverTremp driverT = snapshot.getValue(DriverTremp.class);
                    driverT.getTremps().add(t);
                    Task<Void> task = ref.child("drivers-tremps").child(uid).setValue(driverT);
                    task.addOnFailureListener((x -> callback.callback(false, new Exception(x.getMessage()))));
                    task.addOnSuccessListener((x -> callback.callback(true, null)));
                } else {
                    DriverTremp driverT = new DriverTremp(uid, new ArrayList<>());
                    driverT.getTremps().add(t);
                    Task<Void> task = ref.child("drivers-tremps").child(uid).setValue(driverT);
                    task.addOnFailureListener((x -> callback.callback(false, new Exception(x.getMessage()))));
                    task.addOnSuccessListener((x -> callback.callback(true, null)));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.callback(null, new Exception(error.getMessage()));
            }

        });
    }

    public void joinTremp(Tremp t, TrempistUser trempist, SimpleCallback<Boolean> callback) {

        // int numPlace = Integer.parseInt(ref.child("tremps").child(t.getTrempId()).child("numOfPeople").push().getKey());
        //   ref.child("tremps").child(t.getTrempId()).child("numOfPeople").setValue(numPlace-1);//update the place quantity
        ref.child("trempist-tremp").child(trempist.getId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    TrempistTremp trempistT = snapshot.getValue(TrempistTremp.class);
                    trempistT.getTremps().add(t);
                    Task<Void> task = ref.child("trempist-tremp").child(trempist.getId()).setValue(trempistT);
                    task.addOnFailureListener((x -> callback.callback(false, new Exception(x.getMessage()))));
                    task.addOnSuccessListener((x -> callback.callback(true, null)));
                } else {
                    TrempistTremp trempistT = new TrempistTremp(trempist.getId(), trempist.getFirstName(), trempist.getLastName(), trempist.getPhone(), new ArrayList<>());
                    trempistT.getTremps().add(t);
                    Task<Void> task = ref.child("trempist-tremp").child(trempist.getId()).setValue(trempistT);
                    task.addOnFailureListener((x -> callback.callback(false, new Exception(x.getMessage()))));
                    task.addOnSuccessListener((x -> callback.callback(true, null)));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.callback(null, new Exception(error.getMessage()));
            }
        });
    }


    public void solveSearchQuery(SearchQuery query, SimpleCallback<ArrayList<Tremp>> callback) {

        ref.child("tremps")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        ArrayList<Tremp> tremps = new ArrayList<>();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                            Tremp tremp = snapshot.getValue(Tremp.class);

                            if (passQuery(query, tremp)) {
                                tremps.add(tremp);

                            }
                        }
                        callback.callback(tremps, null);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        callback.callback(null, new Exception(databaseError.getMessage()));
                    }
                });
    }

    private boolean passQuery(SearchQuery query, Tremp tremp) {
        //same date
        if (query.getDateTime().getDay() == tremp.getDateTime().getDay() && query.getDateTime().getMonth() == tremp.getDateTime().getMonth() && query.getDateTime().getYear() == tremp.getDateTime().getYear()) {
            if (query.getDateTime().getHour() < tremp.getDateTime().getHour() || (query.getDateTime().getHour() == tremp.getDateTime().getHour() && query.getDateTime().getMin() <= tremp.getDateTime().getMin())) {
                if (Math.abs(distance(tremp.getSrc(), query.getSrc())) <= query.getRangeSrc() && Math.abs(distance(tremp.getDst(), query.getDst())) <= query.getRangeDst()) {
                    return true;
                }
            }
        }

        return false;
    }


    public double distance(LanLat p1, LanLat p2) {
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
        return (dist * 1000);
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

    public void getTrempistTremps(String uid, SimpleCallback<TrempistTremp> callback){
        ref.child("trempist-tremp").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                TrempistTremp tp = snapshot.getValue(TrempistTremp.class);

                callback.callback(snapshot.getValue(TrempistTremp.class),null);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.callback(null,new Exception(error.getMessage()));
            }
        });
    }


    public void unregisterTremp(String trempistId,Tremp tremp){
        ref.child("trempist-tremp").child(trempistId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<Tremp> updatedLst = new ArrayList<>();
                TrempistTremp tp = snapshot.getValue(TrempistTremp.class);
                for(Tremp x:tp.getTremps()){
                    if(!(x.getTrempId().equals(tremp.getTrempId())))
                        updatedLst.add(x);
                }
                tp.setTremps(updatedLst);
                ref.child("trempist-tremp").child(trempistId).setValue(tp);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void getDriverTremp(String uid, SimpleCallback<DriverTremp> callback) {
        ref.child("drivers-tremps").orderByChild("id").equalTo(uid).limitToFirst(1).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                DriverTremp dt = dataSnapshot.getValue(DriverTremp.class);
                callback.callback(dt, null);
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
                callback.callback(null, new Exception(error.getMessage()));
            }

        });

    }

    public void getFreeSpacesOfTremps(ArrayList<MyTrempObj> p, SimpleCallback<Boolean> callback) {
        Map<String, Integer> trempMap = new HashMap<String, Integer>();
        ref = this.database.getReference();
        ref.child("trempist-tremp")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        int mone = 0;
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            TrempistTremp trempistT = snapshot.getValue(TrempistTremp.class);
                            for (Tremp t : trempistT.getTremps()) {
                                if (trempMap.get(t.getTrempId()) == null)
                                    trempMap.put(t.getTrempId(), new Integer(0));
                                trempMap.put(t.getTrempId(), trempMap.get(t.getTrempId()) + 1);

                            }
                        }
                        for (MyTrempObj cur : p) {
                            if (trempMap.get(cur.getTrempId()) != null)
                                cur.freePlaces = cur.getNumOfPeople() - trempMap.get(cur.getTrempId());
                        }
                        callback.callback(true, null);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        callback.callback(null, new Exception(databaseError.getMessage()));
                    }
                });


    }

    public void delTremp(Tremp t) {
        ref.child("tremps").child(t.getTrempId()).removeValue();

        getDriverTremp(t.getDriverId(), new SimpleCallback<DriverTremp>() {
            @Override
            public void callback(DriverTremp data, Exception error) {

                ArrayList<Tremp> updatedT = new ArrayList<>();
                boolean update = false;

                for (Tremp tremp : data.getTremps()) {

                    if (tremp.getTrempId().equals(t.getTrempId())) {
                        //updatedT.add(t);

                        update = true;
                    } else
                        updatedT.add(tremp);
                }
                data.setTremps(updatedT);
                if (update) {

                    ref = database.getReference();
                    ref.child("drivers-tremps").child(data.getId()).setValue(data);
                }
                ref = database.getReference();
                ref.child("trempist-tremp")
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                    TrempistTremp trempistT = snapshot.getValue(TrempistTremp.class);
                                    boolean update = false;
                                    ArrayList<Tremp> updatedT = new ArrayList<>();
                                    for (Tremp cur : trempistT.getTremps()) {

                                        if (cur.getTrempId().equals(t.getTrempId())) {

                                            //updatedT.add(t);
                                            update = true;
                                        } else
                                            updatedT.add(cur);
                                    }
                                    trempistT.setTremps(updatedT);
                                    if (update) {

                                        ref = database.getReference();
                                        ref.child("trempist-tremp").child(trempistT.getUid()).setValue(trempistT);

                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }


                        });
            }
        });

    }

    public void getRegisteredTrempists(Tremp t, SimpleCallback<ArrayList<UserPhone>> callback) {
        ArrayList<UserPhone> userPhone = new ArrayList<>();
        ref.child("trempist-tremp")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            TrempistTremp trempistT = snapshot.getValue(TrempistTremp.class);
                            boolean update = false;
                            ArrayList<Tremp> updatedT = new ArrayList<>();
                            for (Tremp cur : trempistT.getTremps()) {
                                if (cur.getTrempId().equals(t.getTrempId()))
                                    userPhone.add(new UserPhone(trempistT.getFirstName(), trempistT.getLastName(), trempistT.getPhone()));

                            }
                        }
                        callback.callback(userPhone,null);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        callback.callback(null,new Exception(error.getMessage()));
                    }


                });
    }

    public void myTrempsTrempist(String uid,SimpleCallback<ArrayList<Tremp>> callback){
        ref.child("trempist-tremp").orderByChild("uid").equalTo(uid).limitToFirst(1).addChildEventListener(new ChildEventListener(){

            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                TrempistTremp tt = snapshot.getValue(TrempistTremp.class);
                callback.callback(tt.getTremps(),null);

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
                callback.callback(null,new Exception(error.getMessage()));
            }
        });

        ref.child("trempist-tremp").orderByChild("uid").equalTo(uid).limitToFirst(1).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!snapshot.exists())
                    callback.callback(new ArrayList<>(),null);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.callback(null,new Exception(error.getMessage()));
            }
        });

    }

}

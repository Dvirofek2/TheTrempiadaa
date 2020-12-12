package com.example.thetrempiada;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.thetrempiada.driverActivities.LanLat;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;
import java.util.Locale;

public class mapActivity extends AppCompatActivity implements OnMapReadyCallback {
    Location currentLocation;
    FusedLocationProviderClient client;
    final int REQUEST_CODE = 1;
    private PlacesClient clientSearch;
    private AutocompleteSupportFragment autocompleteFragment;
    MarkerOptions markerOptions;
    GoogleMap myMap;
    LatLng selectedLocation;
    public static final String KEY = "20";
    public Button btnOk;
    final int ZOOM= 12;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_activity);
        btnOk = findViewById(R.id.btnOk);
        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), "AIzaSyA5o1dMIV0ExpvnoY8_bvhR5XkGwB6XIZ4", Locale.US);
        }
        client = LocationServices.getFusedLocationProviderClient(this);
        fetchLastLocation();

        autocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);
        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME,Place.Field.LAT_LNG));
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                if(place.getLatLng()!=null) {
                    //Toast.makeText(mapActivity.this,place.getLatLng().toString(),Toast.LENGTH_LONG).show();
                    LatLng cur = place.getLatLng();
                    MarkerOptions mo = new MarkerOptions().position(cur).title(place.getName());
                    myMap.animateCamera(CameraUpdateFactory.newLatLng(cur));
                    myMap.animateCamera(CameraUpdateFactory.newLatLngZoom(cur, ZOOM));
                    myMap.addMarker(mo);

                }


            }

            @Override
            public void onError(@NonNull Status status) {
                //throw new Error(status.toString());
            }


        });

        btnOk.setOnClickListener(x->sendData());


    }

    private void sendData() {
        Intent returnIntent = new Intent();
        returnIntent.putExtra(KEY,selectedLocation.toString());
        returnIntent.putExtra(KEY+1,selectedLocation);
        setResult(Activity.RESULT_OK,returnIntent);
        finish();
    }




    private void fetchLastLocation() {
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},REQUEST_CODE);
            return;
        }
        Task<Location> task = client.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if(location!=null){
                    currentLocation=location;
                    SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.googleMaps);
                    supportMapFragment.getMapAsync(mapActivity.this);

                }
            }
        });

        task.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Location israelLoc = new Location("Israel");
                currentLocation=israelLoc;
                SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.googleMaps);
                supportMapFragment.getMapAsync(mapActivity.this);

            }
        });

    }

    @Override
    protected void onStart(){
        super.onStart();


    }

    void print(String s){
        Toast.makeText(mapActivity.this,s,Toast.LENGTH_LONG).show();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Toast.makeText(mapActivity.this,"Ready",Toast.LENGTH_LONG).show();
        myMap = googleMap;

        try {
            boolean srcDst = false;
            LatLng cur = null;

            if(getIntent().getExtras()==null||(!getIntent().getExtras().containsKey("location1")&&!getIntent().getExtras().containsKey("SRC"))) {
                cur = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
            }
            else if(getIntent().getExtras().containsKey("location1"))
                cur = (LatLng)getIntent().getExtras().get("location1");
            else {
                srcDst = true;
                LanLat temp = (LanLat)getIntent().getExtras().get("SRC");
                cur  =new LatLng(temp.getLatitude(),temp.getLongitude());
            }
            markerOptions = new MarkerOptions().position(cur).title("This is you");
            selectedLocation = cur;
            googleMap.animateCamera(CameraUpdateFactory.newLatLng(cur));
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(cur, ZOOM));

            if(srcDst){
                Log.w("^^^^^^^^^^^^^^","efsdgsdgds");
                markerOptions.title("src");
                LanLat temp = (LanLat)getIntent().getExtras().get("DST");
                MarkerOptions dst = new MarkerOptions().position(new LatLng(temp.getLatitude(),temp.getLongitude())).title("dst");
                googleMap.addMarker(dst);
            }
            googleMap.addMarker(markerOptions);
        }
        catch (Exception e){
            Toast.makeText(mapActivity.this,"Cannot detect your location",Toast.LENGTH_LONG).show();
            Log.w("!!!",e.getMessage());
        }
        myMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                marker.showInfoWindow();
                print(marker.getPosition().toString());

                selectedLocation = marker.getPosition();
                return true;
            }
        });

        myMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                MarkerOptions mark = new MarkerOptions().position(latLng);
                //googleMap.animateCamera(CameraUpdateFactory.newLatLng(cur));
                //googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(cur,5));
                googleMap.addMarker(mark);
            }
        });



    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CODE) {
            if(grantResults.length>0&&grantResults[0]==PackageManager.PERMISSION_GRANTED) {
                fetchLastLocation();
            }
            //else exit..
        }
    }
}

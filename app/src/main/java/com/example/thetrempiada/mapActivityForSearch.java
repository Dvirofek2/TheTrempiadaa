package com.example.thetrempiada;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.thetrempiada.driverActivities.LanLat;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;

import java.util.Arrays;
import java.util.Locale;

public class mapActivityForSearch extends AppCompatActivity implements OnMapReadyCallback {
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
    LanLat src,dst;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_activity);
        this.src = (LanLat)getIntent().getExtras().get("src");
        this.dst = (LanLat)getIntent().getExtras().get("dst");
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
                    myMap.animateCamera(CameraUpdateFactory.newLatLngZoom(cur, 5));
                    myMap.addMarker(mo);

                }


            }

            @Override
            public void onError(@NonNull Status status) {
                //throw new Error(status.toString());
            }


        });

        this.btnOk.setOnClickListener(x->okClicked());



    }

    private void okClicked() {
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
                    supportMapFragment.getMapAsync(mapActivityForSearch.this);

                }
            }
        });

    }

    @Override
    protected void onStart(){
        super.onStart();


    }

    void print(String s){
        Toast.makeText(mapActivityForSearch.this,s,Toast.LENGTH_LONG).show();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        myMap = googleMap;
        LatLng srcCordinations = new LatLng(src.getLatitude(),src.getLongitude());
        LatLng dstCordinations = new LatLng(dst.getLatitude(),dst.getLongitude());
        MarkerOptions srcMark = new MarkerOptions().position(srcCordinations).title("This is src");
        MarkerOptions dstMark = new MarkerOptions().position(dstCordinations).title("This is dst");

        googleMap.animateCamera(CameraUpdateFactory.newLatLng(srcCordinations));
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(srcCordinations,5));
        googleMap.addMarker(srcMark);
        googleMap.addMarker(dstMark);


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

package com.example.thetrempiada.driverActivities;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import com.example.thetrempiada.ActivityEditUser;
import com.example.thetrempiada.FirebaseDB;
import com.example.thetrempiada.R;
import com.example.thetrempiada.SimpleCallback;
import com.example.thetrempiada.editProfile.Vehicle;
import com.example.thetrempiada.mapActivity;
import com.example.thetrempiada.users.DriverUser;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.SuccessContinuation;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.Calendar;

public class AddTrip extends AppCompatActivity {

    private TextView srcT,dstT,trempB;
    private Button okB;
    private ImageButton srcBtn,dstBtn, timeB, dateB;
    private final int PLACE_PICKER_REQ_SRC= 1;
    private final int PLACE_PICKER_REQ_DST= 2;
    private LatLng src,dst;
    public  int hour,min,year,month,day;
    private AddTrip t = this;
    private DtaeAndTime dateTime;
    private ListView listB;
    ArrayAdapter<Vehicle> adapter;
    int clickedIndex = -1;
    private DriverUser driver;
    int numOfTrempists = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_trip);
        this.srcT = findViewById(R.id.srcT);
        this.dstT = findViewById(R.id.dstT);
        this.srcBtn = findViewById(R.id.srcB);
        this.dstBtn = findViewById(R.id.dstB);
        this.timeB = findViewById(R.id.timeB);
        this.dateB = findViewById(R.id.dateB);
        this.listB = findViewById(R.id.listB);
        this.driver = (DriverUser)getIntent().getExtras().get("driver");
        this.okB = findViewById(R.id.okB);
        this.trempB = findViewById(R.id.trempB);

    }


    @Override
    protected void onStart(){
        super.onStart();
        this.srcBtn.setOnClickListener(x->locationClicked(PLACE_PICKER_REQ_SRC));
        this.dstBtn.setOnClickListener(x->locationClicked(PLACE_PICKER_REQ_DST));
        this.okB.setOnClickListener(x->validateForm());
        this.dateB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new DatePickerFragment();
                newFragment.show(getSupportFragmentManager(), "datePicker");
                DatePickerFragment temp = (DatePickerFragment)newFragment;
                temp.trip = t;
            }
        });
        this.timeB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new TimePickerFragment();
                newFragment.show(getSupportFragmentManager(), "timePicker");
                TimePickerFragment temp = (TimePickerFragment)newFragment;
                temp.trip =t;
            }
        });


        listB.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                clickedIndex = position;
            }


        });
        if(driver.getVehicleIds()==null){
            driver.setVehicleIds(new ArrayList<Vehicle>());
        }
        adapter=new ArrayAdapter<Vehicle>(this,
                android.R.layout.simple_list_item_1,
                driver.getVehicleIds());

        listB.setAdapter(adapter);


    }

    private void validateForm() {
        this.numOfTrempists = Integer.valueOf(trempB.getText().toString());
        if(src == null) {
            Toast.makeText(AddTrip.this, "src location is null", Toast.LENGTH_LONG).show();
            return;
        }
        else if(dst == null) {
            Toast.makeText(AddTrip.this, "dst location is null", Toast.LENGTH_LONG).show();
            return;
        }
        else if(hour ==0 || min == 0) {
            Toast.makeText(AddTrip.this, "time is null", Toast.LENGTH_LONG).show();
            return;
        }
        else if(month == 0 || year == 0||day == 0) {
            Toast.makeText(AddTrip.this, "date is null"+month+""+day+""+year, Toast.LENGTH_LONG).show();
            return;
        }
        else if(!(numOfTrempists >0 && numOfTrempists <5)) {
            Toast.makeText(AddTrip.this, "numOfTrempists must be [1,4]", Toast.LENGTH_LONG).show();
            return;
        }
        else if(this.clickedIndex == -1){
            Toast.makeText(AddTrip.this, "please select a vehicle", Toast.LENGTH_LONG).show();
            return;
        }

        // form is ok
        Tremp tremp = new Tremp(new DtaeAndTime(hour,min,day,year,month),driver.getId(),"",numOfTrempists
                ,driver.getVehicleIds().get(clickedIndex),new LanLat(src.latitude,src.longitude),new LanLat(dst.latitude,dst.longitude),driver.getLastName(),driver.getFirstName(),String.valueOf(driver.getPhone()));
        FirebaseDB db = FirebaseDB.getInstance();
        Task<Void> task = db.writeNewTremp(tremp);
        task.addOnSuccessListener((x -> {
            db.writeUserTremp(tremp,tremp.driverId,new SimpleCallback<Boolean>(){

                @Override
                public void callback(Boolean data, Exception error) {
                    if(error != null)
                        Toast.makeText(AddTrip.this, error.getMessage(), Toast.LENGTH_LONG).show();
                    else {
                        Toast.makeText(AddTrip.this, "Tremp saved!", Toast.LENGTH_LONG).show();
                        finish();
                    }

                }
            });
                }));
        task.addOnFailureListener((x -> Toast.makeText(AddTrip.this, "Tremp did not save!", Toast.LENGTH_LONG).show()));



    }

    private void locationClicked(int x) {
        Intent googleMap = new Intent(AddTrip.this, mapActivity.class);
        startActivityForResult(googleMap,x);

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PLACE_PICKER_REQ_SRC){
            if(resultCode == Activity.RESULT_OK) {
                try {
                    srcT.setText(data.getStringExtra(mapActivity.KEY));
                    src =(LatLng)data.getExtras().get(mapActivity.KEY+1);
                    //Toast.makeText(AddTrip.this, src.toString(), Toast.LENGTH_LONG).show();
                }
                catch (Exception e){
                    srcT.setText("null");
                }
            }
        }
        else if(requestCode == PLACE_PICKER_REQ_DST){
            if(resultCode == Activity.RESULT_OK) {
                try {
                    dstT.setText(data.getStringExtra(mapActivity.KEY));
                    dst =(LatLng)data.getExtras().get(mapActivity.KEY+1);
                }
                catch (Exception e){
                    dstT.setText("null");
                }
            }
        }



    }

    public static class TimePickerFragment extends DialogFragment
            implements TimePickerDialog.OnTimeSetListener {
        //https://developer.android.com/guide/topics/ui/controls/pickers
        public static AddTrip trip;

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current time as the default values for the picker
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            // Create a new instance of TimePickerDialog and return it
            return new TimePickerDialog(getActivity(), this, hour, minute,
                    DateFormat.is24HourFormat(getActivity()));
        }

        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            trip.hour = hourOfDay;
            trip.min = minute;
        }
    }

    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        public static AddTrip trip;
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            trip.year = year;
            trip.day = day;
            trip.month = month+1;
        }
    }
}

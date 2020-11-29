package com.example.thetrempiada.trempistActivities;

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
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import com.example.thetrempiada.FirebaseDB;
import com.example.thetrempiada.R;
import com.example.thetrempiada.SimpleCallback;
import com.example.thetrempiada.driverActivities.AddTrip;
import com.example.thetrempiada.driverActivities.DtaeAndTime;
import com.example.thetrempiada.driverActivities.LanLat;
import com.example.thetrempiada.driverActivities.Tremp;
import com.example.thetrempiada.editProfile.Vehicle;
import com.example.thetrempiada.mapActivity;
import com.example.thetrempiada.users.DriverUser;
import com.example.thetrempiada.users.TrempistUser;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Calendar;

public class ActivitySearchTremp extends AppCompatActivity {

    private TextView srcT, dstT;
    private TrempistUser user;
    private Spinner srcS, dstS;
    private Button srcBtn, dstBtn, okB, timeB, dateB;
    private final int PLACE_PICKER_REQ_SRC = 1;
    private final int PLACE_PICKER_REQ_DST = 2;
    private LatLng src, dst;
    private int hour, min, year, day, month;
    private ActivitySearchTremp t = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_tremp);
        this.srcT = findViewById(R.id.srcT1);
        this.dstT = findViewById(R.id.dstT1);
        this.srcBtn = findViewById(R.id.srcB1);
        this.dstBtn = findViewById(R.id.dstB1);
        this.timeB = findViewById(R.id.timeB1);
        this.dateB = findViewById(R.id.dateB1);
        this.okB = findViewById(R.id.okB1);
        this.srcS = findViewById(R.id.srcS);
        this.dstS = findViewById(R.id.dstS);
        this.user = (TrempistUser)(getIntent().getExtras().get("user"));



    }


    @Override
    protected void onStart() {
        super.onStart();

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.planets_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        this.srcS.setAdapter(adapter);
        this.dstS.setAdapter(adapter);


        this.srcBtn.setOnClickListener(x -> locationClicked(PLACE_PICKER_REQ_SRC));
        this.dstBtn.setOnClickListener(x -> locationClicked(PLACE_PICKER_REQ_DST));
        this.okB.setOnClickListener(x -> validateForm());
        this.dateB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new DatePickerFragment();
                newFragment.show(getSupportFragmentManager(), "datePicker");
                DatePickerFragment temp = (DatePickerFragment) newFragment;
                temp.trip = t;
            }
        });
        this.timeB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new TimePickerFragment();
                newFragment.show(getSupportFragmentManager(), "timePicker");
                TimePickerFragment temp = (TimePickerFragment) newFragment;
                temp.trip = t;
            }
        });


    }

    private void validateForm() {
        if(this.src == null){
            Toast.makeText(ActivitySearchTremp.this, "src location is null", Toast.LENGTH_LONG).show();
            return;
        }
        else if(this.dst == null){
            Toast.makeText(ActivitySearchTremp.this, "dst location is null", Toast.LENGTH_LONG).show();
            return;
        }
        else if(hour ==0 || min == 0){
            Toast.makeText(ActivitySearchTremp.this, "time is null", Toast.LENGTH_LONG).show();
            return;
        }
        else if(month == 0 || year == 0||day == 0) {
            Toast.makeText(ActivitySearchTremp.this, "date is null"+month+""+day+""+year, Toast.LENGTH_LONG).show();
            return;
        }

        int rangeS = Integer.valueOf(srcS.getSelectedItem().toString().substring(0,srcS.getSelectedItem().toString().length()-1));
        int rangeD = Integer.valueOf(dstS.getSelectedItem().toString().substring(0,dstS.getSelectedItem().toString().length()-1));

        SearchQuery query = new SearchQuery(new LanLat(src.latitude,src.longitude),new LanLat(dst.latitude,dst.longitude)
                ,new DtaeAndTime(hour, min, day, year, month),rangeS,rangeD);
        FirebaseDB db = FirebaseDB.getInstance();
        db.solveSearchQuery(query, new SimpleCallback<ArrayList<Tremp>>() {
            @Override
            public void callback(ArrayList<Tremp> data, Exception error) {
                if(error!=null)
                    Toast.makeText(ActivitySearchTremp.this, error.getMessage(), Toast.LENGTH_LONG).show();
                else
                {
                    if(data.size()==0){
                        Toast.makeText(ActivitySearchTremp.this, "No available results", Toast.LENGTH_LONG).show();
                    }
                    else
                    {
                        Intent intent = new Intent(ActivitySearchTremp.this,SearchResults.class);
                        intent.putExtra("user",user);
                        intent.putExtra("tremps",data);
                        startActivity(intent);
                    }
                }
            }
        });






    }

    private void locationClicked(int x) {
        Intent googleMap = new Intent(ActivitySearchTremp.this, mapActivity.class);
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
        public static ActivitySearchTremp trip;

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

        public static ActivitySearchTremp trip;

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
            trip.month = month;
        }

    }
}

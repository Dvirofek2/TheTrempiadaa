package com.example.thetrempiada.trempistActivities;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
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
import com.example.thetrempiada.driverActivities.DtaeAndTime;
import com.example.thetrempiada.driverActivities.LanLat;
import com.example.thetrempiada.driverActivities.MyTrempObj;
import com.example.thetrempiada.driverActivities.MyTremps;
import com.example.thetrempiada.driverActivities.Tremp;
import com.example.thetrempiada.mapActivity;
import com.example.thetrempiada.users.TrempistUser;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Arrays;
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
                        db.myTrempsTrempist(user.getId(), new SimpleCallback<ArrayList<Tremp>>() {
                            @Override
                            public void callback(ArrayList<Tremp> myTremps, Exception error) {
                                ArrayList<MyTrempObj> myTrempObj;
                                if(error==null) {
                                    Toast.makeText(ActivitySearchTremp.this,"data:"+String.valueOf(data.size())+" myTremps:"+String.valueOf(myTremps.size()),Toast.LENGTH_LONG).show();
                                    //Log.w("myTremps:",String.valueOf(myTremps.size()));

                                    ArrayList<Tremp> finalTremps = new ArrayList<>();
                                    if (myTremps == null || myTremps.size() == 0) {

                                        myTrempObj = toMyTrempObj(data);
                                        FirebaseDB.getInstance().getFreeSpacesOfTremps(myTrempObj, new SimpleCallback<Boolean>() {
                                            @Override
                                            public void callback(Boolean data, Exception error) {
                                                if(error==null)
                                                {
                                                    ArrayList<MyTrempObj> temp = filterTrempArray(myTrempObj);
                                                    if(temp.size()==0)
                                                        Toast.makeText(ActivitySearchTremp.this, "No available results", Toast.LENGTH_LONG).show();
                                                    else
                                                        moveToResults(temp);
                                                }
                                                else
                                                    Toast.makeText(ActivitySearchTremp.this,error.getMessage(),Toast.LENGTH_LONG).show();

                                            }});
                                        return;
                                    }

                                    for (Tremp d : data) {
                                        boolean exist = false;
                                        for (Tremp temp : myTremps) {
                                            if (d.getTrempId().equals(temp.getTrempId())) {
                                                exist = true;

                                                break;
                                            }
                                        }

                                        if (!exist){
                                            finalTremps.add(d);
                                            Log.w("****","addddd");}

                                    }
                                    if(finalTremps.size()==0) {
                                        Toast.makeText(ActivitySearchTremp.this, "No available results", Toast.LENGTH_LONG).show();
                                        return;
                                    }
                                    myTrempObj = toMyTrempObj(finalTremps);
                                    FirebaseDB.getInstance().getFreeSpacesOfTremps(myTrempObj, new SimpleCallback<Boolean>() {
                                        @Override
                                        public void callback(Boolean data, Exception error) {

                                            if(error==null) {
                                                ArrayList<MyTrempObj> temp = filterTrempArray(myTrempObj);
                                                if(temp.size()==0)
                                                    Toast.makeText(ActivitySearchTremp.this, "No available results", Toast.LENGTH_LONG).show();
                                                else
                                                    moveToResults(temp);
                                            }
                                            else
                                                Toast.makeText(ActivitySearchTremp.this,error.getMessage(),Toast.LENGTH_LONG).show();

                                        }});
                                }
                                else
                                {
                                    Toast.makeText(ActivitySearchTremp.this,error.getMessage(),Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                    }
                }
            }
        });


    }
    private ArrayList<MyTrempObj> filterTrempArray(ArrayList<MyTrempObj> lst){
        ArrayList<MyTrempObj> temp = new ArrayList<>();
        for(MyTrempObj x:lst){
            if(x.getFreePlaces()>0)
                temp.add(x);
        }
        return temp;
    }

    private ArrayList<MyTrempObj> toMyTrempObj(ArrayList<Tremp> tremps) {
        ArrayList<MyTrempObj> lst = new ArrayList<>();
        for(Tremp t:tremps) {
            MyTrempObj myTremps = new MyTrempObj(t, t.getNumOfPeople());

            lst.add(myTremps);
        }
        return lst;
    }
    private void moveToResults(ArrayList<MyTrempObj> data){
        Log.w("****", Arrays.toString(data.toArray()));
        Intent intent = new Intent(ActivitySearchTremp.this,SearchResults.class);
        intent.putExtra("user",user);
        intent.putExtra("tremps",data);
        intent.putExtra("src",new LanLat(src.latitude,src.longitude));
        startActivity(intent);
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
            trip.month = month+1;
        }

    }
}

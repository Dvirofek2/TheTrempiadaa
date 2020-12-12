package com.example.thetrempiada.driverActivities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import com.example.thetrempiada.DriverMain;
import com.example.thetrempiada.FirebaseDB;
import com.example.thetrempiada.R;
import com.example.thetrempiada.SimpleCallback;
import com.example.thetrempiada.editProfile.Vehicle;
import com.example.thetrempiada.mapActivity;
import com.example.thetrempiada.users.DriverUser;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.Task;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

import javax.microedition.khronos.egl.EGLDisplay;

public class EditTremp extends AppCompatActivity {

    private TextView srcT,dstT,trempB;
    private Button okB,delB,phoneBtn;
    private ImageButton srcBtn,dstBtn, timeB, dateB;
    private final int PLACE_PICKER_REQ_SRC= 1;
    private final int PLACE_PICKER_REQ_DST= 2;
    private LatLng src,dst;
    public  int hour,min,year,month,day;
    private  EditTremp t = this;
    private DtaeAndTime dateTime;
    private Spinner spinner;
    ArrayAdapter<Vehicle> adapter;
    private MyTrempObj tremp;
    private int clickedIndex =0;
    private DriverUser driver;
    int numOfTrempists = 1;
    ArrayList<UserPhone> userPhones;
    Bundle instance;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.edit_tremp_activity);
        this.instance = savedInstanceState;
        this.srcT = findViewById(R.id.srcT2);
        this.dstT = findViewById(R.id.dstT2);
        this.srcBtn = findViewById(R.id.srcB2);
        this.dstBtn = findViewById(R.id.dstB2);
        this.timeB = findViewById(R.id.timeB2);
        this.dateB = findViewById(R.id.dateB2);
        this.driver = (DriverUser)getIntent().getExtras().get("driver");
        this.tremp = (MyTrempObj)getIntent().getExtras().get("tremp");
        this.userPhones = (ArrayList<UserPhone>) getIntent().getExtras().get("trempists");
        this.okB = findViewById(R.id.okB2);
        this.trempB = findViewById(R.id.trempB2);
        this.spinner = findViewById(R.id.spinner);
        this.delB = findViewById(R.id.delB);
        this.phoneBtn = findViewById(R.id.phoneBtn);
        //Log.w("!!!!!!!!!111", Arrays.toString(userPhones.toArray()));
        updateUi(savedInstanceState);

    }

    @Override
    protected void onStart(){
        super.onStart();
        this.srcBtn.setOnClickListener(x->locationClicked(PLACE_PICKER_REQ_SRC));
        this.dstBtn.setOnClickListener(x->locationClicked(PLACE_PICKER_REQ_DST));
        this.okB.setOnClickListener(x->validateForm());
        this.delB.setOnClickListener(x->delBClicked());
        this.phoneBtn.setOnClickListener(x->phoneBtnClicked());
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


        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                clickedIndex = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        adapter=new ArrayAdapter<Vehicle>(this,
                android.R.layout.simple_list_item_1,
                driver.getVehicleIds());

        spinner.setAdapter(adapter);
        spinner.setSelection(findVehicle(tremp.getVehicle()));


    }

    private void phoneBtnClicked() {
        new AlertDialog.Builder(this)
                .setTitle("Information")
                .setMessage(Arrays.toString(this.userPhones.toArray()))

                // Specifying a listener allows you to take an action before dismissing the dialog.
                // The dialog is automatically dismissed when a diaSlog button is clicked.
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Continue with delete operation
                    }
                })

                // A null listener allows the button to dismiss the dialog and take no further action.
                .setNegativeButton(android.R.string.no, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();

    }

    private void delBClicked() {
        FirebaseDB.getInstance().delTremp(tremp);
    }

    private void updateUi(Bundle  b) {
        this.src = new LatLng(tremp.getSrc().getLatitude(),tremp.getSrc().getLongitude());
        this.dst = new LatLng(tremp.getDst().getLatitude(),tremp.getDst().getLongitude());
        this.srcT.setText(this.src.toString());
        this.dstT.setText(this.dst.toString());
        this.numOfTrempists = tremp.numOfPeople;
        this.year = tremp.getDateTime().year;
        this.day = tremp.getDateTime().day;
        this.month = tremp.getDateTime().month;
        this.hour = tremp.getDateTime().hour;
        this.min = tremp.getDateTime().min;

        if(b!=null)
        {
            src = new LatLng(b.getDouble("srcLat"),b.getDouble("srcLon"));
            srcT.setText(src.toString());

        }
    }

    private int findVehicle(Vehicle vehicle) {
        for(int i=0;i<driver.getVehicleIds().size();i++){
            if(driver.getVehicleIds().get(i).getVehicleId()==vehicle.getVehicleId())
                clickedIndex = i;
                return i;
        }
        return -1;
    }

    private void validateForm() {
        this.numOfTrempists = Integer.valueOf(trempB.getText().toString());
        if(src == null) {
            Toast.makeText(EditTremp.this, "src location is null", Toast.LENGTH_LONG).show();
            return;
        }
        else if(dst == null) {
            Toast.makeText(EditTremp.this, "dst location is null", Toast.LENGTH_LONG).show();
            return;
        }
        else if(hour ==0 || min == 0) {
            Toast.makeText(EditTremp.this, "time is null", Toast.LENGTH_LONG).show();
            return;
        }
        else if(month == 0 || year == 0||day == 0) {
            Toast.makeText(EditTremp.this, "date is null"+month+""+day+""+year, Toast.LENGTH_LONG).show();
            return;
        }
        else if(!(numOfTrempists >0 && numOfTrempists <5 )) {
            Toast.makeText(EditTremp.this, "numOfTrempists must be [1,4]", Toast.LENGTH_LONG).show();
            return;
        }
        else if(!(tremp.getFreePlaces()>=tremp.numOfPeople-numOfTrempists))
        {
            Toast.makeText(EditTremp.this, "you have already "+(tremp.numOfPeople-tremp.getFreePlaces())+" trempists!", Toast.LENGTH_LONG).show();
            return;
        }


        // form is ok
        tremp.setDateTime(new DtaeAndTime(hour,min,day,year,month));
        tremp.setNumOfPeople(numOfTrempists);
        tremp.setSrc(new LanLat(src.latitude,src.longitude));
        tremp.setDst(new LanLat(dst.latitude,dst.longitude));
        tremp.setVehicle(driver.getVehicleIds().get(spinner.getSelectedItemPosition()));
        FirebaseDB db = FirebaseDB.getInstance();
        db.updateTremp(tremp);
        //task.addOnFailureListener((x -> Toast.makeText(AddTrip.this, "Tremp did not save!", Toast.LENGTH_LONG).show()));
        Toast.makeText(EditTremp.this,"Tremp updated!",Toast.LENGTH_LONG).show();
        moveToMainDriver();


    }

    private void moveToMainDriver() {
        Intent i = new Intent(EditTremp.this, DriverMain.class);
        startActivity(i);
        finish();
    }

    private void locationClicked(int x) {
        Intent googleMap = new Intent(EditTremp.this, mapActivity.class);
        if(x==PLACE_PICKER_REQ_SRC)
            googleMap.putExtra("location1",new LatLng(src.latitude,src.longitude));
        else
            googleMap.putExtra("location1",new LatLng(dst.latitude,dst.longitude));
        startActivityForResult(googleMap,x);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PLACE_PICKER_REQ_SRC){
            if(resultCode == Activity.RESULT_OK) {
                try {
                    //Log.w("&&&&&&&&&&&&&&&&", String.valueOf((LatLng)data.getExtras().get(mapActivity.KEY+1)));
                    src =(LatLng)data.getExtras().get(mapActivity.KEY+1);
                    srcT.setText(src.toString());
                }
                catch (Exception e){
                    src = null;
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
                    dst = null;
                    dstT.setText("null");
                }
            }
        }



    }

    public static class TimePickerFragment extends DialogFragment
            implements TimePickerDialog.OnTimeSetListener {
        //https://developer.android.com/guide/topics/ui/controls/pickers
        public static EditTremp trip;

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

        public static EditTremp trip;
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

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putDouble("latSrc",src.latitude);
        savedInstanceState.putDouble("lonSrc",src.longitude);
        // Always call the superclass so it can save the view hierarchy state

        super.onSaveInstanceState(savedInstanceState);
    }
}

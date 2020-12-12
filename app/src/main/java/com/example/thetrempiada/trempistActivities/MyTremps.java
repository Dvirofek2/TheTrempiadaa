package com.example.thetrempiada.trempistActivities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.thetrempiada.FirebaseAuthentication;
import com.example.thetrempiada.FirebaseDB;
import com.example.thetrempiada.R;
import com.example.thetrempiada.SimpleCallback;
import com.example.thetrempiada.driverActivities.EditTremp;
import com.example.thetrempiada.driverActivities.MyTrempObj;
import com.example.thetrempiada.driverActivities.Tremp;
import com.example.thetrempiada.driverActivities.TrempDateHighMinComperator;
import com.example.thetrempiada.driverActivities.TrempDateMinHighComperator;
import com.example.thetrempiada.editProfile.Vehicle;
import com.example.thetrempiada.mapActivity;
import com.example.thetrempiada.users.DriverUser;
import com.example.thetrempiada.users.TrempistUser;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

public class MyTremps extends AppCompatActivity {

    ArrayAdapter<MyTrempObj> adapter;
    private TrempistTremp myTremps;
    private ListView listB;
    private Spinner sortS;
    private ArrayList<MyTrempObj> myTrempsLst;
    private TrempistUser trempist;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_tremp_trempist);
        listB =findViewById(R.id.listV);
        sortS = findViewById(R.id.sortS);
        //this.trempist = (TrempistUser) getIntent().getExtras().get("trempist");


        FirebaseDB.getInstance().getTrempistTremps(FirebaseAuthentication.getInstance().mAuth.getCurrentUser().getUid(),
                new SimpleCallback<TrempistTremp>() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void callback(TrempistTremp data, Exception error) {

                        if(error!=null)
                            Toast.makeText(MyTremps.this,error.getMessage(),Toast.LENGTH_LONG).show();
                        else{
                            if(data!=null)
                                updateMyTremps(data);
                            else
                                Toast.makeText(MyTremps.this,"You have no tremps",Toast.LENGTH_LONG).show();
                        }

                    }
                });

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void updateMyTremps(TrempistTremp data) {
        myTremps = data;
        myTrempsLst= toMyTrempObj(myTremps.getTremps());
        myTrempsLst.sort(new TrempDateMinHighComperator());
        Toast.makeText(MyTremps.this,String.valueOf(myTrempsLst.size()),Toast.LENGTH_LONG).show();
        FirebaseDB.getInstance().getFreeSpacesOfTremps(myTrempsLst, new SimpleCallback<Boolean>() {
            @Override
            public void callback(Boolean data, Exception error) {
                if(error==null)
                    updateUI();
                else
                    Toast.makeText(MyTremps.this,error.getMessage(),Toast.LENGTH_LONG).show();

            }});

    }

    private void updateUI(){
        adapter=new ArrayAdapter<MyTrempObj>(this,
                android.R.layout.simple_list_item_1,myTrempsLst
        );

        listB.setAdapter(adapter);

        listB.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MyTremps.this);

                builder.setTitle("Confirm");
                builder.setMessage("Are you sure you want un-register to this tremp?");

                builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        //delete
                        FirebaseDB.getInstance().unregisterTremp(FirebaseAuthentication.getInstance().mAuth.getUid(),myTrempsLst.get(position));
                        Toast.makeText(MyTremps.this,"Tremp deleted",Toast.LENGTH_LONG).show();
                        myTrempsLst.remove(position);
                        adapter.notifyDataSetChanged();
                        dialog.dismiss();
                    }

                });

                builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        // Do nothing
                        dialog.dismiss();
                    }
                });

                AlertDialog alert = builder.create();
                alert.show();
            }
        });

        listB.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                Intent googleMap = new Intent(MyTremps.this, mapActivity.class);
                googleMap.putExtra("SRC", myTrempsLst.get(position).getSrc());
                googleMap.putExtra("DST", myTrempsLst.get(position).getDst());
                startActivity(googleMap);
                return true;
            }
        });


        ArrayAdapter<CharSequence> adapterS = ArrayAdapter.createFromResource(this,
                R.array.sortOptions, android.R.layout.simple_spinner_item);
        adapterS.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        this.sortS.setAdapter(adapterS);

        sortS.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                switch (position){
                    case 1:{
                        myTrempsLst.sort(new TrempDateHighMinComperator());
                        adapter.notifyDataSetChanged();
                        break;
                    }

                    case 0:
                    {
                        myTrempsLst.sort(new TrempDateMinHighComperator());
                        adapter.notifyDataSetChanged();
                        break;
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });
    }






    private ArrayList<MyTrempObj> toMyTrempObj(ArrayList<Tremp> tremps) {
        ArrayList<MyTrempObj> lst = new ArrayList<>();
        for(Tremp t:tremps) {
            MyTrempObj myTremps = new MyTrempObj(t, t.getNumOfPeople());

            lst.add(myTremps);
        }
        return lst;
    }

    @Override
    protected void onStart(){
        super.onStart();
    }


}

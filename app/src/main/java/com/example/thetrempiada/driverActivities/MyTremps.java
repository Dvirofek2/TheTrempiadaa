package com.example.thetrempiada.driverActivities;

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
import com.example.thetrempiada.editProfile.Vehicle;
import com.example.thetrempiada.users.DriverUser;

import java.util.ArrayList;

public class MyTremps extends AppCompatActivity {

    ArrayAdapter<MyTrempObj> adapter;
    private DriverTremp myTremps;
    private ListView listB;
    private Spinner sortS;
    private ArrayList<MyTrempObj> myTrempsLst;
    private DriverUser driver;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_tremp_driver);
        listB =findViewById(R.id.listV);
        sortS = findViewById(R.id.sortS);
        this.driver = (DriverUser)getIntent().getExtras().get("driver");


        FirebaseDB.getInstance().getDriverTremp(FirebaseAuthentication.getInstance().mAuth.getCurrentUser().getUid(),
                new SimpleCallback<DriverTremp>() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void callback(DriverTremp data, Exception error) {

                        if(error!=null)
                            Toast.makeText(MyTremps.this,error.getMessage(),Toast.LENGTH_LONG).show();
                        else{
                            updateMyTremps(data);
                        }

                    }
                });

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void updateMyTremps(DriverTremp data) {
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

        listB.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                                           int pos, long id) {
                FirebaseDB.getInstance().getRegisteredTrempists(myTrempsLst.get(pos), new SimpleCallback<ArrayList<UserPhone>>() {
                    @Override
                    public void callback(ArrayList<UserPhone> data, Exception error) {
                        if(error==null){
                        Intent intent = new Intent(MyTremps.this,EditTremp.class);
                        intent.putExtra("tremp",myTrempsLst.get(pos));
                        intent.putExtra("driver",driver);
                        intent.putExtra("trempists",data);
                        startActivity(intent);
                    }
                        else
                            Toast.makeText(MyTremps.this,error.getMessage(),Toast.LENGTH_LONG).show();

                    }
                });


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
            MyTrempObj myTremps = new MyTrempObj(t, t.numOfPeople);

            lst.add(myTremps);
        }
        return lst;
    }

    @Override
    protected void onStart(){
        super.onStart();
    }


}

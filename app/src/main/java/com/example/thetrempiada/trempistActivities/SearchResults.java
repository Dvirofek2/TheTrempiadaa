package com.example.thetrempiada.trempistActivities;

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
import androidx.fragment.app.DialogFragment;

import com.example.thetrempiada.FirebaseDB;
import com.example.thetrempiada.R;
import com.example.thetrempiada.SimpleCallback;
import com.example.thetrempiada.driverActivities.AddTrip;
import com.example.thetrempiada.driverActivities.LanLat;
import com.example.thetrempiada.driverActivities.MyTrempObj;
import com.example.thetrempiada.driverActivities.Tremp;
import com.example.thetrempiada.driverActivities.TrempDateMinHighComperator;
import com.example.thetrempiada.editProfile.Vehicle;
import com.example.thetrempiada.mapActivityForSearch;
import com.example.thetrempiada.users.DriverUser;
import com.example.thetrempiada.users.TrempistUser;

import java.io.Serializable;
import java.util.ArrayList;

public class SearchResults extends AppCompatActivity {
    private ArrayList<MyTrempObj> tremps;
    private TrempistUser trempist;
    private ListView listV;
    ArrayAdapter<MyTrempObj> adapter;
    int choosedIndex = -1;
    private Spinner srcS;
    private LanLat src;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_search_results);
        this.listV = findViewById(R.id.listB);
        this.trempist = (TrempistUser)(getIntent().getExtras().get("user"));
        this.tremps = (ArrayList<MyTrempObj>) (getIntent().getExtras().get("tremps"));
        this.srcS = findViewById(R.id.srcS);
        this.src = (LanLat) (getIntent().getExtras().get("src"));
    }


    @Override
    protected void onStart(){
        super.onStart();
        ArrayAdapter<CharSequence> adapterSrc = ArrayAdapter.createFromResource(this,
                R.array.sortBy, android.R.layout.simple_spinner_item);
        adapterSrc.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        this.srcS.setAdapter(adapterSrc);

        listV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                choosedIndex = position;
                FirebaseDB db = FirebaseDB.getInstance();
                db.joinTremp(tremps.get(choosedIndex), trempist, new SimpleCallback<Boolean>() {
                    @Override
                    public void callback(Boolean data, Exception error) {
                        Toast.makeText(SearchResults.this, "Join successfully!", Toast.LENGTH_LONG).show();
                        finish();
                    }
                });
            }


        });

        listV.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                                           int pos, long id) {
                Intent intent = new Intent(SearchResults.this, mapActivityForSearch.class);
                intent.putExtra("src",tremps.get(pos).getSrc());
                intent.putExtra("dst",tremps.get(pos).getDst());
                startActivity(intent);
                return true;
            }
        });


        adapter=new ArrayAdapter<MyTrempObj>(this,
                android.R.layout.simple_list_item_1,
                tremps);

        listV.setAdapter(adapter);
        srcS.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                switch (position){
                    case 0:{
                        FirebaseDB db = FirebaseDB.getInstance();
                        tremps.sort( new TrempTrmpistDistComperator(db,src));
                        adapter.notifyDataSetChanged();
                        break;
                    }
                    case 1:
                    {
                        tremps.sort(new TrempDateMinHighComperator());
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

}

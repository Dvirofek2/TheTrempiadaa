package com.example.thetrempiada.trempistActivities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import com.example.thetrempiada.R;
import com.example.thetrempiada.driverActivities.AddTrip;
import com.example.thetrempiada.driverActivities.Tremp;
import com.example.thetrempiada.editProfile.Vehicle;
import com.example.thetrempiada.mapActivityForSearch;
import com.example.thetrempiada.users.DriverUser;
import com.example.thetrempiada.users.TrempistUser;

import java.io.Serializable;
import java.util.ArrayList;

public class SearchResults extends AppCompatActivity {
    private ArrayList<Tremp> tremps;
    private TrempistUser trempist;
    private ListView listV;
    ArrayAdapter<Tremp> adapter;
    int choosedIndex = -1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_search_results);
        this.listV = findViewById(R.id.listB);
        this.trempist = (TrempistUser)(getIntent().getExtras().get("user"));
        this.tremps = (ArrayList<Tremp>) (getIntent().getExtras().get("tremps"));
    }


    @Override
    protected void onStart(){
        super.onStart();

        listV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                choosedIndex = position;
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


        adapter=new ArrayAdapter<Tremp>(this,
                android.R.layout.simple_list_item_1,
                tremps);

        listV.setAdapter(adapter);
    }

}

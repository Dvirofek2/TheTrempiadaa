package com.example.thetrempiada;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class preMainActivity extends AppCompatActivity  {


    private  FirebaseDB db;
    private FirebaseAuthentication auth;
    private TextView nameTxt;
    private Button driver,trempist;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pre_main);
        this.driver = findViewById(R.id.driverButton);
        this.trempist = findViewById(R.id.joinButton);

    }

    @Override
    protected void onStart() {
        super.onStart();
        this.driver.setOnClickListener(x->clickedOnDriver());
        this.trempist.setOnClickListener(x->clickedOnTrempist());
    }

    private void clickedOnTrempist() {
        Intent trempistIntent = new Intent(preMainActivity.this,MainActivity.class);
        trempistIntent.putExtra("type",UserType.TREMPIST);
        startActivity(trempistIntent);
    }

    private void clickedOnDriver() {
        Intent trempistIntent = new Intent(preMainActivity.this,MainActivity.class);
        trempistIntent.putExtra("type",UserType.DRIVER);
        startActivity(trempistIntent);
    }
}

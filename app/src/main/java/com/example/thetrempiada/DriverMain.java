package com.example.thetrempiada;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.thetrempiada.driverActivities.AddTrip;
import com.example.thetrempiada.driverActivities.DriverTremp;
import com.example.thetrempiada.driverActivities.MyTremps;
import com.example.thetrempiada.users.DriverUser;
import com.example.thetrempiada.users.User;

public class DriverMain extends AppCompatActivity {
    private DriverUser driver;
    private  FirebaseDB db;
    private FirebaseAuthentication auth;
    private TextView nameTxt;
    private Button addTrip,editProfile,myTremps;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_driver);
        this.db = FirebaseDB.getInstance();
        this.auth = FirebaseAuthentication.getInstance();
        this.nameTxt = findViewById(R.id.textName);
        this.editProfile = findViewById(R.id.profileBtn);
        this.addTrip = findViewById(R.id.SearchTremp);
        this.myTremps = findViewById(R.id.mytrempsB);

    }

    @Override
    protected void onStart() {
        super.onStart();
        setButtonsEnabled(false);
        editProfile.setOnClickListener(x->clickedOnEditProfile());
        addTrip.setOnClickListener(x->addTrip());
        myTremps.setOnClickListener(x->myTrempsClicked());
        this.db.getUserById(this.auth.mAuth.getUid(), new SimpleCallback<User>() {

            @Override
            public void callback(User data, Exception error) {
                if(data == null){
                    Toast.makeText(getApplicationContext(),error.getMessage(),Toast.LENGTH_LONG).show();
                }
                else
                    updateUser(data);
            }
        },UserType.DRIVER);

    }

    private void myTrempsClicked() {
        Intent intent = new Intent(DriverMain.this, MyTremps.class);
        intent.putExtra("driver",driver);
        startActivity(intent);
    }

    private void addTrip() {
        Intent intent = new Intent(DriverMain.this, AddTrip.class);
        intent.putExtra("driver",driver);
        startActivity(intent);
    }

    private void updateUser(User userObj){
        this.driver = (DriverUser) userObj;
        this.nameTxt.setText("Hello "+this.driver.getFirstName());
        setButtonsEnabled(true);
    }

    private void setButtonsEnabled(boolean flag){
        editProfile.setEnabled(flag);
        addTrip.setEnabled(flag);
    }

    private void clickedOnEditProfile(){
        Intent editProfile = new Intent(DriverMain.this,ActivityEditUser.class);
        editProfile.putExtra("user",this.driver);
        editProfile.putExtra("user-type",UserType.DRIVER);
        startActivity(editProfile);

    }




}

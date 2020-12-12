package com.example.thetrempiada;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.thetrempiada.trempistActivities.ActivitySearchTremp;
import com.example.thetrempiada.trempistActivities.MyTremps;
import com.example.thetrempiada.users.DriverUser;
import com.example.thetrempiada.users.TrempistUser;
import com.example.thetrempiada.users.User;

public class TrempistMain extends AppCompatActivity {
    private TrempistUser trempist;
    private  FirebaseDB db;
    private FirebaseAuthentication auth;
    private TextView nameTxt;
    private Button editProfile,searchTremp,myTbtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_trempist);
        this.db = FirebaseDB.getInstance();
        this.auth = FirebaseAuthentication.getInstance();
        this.nameTxt = findViewById(R.id.textName);
        this.editProfile = findViewById(R.id.profileBtn);
        this.searchTremp = findViewById(R.id.SearchTremp);
        this.myTbtn = findViewById(R.id.myTbtn);

    }

    @Override
    protected void onStart() {
        super.onStart();
        setButtonsEnabled(false);
        this.editProfile.setOnClickListener(x->clickedOnEditProfile());
        this.searchTremp.setOnClickListener(x->searchClicked());
        this.myTbtn.setOnClickListener(x->myTremps());
        //editProfile.setOnClickListener(x->clickedOnEditProfile());
        this.db.getUserById(this.auth.mAuth.getUid(), new SimpleCallback<User>() {

            @Override
            public void callback(User data, Exception error) {
                if(data == null){
                    Toast.makeText(getApplicationContext(),error.getMessage(),Toast.LENGTH_LONG).show();
                }
                else
                    updateUser(data);
            }
        },UserType.TREMPIST);

    }

    private void myTremps() {
        Intent i = new Intent(TrempistMain.this, MyTremps.class);
        startActivity(i);
    }

    private void searchClicked() {
        Intent search = new Intent(TrempistMain.this, ActivitySearchTremp.class);
        search.putExtra("user",trempist);
        startActivity(search);
    }

    private void updateUser(User userObj){
        this.trempist = (TrempistUser) userObj;
        this.nameTxt.setText("Hello "+this.trempist.getFirstName());
        setButtonsEnabled(true);
    }

    private void setButtonsEnabled(boolean flag){
        editProfile.setEnabled(flag);

    }

    private void clickedOnEditProfile(){
        Intent editProfile = new Intent(TrempistMain.this,ActivityEditUser.class);
        editProfile.putExtra("user",this.trempist);
        editProfile.putExtra("user-type",UserType.TREMPIST);
        startActivity(editProfile);

    }




}

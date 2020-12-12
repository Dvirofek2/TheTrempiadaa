package com.example.thetrempiada;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.thetrempiada.editProfile.Vehicle;
import com.example.thetrempiada.users.DriverUser;
import com.example.thetrempiada.users.TrempistUser;
import com.example.thetrempiada.users.User;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;

public class ActivityEditUser extends AppCompatActivity {

    private Button saveBox, plusB,minusB,logoutB;
    private TextView nameB,lastB,phoneB,emailB,typeB, myVehicleTxt;
    private User user;
    private UserType userType;
    private ListView listB;
    private int clickedIndex;
    private  int mone = 0;
    ArrayAdapter<Vehicle> adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        this.nameB= findViewById(R.id.nameB);
        this.emailB= findViewById(R.id.emailB);
        this.phoneB= findViewById(R.id.phoneBtn);
        this.lastB= findViewById(R.id.lastB);
        this.typeB= findViewById(R.id.typeB);
        this.plusB = findViewById(R.id.plusB);
        this.minusB = findViewById(R.id.minusB);
        this.listB = findViewById(R.id.listB);
        this.saveBox = findViewById(R.id.saveB);
        this.logoutB = findViewById(R.id.logoutB);
        this.myVehicleTxt = findViewById(R.id.myVehicleTxt);


    }

    @Override
    protected void onStart(){
        super.onStart();
        updateUI();
        this.plusB.setOnClickListener(x->plusClicked());
        this.minusB.setOnClickListener(x->minusClicked());
        this.saveBox.setOnClickListener(x->saveProfile());
        this.logoutB.setOnClickListener(x->logout());



    }

    private void logout() {
        GoogleLogin login = GoogleLogin.getInstance();
        FirebaseAuthentication auth = FirebaseAuthentication.getInstance();
        auth.mAuth.signOut();
        login.signOut().addOnCompleteListener(this,
                task -> {
                    Intent i = new Intent(ActivityEditUser.this, preMainActivity.class);
                    startActivity(i);
                });
    }




    private void saveProfile() {
        Long phone= Long.valueOf(this.phoneB.getText().toString());
        boolean phoneOk = android.util.Patterns.PHONE.matcher(phone.toString()).matches();
        if(phoneOk) {
            this.user.setPhone(Long.valueOf(this.phoneB.getText().toString()));
            this.user.setFirstName(this.nameB.getText().toString());
            this.user.setLastName(this.lastB.getText().toString());
            FirebaseDB db = FirebaseDB.getInstance();
            Task<Void> savedTask = db.updateUser(user);
            savedTask.addOnSuccessListener((x -> Toast.makeText(ActivityEditUser.this, "User updated!", Toast.LENGTH_LONG).show()));
            savedTask.addOnFailureListener((x -> Toast.makeText(ActivityEditUser.this, "User did not updated!", Toast.LENGTH_LONG).show()));

            if(user.getType() == UserType.DRIVER) {
                Intent driver = new Intent(ActivityEditUser.this,DriverMain.class);
                startActivity(driver);
            }
            else {
                Intent trempist = new Intent(ActivityEditUser.this,TrempistMain.class);
                startActivity(trempist);
            }
            finish();

        }
        else
            Toast.makeText(ActivityEditUser.this, "Invalid phone", Toast.LENGTH_LONG).show();


    }

    private void minusClicked() {
        DriverUser driver = (DriverUser)user;
        driver.getVehicleIds().remove(clickedIndex);
        adapter.notifyDataSetChanged();


    }

    private void plusClicked() {
        LayoutInflater li = LayoutInflater.from(this);
        View promptsView = li.inflate(R.layout.input_dialig, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                this);

        // set prompts.xml to alertdialog builder
        alertDialogBuilder.setView(promptsView);

        final EditText userInput = (EditText) promptsView
                .findViewById(R.id.userInput);
        final EditText modelInput = (EditText) promptsView
                .findViewById(R.id.modelInput);
        // set dialog message
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                // get user input and set it to result
                                // edit text
                                if(userInput.getText().length()<7){
                                    Toast.makeText(ActivityEditUser.this,"Vehicle id length must be 7 or 8",Toast.LENGTH_LONG).show();
                                }
                                else {
                                    DriverUser driver = (DriverUser) user;
                                    mone++;
                                    int vehicleId = Integer.valueOf(userInput.getText().toString());
                                    String model = modelInput.getText().toString();
                                    driver.getVehicleIds().add(new Vehicle(vehicleId,model));
                                    adapter.notifyDataSetChanged();
                                }
                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                dialog.cancel();
                            }
                        });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }

    private void updateUI() {
        Bundle extras = getIntent().getExtras();
        UserType userType = (UserType)extras.get("user-type");

        if(userType == UserType.DRIVER){
            user = (DriverUser)extras.get("user");
            DriverUser driver = (DriverUser)user;
            if(driver.getVehicleIds()==null){
                driver.setVehicleIds(new ArrayList<Vehicle>());
            }
            adapter=new ArrayAdapter<Vehicle>(this,
                    android.R.layout.simple_list_item_1,
                    driver.getVehicleIds());

            listB.setAdapter(adapter);

            listB.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    clickedIndex = position;
                }


            });



        }

        else {
            user = (TrempistUser) extras.get("user");
            this.listB.setVisibility(View.INVISIBLE);
            this.listB.setEnabled(false);
            this.plusB.setVisibility(View.INVISIBLE);
            this.plusB.setEnabled(false);
            this.minusB.setVisibility(View.INVISIBLE);
            this.minusB.setEnabled(false);
            this.myVehicleTxt.setVisibility(View.INVISIBLE);
            this.myVehicleTxt.setEnabled(false);


        }

        this.emailB.setText(user.getEmail());
        this.nameB.setText(user.getFirstName());
        this.lastB.setText(user.getLastName());
        this.phoneB.setText(String.valueOf(user.getPhone()));
        this.typeB.setText(UserType.getName(user.getType().toString()));

    }



}

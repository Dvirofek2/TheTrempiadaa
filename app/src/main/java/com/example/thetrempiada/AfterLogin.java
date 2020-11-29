package com.example.thetrempiada;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;

public class AfterLogin extends AppCompatActivity {

    TextView userText;
    Button logoutBtn;
    //GoogleSignInClient mGoogleSignInClient;
    GoogleLogin googleLogin;
    FirebaseAuthentication firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.after_login);
        googleLogin = GoogleLogin.getInstance();
        firebaseAuth = FirebaseAuthentication.getInstance();
        //mGoogleSignInClient = MainActivity.mGoogleSignInClient;
        userText = findViewById(R.id.userText);
        logoutBtn = findViewById(R.id.logoutBtn);
        logoutBtn.setOnClickListener(v->logOut());
    }

    @Override
    protected void onStart(){
        super.onStart();
        FirebaseUser account = firebaseAuth.mAuth.getCurrentUser();
        String name = account.getDisplayName();
        String email = account.getEmail();
        String photo = String.valueOf(account.getPhotoUrl());
        userText.append("Hello "+name+ "\n your email: "+email+"\n");
        
    }

    private void logOut() {
        //FirebaseAuth.getInstance().signOut();
        firebaseAuth.mAuth.signOut();
        googleLogin.signOut().addOnCompleteListener(this,
                task -> {
                        Intent i = new Intent(AfterLogin.this, MainActivity.class);
                        startActivity(i);
                });
    }

}

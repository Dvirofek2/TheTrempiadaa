package com.example.thetrempiada;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class FirebaseAuthentication {

    private static FirebaseAuthentication instance;
    public FirebaseAuth mAuth;

    private FirebaseAuthentication(){
        mAuth = FirebaseAuth.getInstance();
    }

    public static FirebaseAuthentication getInstance(){
        if(instance == null){
            synchronized (GoogleLogin.class){
                if(instance == null)
                    instance = new FirebaseAuthentication();
            }
        }
        return instance;
    }

    public Task<AuthResult> sighInFirebase(GoogleSignInAccount account){
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(),"driver");
        return mAuth.signInWithCredential(credential);
    }

}

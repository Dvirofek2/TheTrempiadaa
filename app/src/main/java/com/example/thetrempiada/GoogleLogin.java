package com.example.thetrempiada;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.Task;


public class GoogleLogin {
    private static GoogleLogin instance = null;
    private static final int GOOGLE_SIGN_IN = 123;

    public GoogleSignInClient mGoogleSignInClient;
    private GoogleSignInOptions gso;


    private GoogleLogin(){

    }

    public static GoogleLogin getInstance(){
        if(instance == null){
            synchronized (GoogleLogin.class){
                if(instance == null)
                    instance = new GoogleLogin();
            }
        }

        return instance;
    }

    public int getGoogleSignInCode(){return GOOGLE_SIGN_IN;}

    public void setOptions(Activity activity){
        this.gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(activity.getResources().getString((R.string.default_web_client_id)))
                .requestEmail()
                .build();
    }
    public void getClient(Activity activity){
        this.mGoogleSignInClient = GoogleSignIn.getClient(activity, this.gso);
        //return mGoogleSignInClient;
    }

    public Intent getGoogleSighInIntent(){
        return mGoogleSignInClient.getSignInIntent();
    }

    public static Task<GoogleSignInAccount> getSignedInAccountFromIntent(Intent i){
        return GoogleSignIn.getSignedInAccountFromIntent(i);
    }

    public Task<Void> signOut(){ return mGoogleSignInClient.signOut();}



}
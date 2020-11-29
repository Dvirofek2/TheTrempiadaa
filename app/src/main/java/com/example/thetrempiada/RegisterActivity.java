package com.example.thetrempiada;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.thetrempiada.FirebaseAuthentication;
import com.example.thetrempiada.FirebaseDB;
import com.example.thetrempiada.GoogleLogin;
import com.example.thetrempiada.R;
import com.example.thetrempiada.UserType;
import com.example.thetrempiada.errors.ErrorsMessages;

import java.util.logging.ErrorManager;

public class RegisterActivity  extends AppCompatActivity {
    private EditText emailBox;
    private EditText passwordBox;
    private EditText firstBox;
    private EditText lastBox;
    private Button registerBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        this.emailBox = findViewById(R.id.emailBox);
        this.passwordBox = findViewById(R.id.passwordBox);
        this.firstBox = findViewById(R.id.firstBox);
        this.lastBox = findViewById(R.id.lastBox);
        this.registerBtn = findViewById(R.id.register_btn);
    }

    @Override
    protected void onStart() {
        super.onStart();

        this.registerBtn.setOnClickListener(v->register());
    }

    private void register() {
        try{
            validForm();
        }
        catch (Error e){
            Toast.makeText(this, e.getMessage(),
                    Toast.LENGTH_LONG).show();
        }
    }

    private void validForm(){
        if(!isEmailValid(this.emailBox.getText().toString()))
            throw new Error(ErrorsMessages.invalid_email);
        if(isPasswordLengthOk(this.passwordBox.getText().toString()))
            throw new Error(ErrorsMessages.invalid_password_length);
        if(this.firstBox.getText().toString() == null || this.firstBox.getText().toString().isEmpty())
            throw new Error(String.format(ErrorsMessages.empty_string,"first name"));
        if(this.lastBox.getText().toString() == null || this.firstBox.getText().toString().isEmpty())
            throw new Error(String.format(ErrorsMessages.empty_string,"last name"));

    }

    private boolean isEmailValid(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private boolean isPasswordLengthOk(String pass){
        return pass.length() > 5;
    }

}

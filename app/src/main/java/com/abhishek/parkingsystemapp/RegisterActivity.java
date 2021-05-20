package com.abhishek.parkingsystemapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.abhishek.parkingsystemapp.Models.AppUser;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class RegisterActivity extends AppCompatActivity {

    EditText etName, etPhone, etEmail, etPassword;
    EditText etLicense1, etLicense2, etLicense3, etLicense4;
    EditText etDlNumber1, etDlNumber2;
    Button btnRegister;
    ProgressBar progressBar;

    FirebaseAuth firebaseAuth;
    FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etName = findViewById(R.id.etName_Register);
        etLicense1 = findViewById(R.id.etLicense1_Register);
        etLicense2 = findViewById(R.id.etLicense2_Register);
        etLicense3 = findViewById(R.id.etLicense3_Register);
        etLicense4 = findViewById(R.id.etLicense4_Register);
        etPhone = findViewById(R.id.etPhone_Register);
        etEmail = findViewById(R.id.etEmail_Register);
        etPassword = findViewById(R.id.etPassword_Register);
        etDlNumber1 = findViewById(R.id.etdlNumber1_Register);
        etDlNumber2 = findViewById(R.id.etdlNumber2_Register);
        btnRegister= findViewById(R.id.btnRegister_Register);
        progressBar = findViewById(R.id.progressBar);

        progressBar.setVisibility(View.INVISIBLE);

       etLicense1.addTextChangedListener(new TextWatcher() {
           @Override
           public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

           @Override
           public void onTextChanged(CharSequence s, int start, int before, int count) { }

           @Override
           public void afterTextChanged(Editable s) {
               if(s.length() >= 2){
                   etLicense2.requestFocus();
                   //etLicense1.setText(s.toString().toUpperCase());
               }
           }
       });

        etLicense2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.length() >= 2)
                    etLicense3.requestFocus();
            }
        });

        etLicense3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.length() >= 2){
                    etLicense4.requestFocus();
                    //etLicense3.setText(s.toString().toUpperCase());
                }
            }
        });

        etDlNumber1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.length() >= 4){
                    etDlNumber2.requestFocus();
                    //etDlNumber1.setText(s.toString().toUpperCase());
                }
                if(s.length() >= 2)
                    etDlNumber1.setInputType(InputType.TYPE_CLASS_NUMBER);
                else
                    etDlNumber1.setInputType(InputType.TYPE_CLASS_TEXT);
            }
        });


        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String name = etName.getText().toString().trim();
                String license = etLicense1.getText().toString().trim() + " - " + etLicense2.getText().toString().trim() + " - " +
                         etLicense3.getText().toString().trim() + " - " + etLicense4.getText().toString().trim();
                license = license.toUpperCase();
                Log.d("License Plate:: ", license);
                String phone = etPhone.getText().toString().trim();
                String email = etEmail.getText().toString().trim();
                String password = etPassword.getText().toString().trim();
                String dlNumber = etDlNumber1.getText().toString().trim() + " " + etDlNumber2.getText().toString().trim();
                dlNumber = dlNumber.toUpperCase();
                Log.d("Driving License :: ", dlNumber);

                if(name.isEmpty() || license.isEmpty() || email.isEmpty() || password.isEmpty() || phone.isEmpty() || dlNumber.isEmpty()){
                    Toast.makeText(RegisterActivity.this, "Enter all Fields!!", Toast.LENGTH_SHORT).show();
                }
                else{

                    if(license.length() == 19 && dlNumber.length() == 16) //19 characters including " - " and 16 characters for dlNumber
                        registerUser(name, license, phone, email, password, dlNumber);
                    else
                        Toast.makeText(RegisterActivity.this, "Enter correct credentials", Toast.LENGTH_SHORT).show();

                }

            }
        });

    }

    private void registerUser(String name, String license, String phone, String email, String password, String dlNumber) {

        progressBar.setVisibility(View.VISIBLE);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {

                        firebaseAuth.getCurrentUser().sendEmailVerification()
                                .addOnCompleteListener(new OnCompleteListener() {
                                    @Override
                                    public void onComplete(@NonNull @NotNull Task task) {
                                        if (task.isSuccessful()){
                                            firestore = FirebaseFirestore.getInstance();
                                            AppUser user = new AppUser(name, license, phone, email, 0.00, dlNumber, "", "");
                                            firestore.collection("USERS")
                                                    .document(firebaseAuth.getCurrentUser().getUid())
                                                    .set(user)
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull @NotNull Task<Void> task) {
                                                            if(task.isSuccessful()){
                                                                Toast.makeText(RegisterActivity.this,
                                                                        "Successfully created, Please Login!!",
                                                                        Toast.LENGTH_SHORT).show();
                                                                progressBar.setVisibility(View.INVISIBLE);
                                                                startActivity(new Intent(RegisterActivity.this,
                                                                        StartActivity.class));
                                                                finish();
                                                            }
                                                            else{
                                                                Toast.makeText(RegisterActivity.this,
                                                                        "Sorry Unable to make an account. Please Try again!!",
                                                                        Toast.LENGTH_SHORT).show();
                                                                progressBar.setVisibility(View.INVISIBLE);
                                                            }
                                                        }
                                                    });
                                        }
                                        else {
                                            Toast.makeText(RegisterActivity.this, "Email not valid!!", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull @org.jetbrains.annotations.NotNull Exception e) {
                        Toast.makeText(RegisterActivity.this, "Sorry Unable to make an account. Please Try again!!",
                                Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.INVISIBLE);
                    }
                });

    }

    public void goToLoginActivity(View view) {

        startActivity(new Intent(RegisterActivity.this, com.abhishek.parkingsystemapp.LoginActivity.class));
        finish();

    }
}
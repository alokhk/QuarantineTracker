package com.ak86.quarantinetracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private EditText inputEmail, inputPassword;
    private FirebaseAuth auth;
    private ProgressBar progressBar;
    private Button btnSignIn, btnReset, btnNewUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().setLogo(R.drawable.image);
        getSupportActionBar().setDisplayUseLogoEnabled(true);

        inputEmail = findViewById(R.id.email);
        inputPassword = findViewById(R.id.password);
        btnSignIn =  this.findViewById(R.id.login);
        btnReset = this.findViewById(R.id.reset);
        btnNewUser = this.findViewById(R.id.btn_CreateNewUser);

        auth = FirebaseAuth.getInstance();

        btnSignIn.setOnClickListener(new View.OnClickListener(){
            public void onClick (View v){
                String email = inputEmail.getText().toString();
                final String password = inputPassword.getText().toString();
                if(TextUtils.isEmpty(email)){
                    Toast.makeText(getApplicationContext(),"Enter your email address!",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(TextUtils.isEmpty(password)){
                    Toast.makeText(getApplicationContext(),"Enter your password!",Toast.LENGTH_SHORT).show();
                    return;
                }
                progressBar = findViewById(R.id.progressBar);
                if (progressBar != null)  {
                    progressBar.setVisibility(View.VISIBLE);
                }
                inputEmail.setEnabled(false);
                inputPassword.setEnabled(false);

                auth.signInWithEmailAndPassword(email,password)
                        .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {

                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {

                                progressBar.setVisibility(View.GONE);
                                inputPassword.setEnabled(true);
                                inputEmail.setEnabled(true);

                                if(!task.isSuccessful()){
                                    if(password.length() < 6){
                                        inputPassword.setError("Min password lenght is 6");
                                    } else {
                                        Toast.makeText(getApplicationContext(),"Authentication Failed!",Toast.LENGTH_SHORT).show();
                                    }
                                } else {

                                    Toast.makeText(getApplicationContext(),auth.getCurrentUser().getEmail()+" logged in!",Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(getApplicationContext(), LandingActivity.class);
                                    startActivity(intent);

                                }

                            }
                        });
            }
        });

        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), PasswordResetActivity.class);
                startActivity(intent);
            }
        });

        btnNewUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), CreateNewUser.class);
                startActivity(intent);
            }
        });
    }
}
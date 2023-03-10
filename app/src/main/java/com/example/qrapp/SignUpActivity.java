package com.example.qrapp;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SignUpActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private EditText mEmailField;
    private EditText mPasswordField;
    private Button mSignUpButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_signup);
//
//        mAuth = FirebaseAuth.getInstance();
//        mEmailField = findViewById(R.id.email_field);
//        mPasswordField = findViewById(R.id.password_field);
//        mSignUpButton = findViewById(R.id.signup_button);
//
//        mSignUpButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String email = mEmailField.getText().toString();
//                String password = mPasswordField.getText().toString();
//
//                // Create a new user account with email and password
//                mAuth.createUserWithEmailAndPassword(email, password)
//                        .addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
//                            @Override
//                            public void onComplete(@NonNull Task<AuthResult> task) {
//                                if (task.isSuccessful()) {
//                                    // Sign up success, update UI with the signed-in user's information
//                                    Toast.makeText(SignUpActivity.this, "Sign up successful", Toast.LENGTH_SHORT).show();
//                                    finish(); // return to MainActivity
//                                } else {
//                                    // If sign up fails, display a message to the user.
//                                    Toast.makeText(SignUpActivity.this, "Sign up failed: " + task.getException().getMessage(),
//                                            Toast.LENGTH_SHORT).show();
//                                }
//                            }
//                        });
//            }
//        });
    }
}

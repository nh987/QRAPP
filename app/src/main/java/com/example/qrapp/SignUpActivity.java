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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class SignUpActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private EditText emailField;
    private EditText passwordField;
    private EditText username;
    private Button signUpButton;

    @Override
    /**
     * This method is called when the activity is created. The layout is set and the Firebase authentication and database are initialized.
     * @param savedInstanceState the saved instance state
     */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        auth = FirebaseAuth.getInstance();
        username = findViewById(R.id.username);
        emailField = findViewById(R.id.email_field);
        passwordField = findViewById(R.id.password_field);
        signUpButton = findViewById(R.id.signup_button);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            /**
             * This method is called when the sign up button is clicked. The entered email, password, and username are retrieved. The username is checked to see if it already exists. If it does, a toast message is displayed. If it does not, a new user is created with the entered email and password.
             * @param v the view that was clicked
             */
            public void onClick(View v) {
                String email = emailField.getText().toString();
                String password = passwordField.getText().toString();
                String uname = username.getText().toString();

                // Check if the username already exists
                db.collection("Users")
                        .whereEqualTo("username", uname)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            /**
                             * This method is called when the task is complete. If the username already exists, a toast message is displayed. If the username is unique, a new user is created with the entered email and password.
                             * @param task the completed task
                             */
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    if (!task.getResult().isEmpty()) {
                                        // Username already exists
                                        Toast.makeText(SignUpActivity.this, "Username already exists", Toast.LENGTH_SHORT).show();
                                        return;
                                    }

                                    // Username is unique, create the new user
                                    auth.createUserWithEmailAndPassword(email, password)
                                            .addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
                                                @Override
                                                /**
                                                 * This method is called when the task is complete. If the task is successful, a new user is created in the database with the entered username, email, location, and phone number. If the task is unsuccessful, a toast message is displayed.
                                                 * @param task the completed task
                                                 */
                                                public void onComplete(@NonNull Task<AuthResult> task) {
                                                    if (task.isSuccessful()) {
                                                        Map<String, Object> newUser = new HashMap<>();
                                                        newUser.put("username", uname);
                                                        newUser.put("email", email);
                                                        newUser.put("location", "");
                                                        newUser.put("phoneNumber", "");
                                                        db.collection("Users").document(auth.getCurrentUser().getUid()).set(newUser);
                                                        Toast.makeText(SignUpActivity.this, "Sign up successful", Toast.LENGTH_SHORT).show();
                                                        finish();
                                                    } else {
                                                        Toast.makeText(SignUpActivity.this, "Sign up failed: " + task.getException().getMessage(),
                                                                Toast.LENGTH_SHORT).show();
                                                        System.out.println("Sign up failed: " + task.getException().getMessage());
                                                    }
                                                }
                                            });
                                } else {
                                    Toast.makeText(SignUpActivity.this, "Error checking username: " + task.getException().getMessage(),
                                            Toast.LENGTH_SHORT).show();
                                    System.out.println("Error checking username: " + task.getException().getMessage());
                                }
                            }
                        });
            }
        });
    }
}

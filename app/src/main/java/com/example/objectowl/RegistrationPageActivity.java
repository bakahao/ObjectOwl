package com.example.objectowl;

import static android.content.ContentValues.TAG;

import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import android.content.Intent;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegistrationPageActivity extends AppCompatActivity {

    // Declare Firebase Auth
    private FirebaseAuth mAuth;
    EditText editTextEmail, editTextRePassword, editTextName, editTextPassword;
    Button registerButton;
    ImageButton backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        setContentView(R.layout.activity_registration_page);

        //initialize firebase
        FirebaseApp.initializeApp(RegistrationPageActivity.this);
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);


        editTextName = findViewById(R.id.editTextName);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        editTextRePassword = findViewById(R.id.editTextRePassword);
        registerButton = findViewById(R.id.registerButton);
        backButton = findViewById(R.id.backButton);

        // Set OnClickListener to the back icon button
        backButton.setOnClickListener(v -> finish()); // Close the current activity and go back to the previous one

        // Set OnClickListener to the register button
        registerButton.setOnClickListener(v -> {
            String email = editTextEmail.getText().toString();
            String rePassword = editTextRePassword.getText().toString();
            String password = editTextPassword.getText().toString();
            String name = editTextName.getText().toString();

            if (email.isEmpty() || rePassword.isEmpty() || password.isEmpty() || name.isEmpty()) {
                Toast.makeText(RegistrationPageActivity.this, "Please fill in all fields.", Toast.LENGTH_SHORT).show();
                return; // Exit the method if any field is empty
            }
            // Check if passwords match
            if (!password.equals(rePassword)) {
                Toast.makeText(RegistrationPageActivity.this, "Passwords do not match.", Toast.LENGTH_SHORT).show();
                return; // Exit the method if passwords do not match
            }

            // If all validations pass, proceed to create the account
            createAccount(email, password);
        });

        // Show/hide password in password text field
        EditText passwordText = findViewById(R.id.editTextPassword);
        ToggleButton toggleButton = findViewById(R.id.toggle_editpassword);
        toggleButton.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                passwordText.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            } else {
                passwordText.setTransformationMethod(PasswordTransformationMethod.getInstance());
            }
        });

        // Show/hide password in Re-enter password text field
        EditText checkPasswordText = findViewById(R.id.editTextRePassword);
        ToggleButton checkPasswordtoggleButton = findViewById(R.id.toggle_repassword);
        checkPasswordtoggleButton.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                checkPasswordText.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            } else {
                checkPasswordText.setTransformationMethod(PasswordTransformationMethod.getInstance());
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            reload();
        }
    }

    private void createAccount(String email, String password) {
        // Create a user with email and password
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();

                            // Get user's UID and save user details in Firebase Realtime Database
                            if (user != null) {
                                String userId = user.getUid();
                                String name = editTextName.getText().toString();
                                String email = editTextEmail.getText().toString();

                                // Save user details in Firebase Realtime Database
                                saveUserToDatabase(userId, name, email);

                                // Navigate to LoginPageActivity
                                Intent intent = new Intent(RegistrationPageActivity.this, LoginPageActivity.class);
                                startActivity(intent);
                                finish(); // Close the current activity
                            }
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(RegistrationPageActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void saveUserToDatabase(String userId, String name, String email) {
        // Initialize Firebase Database reference
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://objectowl-ad2b1-default-rtdb.asia-southeast1.firebasedatabase.app");
        DatabaseReference myRef = database.getReference("Users");

        // Log the reference URL to confirm the path
        Log.d(TAG, "Database Reference URL: " + myRef.child(userId).toString());

        // Create a new user object
        User newUser = new User(name, email);

        // Save the user under their UID in the database
        myRef.child(userId).setValue(newUser).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(RegistrationPageActivity.this, "Registration and data saved successfully.", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(RegistrationPageActivity.this, "Failed to save user data.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void reload() { }

}

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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.AuthResult;
public class RegistrationPageActivity extends AppCompatActivity {

    //declare auth
    private FirebaseAuth mAuth;
    private EditText editTextEmail, editTextRePassword, editTextName, editTextPassword;
    private Button registerButton;
    private ImageButton backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        setContentView(R.layout.activity_registration_page);

        editTextName = findViewById(R.id.editTextName);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        editTextRePassword = findViewById(R.id.editTextRePassword);
        registerButton = findViewById(R.id.registerButton);
        backButton = findViewById(R.id.backButton);

        // Set OnClickListener to the back icon button
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate back to LoginPage.java
                finish(); // Close the current activity and go back to the previous one
            }
        });

        // Set OnClickListener to the register button
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
            }
        });

        ////Show password in password text field
        EditText passwordText = findViewById(R.id.editTextPassword);
        ToggleButton toggleButton = findViewById(R.id.toggle_editpassword);

        toggleButton.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                // Show password
                passwordText.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            } else {
                // Hide password
                passwordText.setTransformationMethod(PasswordTransformationMethod.getInstance());
            }
        });

        //Show password in Re-enter password text field
        EditText checkPasswordText = findViewById(R.id.editTextRePassword);
        ToggleButton checkPasswordtoggleButton = findViewById(R.id.toggle_repassword);

        checkPasswordtoggleButton.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                // Show password
                checkPasswordText.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            } else {
                // Hide password
                checkPasswordText.setTransformationMethod(PasswordTransformationMethod.getInstance());
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            reload();
        }
    }

    private void createAccount(String email, String password) {
        // [START create_user_with_email]
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            Toast.makeText(RegistrationPageActivity.this, "Registration successful.", Toast.LENGTH_SHORT).show();

                            // Navigate to LoginPageActivity
                            Intent intent = new Intent(RegistrationPageActivity.this, LoginPageActivity.class);
                            startActivity(intent);
                            finish(); // Close the current activity
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(RegistrationPageActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                    }
                });
        // [END create_user_with_email]
    }

    private void reload() { }

    private void updateUI(FirebaseUser user) {

    }
}
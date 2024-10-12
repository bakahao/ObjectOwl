package com.example.objectowl;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Outline;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.LinkMovementMethod;
import android.text.method.PasswordTransformationMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LoginPageActivity extends AppCompatActivity {

    // Declare FirebaseAuth instance
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);

        // Initialize FirebaseAuth
        mAuth = FirebaseAuth.getInstance();

        // Set up "Register here" clickable and underlined
        TextView registerText = findViewById(R.id.register_text);
        SpannableString spannableString = new SpannableString(registerText.getText());
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                // Start the RegistrationPageActivity
                Intent intent = new Intent(LoginPageActivity.this, RegistrationPageActivity.class);
                startActivity(intent);
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(true);
                ds.setColor(ContextCompat.getColor(getApplicationContext(), R.color.orange));
            }
        };

        spannableString.setSpan(clickableSpan, 16, spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        registerText.setText(spannableString);
        registerText.setMovementMethod(LinkMovementMethod.getInstance());
        registerText.setHighlightColor(Color.TRANSPARENT);

        EditText passwordEditText = findViewById(R.id.password);
        ToggleButton toggleButton = findViewById(R.id.toggle_password);

        toggleButton.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                // Show password
                passwordEditText.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            } else {
                // Hide password
                passwordEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
            }
        });

        ImageView logo = findViewById(R.id.logo);
        logo.setClipToOutline(true);
        logo.setOutlineProvider(new ViewOutlineProvider() {
            @Override
            public void getOutline(View view, Outline outline) {
                outline.setRoundRect(0, 0, view.getWidth(), view.getHeight(), 16 * getResources().getDisplayMetrics().density);
            }
        });

        MaterialButton loginButton = findViewById(R.id.login_button);
        EditText emailEditText = findViewById(R.id.email);

        // Set up the login button click listener with input validation and Firebase Authentication
        loginButton.setOnClickListener(v -> {
            // Get the text from email and password fields
            String email = emailEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();

            // Validate email and password inputs
            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(LoginPageActivity.this, "Please fill in your Email and Password.", Toast.LENGTH_SHORT).show();
            } else {
                // Sign in with Firebase
                signInWithFirebase(email, password, emailEditText, passwordEditText);
            }
        });
    }

    private void signInWithFirebase(String email, String password, EditText emailEditText, EditText passwordEditText) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, navigate to HomePageActivity
                        FirebaseUser user = mAuth.getCurrentUser();

                        Intent intent = new Intent(LoginPageActivity.this, HomePageActivity.class);
                        startActivity(intent);
                        finish();  // Close the login page
                    } else {
                        // If sign in fails, display a message to the user
                        try {
                            throw task.getException();
                        } catch (Exception e) {
                            Toast.makeText(LoginPageActivity.this, "Email or Password is incorrect", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}

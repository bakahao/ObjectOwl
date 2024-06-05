package com.example.objectowl;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;


public class RegistrationPageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration_page);

        // Find the back icon button
        ImageButton backButton = findViewById(R.id.backButton);

        // Set OnClickListener to the back icon button
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate back to LoginPage.java
                finish(); // Close the current activity and go back to the previous one
            }
        });

        // Other initialization code if needed
    }
}
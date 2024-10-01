package com.example.objectowl;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

public class ClassifiedPage extends AppCompatActivity {

    ImageView classifiedImageView;
    TextView classifiedResult, classifiedConfidence;
    Button classifyButton;
    Bitmap image;
    MaterialButton householdButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_classified);

        classifiedImageView = findViewById(R.id.imageView);
        householdButton = findViewById(R.id.householdButton);

        // Get the image from the intent
        image = getIntent().getParcelableExtra("image");
        // Display the image on the screen
        classifiedImageView.setImageBitmap(image);

        // Set an OnClickListener to classify the image when the user clicks the household button
        householdButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                runHouseholdModelAndDisplayResult();
            }
        });
    }

    private void runHouseholdModelAndDisplayResult() {
        // Initialize TextViews for result and confidence (placeholders)
        TextView resultTextView = new TextView(this);
        TextView confidenceTextView = new TextView(this);

        // Call the HouseholdModel to classify the image
        HouseHoldModel.classifyImage(image, this, resultTextView, confidenceTextView, classifiedImageView);

        // Retrieve classification result and confidence
        String result = resultTextView.getText().toString();
        String confidence = confidenceTextView.getText().toString();

        // Pass the image, result, and confidence to ResultPage
        Intent intent = new Intent(ClassifiedPage.this, ResultPage.class);
        intent.putExtra("image", image);
        intent.putExtra("result", result);
        intent.putExtra("confidence", confidence);
        startActivity(intent);
    }
}

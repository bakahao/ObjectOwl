package com.example.objectowl;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

public class ClassifiedPage extends AppCompatActivity {

    ImageView classifiedImageView;
    Bitmap image;
    MaterialButton householdButton, animalButton, vehicleButton, fruitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_classified);

        classifiedImageView = findViewById(R.id.imageView);
        householdButton = findViewById(R.id.householdButton);
        animalButton = findViewById(R.id.animalButton);
        vehicleButton = findViewById(R.id.vehicleButton);
        fruitButton = findViewById(R.id.fruitButton);

        // Get the image from the intent
        image = getIntent().getParcelableExtra("image");
        // Display the image on the screen
        classifiedImageView.setImageBitmap(image);

        // Set an OnClickListener to classify the image when the user clicks the household button
        householdButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Call the HouseholdModel to classify the image
                runModelAndDisplayResult("household");
            }
        });

        // Set an OnClickListener to classify the image when the user clicks the animal button
        animalButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Call the AnimalModel to classify the image
                runModelAndDisplayResult("animal");
            }
        });

        // Set an OnClickListener to classify the image when the user clicks the vehicle button
        vehicleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Call the AnimalModel to classify the image
                runModelAndDisplayResult("vehicle");
            }
        });

        // Set an OnClickListener to classify the image when the user clicks the fruit button
        fruitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Call the AnimalModel to classify the image
                runModelAndDisplayResult("fruit");
            }
        });
    }

    private void runModelAndDisplayResult(String modelType) {
        // Initialize TextViews for result and confidence (placeholders)
        TextView resultTextView = new TextView(this);
        TextView confidenceTextView = new TextView(this);

        // Check which model to run
        if (modelType.equals("household")) {
            // Create an instance of HouseholdModel and call classifyImage
            HouseHoldModel houseHoldModel = new HouseHoldModel();
            houseHoldModel.classifyImage(image, this, resultTextView, confidenceTextView, classifiedImageView);
        } else if (modelType.equals("animal")) {
            // Create an instance of AnimalModel and call classifyImage
            AnimalModel animalModel = new AnimalModel();
            animalModel.classifyImage(image, this, resultTextView, confidenceTextView, classifiedImageView);
        } else if (modelType.equals("vehicle")) {
            // Create an instance of VehicleModel and call classifyImage
            VehicleModel vehicleModel = new VehicleModel();
            vehicleModel.classifyImage(image, this, resultTextView, confidenceTextView, classifiedImageView);
        } else if (modelType.equals("fruit")) {
            // Create an instance of FruitModel and call classifyImage
            FruitModel fruitModel = new FruitModel();
            fruitModel.classifyImage(image, this, resultTextView, confidenceTextView, classifiedImageView);
        }

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

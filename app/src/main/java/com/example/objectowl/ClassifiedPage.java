package com.example.objectowl;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ClassifiedPage extends AppCompatActivity {

    ImageView classifiedImageView;
    TextView classifiedResult, classifiedConfidence;
    Button classifyButton;
    Bitmap image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_classified);

        classifiedImageView = findViewById(R.id.imageView);
//        classifiedResult = findViewById(R.id.classifiedResult);
//        classifiedConfidence = findViewById(R.id.classifiedConfidence);
//        classifyButton = findViewById(R.id.classifyButton);

        // Get the image from the intent
        image = getIntent().getParcelableExtra("image");

        // Display the image on the screen
        classifiedImageView.setImageBitmap(image);

        // Set an OnClickListener to classify the image when the user clicks the button
//        classifyButton.setOnClickListener(v -> {
//            runClassification();
//        });
    }

//    private void runClassification() {
//        // Run the classification model on the image and display the results
//        String[] classificationResults = HouseholdModel.classifyImage(image, this);
//        classifiedResult.setText(classificationResults[0]); // Show result
//        classifiedConfidence.setText(classificationResults[1]); // Show confidence
//    }
}

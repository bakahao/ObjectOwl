package com.example.objectowl;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ResultPage extends AppCompatActivity {

    ImageView resultImageView, homeButton;
    TextView classifiedResult, classifiedConfidence;
    Bitmap image;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result_page);

        resultImageView = findViewById(R.id.resultImage);
        classifiedResult = findViewById(R.id.objectName);
        classifiedConfidence = findViewById(R.id.objectDetails);
        homeButton = findViewById(R.id.homeButton);

        image = getIntent().getParcelableExtra("image");
        String result = getIntent().getStringExtra("result");
        String confidence = getIntent().getStringExtra("confidence");

        // Display the image, result, and confidence on the screen
        resultImageView.setImageBitmap(image);
        classifiedResult.setText(result);
        classifiedConfidence.setText(confidence);

        homeButton.setOnClickListener(v -> {
            Intent intent = new Intent(ResultPage.this, HomePageActivity.class);
            startActivity(intent);
            finish(); // Close the current ResultPage activity
        });
    }
}

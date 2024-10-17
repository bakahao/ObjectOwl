package com.example.objectowl;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

public class HistoryResultPageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_result_page);

        ImageView resultImageView = findViewById(R.id.resultImage);
        TextView resultTextView = findViewById(R.id.objectName);
        TextView confidenceTextView = findViewById(R.id.objectDetails);

        // Retrieve the data from the intent
        Intent intent = getIntent();
        String result = intent.getStringExtra("result");
        String confidence = intent.getStringExtra("confidence");
        String imageUrl = intent.getStringExtra("imageUrl");

        // Set the text and load the image
        resultTextView.setText(result);
        confidenceTextView.setText(confidence);

        // Use Glide or Picasso to load the image from Firebase Storage using the URL
        Glide.with(this).load(imageUrl).into(resultImageView);
    }

}

package com.example.objectowl;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.firebase.auth.FirebaseUser;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

public class ClassifiedPage extends AppCompatActivity {

    ImageView classifiedImageView;
    Bitmap image;
    MaterialButton householdButton, animalButton, vehicleButton, fruitButton;
    FirebaseStorage storage;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_classified);

        classifiedImageView = findViewById(R.id.imageView);
        householdButton = findViewById(R.id.householdButton);
        animalButton = findViewById(R.id.animalButton);
        vehicleButton = findViewById(R.id.vehicleButton);
        fruitButton = findViewById(R.id.fruitButton);

        // Initialize Firebase Storage and Realtime Database references
        storage = FirebaseStorage.getInstance();
        databaseReference = FirebaseDatabase.getInstance("https://objectowl-ad2b1-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("History");

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

        //store detection data with the timestamp
        saveDetectionToFirebase(result);

        incrementObjectDetectionCount(result);

        saveScannedObjectToFirebase(result);

        //upload image and save metadata to firebase storage and realtime database
        uploadImageToFirebaseStorage(result, confidence);
    }

    private void uploadImageToFirebaseStorage(String recognizedObject, String description) {
        // Create a reference to the Firebase Storage location
        StorageReference storageRef = storage.getReferenceFromUrl("gs://objectowl-ad2b1.appspot.com").child("images/" + System.currentTimeMillis() + ".jpg");

        // Convert bitmap to byte array
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageData = baos.toByteArray();

        // Upload image to Firebase Storage
        UploadTask uploadTask = storageRef.putBytes(imageData);
        uploadTask.addOnSuccessListener(taskSnapshot -> {
            // Once the image is uploaded, get the download URL
            storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                String downloadUrl = uri.toString();
                // Save metadata (recognized object, description, and image URL) to Firebase Realtime Database
                saveMetadataToFirebase(recognizedObject, description, downloadUrl);
            });
        }).addOnFailureListener(e -> {
            // Handle any errors during upload
            e.printStackTrace();
        });
    }

    private void saveMetadataToFirebase(String recognizedObject, String description, String imageUrl) {
        // Get the current user's UID (make sure the user is authenticated)
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            // Handle the case where the user is not logged in
            System.out.println("Error: User not authenticated.");
            return;
        }
        String userUID = currentUser.getUid();  // Get the unique user ID

        // Create a unique ID for each history entry under the user UID
        String historyId = databaseReference.child(userUID).push().getKey();

        // Create a map to store the history data
        Map<String, Object> historyData = new HashMap<>();
        historyData.put("objectName", recognizedObject);
        historyData.put("description", description);
        historyData.put("imageUrl", imageUrl);
        historyData.put("timestamp", System.currentTimeMillis());

        // Save the history data to Firebase Realtime Database under "History -> userUID -> historyId"
        if (historyId != null) {
            databaseReference.child(userUID).child(historyId).setValue(historyData).addOnSuccessListener(aVoid -> {
                // Once the data is saved, navigate to the ResultPage with the image, result, and confidence
                navigateToResultPage(recognizedObject, description, image);
            }).addOnFailureListener(e -> {
                // Handle errors if data saving fails
                e.printStackTrace();
            });
        }
    }

    //Save Detection Data with Timestamp to Firebase
    private void saveDetectionToFirebase(String recognizedObject) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            System.out.println("User not authenticated.");
            return;
        }
        String userUID = currentUser.getUid();  // Get current user ID
        long timestamp = System.currentTimeMillis();  // Get current timestamp

        // Create a map to store the detection data
        Map<String, Object> detectionData = new HashMap<>();
        detectionData.put("objectName", recognizedObject);
        detectionData.put("timestamp", timestamp);

        // Save the detection data under "Detections -> userUID"
        DatabaseReference detectionRef = FirebaseDatabase.getInstance("https://objectowl-ad2b1-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("Detections").child(userUID).push();
        detectionRef.setValue(detectionData);
    }

    // Add this method to ClassifiedPage.java
    public void displayWeeklyDetections(final TextView weeklyCountTextView) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            System.out.println("User not authenticated.");
            return;
        }

        String userUID = currentUser.getUid();  // Get current user ID

        // Get the start of the current week (7 days ago)
        long oneWeekAgo = System.currentTimeMillis() - (7 * 24 * 60 * 60 * 1000); // 7 days in milliseconds

        // Reference to the detections node for the current user
        DatabaseReference detectionRef = FirebaseDatabase.getInstance("https://objectowl-ad2b1-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference("Detections").child(userUID);

        detectionRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int weeklyCount = 0;  // Count of detections this week

                // Iterate through all detections for the current user
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    long timestamp = snapshot.child("timestamp").getValue(Long.class);

                    // Check if the detection occurred within the past 7 days
                    if (timestamp >= oneWeekAgo) {
                        weeklyCount++;  // Increment the count if within the past week
                    }
                }

                // Update the TextView with the number of objects detected this week
                weeklyCountTextView.setText("Number of objects detected (this week): " + weeklyCount);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                System.out.println("Error retrieving data: " + databaseError.getMessage());
            }
        });
    }

    private void incrementObjectDetectionCount(String recognizedObject) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            System.out.println("User not authenticated.");
            return;
        }
        String userUID = currentUser.getUid();  // Get current user ID

        // Reference to the object count in the Realtime Database
        DatabaseReference objectCountRef = FirebaseDatabase.getInstance("https://objectowl-ad2b1-default-rtdb.asia-southeast1.firebasedatabase.app")
                .getReference("ObjectCounts").child(userUID).child(recognizedObject);

        // Increment the count of the recognized object
        objectCountRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                long currentCount = 0;
                if (dataSnapshot.exists()) {
                    currentCount = dataSnapshot.getValue(Long.class);  // Get current count
                }
                objectCountRef.setValue(currentCount + 1);  // Increment count by 1
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                System.out.println("Error updating object count: " + databaseError.getMessage());
            }
        });
    }

    private void saveScannedObjectToFirebase(final String objectName) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            System.out.println("User not authenticated.");
            return;
        }
        String userUID = currentUser.getUid();  // Get current user ID

        // Reference to ScannedObject node for the user
        DatabaseReference scannedObjectRef = FirebaseDatabase.getInstance("https://objectowl-ad2b1-default-rtdb.asia-southeast1.firebasedatabase.app")
                .getReference("ScannedObject").child(userUID);

        // Check if the object name already exists
        scannedObjectRef.child(objectName).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    // Object does not exist, so save it
                    scannedObjectRef.child(objectName).setValue(true);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                System.out.println("Error checking object: " + databaseError.getMessage());
            }
        });
    }


    private void navigateToResultPage(String result, String confidence, Bitmap image) {
        // Create an intent to pass the result, confidence, and image to ResultPage
        Intent intent = new Intent(ClassifiedPage.this, ResultPage.class);
        intent.putExtra("result", result);
        intent.putExtra("confidence", confidence);
        intent.putExtra("image", image);
        startActivity(intent);
    }



}
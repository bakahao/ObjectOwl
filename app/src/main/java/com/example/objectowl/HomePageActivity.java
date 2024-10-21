package com.example.objectowl;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.service.controls.templates.ThumbnailTemplate;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import android.Manifest;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class HomePageActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;
    private NavigationView navigationView;
    ImageButton cameraButton;
    private static final int CAMERA_REQUEST_CODE = 100;
    LinearLayout historyLayout;
    DatabaseReference historyRef;
    FirebaseAuth auth;
    FirebaseUser currentUser;
    TextView weeklyCountTextView, mostDetectedObjectTextView;
    ImageView lockedMilestoneImage, unlockedMilestoneImage, tenLockedMilestoneImage, tenUnlockedMilestoneImage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        // Initialize the DrawerLayout and NavigationView
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);
        cameraButton = findViewById(R.id.cameraButton);
        historyLayout = findViewById(R.id.historyLayout);
        weeklyCountTextView = findViewById(R.id.numberOfObjectsDetected);
        mostDetectedObjectTextView = findViewById(R.id.mostCommonlyDetectedObject);
        lockedMilestoneImage = findViewById(R.id.lockedMilestoneImage);
        unlockedMilestoneImage = findViewById(R.id.unlockedMilestoneImage);
        tenLockedMilestoneImage = findViewById(R.id.ten_lockedMilestoneImage);
        tenUnlockedMilestoneImage = findViewById(R.id.ten_unlockedMilestoneImage);

        // Set onClickListener for locked milestone
        lockedMilestoneImage.setOnClickListener(v -> showMilestoneDetails(false, "first_milestone"));
        unlockedMilestoneImage.setOnClickListener(v -> showMilestoneDetails(true, "first_milestone"));

        tenLockedMilestoneImage.setOnClickListener(v -> showMilestoneDetails(false, "ten_milestone"));
        tenUnlockedMilestoneImage.setOnClickListener(v -> showMilestoneDetails(true, "ten_milestone"));

        // Initialize Firebase Authentication and get the current user
        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();

        // Initialize Firebase Realtime Database reference for "History" node
        historyRef = FirebaseDatabase.getInstance("https://objectowl-ad2b1-default-rtdb.asia-southeast1.firebasedatabase.app")
                .getReference("History");

        // Check if the user is authenticated before proceeding
        if (currentUser != null) {
            // Get the current user UID
            String userUID = currentUser.getUid();

            // Initialize Firebase Realtime Database reference for "History -> userUID"
            historyRef = FirebaseDatabase.getInstance("https://objectowl-ad2b1-default-rtdb.asia-southeast1.firebasedatabase.app")
                    .getReference("History").child(userUID);

            // Call method to load history and create buttons
            loadHistoryFromFirebase();
        } else {
            // Handle case where the user is not authenticated
            System.out.println("Error: User not authenticated.");
        }

        ClassifiedPage classifiedPage = new ClassifiedPage();
        classifiedPage.displayWeeklyDetections(weeklyCountTextView);
        displayMostCommonlyDetectedObject();

        //check milestone
        checkForFirstMilestoneAchievement();
        checkTenObjectMilestone();

        cameraButton.setOnClickListener(v -> handleRecognizeClick());

        // Setup ActionBarDrawerToggle
        toggle = new ActionBarDrawerToggle(this, drawerLayout,
                R.string.open_drawer, R.string.close_drawer);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // Handle menu button click to open the drawer
        ImageButton menuButton = findViewById(R.id.menuButton);
        menuButton.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));

        // Initialize and set click listeners for each navigation item
        MenuItem homeItem = navigationView.getMenu().findItem(R.id.nav_home);
        homeItem.setOnMenuItemClickListener(item -> {
            handleHomeClick();
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });

        MenuItem recognizeItem = navigationView.getMenu().findItem(R.id.nav_recognize);
        recognizeItem.setOnMenuItemClickListener(item -> {
            handleRecognizeClick();
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });

        MenuItem guideItem = navigationView.getMenu().findItem(R.id.nav_guide);
        guideItem.setOnMenuItemClickListener(item -> {
            handleGuideClick();
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });

        MenuItem supportItem = navigationView.getMenu().findItem(R.id.nav_support);
        supportItem.setOnMenuItemClickListener(item -> {
            handleSupportClick();
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });

        MenuItem logoutItem = navigationView.getMenu().findItem(R.id.nav_logout);
        logoutItem.setOnMenuItemClickListener(item -> {
            handleLogoutClick();
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            // Close the drawer if it's open
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            // Handle the back press normally
            super.onBackPressed();
        }
    }

    // Methods to handle clicks for each menu item
    private void handleHomeClick() {
        // TODO: Add your action for the Home button here
    }

    private void handleRecognizeClick() {
        // Check if the camera permission is granted
        if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            //Start Camera intent if permission is granted
            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(cameraIntent, 1);
        } else {
            //Request camera permission if we don't have it.
            requestPermissions(new String[]{Manifest.permission.CAMERA}, 100);
        }
    }

    // Override onRequestPermissionsResult to handle the user's response to the permission request
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == CAMERA_REQUEST_CODE) {
            // Check if the permission was granted
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission was granted, so launch the camera
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, 1);
            }
        }
    }

    private void handleGuideClick() {
        // Navigate to User Guide Page
        Intent intent = new Intent(HomePageActivity.this, UserGuidePageActivity.class);
        startActivity(intent);
        finish(); // Close the HomePageActivity to prevent returning with the back button
    }

    private void handleSupportClick() {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:customerSupport_ObjectOwl@gmail.com")); // Replace with your support email

        // Add email addresses, subject, and body (optional)
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"customerSupport_ObjectOwl@gmail.com"}); // Replace with your support email
        intent.putExtra(Intent.EXTRA_SUBJECT, "Support Request");
        intent.putExtra(Intent.EXTRA_TEXT, "Hello, I need help with...");

        // Set Gmail package to open Gmail directly
        intent.setPackage("com.google.android.gm");

        // Check if Gmail is installed
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        } else {
            // Fallback to chooser if Gmail is not installed
            Intent chooserIntent = Intent.createChooser(intent, "Send Email");
            startActivity(chooserIntent);
        }
    }

    private void handleLogoutClick() {
        // Navigate to LoginPageActivity when logout is clicked
        Intent intent = new Intent(HomePageActivity.this, LoginPageActivity.class);
        startActivity(intent);
        finish(); // Close the HomePageActivity to prevent returning with the back button
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 1 && resultCode == RESULT_OK) {
            // Get the image taken by the user
            Bitmap image = (Bitmap) data.getExtras().get("data");

            // Crop the image to fit TensorFlow Lite dimension
            int dimension = Math.min(image.getWidth(), image.getHeight());
            image = ThumbnailUtils.extractThumbnail(image, dimension, dimension);

            // Navigate to ClassifiedPage with the image (no classification yet)
            navigateToClassifiedPage(image);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void navigateToClassifiedPage(Bitmap image) {
        Intent intent = new Intent(HomePageActivity.this, ClassifiedPage.class);
        intent.putExtra("image", image); // Send image to ClassifiedPage
        startActivity(intent);
    }

    private void loadHistoryFromFirebase() {
        // Retrieve all history data from Firebase
        historyRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Clear the LinearLayout to avoid duplications
                historyLayout.removeAllViews();

                // Iterate through each history entry
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    // Get the object name, description, and imageUrl from the database
                    String objectName = snapshot.child("objectName").getValue(String.class);
                    String description = snapshot.child("description").getValue(String.class);
                    String imageUrl = snapshot.child("imageUrl").getValue(String.class);

                    // Create a button for each history entry
                    Button historyButton = new Button(HomePageActivity.this);
                    historyButton.setText(objectName);  // Set the object name as button text
                    historyButton.setGravity(Gravity.START | Gravity.CENTER_VERTICAL);  // Align text to the start, keep vertical centering
                    historyButton.setBackgroundResource(R.drawable.history_button);

                    // Create layout parameters for the button with margin
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,  // Width
                            LinearLayout.LayoutParams.WRAP_CONTENT   // Height
                    );
                    params.setMargins(0, 16, 0, 16);
                    historyButton.setLayoutParams(params);
                    historyLayout.addView(historyButton);  // Add the button to the LinearLayout

                    // Set click listener to show the details when the button is clicked
                    historyButton.setOnClickListener(v -> {
                        // When the button is clicked, navigate to ResultPage or show the image/details
                        navigateToHistoryResultPage(objectName, description, imageUrl);
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle any errors while loading data
                System.out.println("Error loading history data: " + databaseError.getMessage());
            }
        });
    }

    private void displayMostCommonlyDetectedObject() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            System.out.println("User not authenticated.");
            return;
        }
        String userUID = currentUser.getUid();  // Get current user ID

        // Reference to the ObjectCounts node for the current user
        DatabaseReference objectCountsRef = FirebaseDatabase.getInstance("https://objectowl-ad2b1-default-rtdb.asia-southeast1.firebasedatabase.app")
                .getReference("ObjectCounts").child(userUID);

        objectCountsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String mostCommonObject = null;
                long highestCount = 0;

                // Iterate through all detected objects and find the one with the highest count
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String objectName = snapshot.getKey();  // The object's name
                    long count = snapshot.getValue(Long.class);  // The object's detection count

                    if (count > highestCount) {
                        highestCount = count;
                        mostCommonObject = objectName;
                    }
                }

                // Display the most commonly detected object
                if (mostCommonObject != null) {
                    mostDetectedObjectTextView.setText("Most Commonly Detected Object: " + mostCommonObject);
                } else {
                    mostDetectedObjectTextView.setText("Most Commonly Detected Object: None");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                System.out.println("Error retrieving object counts: " + databaseError.getMessage());
            }
        });
    }

    private void checkForFirstMilestoneAchievement() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            System.out.println("User not authenticated.");
            return;
        }
        String userUID = currentUser.getUid();  // Get current user ID

        // Reference to ScannedObject node for the user
        DatabaseReference scannedObjectRef = FirebaseDatabase.getInstance("https://objectowl-ad2b1-default-rtdb.asia-southeast1.firebasedatabase.app")
                .getReference("ScannedObject").child(userUID);

        // Set up a ValueEventListener to listen for changes in ScannedObject
        scannedObjectRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Check if any object exists in ScannedObject
                if (dataSnapshot.exists()) {
                    // At least one object exists, unlock the milestone
                    unlockFirstMilestone();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                System.out.println("Error checking for milestone: " + databaseError.getMessage());
            }
        });
    }


    private void unlockFirstMilestone() {
        // Find the milestone images in HomePageActivity

        if (lockedMilestoneImage == null || unlockedMilestoneImage == null) {
            Log.e("ScannedObject", "Milestone images not found in activity_home_page.xml");
            return; // Exit if the views are not found
        }

        // Hide the locked milestone image and show the unlocked one
        lockedMilestoneImage.setVisibility(View.GONE);
        unlockedMilestoneImage.setVisibility(View.VISIBLE);
    }

    private void checkTenObjectMilestone() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) return;

        String userUID = currentUser.getUid();
        DatabaseReference scannedObjectRef = FirebaseDatabase.getInstance("https://objectowl-ad2b1-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("ScannedObject").child(userUID);

        scannedObjectRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // If ScannedObject exists, count the number of unique objects
                    long scannedCount = dataSnapshot.getChildrenCount(); // Number of unique scanned objects

                    if (scannedCount >= 10) {
                        // Unlock the 10-object milestone
                        tenLockedMilestoneImage.setVisibility(View.GONE);  // Hide locked milestone image
                        tenUnlockedMilestoneImage.setVisibility(View.VISIBLE);  // Show unlocked milestone image
                    }
                } else {
                    // ScannedObject does not exist, meaning no objects have been scanned yet
                    tenLockedMilestoneImage.setVisibility(View.VISIBLE);  // Show locked milestone image
                    tenUnlockedMilestoneImage.setVisibility(View.GONE);  // Hide unlocked milestone image
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
                System.out.println("Error checking scanned objects: " + error.getMessage());
            }
        });
    }



    private void showMilestoneDetails(boolean isUnlocked, String milestoneType) {
        // Create an AlertDialog builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // Set title
        builder.setTitle("Milestone Details");

        // Display different messages based on the milestone type and its locked/unlocked state
        if (isUnlocked) {
            // Display unlocked message for specific milestone
            switch (milestoneType) {
                case "first_milestone":
                    builder.setMessage("Yay! You did it! You’ve unlocked your first achievement by scanning your very first item! " +
                            "You're opening a whole new world of fun and learning. Keep going, superstar—you’re amazing!");
                    break;
                case "ten_milestone":
                    builder.setMessage("Wow, you're incredible! You've recognized 10 different objects! Your curiosity and sharp thinking are opening up a whole new world of discovery. " +
                            "Keep up the amazing work—you're a superstar at learning and exploring!");
                    break;
                // Add more milestones here as needed
                default:
                    builder.setMessage("Milestone unlocked!");
                    break;
            }
        } else {
            // Display locked message for specific milestone
            switch (milestoneType) {
                case "first_milestone":
                    builder.setMessage("Locked Milestone. Scan at least 1 object to unlock this achievement!");
                    break;
                case "ten_milestone":
                    builder.setMessage("Locked Milestone. Scan 10 types of objects to unlock this achievement!");
                    break;
                // Add more milestones here as needed
                default:
                    builder.setMessage("This milestone is locked. Keep scanning to unlock!");
                    break;
            }
        }

        // Set a positive button to dismiss the dialog
        builder.setPositiveButton("Okay", (dialog, which) -> dialog.dismiss());

        // Create and show the dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }



    private void navigateToHistoryResultPage(String objectName, String description, String imageUrl) {
        // Navigate to ResultPage with the retrieved animal name, description, and image URL
        Intent intent = new Intent(HomePageActivity.this, HistoryResultPageActivity.class);
        intent.putExtra("result", objectName);
        intent.putExtra("confidence", description);
        intent.putExtra("imageUrl", imageUrl);
        startActivity(intent);
    }
}
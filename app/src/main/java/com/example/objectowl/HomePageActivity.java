package com.example.objectowl;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.provider.MediaStore;
import android.service.controls.templates.ThumbnailTemplate;
import android.view.MenuItem;
import android.widget.ImageButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.objectowl.ml.HouseHoldModelV1;
import com.google.android.material.navigation.NavigationView;
import android.Manifest;
import android.widget.ImageView;
import android.widget.TextView;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class HomePageActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;
    private NavigationView navigationView;
    TextView result, confidence;
    ImageView imageView;
    int imageSize = 224;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        // Initialize the DrawerLayout and NavigationView
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);

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

    private void handleGuideClick() {
        // TODO: Add your action for the User Guide button here
        // Navigate to User Guide Page
        Intent intent = new Intent(HomePageActivity.this, UserGuidePageActivity.class);
        startActivity(intent);
        finish(); // Close the HomePageActivity to prevent returning with the back button
    }

    private void handleSupportClick() {
        // TODO: Add your action for the Customer Support button here
    }

    private void handleLogoutClick() {
        // Navigate to LoginPageActivity when logout is clicked
        Intent intent = new Intent(HomePageActivity.this, LoginPageActivity.class);
        startActivity(intent);
        finish(); // Close the HomePageActivity to prevent returning with the back button
    }

    //Household model
    public void classifyImage(Bitmap image){
        try {
            HouseHoldModelV1 model = HouseHoldModelV1.newInstance(getApplicationContext());

            // Creates inputs for reference.
            TensorBuffer inputFeature0 = TensorBuffer.createFixedSize(new int[]{1, 224, 224, 3}, DataType.FLOAT32);
            ByteBuffer byteBuffer = ByteBuffer.allocateDirect(4 * imageSize * imageSize * 3);
            byteBuffer.order(ByteOrder.nativeOrder());

            int [] intValues = new int[imageSize * imageSize];
            image.getPixels(intValues, 0, image.getWidth(), 0, 0, image.getWidth(), image.getHeight());
            int pixel = 0;
            for(int i = 0; i < imageSize; i++){
                for(int j = 0; j< imageSize; j++){
                    int val = intValues[pixel++];
                    byteBuffer.putFloat(((val >> 16) & 0xFF) * (1.f / 255.f));
                    byteBuffer.putFloat(((val >> 8) & 0xFF) * (1.f / 255.f));
                    byteBuffer.putFloat((val & 0xFF) * (1.f / 255.f));
                }
            }

            inputFeature0.loadBuffer(byteBuffer);

            // Runs model inference and gets result.
            HouseHoldModelV1.Outputs outputs = model.process(inputFeature0);
            TensorBuffer outputFeature0 = outputs.getOutputFeature0AsTensorBuffer();

            //find highest confidence value of the scanned image
            float[] confidences = outputFeature0.getFloatArray();
            int maxPos = 0;
            float maxConfidence = 0;
            for(int i =0; i < confidences.length; i++){
                if(confidences[i] > maxConfidence){
                    maxConfidence = confidences[i];
                    maxPos = i;
                }
            }
            String[] classes = {"Table", "Chair", "Sofa", "Bed", "Television",
                                "Lamp", "Fan", "Cup", "Plate", "Spoon",
                                "Fork", "Knife", "Toothbrush", "Towel", "Fridge",
                                "Remote Control", "Clock", "Mirror"};

            result.setText(classes[maxPos]);

            //show the confidence for all classes
            String s = "";
            for(int i = 0; i < classes.length; i++){
                s += String.format("%s: %.1f%%\n", classes[i], confidences[i] * 100);
            }
            confidence.setText(s);

            // Releases model resources if no longer used.
            model.close();
        } catch (IOException e) {
            // TODO Handle the exception
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 1 && resultCode == RESULT_OK) {
            //get the image taken by user
            Bitmap image = (Bitmap) data.getExtras().get("data");

            //crop the image to fix tensorflow lite dimension
            int dimension = Math.min(image.getWidth(), image.getHeight());
            image = ThumbnailUtils.extractThumbnail(image, dimension, dimension);
            imageView.setImageBitmap(image);

            image = Bitmap.createScaledBitmap(image, imageSize, imageSize, false);
            classifyImage(image);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
package com.example.objectowl;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.navigation.NavigationView;

public class UserGuidePageActivity extends AppCompatActivity {

    private boolean isContentVisible = false, isContentVisible1 = false;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;
    private NavigationView navigationView;
    private MaterialButton textButton, textButton1;
    private LinearLayout slidingContent, slidingContent1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_guide_page);

        // Initialize the DrawerLayout and NavigationView
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);
        textButton = findViewById(R.id.text_button);
        slidingContent = findViewById(R.id.sliding_content);
        textButton1 = findViewById(R.id.text_button1);
        slidingContent1 = findViewById(R.id.sliding_content1);

        textButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isContentVisible) {
                    // Slide up and hide content
                    slidingContent.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_up));
                    slidingContent.setVisibility(View.GONE);
                } else {
                    // Slide down and show content
                    slidingContent.setVisibility(View.VISIBLE);
                    slidingContent.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_down));
                }
                isContentVisible = !isContentVisible;
            }
        });

        textButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isContentVisible1) {
                    // Slide up and hide content
                    slidingContent1.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_up));
                    slidingContent1.setVisibility(View.GONE);
                } else {
                    // Slide down and show content
                    slidingContent1.setVisibility(View.VISIBLE);
                    slidingContent1.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_down));
                }
                isContentVisible1 = !isContentVisible1;
            }
        });

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
        Intent intent = new Intent(UserGuidePageActivity.this, HomePageActivity.class);
        startActivity(intent);
        finish(); // Close the HomePageActivity to prevent returning with the back button
    }

    private void handleRecognizeClick() {
        // TODO: Add your action for the Recognize Object button here
    }

    private void handleGuideClick() {
        // TODO: Add your action for the User Guide button here
        Intent intent = new Intent(UserGuidePageActivity.this, UserGuidePageActivity.class);
        startActivity(intent);
        finish(); // Close the HomePageActivity to prevent returning with the back button
    }

    private void handleSupportClick() {
        // TODO: Add your action for the Customer Support button here
    }

    private void handleLogoutClick() {
        // Navigate to LoginPageActivity when logout is clicked
        Intent intent = new Intent(UserGuidePageActivity.this, LoginPageActivity.class);
        startActivity(intent);
        finish(); // Close the HomePageActivity to prevent returning with the back button
    }

}

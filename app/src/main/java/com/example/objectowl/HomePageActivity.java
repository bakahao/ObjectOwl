package com.example.objectowl;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageButton;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import com.google.android.material.navigation.NavigationView;

public class HomePageActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;
    private NavigationView navigationView;

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
        // TODO: Add your action for the Recognize Object button here
    }

    private void handleGuideClick() {
        // TODO: Add your action for the User Guide button here
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
}

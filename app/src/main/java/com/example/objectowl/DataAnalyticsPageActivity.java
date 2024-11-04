package com.example.objectowl;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;

public class DataAnalyticsPageActivity extends AppCompatActivity {

    ImageButton returnButton;
    PieChart pieChart;
    DatabaseReference objectCountsRef;
    FirebaseAuth auth;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_analytics_page);

        // Initialize the return button and PieChart
        returnButton = findViewById(R.id.returnButton);
        pieChart = findViewById(R.id.pieChart);

        // Initialize Firebase Authentication and get the current user
        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();

        // Set the click listener for the return button
        returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate back to HomePageActivity
                Intent intent = new Intent(DataAnalyticsPageActivity.this, HomePageActivity.class);
                startActivity(intent);
                finish();
            }
        });

        // Check if the user is authenticated
        if (currentUser != null) {
            // Get the current user UID
            String userUID = currentUser.getUid();

            // Initialize Firebase Realtime Database reference for "ObjectCounts -> userUID"
            objectCountsRef = FirebaseDatabase.getInstance("https://objectowl-ad2b1-default-rtdb.asia-southeast1.firebasedatabase.app")
                    .getReference("ObjectCounts").child(userUID);

            // Load the object counts from Firebase and display the pie chart
            loadObjectCountsFromFirebase();
        }
    }

    private void loadObjectCountsFromFirebase() {
        objectCountsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<PieEntry> pieEntries = new ArrayList<>();

                // Iterate through all detected objects and their counts
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String objectName = snapshot.getKey();  // The object's name
                    long count = snapshot.getValue(Long.class);  // The object's detection count

                    // Add the object name and count to the PieEntry list
                    pieEntries.add(new PieEntry(count, objectName));
                }

                // Create a PieDataSet with the pieEntries
                PieDataSet dataSet = new PieDataSet(pieEntries, "Detected Objects Summary");
                dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
                dataSet.setValueTextSize(18f);  

                // Create PieData and set it to the PieChart
                PieData pieData = new PieData(dataSet);
                pieData.setValueTextSize(18f);  // Set the text size for the value labels
                pieData.setValueTextColor(getResources().getColor(R.color.black));

                // Apply data to the chart
                pieChart.setData(pieData);

                // Set Legend label if needed
                pieChart.getLegend().setForm(Legend.LegendForm.CIRCLE);
                pieChart.getLegend().setTextSize(12f);
                pieChart.getLegend().setEnabled(true);

                // Force chart to refresh
                pieChart.invalidate();  // Refresh the chart with new data
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle possible errors
                System.out.println("Error loading object counts: " + databaseError.getMessage());
            }
        });
    }
}

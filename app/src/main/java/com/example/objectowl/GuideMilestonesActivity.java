package com.example.objectowl;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import java.io.InputStream;

public class GuideMilestonesActivity extends AppCompatActivity {

    TextView descriptionTextView;
    ImageButton returnButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide_milestones);

        // Find the TextViews and Button by their IDs
        descriptionTextView = findViewById(R.id.description);
        returnButton = findViewById(R.id.returnButton);

        // Call the method to read from Excel and set the description text
        String description1 = readDescriptionFromExcel(this, 3); // Read first description

        descriptionTextView.setText(description1);  // Set first description

        // Set the onClickListener for the returnButton
        returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate back to UserGuidePageActivity
                Intent intent = new Intent(GuideMilestonesActivity.this, UserGuidePageActivity.class);
                startActivity(intent);
                finish(); // Close Guide1Activity to prevent returning with the back button
            }
        });
    }

    // Method to read the description from the Excel file in the assets folder
    public String readDescriptionFromExcel(Context context, int rowIndex) {
        String description = "";
        try {
            // Access the assets folder
            AssetManager assetManager = context.getAssets();
            InputStream inputStream = assetManager.open("user_guide.xlsx");

            // Open the Excel file using Apache POI
            Workbook workbook = WorkbookFactory.create(inputStream);
            Sheet sheet = workbook.getSheetAt(0);

            // Get the row based on the index and the second column (index 1) for the description
            Row row = sheet.getRow(rowIndex); // Use rowIndex to read different rows
            Cell cell = row.getCell(1); // Column index starts from 0, so 1 is the second column

            // Extract the description text from the cell
            description = cell.getStringCellValue();

            // Close the workbook and input stream
            workbook.close();
            inputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return description;
    }
}

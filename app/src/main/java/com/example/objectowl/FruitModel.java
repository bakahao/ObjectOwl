package com.example.objectowl;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.objectowl.ml.FruitModelV1;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.tensorflow.lite.DataType;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class FruitModel {

    private static final int imageSize = 224; // Make sure this matches the input size your model expects

    public static void classifyImage(Bitmap image, Context context, TextView result, TextView confidence, ImageView imageView) {
        try {
            // Load the TensorFlow Lite model
            FruitModelV1 model = FruitModelV1.newInstance(context);

            // Resize the image to the expected input size (224x224)
            Bitmap resizedImage = ThumbnailUtils.extractThumbnail(image, imageSize, imageSize);

            // Preprocess the image (convert it into ByteBuffer and normalize it)
            ByteBuffer byteBuffer = preprocessImage(resizedImage);

            // Create input buffer for the model
            TensorBuffer inputFeature0 = TensorBuffer.createFixedSize(new int[]{1, imageSize, imageSize, 3}, DataType.FLOAT32);
            inputFeature0.loadBuffer(byteBuffer);

            // Run the model
            FruitModelV1.Outputs outputs = model.process(inputFeature0);
            TensorBuffer outputFeature0 = outputs.getOutputFeature0AsTensorBuffer();

            // Get the confidence scores from the model
            float[] confidences = outputFeature0.getFloatArray();

            // Find the class with the highest confidence
            int maxPos = getMaxConfidencePos(confidences);

            // The labels for your classes (ensure this matches your model's output classes)
            String[] classes = {"Apple", "Banana", "Orange", "Grapes", "Strawberry",
                    "Blueberry", "Mango", "Pineapple", "Watermelon", "Pear"};

            // Set the result in the TextView
            result.setText(classes[maxPos]);

            // Display confidence levels for all classes
            displayConfidences(context, classes[maxPos], confidence);

            // Set the resized image in the ImageView (for displaying)
            imageView.setImageBitmap(resizedImage);

            // Close the model when done
            model.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // This method preprocesses the image: resize and normalize pixel values
    private static ByteBuffer preprocessImage(Bitmap image) {
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(4 * imageSize * imageSize * 3);
        byteBuffer.order(ByteOrder.nativeOrder());

        int[] intValues = new int[imageSize * imageSize];
        image.getPixels(intValues, 0, image.getWidth(), 0, 0, image.getWidth(), image.getHeight());

        int pixel = 0;
        for (int i = 0; i < imageSize; i++) {
            for (int j = 0; j < imageSize; j++) {
                int val = intValues[pixel++];
                // Normalize the pixel values to [-1, 1] based on Teachable Machine's typical behavior
                byteBuffer.putFloat((((val >> 16) & 0xFF) / 127.5f) - 1.0f); // Red
                byteBuffer.putFloat((((val >> 8) & 0xFF) / 127.5f) - 1.0f);  // Green
                byteBuffer.putFloat(((val & 0xFF) / 127.5f) - 1.0f);         // Blue
            }
        }
        return byteBuffer;
    }

    // Find the position of the class with the highest confidence score
    private static int getMaxConfidencePos(float[] confidences) {
        int maxPos = 0;
        float maxConfidence = 0;
        for (int i = 0; i < confidences.length; i++) {
            if (confidences[i] > maxConfidence) {
                maxConfidence = confidences[i];
                maxPos = i;
            }
        }
        return maxPos;
    }

    //display the description of the recognized animal
    private static void displayConfidences(Context context, String recognizedAnimal, TextView confidence) {
        try {
            // Get the asset manager
            AssetManager assetManager = context.getAssets();

            // Open the Excel file from the assets folder
            InputStream fis = assetManager.open("fruit_des.xlsx");

            // Create a workbook and get the first sheet
            Workbook workbook = WorkbookFactory.create(fis);
            Sheet sheet = workbook.getSheetAt(0); // Assuming the descriptions are in the first sheet

            // Loop through the rows of the sheet
            for (Row row : sheet) {
                String fruitName = row.getCell(0).getStringCellValue(); // Assuming animal names are in the first column
                String description = row.getCell(1).getStringCellValue(); // Assuming descriptions are in the second column

                // If the recognized animal matches the animal name in the Excel file, display the description
                if (fruitName.equalsIgnoreCase(recognizedAnimal)) {
                    confidence.setText(description); // Set the description in the TextView
                    break; // Exit the loop once the description is found
                }
            }

            // Close the file input stream and workbook
            fis.close();
            workbook.close();

        } catch (IOException e) {
            e.printStackTrace();
            confidence.setText("Error loading description"); // Error message if something goes wrong
        } catch (Exception e) {
            e.printStackTrace();
            confidence.setText("Error processing file");
        }
    }
}

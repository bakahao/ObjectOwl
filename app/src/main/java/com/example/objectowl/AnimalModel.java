package com.example.objectowl;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.objectowl.ml.AnimalModelV1;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import android.os.Environment;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.FileDownloadTask;
import java.io.File;
import java.io.FileInputStream;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Row;

public class AnimalModel {

    // Initialize Firebase Storage
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReferenceFromUrl("gs://objectowl-ad2b1.appspot.com/AnimalModel/animal_des.xlsx");


    private static final int imageSize = 224; // Make sure this matches the input size your model expects

    public static void classifyImage(Bitmap image, Context context, TextView result, TextView confidence, ImageView imageView) {
        try {
            // Load the TensorFlow Lite model
            AnimalModelV1 model = AnimalModelV1.newInstance(context);

            // Resize the image to the expected input size (224x224)
            Bitmap resizedImage = ThumbnailUtils.extractThumbnail(image, imageSize, imageSize);

            // Preprocess the image (convert it into ByteBuffer and normalize it)
            ByteBuffer byteBuffer = preprocessImage(resizedImage);

            // Create input buffer for the model
            TensorBuffer inputFeature0 = TensorBuffer.createFixedSize(new int[]{1, imageSize, imageSize, 3}, DataType.FLOAT32);
            inputFeature0.loadBuffer(byteBuffer);

            // Run the model
            AnimalModelV1.Outputs outputs = model.process(inputFeature0);
            TensorBuffer outputFeature0 = outputs.getOutputFeature0AsTensorBuffer();

            // Get the confidence scores from the model
            float[] confidences = outputFeature0.getFloatArray();

            // Find the class with the highest confidence
            int maxPos = getMaxConfidencePos(confidences);

            // The labels for your classes (ensure this matches your model's output classes)
            String[] classes = {"Dog", "Cat", "Bird", "Fish", "Horse",
                    "Elephant", "Lion", "Tiger", "Cow", "Sheep",
                    "Rabbit", "Zebra", "Giraffe", "Monkey", "Panda",
                    "Kangaroo", "Penguin", "Koala", "Snake", "Crocodile",
                    "Dolphin", "Whale", "Shark", "Turtle", "Frog",
                    "Duck", "Goat", "Pig", "Owl", "Eagle",
                    "Deer", "Fox"};

            // Set the result in the TextView
            result.setText(classes[maxPos]);

            // Display confidence levels for all classes
            displayConfidences(confidences, classes, confidence);

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

    // Display confidence values for each class in a TextView
    private static void displayConfidences(float[] confidences, String[] classes, TextView confidence) {
        StringBuilder confidenceText = new StringBuilder();
        for (int i = 0; i < classes.length; i++) {
            confidenceText.append(String.format("%s: %.1f%%\n", classes[i], confidences[i] * 100));
        }
        confidence.setText(confidenceText.toString());
    }
}
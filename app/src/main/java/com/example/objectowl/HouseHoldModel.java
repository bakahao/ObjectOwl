package com.example.objectowl;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.objectowl.ml.HouseHoldModelV1;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class HouseHoldModel {

    private static final int imageSize = 224;

    public static void classifyImage(Bitmap image, Context context, TextView result, TextView confidence, ImageView imageView) {
        try {
            HouseHoldModelV1 model = HouseHoldModelV1.newInstance(context);

            // Preprocess the image
            ByteBuffer byteBuffer = preprocessImage(image);

            // Create input buffer
            TensorBuffer inputFeature0 = TensorBuffer.createFixedSize(new int[]{1, imageSize, imageSize, 3}, DataType.FLOAT32);
            inputFeature0.loadBuffer(byteBuffer);

            // Run the model
            HouseHoldModelV1.Outputs outputs = model.process(inputFeature0);
            TensorBuffer outputFeature0 = outputs.getOutputFeature0AsTensorBuffer();

            // Find the most confident result
            float[] confidences = outputFeature0.getFloatArray();
            int maxPos = getMaxConfidencePos(confidences);

            String[] classes = {"Table", "Chair", "Sofa", "Bed", "Television",
                    "Lamp", "Fan", "Cup", "Plate", "Spoon",
                    "Fork", "Knife", "Toothbrush", "Towel", "Fridge",
                    "Remote Control", "Clock", "Mirror"};

            result.setText(classes[maxPos]);

            // Display confidence levels for all classes
            displayConfidences(confidences, classes, confidence);

            // Set the processed image in the ImageView
            imageView.setImageBitmap(image);

            model.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static ByteBuffer preprocessImage(Bitmap image) {
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(4 * imageSize * imageSize * 3);
        byteBuffer.order(ByteOrder.nativeOrder());

        int[] intValues = new int[imageSize * imageSize];
        image.getPixels(intValues, 0, image.getWidth(), 0, 0, image.getWidth(), image.getHeight());

        int pixel = 0;
        for (int i = 0; i < imageSize; i++) {
            for (int j = 0; j < imageSize; j++) {
                int val = intValues[pixel++];
                byteBuffer.putFloat(((val >> 16) & 0xFF) * (1.f / 255.f));
                byteBuffer.putFloat(((val >> 8) & 0xFF) * (1.f / 255.f));
                byteBuffer.putFloat((val & 0xFF) * (1.f / 255.f));
            }
        }
        return byteBuffer;
    }

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

    private static void displayConfidences(float[] confidences, String[] classes, TextView confidence) {
        StringBuilder confidenceText = new StringBuilder();
        for (int i = 0; i < classes.length; i++) {
            confidenceText.append(String.format("%s: %.1f%%\n", classes[i], confidences[i] * 100));
        }
        confidence.setText(confidenceText.toString());
    }
}

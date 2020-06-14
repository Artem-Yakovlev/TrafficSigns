package com.example.mlroadsigndetection;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mlroadsigndetection.databinding.ActivityMainBinding;
import com.google.firebase.ml.common.FirebaseMLException;

import static com.example.mlroadsigndetection.AppConstants.APP_TAG;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private ImageClassifier classifier;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

//        try {
//            classifier = new ImageClassifier(this);
//        } catch (FirebaseMLException exception) {
//            Log.e(APP_TAG, exception.getMessage(), exception);
//        }
//        binding.startButton.setOnClickListener(v -> {
//            classifyImage(BitmapFactory.decodeResource(getResources(), R.drawable.general_1));
//        });
    }

    private void classifyImage(Bitmap bitmap) {
        if (classifier == null) {
            binding.resultText.setText("Classifier is null");
            return;
        }
        binding.imagePreview.setImageBitmap(bitmap);

        // Classify image.
        classifier.classifyFrame(bitmap).addOnSuccessListener(s -> {
            binding.resultText.setText(s);
        }).addOnFailureListener(e -> {
            Log.e(APP_TAG, "Error classifying frame", e);
            binding.resultText.setText(e.getMessage());
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        classifier.close();
    }
}

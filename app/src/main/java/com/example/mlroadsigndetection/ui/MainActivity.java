package com.example.mlroadsigndetection.ui;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mlroadsigndetection.databinding.ActivityMainBinding;


public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }
}


//    private void classifyImage(Bitmap bitmap) {
//        if (classifier == null) {
//            binding.resultText.setText("Classifier is null");
//            return;
//        }
//        binding.imagePreview.setImageBitmap(bitmap);
//
//        // Classify image.
//        classifier.classifyFrame(bitmap).addOnSuccessListener(s -> {
//            binding.resultText.setText(s);
//        }).addOnFailureListener(e -> {
//            Log.e(APP_TAG, "Error classifying frame", e);
//            binding.resultText.setText(e.getMessage());
//        });
//    }

//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        classifier.close();
//    }


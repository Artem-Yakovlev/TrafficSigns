package com.example.mlroadsigndetection;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.firebase.ml.common.FirebaseMLException;
import com.google.firebase.ml.common.modeldownload.FirebaseModelManager;
import com.google.firebase.ml.common.modeldownload.FirebaseRemoteModel;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.label.FirebaseVisionImageLabel;
import com.google.firebase.ml.vision.label.FirebaseVisionImageLabeler;
import com.google.firebase.ml.vision.label.FirebaseVisionOnDeviceAutoMLImageLabelerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import static com.example.mlroadsigndetection.AppConstants.APP_TAG;
import static com.example.mlroadsigndetection.AppConstants.CONFIDENCE_THRESHOLD;
import static com.example.mlroadsigndetection.AppConstants.REMOTE_MODEL_NAME;

public class ImageClassifier {

    private FirebaseVisionImageLabeler labeler;
    private boolean remoteModelDownloadSucceeded = false;

    ImageClassifier(Context context) throws FirebaseMLException {
        FirebaseRemoteModel remoteModel = new FirebaseRemoteModel.Builder(REMOTE_MODEL_NAME).build();
        FirebaseModelManager.getInstance().registerRemoteModel(remoteModel);

        FirebaseVisionOnDeviceAutoMLImageLabelerOptions options =
                new FirebaseVisionOnDeviceAutoMLImageLabelerOptions.Builder()
                        .setConfidenceThreshold(CONFIDENCE_THRESHOLD)
                        .setRemoteModelName(REMOTE_MODEL_NAME)
                        .build();

        labeler = FirebaseVision.getInstance().getOnDeviceAutoMLImageLabeler(options);

        Toast.makeText(context, "Begin downloading", Toast.LENGTH_SHORT).show();

        // Track the remote model download progress.
        FirebaseModelManager.getInstance()
                .downloadRemoteModelIfNeeded(remoteModel)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show();
                    remoteModelDownloadSucceeded = true;
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e(APP_TAG, "", e);
                });
    }

    /**
     * Classifies a frame from the preview stream.
     */
    public Task<String> classifyFrame(Bitmap bitmap) {
        if (labeler == null) {
            Log.e(APP_TAG, "Image classifier has not been initialized; Skipped.");

            TaskCompletionSource<String> completionSource = new TaskCompletionSource<>();
            completionSource.setException(new IllegalStateException("Uninitialized Classifier."));
            return completionSource.getTask();
        }

        long startTime = SystemClock.uptimeMillis();
        FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(bitmap);


        return labeler.processImage(image).continueWith(task -> {
            long endTime = SystemClock.uptimeMillis();
            Log.d(APP_TAG, "Time to run model inference: " + (endTime - startTime));
            List<FirebaseVisionImageLabel> labelProbList = task.getResult();

            return new StringBuilder("Latency: ")
                    .append((endTime - startTime))
                    .append("ms\n")
                    .append(labelProbList.isEmpty() ? "No results" : printTopKLabels(labelProbList))
                    .toString();
        });
    }

    /**
     * Prints top-K labels, to be shown in UI as the results.
     */
    private String printTopKLabels(List<FirebaseVisionImageLabel> labelProbList) {
        StringBuilder resultLabels = new StringBuilder();

        for (FirebaseVisionImageLabel label : labelProbList) {
            resultLabels.append(String.format(
                    Locale.getDefault(), "Label: %s, Confidence: %4.2f \n",
                    label.getText(), label.getConfidence()
            ));
        }
        return resultLabels.toString();
    }

    /**
     * Closes labeler to release resources.
     */
    public void close() {
        try {
            labeler.close();
        } catch (IOException e) {
            Log.e(APP_TAG, "Unable to close the labeler instance", e);
        }

    }

}

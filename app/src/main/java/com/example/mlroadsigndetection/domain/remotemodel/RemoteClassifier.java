package com.example.mlroadsigndetection.domain.remotemodel;

import android.graphics.Bitmap;
import android.util.Log;

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

import io.reactivex.Completable;
import io.reactivex.Single;

import static android.media.FaceDetector.Face.CONFIDENCE_THRESHOLD;
import static com.example.mlroadsigndetection.data.AppConstants.APP_TAG;
import static com.example.mlroadsigndetection.data.AppConstants.REMOTE_MODEL_NAME;

public class RemoteClassifier {

    private FirebaseVisionImageLabeler labeler;
    private FirebaseRemoteModel remoteModel;

    public RemoteClassifier() {
        remoteModel = new FirebaseRemoteModel.Builder(REMOTE_MODEL_NAME).build();
        FirebaseModelManager.getInstance().registerRemoteModel(remoteModel);

        FirebaseVisionOnDeviceAutoMLImageLabelerOptions options =
                new FirebaseVisionOnDeviceAutoMLImageLabelerOptions.Builder()
                        .setConfidenceThreshold(CONFIDENCE_THRESHOLD)
                        .setRemoteModelName(REMOTE_MODEL_NAME)
                        .build();

        try {
            labeler = FirebaseVision.getInstance().getOnDeviceAutoMLImageLabeler(options);
        } catch (FirebaseMLException e) {
            e.printStackTrace();
        }
    }

    public Completable downloadModel() {
        return Completable.create(emitter -> {
            FirebaseModelManager.getInstance()
                    .downloadRemoteModelIfNeeded(remoteModel)
                    .addOnSuccessListener(it -> emitter.onComplete())
                    .addOnFailureListener(emitter::onError);
        });
    }

    public Single<String> classifyBitmap(Bitmap bitmap) {
        return Single.create(emit -> {
            if (labeler == null) {
                Log.e(APP_TAG, "Image classifier has not been initialized; Skipped.");
                emit.onError(new IllegalStateException("Uninitialized Classifier."));
            }

            labeler.processImage(FirebaseVisionImage.fromBitmap(bitmap))
                    .addOnSuccessListener(imageLabels -> emit.onSuccess(getTopLabel(imageLabels)))
                    .addOnFailureListener(emit::onError);
        });

    }

    private String getTopLabel(List<FirebaseVisionImageLabel> imageLabels) {
        if (imageLabels.isEmpty()) {
            return "Nothing";
        } else {
            return String.format(Locale.getDefault(), "Label: %s, Confidence: %4.2f \n",
                    imageLabels.get(0).getText(), imageLabels.get(0).getConfidence()
            );
        }
    }

    public void close() {
        try {
            labeler.close();
        } catch (IOException e) {
            Log.e(APP_TAG, "Unable to close the labeler instance", e);
        }

    }

}

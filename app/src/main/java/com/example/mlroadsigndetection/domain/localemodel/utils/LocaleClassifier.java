package com.example.mlroadsigndetection.domain.localemodel.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.util.Log;

import androidx.annotation.NonNull;

import com.esafirm.imagepicker.model.Image;
import com.example.mlroadsigndetection.data.ModelConstants;

import org.tensorflow.lite.Interpreter;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

public class LocaleClassifier {

    private final Interpreter interpreter;
    private final List<String> labels;

    public LocaleClassifier(Context context) throws IOException {
        ByteBuffer model = AssetsUtils.loadFile(context, ModelConstants.LOCALE_MODEL_FILENAME);
        this.interpreter = new Interpreter(model);
        this.labels = AssetsUtils.loadLines(context, ModelConstants.LABELS_FILENAME);
    }

    public void classifyImage(@NonNull Image image) {

        ByteBuffer byteBufferToClassify = bitmapToModelsMatchingByteBuffer(
                ThumbnailUtils.extractThumbnail(
                        BitmapFactory.decodeFile(image.getPath()),
                        ModelConstants.INPUT_WIDTH, ModelConstants.INPUT_HEIGHT
                ));

        byte[][] result = new byte[1][labels.size()];
        interpreter.run(byteBufferToClassify, result);
        float[][] resultFloats = new float[1][labels.size()];

        byte[] bytes = result[0];
        for (int i = 0; i < bytes.length; i++) {
            resultFloats[0][i] = (bytes[i] & 0xff) / 255.f;
        }

        Log.d("ASMR", "REsult:");
        Log.d("ASMR", getSortedResult(resultFloats).toString());
    }

    private ByteBuffer bitmapToModelsMatchingByteBuffer(Bitmap bitmap) {
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(ModelConstants.INPUT_SIZE);
        byteBuffer.order(ByteOrder.nativeOrder());

        int[] intValues = new int[ModelConstants.INPUT_WIDTH * ModelConstants.INPUT_HEIGHT];
        bitmap.getPixels(intValues, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());

        int pixel = 0;
        for (int i = 0; i < ModelConstants.INPUT_WIDTH; i++) {
            for (int j = 0; j < ModelConstants.INPUT_HEIGHT; j++) {
                for (byte channelVal : pixelToChannelValuesQuant(intValues[pixel++])) {
                    byteBuffer.put(channelVal);
                }
            }
        }
        return byteBuffer;
    }

    private byte[] pixelToChannelValuesQuant(int pixel) {
        return new byte[]{
                (byte) ((pixel >> 16) & 0xFF),
                (byte) ((pixel >> 8) & 0xFF),
                (byte) ((pixel) & 0xFF)
        };
    }

    private List<ClassificationResult> getSortedResult(float[][] resultsArray) {
        PriorityQueue<ClassificationResult> sortedResults = new PriorityQueue<>(
                ModelConstants.MAX_CLASSIFICATION_RESULTS,
                (lhs, rhs) -> Float.compare(rhs.confidence, lhs.confidence)
        );

        for (int i = 0; i < labels.size(); i++) {
            float confidence = resultsArray[0][i];
            if (confidence > ModelConstants.CLASSIFICATION_THRESHOLD) {
                sortedResults.add(new ClassificationResult(labels.get(i), confidence));
            }
        }

        return new ArrayList<>(sortedResults);
    }
}

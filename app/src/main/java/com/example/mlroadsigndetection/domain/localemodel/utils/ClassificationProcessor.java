package com.example.mlroadsigndetection.domain.localemodel.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.util.Log;

import androidx.annotation.NonNull;

import com.esafirm.imagepicker.model.Image;
import com.example.mlroadsigndetection.domain.localemodel.configs.GtsrbQuantConfig;
import com.example.mlroadsigndetection.domain.localemodel.configs.ModelConfig;

import org.tensorflow.lite.Interpreter;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

public class ClassificationProcessor {

    private static final int MAX_CLASSIFICATION_RESULTS = 3;
    private static final float CLASSIFICATION_THRESHOLD = 0.2f;

    private final Interpreter interpreter;
    private final List<String> labels;
    private final ModelConfig modelConfig;

    public ClassificationProcessor(Context context) throws IOException {
        ModelConfig modelConfig = new GtsrbQuantConfig();
        ByteBuffer model = AssetsUtils.loadFile(context, modelConfig.getModelFilename());
        this.interpreter = new Interpreter(model);
        this.labels = AssetsUtils.loadLines(context, modelConfig.getLabelsFilename());
        this.modelConfig = modelConfig;
    }

    public void process(@NonNull Image image) {
        Bitmap bitmap = BitmapFactory.decodeFile(image.getPath());
        Bitmap toClassify = ThumbnailUtils.extractThumbnail(
                bitmap, modelConfig.getInputWidth(), modelConfig.getInputHeight()
        );
        bitmap.recycle();

        ByteBuffer byteBufferToClassify = bitmapToModelsMatchingByteBuffer(toClassify);
        runInferenceOnQuantizedModel(byteBufferToClassify);
    }

    private void runInferenceOnQuantizedModel(ByteBuffer byteBufferToClassify) {
        byte[][] result = new byte[1][labels.size()];
        interpreter.run(byteBufferToClassify, result);
        float[][] resultFloats = new float[1][labels.size()];
        byte[] bytes = result[0];
        for (int i = 0; i < bytes.length; i++) {
            float resultF = (bytes[i] & 0xff) / 255.f;
            resultFloats[0][i] = resultF;

        }
        Log.d("ASMR", "REsult:");
        Log.d("ASMR", getSortedResult(resultFloats).toString());
    }

    private ByteBuffer bitmapToModelsMatchingByteBuffer(Bitmap bitmap) {
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(modelConfig.getInputSize());
        byteBuffer.order(ByteOrder.nativeOrder());
        int[] intValues = new int[modelConfig.getInputWidth() * modelConfig.getInputHeight()];
        bitmap.getPixels(intValues, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
        int pixel = 0;
        for (int i = 0; i < modelConfig.getInputWidth(); ++i) {
            for (int j = 0; j < modelConfig.getInputHeight(); ++j) {
                int pixelVal = intValues[pixel++];
                for (byte channelVal : pixelToChannelValuesQuant(pixelVal)) {
                    byteBuffer.put(channelVal);
                }
            }
        }
        return byteBuffer;
    }

    private byte[] pixelToChannelValuesQuant(int pixel) {
        byte[] rgbVals = new byte[3];
        rgbVals[0] = (byte) ((pixel >> 16) & 0xFF);
        rgbVals[1] = (byte) ((pixel >> 8) & 0xFF);
        rgbVals[2] = (byte) ((pixel) & 0xFF);
        return rgbVals;
    }

    private List<ClassificationResult> getSortedResult(float[][] resultsArray) {
        PriorityQueue<ClassificationResult> sortedResults = new PriorityQueue<>(
                MAX_CLASSIFICATION_RESULTS,
                (lhs, rhs) -> Float.compare(rhs.confidence, lhs.confidence)
        );

        for (int i = 0; i < labels.size(); ++i) {
            float confidence = resultsArray[0][i];
            if (confidence > CLASSIFICATION_THRESHOLD) {
                sortedResults.add(new ClassificationResult(labels.get(i), confidence));
            }
        }

        return new ArrayList<>(sortedResults);
    }
}

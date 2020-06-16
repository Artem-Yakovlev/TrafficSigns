package com.example.mlroadsigndetection.data;

public final class ModelConstants {

    private ModelConstants() {

    }
    public static final int FLOAT_BYTES_COUNT = 4;
    public static final int QUANT_BYTES_COUNT = 1;
    public static final String LOCALE_MODEL_FILENAME = "gtsrb_demo.tflite";
    public static final String LABELS_FILENAME = "gtsrb_demo_labels.txt";
    public static final int INPUT_WIDTH = 224;
    public static final int INPUT_HEIGHT = 224;
    public static final int CHANNELS_COUNT = 3;
    public static final int INPUT_SIZE = INPUT_WIDTH * INPUT_HEIGHT * CHANNELS_COUNT * QUANT_BYTES_COUNT;

}

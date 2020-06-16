package com.example.mlroadsigndetection.data;

import com.esafirm.imagepicker.model.Image;

public class AnalysisResults {

    private final String label;
    private final float confidence;

    public AnalysisResults(String label, float confidence) {
        this.label = label;
        this.confidence = confidence;
    }

    public String getLabel() {
        return label;
    }

    public float getConfidence() {
        return confidence;
    }
}



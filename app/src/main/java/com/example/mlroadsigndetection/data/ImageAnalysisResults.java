package com.example.mlroadsigndetection.data;

import android.graphics.Bitmap;

public class ImageAnalysisResults {
    private final Bitmap bitmap;
    private final AnalysisResults analysisResults;

    public ImageAnalysisResults(Bitmap bitmap, AnalysisResults analysisResults) {
        this.bitmap = bitmap;
        this.analysisResults = analysisResults;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public AnalysisResults getAnalysisResults() {
        return analysisResults;
    }
}

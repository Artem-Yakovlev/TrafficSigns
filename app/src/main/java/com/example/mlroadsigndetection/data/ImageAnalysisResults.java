package com.example.mlroadsigndetection.data;

import com.esafirm.imagepicker.model.Image;

public class ImageAnalysisResults {
    private final Image image;
    private final AnalysisResults analysisResults;

    public ImageAnalysisResults(Image image, AnalysisResults analysisResults) {
        this.image = image;
        this.analysisResults = analysisResults;
    }

    public Image getImage() {
        return image;
    }

    public AnalysisResults getAnalysisResults() {
        return analysisResults;
    }
}

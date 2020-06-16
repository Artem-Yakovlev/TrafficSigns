package com.example.mlroadsigndetection.data;

import com.esafirm.imagepicker.model.Image;

public class AnalysisResults {
    private final Image image;
    private final String result;

    public AnalysisResults(Image image, String result) {
        this.image = image;
        this.result = result;
    }

    public Image getImage() {
        return image;
    }

    public String getResult() {
        return result;
    }
}

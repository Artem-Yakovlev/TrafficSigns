package com.example.mlroadsigndetection.presenter.selectimage;


import android.graphics.Bitmap;

import com.example.mlroadsigndetection.data.AnalysisResults;

public class SelectImageViewState {

    private boolean isLoading = false;
    private boolean isButtonEnabled = true;
    private Bitmap bitmap;
    private AnalysisResults analysisResults = new AnalysisResults("", 0);

    public SelectImageViewState() {

    }

    public SelectImageViewState(boolean isLoading, boolean isButtonEnabled, Bitmap bitmap, AnalysisResults analysisResults) {
        this.isLoading = isLoading;
        this.isButtonEnabled = isButtonEnabled;
        this.bitmap = bitmap;
        this.analysisResults = analysisResults;
    }

    public boolean isLoading() {
        return isLoading;
    }

    public boolean isButtonEnabled() {
        return isButtonEnabled;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public AnalysisResults getAnalysisResults() {
        return analysisResults;
    }
}

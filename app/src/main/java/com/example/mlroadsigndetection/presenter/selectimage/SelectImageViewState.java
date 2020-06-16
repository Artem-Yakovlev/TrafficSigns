package com.example.mlroadsigndetection.presenter.selectimage;


import com.example.mlroadsigndetection.data.AnalysisResults;

public class SelectImageViewState {

    private boolean isLoading = false;
    private boolean isButtonEnabled = true;
    private String imagePath = "";
    private AnalysisResults analysisResults = new AnalysisResults("", 0);

    public SelectImageViewState() {

    }

    public SelectImageViewState(boolean isLoading, boolean isButtonEnabled, String imagePath,
                                AnalysisResults analysisResults) {
        this.isLoading = isLoading;
        this.isButtonEnabled = isButtonEnabled;
        this.imagePath = imagePath;
        this.analysisResults = analysisResults;
    }

    public boolean isLoading() {
        return isLoading;
    }

    public boolean isButtonEnabled() {
        return isButtonEnabled;
    }

    public String getImagePath() {
        return imagePath;
    }

    public AnalysisResults getAnalysisResults() {
        return analysisResults;
    }
}

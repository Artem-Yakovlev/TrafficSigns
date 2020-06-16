package com.example.mlroadsigndetection.presenter.selectimage;


public class SelectImageViewState {

    private Boolean isLoading = false;
    private Boolean isButtonEnabled = true;
    private String imagePath = "";
    private String resultText = "No result";

    public SelectImageViewState() {

    }

    public SelectImageViewState(Boolean isLoading,
                                Boolean isButtonEnabled,
                                String imagePath,
                                String resultText) {
        this.isLoading = isLoading;
        this.isButtonEnabled = isButtonEnabled;
        this.imagePath = imagePath;
        this.resultText = resultText;
    }

    public Boolean getLoading() {
        return isLoading;
    }

    public Boolean getButtonEnabled() {
        return isButtonEnabled;
    }

    public String getImagePath() {
        return imagePath;
    }

    public String getResultText() {
        return resultText;
    }
}

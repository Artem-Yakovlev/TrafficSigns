package com.example.mlroadsigndetection.presenter.selectimage.reducers;

import com.example.mlroadsigndetection.presenter.selectimage.SelectImageViewState;

public class ImagePathReducer extends SelectImageReducer {

    private String imagePath;

    public ImagePathReducer(String imagePath) {
        this.imagePath = imagePath;
    }

    @Override
    public SelectImageViewState apply(SelectImageViewState previousState) {
        return new SelectImageViewState(previousState.getLoading(),
                previousState.getButtonEnabled(), imagePath, previousState.getResultText());
    }
}

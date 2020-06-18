package com.example.mlroadsigndetection.presenter.selectimage.reducers;

import android.graphics.Bitmap;

import com.example.mlroadsigndetection.presenter.selectimage.SelectImageViewState;

public class ImagePathReducer extends SelectImageReducer {

    private Bitmap bitmap;

    public ImagePathReducer(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    @Override
    public SelectImageViewState apply(SelectImageViewState previousState) {
        return new SelectImageViewState(previousState.isLoading(),
                previousState.isButtonEnabled(), bitmap, previousState.getAnalysisResults());
    }
}

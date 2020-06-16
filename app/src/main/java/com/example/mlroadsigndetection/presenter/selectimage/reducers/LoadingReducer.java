package com.example.mlroadsigndetection.presenter.selectimage.reducers;

import com.example.mlroadsigndetection.presenter.selectimage.SelectImageViewState;

public class LoadingReducer extends SelectImageReducer {

    private boolean isLoading;

    public LoadingReducer(boolean isLoading) {
        this.isLoading = isLoading;
    }

    @Override
    public SelectImageViewState apply(SelectImageViewState previousState) {
        return new SelectImageViewState(isLoading, previousState.getButtonEnabled(),
                previousState.getImagePath(), previousState.getResultText());
    }
}

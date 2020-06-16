package com.example.mlroadsigndetection.presenter.selectimage.reducers;

import com.example.mlroadsigndetection.presenter.selectimage.SelectImageViewState;

public class ButtonReducer extends SelectImageReducer {

    private boolean isButtonEnabled;

    public ButtonReducer(boolean isButtonEnabled) {
        this.isButtonEnabled = isButtonEnabled;
    }

    @Override
    public SelectImageViewState apply(SelectImageViewState previousState) {
        return new SelectImageViewState(previousState.getLoading(), isButtonEnabled,
                previousState.getImagePath(), previousState.getResultText());
    }
}

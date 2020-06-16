package com.example.mlroadsigndetection.presenter.selectimage.reducers;

import com.example.mlroadsigndetection.presenter.selectimage.SelectImageViewState;

public class ResultReducer extends SelectImageReducer {

    private String result;

    public ResultReducer(String result) {
        this.result = result;
    }

    @Override
    public SelectImageViewState apply(SelectImageViewState previousState) {
        return new SelectImageViewState(previousState.getLoading(),
                previousState.getButtonEnabled(),
                previousState.getImagePath(),
                result);
    }
}
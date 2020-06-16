package com.example.mlroadsigndetection.presenter.selectimage.reducers;

import com.example.mlroadsigndetection.data.AnalysisResults;
import com.example.mlroadsigndetection.presenter.selectimage.SelectImageViewState;

public class AnalysisResultsReducer extends SelectImageReducer {

    private AnalysisResults analysisResults;

    public AnalysisResultsReducer(AnalysisResults analysisResults) {
        this.analysisResults = analysisResults;
    }

    @Override
    public SelectImageViewState apply(SelectImageViewState previousState) {
        return new SelectImageViewState(previousState.isLoading(), previousState.isButtonEnabled(),
                previousState.getImagePath(), analysisResults
        );
    }
}
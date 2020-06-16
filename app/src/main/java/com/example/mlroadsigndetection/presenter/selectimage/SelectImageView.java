package com.example.mlroadsigndetection.presenter.selectimage;


import moxy.MvpView;
import moxy.viewstate.strategy.alias.AddToEndSingle;
import moxy.viewstate.strategy.alias.OneExecution;

public interface SelectImageView extends MvpView {

    @AddToEndSingle
    void onViewStateChanged(SelectImageViewState state);

    @OneExecution
    void showUnexpectedError(Throwable throwable);

    @OneExecution
    void showDownloadSuccess();

    @OneExecution
    void showDownloadFailure(Throwable throwable);
}

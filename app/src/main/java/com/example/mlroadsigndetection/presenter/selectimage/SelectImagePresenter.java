package com.example.mlroadsigndetection.presenter.selectimage;


import android.graphics.BitmapFactory;

import com.esafirm.imagepicker.model.Image;
import com.example.mlroadsigndetection.arch.DataStatus;
import com.example.mlroadsigndetection.arch.Resource;
import com.example.mlroadsigndetection.data.ImageAnalysisResults;
import com.example.mlroadsigndetection.domain.RemoteClassifier;
import com.example.mlroadsigndetection.presenter.selectimage.reducers.AnalysisResultsReducer;
import com.example.mlroadsigndetection.presenter.selectimage.reducers.ButtonReducer;
import com.example.mlroadsigndetection.presenter.selectimage.reducers.ImagePathReducer;
import com.example.mlroadsigndetection.presenter.selectimage.reducers.LoadingReducer;

import java.util.Objects;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.processors.BehaviorProcessor;
import io.reactivex.schedulers.Schedulers;
import moxy.InjectViewState;
import moxy.MvpPresenter;

@InjectViewState
public class SelectImagePresenter extends MvpPresenter<SelectImageView> {

    private CompositeDisposable disposableBag = new CompositeDisposable();

    private BehaviorProcessor<Image> photoProcessor = BehaviorProcessor.create();
    private BehaviorProcessor<Resource<ImageAnalysisResults>> resultProcessor = BehaviorProcessor.create();

    private RemoteClassifier remoteClassifier;

    public SelectImagePresenter(RemoteClassifier remoteClassifier) {
        this.remoteClassifier = remoteClassifier;
    }

    @Override
    protected void onFirstViewAttach() {
        super.onFirstViewAttach();

        //Reducers
        Flowable<ImagePathReducer> imagePathReducerFlowable = resultProcessor
                .filter(it -> it.getStatus() == DataStatus.DATA)
                .map(it -> Objects.requireNonNull(it.getData()).getImage())
                .map(Image::getPath)
                .map(ImagePathReducer::new);

        Flowable<LoadingReducer> loadingReducerFlowable = resultProcessor
                .map(it -> it.getStatus() == DataStatus.LOADING)
                .map(LoadingReducer::new);

        Flowable<ButtonReducer> buttonReducerFlowable = resultProcessor
                .map(it -> it.getStatus() != DataStatus.LOADING)
                .map(ButtonReducer::new);

        Flowable<AnalysisResultsReducer> analysisResultsReducerFlowable = resultProcessor
                .filter(it -> it.getStatus() == DataStatus.DATA)
                .map(it -> Objects.requireNonNull(it.getData()).getAnalysisResults())
                .map(AnalysisResultsReducer::new);

        //State
        Disposable disposable = Flowable
                .merge(imagePathReducerFlowable, loadingReducerFlowable,
                        buttonReducerFlowable, analysisResultsReducerFlowable)
                .scan(new SelectImageViewState(), (state, reducer) -> reducer.apply(state))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(getViewState()::onViewStateChanged, getViewState()::showUnexpectedError);

        disposableBag.add(disposable);

        //Actions
        disposableBag.add(photoProcessor.observeOn(Schedulers.computation())
                .subscribe(this::imageAnalyse));

        disposableBag.add(remoteClassifier.downloadModel()
                .subscribe(getViewState()::showDownloadSuccess, getViewState()::showDownloadFailure)
        );

        disposableBag.add(resultProcessor
                .filter(it -> it.getStatus() == DataStatus.ERROR)
                .map(Resource::getError)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(getViewState()::showUnexpectedError, getViewState()::showUnexpectedError));

    }

    private void imageAnalyse(Image image) {
        resultProcessor.onNext(new Resource<ImageAnalysisResults>().loading());
        disposableBag.add(remoteClassifier.classifyBitmap(BitmapFactory.decodeFile(image.getPath()))
                .subscribe(analysisResults -> resultProcessor.onNext(
                        new Resource<ImageAnalysisResults>()
                                .data(new ImageAnalysisResults(image, analysisResults))
                        ),
                        e -> resultProcessor.onNext(new Resource<ImageAnalysisResults>().error(e))
                ));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        disposableBag.dispose();
        remoteClassifier.close();
    }

    public void onPhotoChanged(Image image) {
        photoProcessor.onNext(image);
    }

}

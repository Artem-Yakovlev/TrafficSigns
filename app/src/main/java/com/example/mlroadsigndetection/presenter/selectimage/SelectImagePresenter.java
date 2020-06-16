package com.example.mlroadsigndetection.presenter.selectimage;


import android.graphics.BitmapFactory;

import com.esafirm.imagepicker.model.Image;
import com.example.mlroadsigndetection.data.AnalysisResults;
import com.example.mlroadsigndetection.data.DataStatus;
import com.example.mlroadsigndetection.data.Resource;
import com.example.mlroadsigndetection.domain.remotemodel.RxImageClassifier;
import com.example.mlroadsigndetection.presenter.selectimage.reducers.ButtonReducer;
import com.example.mlroadsigndetection.presenter.selectimage.reducers.ImagePathReducer;
import com.example.mlroadsigndetection.presenter.selectimage.reducers.LoadingReducer;
import com.example.mlroadsigndetection.presenter.selectimage.reducers.ResultReducer;

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
    private BehaviorProcessor<Resource<AnalysisResults>> resultProcessor = BehaviorProcessor.create();

    private RxImageClassifier imageClassifier = new RxImageClassifier();

    @Override
    protected void onFirstViewAttach() {
        super.onFirstViewAttach();

//        Reducers
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

        Flowable<ResultReducer> resultReducerFlowable = resultProcessor
                .filter(it -> it.getStatus() == DataStatus.DATA)
                .map(it -> Objects.requireNonNull(it.getData()).getResult())
                .map(ResultReducer::new);

        //State
        Disposable disposable = Flowable
                .merge(imagePathReducerFlowable, loadingReducerFlowable, buttonReducerFlowable,
                        resultReducerFlowable)
                .scan(new SelectImageViewState(), (state, reducer) -> reducer.apply(state))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(getViewState()::onViewStateChanged);

        disposableBag.add(disposable);

        //Actions

        disposableBag.add(photoProcessor.observeOn(Schedulers.computation())
                .subscribe(this::imageAnalyse));

        disposableBag.add(imageClassifier.downloadModel()
                .subscribe(getViewState()::showDownloadSuccess, getViewState()::showDownloadFailure)
        );
    }

    private void imageAnalyse(Image image) {
        resultProcessor.onNext(new Resource<AnalysisResults>().loading());
        disposableBag.add(imageClassifier.classifyBitmap(BitmapFactory.decodeFile(image.getPath()))
                .subscribe(s -> {
                    resultProcessor.onNext(new Resource<AnalysisResults>()
                            .data(new AnalysisResults(image, s))
                    );
                }));

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        disposableBag.dispose();
    }

    public void onPhotoChanged(Image image) {
        photoProcessor.onNext(image);
    }

}

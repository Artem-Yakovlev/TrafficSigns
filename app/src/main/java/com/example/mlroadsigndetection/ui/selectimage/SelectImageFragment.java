package com.example.mlroadsigndetection.ui.selectimage;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.esafirm.imagepicker.features.ImagePicker;
import com.esafirm.imagepicker.model.Image;
import com.example.mlroadsigndetection.R;
import com.example.mlroadsigndetection.databinding.FragmentSelectImageBinding;
import com.example.mlroadsigndetection.domain.localemodel.utils.ClassificationProcessor;
import com.example.mlroadsigndetection.presenter.selectimage.SelectImagePresenter;
import com.example.mlroadsigndetection.presenter.selectimage.SelectImageView;
import com.example.mlroadsigndetection.presenter.selectimage.SelectImageViewState;

import java.io.IOException;

import moxy.MvpAppCompatFragment;
import moxy.presenter.InjectPresenter;


public class SelectImageFragment extends MvpAppCompatFragment implements SelectImageView {

    private FragmentSelectImageBinding binding;

    @InjectPresenter
    SelectImagePresenter presenter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSelectImageBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.selectImageButton.setOnClickListener(v -> {
            ImagePicker.create(this).single().start();
        });
    }

    @Override
    public void onViewStateChanged(SelectImageViewState state) {
        binding.selectImageButton.setEnabled(state.getButtonEnabled());
        showLoading(state.getLoading());
        Glide.with(this.requireContext()).load(state.getImagePath()).into(binding.analyzedImage);
        binding.resultText.setText(state.getResultText());
    }

    @Override
    public void showDownloadSuccess() {
        Toast.makeText(requireContext(), R.string.model_download_success, Toast.LENGTH_LONG).show();
        binding.selectImageButton.setVisibility(View.VISIBLE);
    }

    @Override
    public void showDownloadFailure(Throwable throwable) {
        Toast.makeText(requireContext(), R.string.model_download_failure, Toast.LENGTH_LONG).show();
    }

    private void showLoading(Boolean isLoading) {
        if (isLoading) {
            binding.progressBar.setVisibility(View.VISIBLE);
            binding.analyzedImage.setVisibility(View.INVISIBLE);
            binding.resultCardview.setVisibility(View.INVISIBLE);
        } else {
            binding.progressBar.setVisibility(View.INVISIBLE);
            binding.analyzedImage.setVisibility(View.VISIBLE);
            binding.resultCardview.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (ImagePicker.shouldHandle(requestCode, resultCode, data)) {
            Image image = ImagePicker.getFirstImageOrNull(data);
            if (image != null) {
//                try {
//                    ClassificationProcessor classificationProcessor = new ClassificationProcessor(requireContext());
//                    classificationProcessor.classifyImage(image);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }

                presenter.onPhotoChanged(image);
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

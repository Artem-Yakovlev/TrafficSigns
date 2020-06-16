package com.example.mlroadsigndetection.data;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class Resource<T> {

    @NonNull
    private DataStatus status;

    @Nullable
    private T data;

    @Nullable
    private Throwable error;

    public Resource() {
        this.status = DataStatus.CREATED;
        this.data = null;
        this.error = null;
    }

    public Resource<T> loading() {
        this.status = DataStatus.LOADING;
        this.data = null;
        this.error = null;
        return this;
    }

    public Resource<T> data(@NonNull T data) {
        this.status = DataStatus.DATA;
        this.data = data;
        this.error = null;
        return this;
    }

    public Resource<T> error(@NonNull Throwable error) {
        this.status = DataStatus.ERROR;
        this.data = null;
        this.error = error;
        return this;
    }

    @NonNull
    public DataStatus getStatus() {
        return status;
    }

    @Nullable
    public T getData() {
        return data;
    }

    @Nullable
    public Throwable getError() {
        return error;
    }

}
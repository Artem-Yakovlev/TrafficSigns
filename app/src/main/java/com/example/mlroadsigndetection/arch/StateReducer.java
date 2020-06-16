package com.example.mlroadsigndetection.arch;

public abstract class StateReducer<State> {

    public abstract State apply(State previousState);
}

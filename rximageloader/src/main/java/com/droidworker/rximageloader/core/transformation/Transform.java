package com.droidworker.rximageloader.core.transformation;


import com.droidworker.rximageloader.core.key;

import rx.functions.Func1;

/**
 * @author DroidWorkerLYF
 */
public abstract class Transform<Old, New> implements Func1<Old, New>, key {

    @Override
    public String getKey() {
        return getClass().getSimpleName();
    }
}

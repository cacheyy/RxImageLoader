package com.droidworker.rximageloader.core.request;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.view.View;
import android.widget.ImageView;

import com.droidworker.rximageloader.utils.Utils;

import java.lang.ref.WeakReference;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Action1;

/**
 * @author DroidWorkerLYF
 */
public class Request<T extends Bitmap> extends Subscriber<T> {
    protected String mKey;
    protected Option mOption;
    protected WeakReference<View> mReference;
    protected Action1<Float> onProgress;
    /**
     * If this is true, then we will not cache the bitmap in memory
     */
    private boolean skipCacheInMem;
    /**
     * If this is true, the we will not cache the bitmap in disk
     */
    private boolean skipCacheInDisk;
    private int errorId;
    private int placeholderId;
    private Subscriber<Request> internalSubscriber;

    public Request load(String url) {
        this.mKey = url;
        return this;
    }

    public Request progress(Action1<Float> onProgress) {
        this.onProgress = onProgress;
        return this;
    }

    public Request skipCacheInMem(boolean skip) {
        this.skipCacheInMem = skip;
        return this;
    }

    public Request skipCacheInDisk(boolean skip) {
        this.skipCacheInDisk = skip;
        return this;
    }

    public Request error(int resId) {
        this.errorId = resId;
        return this;
    }

    public Request placeholder(int resId) {
        this.placeholderId = resId;
        return this;
    }

    public void into(View view) {
        if (view == null) {
            throw new IllegalArgumentException("can not load into a null object");
        }
        if (mReference != null) {
            mReference.clear();
        }
        mReference = new WeakReference<>(view);
        Observable.just(this).subscribe(internalSubscriber);
    }

    public void setNotifySubscriber(Subscriber<Request> subscriber) {
        this.internalSubscriber = subscriber;
    }

    public Observable<Request> get() {
        return Observable.just(this);
    }

    public Option getOption() {
        return mOption;
    }

    public String getKey() {
        return mKey;
    }

    public View getAttachedView() {
        if (mReference == null) {
            return null;
        }
        return mReference.get();
    }

    public void clear() {
        mKey = null;
        mOption = null;
        mReference.clear();
        skipCacheInMem = false;
        skipCacheInDisk = false;
    }

    public void onProgress(float percent) {
        if (onProgress != null) {
            onProgress.call(percent);
        }
    }

    @Override
    public void onCompleted() {
        clear();
        unsubscribe();
    }

    @Override
    public void onError(Throwable e) {
        if (isUnsubscribed() || checkNull() || errorId == 0) {
            return;
        }
        View view = mReference.get();
        view.setBackgroundResource(errorId);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (isUnsubscribed() || checkNull() || placeholderId == 0) {
            return;
        }
        View view = mReference.get();
        view.post(() -> view.setBackgroundResource(placeholderId));
    }

    @Override
    public void onNext(T requestResult) {
        if (isUnsubscribed() || checkNull()) {
            return;
        }
        View view = mReference.get();
        if (view instanceof ImageView) {
            ((ImageView) view).setImageBitmap(requestResult);
        } else {
            if (Utils.hasJellyBean()) {
                view.setBackground(new BitmapDrawable(view.getResources(), requestResult));
            } else {
                //noinspection deprecation
                view.setBackgroundDrawable(new BitmapDrawable(view.getResources(), requestResult));
            }
        }
    }

    private boolean checkNull() {
        return mReference == null || mReference.get() == null;
    }

    public class Option {
        public int reqWidth;
        public int reqHeight;
        public Bitmap.Config config;
    }
}

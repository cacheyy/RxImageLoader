package com.droidworker.rximageloader.core.request;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.droidworker.rximageloader.core.LoaderConfig;
import com.droidworker.rximageloader.utils.Utils;

import java.lang.ref.WeakReference;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Action1;

/**
 * This class contains all things that a task needed
 *
 * @author DroidWorkerLYF
 */
public class Request<T extends Bitmap> extends Subscriber<T> {
    private static final String TAG = "Request";
    protected String mPath;
    protected WeakReference<View> mReference;
    protected Action1<Float> onProgress;
    protected ImageView.ScaleType mScaleType = null;
    public int reqWidth;
    public int reqHeight;
    public Bitmap.Config mConfig;
    public Bitmap.CompressFormat mCompressFormat;
    public int mCompressQuality;
    /**
     * If this is true, then we will not cache the bitmap in memory
     */
    private boolean skipCacheInMem;
    /**
     * If this is true, the we will not cache the bitmap in disk
     */
    private boolean skipCacheInDisk;
    /**
     * When {@link Subscriber#onError(Throwable)} called, set this to view's background
     */
    private int errorId;
    /**
     * Before load an image, set this to view's background
     */
    private int placeholderId;
    /**
     * This {@link Subscriber} is used to notify the {@link RequestManager} that the request has
     * been created and we shall perform a load task
     */
    private Subscriber<Request> internalSubscriber;

    public Request(LoaderConfig loaderConfig) {
        mConfig = loaderConfig.mConfig;
        reqWidth = loaderConfig.screenWidth / 4;
        reqHeight = loaderConfig.screenHeight / 4;
    }

    /**
     * set path
     *
     * @param path url or local path
     * @return this request
     */
    public Request load(String path) {
        this.mPath = path;
        return this;
    }

    /**
     * @param onProgress {@link Action1} used to update progress
     * @return this request
     */
    public Request progress(Action1<Float> onProgress) {
        this.onProgress = onProgress;
        return this;
    }

    /**
     * @param skip skip cache in memory or not
     * @return this request
     */
    public Request skipCacheInMem(boolean skip) {
        this.skipCacheInMem = skip;
        return this;
    }

    /**
     * @param skip skip cache in disk or not
     * @return this request
     */
    public Request skipCacheInDisk(boolean skip) {
        this.skipCacheInDisk = skip;
        return this;
    }

    /**
     * set resource id used for {@link Subscriber#onError(Throwable)}
     *
     * @param resId the resource id
     * @return this request
     */
    public Request error(int resId) {
        this.errorId = resId;
        return this;
    }

    /**
     * set resource id used for {@link Subscriber#onStart()}
     *
     * @param resId the resource id
     * @return this request
     */
    public Request placeholder(int resId) {
        this.placeholderId = resId;
        return this;
    }

    public Request scaleType(ImageView.ScaleType scaleType) {
        this.mScaleType = scaleType;
        return this;
    }

    public Request tempConfig(Bitmap.Config config) {
        this.mConfig = config;
        return this;
    }

    public Request tempConfig(Bitmap.CompressFormat compressFormat) {
        this.mCompressFormat = compressFormat;
        return this;
    }

    public Request tempConfig(int compressQuality) {
        this.mCompressQuality = compressQuality;
        return this;
    }

    /**
     * set the view will be used to set the bitmap
     *
     * @param view the container
     */
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

    /**
     * @param subscriber set this {@link Subscriber} as notify subscriber
     */
    public void setNotifySubscriber(Subscriber<Request> subscriber) {
        this.internalSubscriber = subscriber;
    }

    /**
     * @return an Observable which will be used to trigger the internalSubscriber
     */
    private Observable<Request> get() {
        return Observable.just(this);
    }

    /**
     * @return the path
     */
    public String getPath() {
        return mPath;
    }

    public String getKey() {
        return mPath.substring(mPath.lastIndexOf("/") + 1, mPath.length());
    }

    /**
     * @return the view attached to this request
     */
    public View getAttachedView() {
        if (mReference == null) {
            return null;
        }
        return mReference.get();
    }

    /**
     * Clear
     */
    public void clear() {
        mPath = null;
        mReference.clear();
        skipCacheInMem = false;
        skipCacheInDisk = false;
    }

    /**
     * Update progress
     *
     * @param percent current progress
     */
    public void onProgress(float percent) {
        if (onProgress != null) {
            onProgress.call(percent);
        }
    }

    @Override
    public void onCompleted() {
        Log.e(TAG, "onCompleted");
        clear();
        unsubscribe();
    }

    @Override
    public void onError(Throwable e) {
        if (isUnsubscribed() || checkNull() || errorId == 0) {
            return;
        }
        Log.e(TAG, "onError");
        View view = mReference.get();
        view.setBackgroundResource(errorId);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (isUnsubscribed() || checkNull() || placeholderId == 0) {
            return;
        }
        Log.e(TAG, "onStart");
        View view = mReference.get();
        view.post(() -> view.setBackgroundResource(placeholderId));
    }

    @Override
    public void onNext(T requestResult) {
        if (isUnsubscribed() || checkNull()) {
            return;
        }
        Log.e(TAG, "onNext");
        View view = mReference.get();
        view.post(() -> view.setBackgroundResource(0));
//        view.setBackgroundResource(0);
        if (view instanceof ImageView) {
            final ImageView imageView = ((ImageView) view);
            if (mScaleType != null) {
                imageView.setScaleType(mScaleType);
            }
            imageView.setImageBitmap(requestResult);
        } else {
            if (Utils.hasJellyBean()) {
                view.setBackground(new BitmapDrawable(view.getResources(), requestResult));
            } else {
                //noinspection deprecation
                view.setBackgroundDrawable(new BitmapDrawable(view.getResources(), requestResult));
            }
        }
    }

    /**
     * @return true if we don't have a view
     */
    private boolean checkNull() {
        return mReference == null || mReference.get() == null;
    }

    public int getReqWidth() {
        return reqWidth;
    }

    public int getReqHeight() {
        return reqHeight;
    }

    public Bitmap.Config getConfig() {
        return mConfig;
    }

    public Bitmap.CompressFormat getCompressFormat() {
        return mCompressFormat;
    }

    public int getCompressQuality() {
        return mCompressQuality;
    }
}

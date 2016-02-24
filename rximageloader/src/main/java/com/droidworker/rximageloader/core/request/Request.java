package com.droidworker.rximageloader.core.request;

import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.droidworker.rximageloader.core.LoaderConfig;
import com.droidworker.rximageloader.core.LoaderCore;
import com.droidworker.rximageloader.core.request.manager.RequestManager;
import com.droidworker.rximageloader.core.transition.Transition;

import java.io.OutputStream;
import java.lang.ref.WeakReference;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Action1;
import rx.functions.Func1;

/**
 * This class contains all things that a task needed
 *
 * @author DroidWorkerLYF
 */
public abstract class Request extends Subscriber<Bitmap> {
    private static final String TAG = Request.class.getSimpleName();
    /**
     * The path of resource
     */
    protected String mPath;
    protected WeakReference<View> mReference;
    protected Action1<Float> onProgress;
    /**
     * If you load image into {@link ImageView}, you can set ScaleType to this request
     */
    protected ImageView.ScaleType mScaleType = null;
    /**
     * The required width
     */
    protected int reqWidth;
    /**
     * The required height
     */
    protected int reqHeight;
    /**
     * {@link android.graphics.Bitmap.Config}
     */
    protected Bitmap.Config mConfig;
    /**
     * {@link android.graphics.Bitmap.CompressFormat}
     */
    protected Bitmap.CompressFormat mCompressFormat;
    /**
     * The compress quality
     */
    protected int mCompressQuality;
    /**
     * If this is true, then we will not cache the bitmap in memory
     */
    protected boolean skipCacheInMem;
    /**
     * If this is true, the we will not cache the bitmap in disk
     */
    protected boolean skipCacheInDisk;
    /**
     * When {@link Subscriber#onError(Throwable)} called, set this to view's background
     */
    protected int errorId;
    /**
     * Before load an image, set this to view's background
     */
    protected int placeholderId;
    /**
     * This {@link Subscriber} is used to notify the {@link RequestManager} that the request has
     * been created and we shall perform a load task
     */
    protected Action1<Request> internalSubscriber;
    protected boolean resized;
    protected Func1<Bitmap, Bitmap> mTransformer = bitmap -> bitmap;
    protected Transition mTransition;

    public Request() {
        LoaderConfig loaderConfig = LoaderCore.getGlobalConfig();
        mConfig = loaderConfig.mConfig;
//        reqWidth = reqHeight = loaderConfig.screenWidth / 4;
    }

    /**
     * @param subscriber set this {@link Subscriber} as notify subscriber
     */
    public void setNotifySubscriber(Action1<Request> subscriber) {
        this.internalSubscriber = subscriber;
    }

    /**
     * Set path
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
     * Set resource id used for {@link Subscriber#onError(Throwable)}
     *
     * @param resId the resource id
     * @return this request
     */
    public Request error(int resId) {
        this.errorId = resId;
        return this;
    }

    /**
     * Set resource id used for {@link Subscriber#onStart()}
     *
     * @param resId the resource id
     * @return this request
     */
    public Request placeholder(int resId) {
        this.placeholderId = resId;
        return this;
    }

    /**
     * Set scaleType
     *
     * @param scaleType {@link android.widget.ImageView.ScaleType}
     * @return this request
     */
    public Request scaleType(ImageView.ScaleType scaleType) {
        this.mScaleType = scaleType;
        return this;
    }

    /**
     * Set config
     *
     * @param config {@link android.graphics.Bitmap.Config}
     * @return this request
     */
    public Request localConfig(Bitmap.Config config) {
        this.mConfig = config;
        return this;
    }

    /**
     * Set compressFormat
     *
     * @param compressFormat {@link android.graphics.Bitmap.CompressFormat}
     * @return this request
     */
    public Request localFormat(Bitmap.CompressFormat compressFormat) {
        this.mCompressFormat = compressFormat;
        return this;
    }

    /**
     * Set compress quality
     *
     * @param compressQuality use for {@link Bitmap#compress(Bitmap.CompressFormat, int, OutputStream)}
     * @return this request
     */
    public Request localQuality(int compressQuality) {
        this.mCompressQuality = compressQuality;
        return this;
    }

    /**
     * Set the require size of this request
     *
     * @param width  require width
     * @param height require height
     * @return this request
     */
    public Request resize(int width, int height) {
        this.reqWidth = width;
        this.reqHeight = height;
        resized = true;
        return this;
    }

    public Request transform(Func1<Bitmap, Bitmap> transformer) {
        this.mTransformer = transformer;
        return this;
    }

    public Request transition(Transition transition) {
        this.mTransition = transition;
        return this;
    }

    /**
     * Set the view will be used to set the bitmap and notify {@link RequestManager} to trigger
     * this request
     *
     * @param view the container
     */
    public void into(View view) {
        prepareView(view);
        Observable.just(this).subscribe(internalSubscriber);
    }

    /**
     * Set the view will be used to set the bitmap and create a new load task, you should
     * manage this request by yourself
     *
     * @param view the container
     * @return An Observable of load task
     */
    public abstract Observable<Bitmap> observable(View view);

    /**
     * Prepare reference of the view
     *
     * @param view the container
     */
    protected void prepareView(View view) {
        if (view == null) {
            throw new IllegalArgumentException("can not load into a null object");
        }

        if (view instanceof ImageView) {
            final ImageView imageView = ((ImageView) view);
            if (mScaleType != null) {
                imageView.setScaleType(mScaleType);
            }
            imageView.setImageResource(placeholderId);
        }else {
            view.setBackgroundResource(placeholderId);
        }

        if (mReference != null) {
            mReference.clear();
        }
        mReference = new WeakReference<>(view);
    }

    /**
     * @return the path
     */
    public String getPath() {
        return mPath;
    }

    /**
     * @return key
     */
    public String getKey() {
        getReqWidth();
        if (resized) {
            return getReqWidth() + "_" + getReqHeight() + "_" + getRawKey();
        }
        return getRawKey();
    }

    public String getRawKey() {
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
     * @return the required width
     */
    public int getReqWidth() {
        if (resized) {
            Log.e(TAG, "resized " + resized + " " + reqWidth);
            return reqWidth;
        }
        if (!checkNull()) {
            View view = getAttachedView();
            if (view.getMeasuredWidth() != 0) {
                resized = true;
                reqWidth = view.getMeasuredWidth();
            }
        }
        Log.e(TAG, "resized " + resized + " " + reqWidth);
        return reqWidth;
    }

    /**
     * @return the required height
     */
    public int getReqHeight() {
        if (resized) {
            return reqHeight;
        }
        if (!checkNull()) {
            View view = getAttachedView();
            if (view.getMeasuredHeight() != 0) {
                resized = true;
                reqHeight = view.getMeasuredHeight();
            }
        }
        return reqHeight;
    }

    /**
     * @return config
     */
    public Bitmap.Config getConfig() {
        return mConfig;
    }

    /**
     * @return compress format
     */
    public Bitmap.CompressFormat getCompressFormat() {
        return mCompressFormat;
    }

    /**
     * @return compress quality
     */
    public int getCompressQuality() {
        return mCompressQuality;
    }

    public Func1<Bitmap, Bitmap> getTransformer() {
        return mTransformer;
    }

    public boolean isResized(){
        return resized;
    }

    /**
     * Clear
     */
    public void clear() {
        mPath = null;
        if (mReference != null) {
            mReference.clear();
        }
        onProgress = null;
        mScaleType = null;
        reqWidth = reqHeight = LoaderCore.getGlobalConfig().screenWidth / 4;
        mConfig = null;
        mCompressFormat = null;
        mCompressQuality = 100;
        skipCacheInMem = false;
        skipCacheInDisk = false;
        errorId = 0;
        placeholderId = 0;
        internalSubscriber = null;
        resized = false;
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
        clear();
        unsubscribe();
    }

    @Override
    public void onError(Throwable e) {
        if (isUnsubscribed() || checkNull() || errorId == 0) {
            return;
        }
        Log.e(TAG, e.getMessage());
        View view = mReference.get();
        if (view instanceof ImageView) {
            ((ImageView) view).setImageResource(errorId);
        } else {
            view.setBackgroundResource(errorId);
        }
    }

    /**
     * @return true if we don't have a view
     */
    protected boolean checkNull() {
        return mReference == null || mReference.get() == null;
    }
}

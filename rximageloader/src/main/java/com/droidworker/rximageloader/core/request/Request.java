package com.droidworker.rximageloader.core.request;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.droidworker.rximageloader.core.LoaderConfig;
import com.droidworker.rximageloader.core.LoaderTask;
import com.droidworker.rximageloader.utils.Utils;

import java.io.OutputStream;
import java.lang.ref.WeakReference;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Action1;

/**
 * This class contains all things that a task needed
 *
 * @author DroidWorkerLYF
 */
public class Request extends Subscriber<Bitmap> {
    private static final String TAG = "Request";
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
    public int reqWidth;
    /**
     * The required height
     */
    public int reqHeight;
    /**
     * {@link android.graphics.Bitmap.Config}
     */
    public Bitmap.Config mConfig;
    /**
     * {@link android.graphics.Bitmap.CompressFormat}
     */
    public Bitmap.CompressFormat mCompressFormat;
    /**
     * The compress quality
     */
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
     * Set the view will be used to set the bitmap and notify {@link RequestManager} to trigger
     * this request
     *
     * @param view the container
     */
    public void into(View view) {
        prepare(view);
        Observable.just(this).subscribe(internalSubscriber);
    }

    /**
     * Set the view will be used to set the bitmap and create a new load task
     * @param view the container
     * @return An Observable of load task
     */
    public Observable<Bitmap> intoRx(View view){
        prepare(view);
        return LoaderTask.newTask(this);
    }

    private void prepare(View view){
        if (view == null) {
            throw new IllegalArgumentException("can not load into a null object");
        }
        if (mReference != null) {
            mReference.clear();
        }
        mReference = new WeakReference<>(view);
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

    /**
     * @return key
     */
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
    public void onNext(Bitmap requestResult) {
        if (isUnsubscribed() || checkNull()) {
            return;
        }
        Log.e(TAG, "onNext");
        View view = mReference.get();
        view.post(() -> view.setBackgroundResource(0));
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

    /**
     * @return the required width
     */
    public int getReqWidth() {
        return reqWidth;
    }

    /**
     * @return the required height
     */
    public int getReqHeight() {
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
}

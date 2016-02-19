package com.droidworker.rximageloader.core.request;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.view.View;
import android.widget.ImageView;

import com.droidworker.rximageloader.core.LoaderConfig;
import com.droidworker.rximageloader.core.LoaderCore;
import com.droidworker.rximageloader.core.LoaderTask;
import com.droidworker.rximageloader.core.transition.Transition;
import com.droidworker.rximageloader.utils.Utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
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
public class Request extends Subscriber<Bitmap> {
    private static final String TAG = "Request";
    /**
     * The path of resource
     */
    private String mPath;
    private WeakReference<View> mReference;
    private Action1<Float> onProgress;
    /**
     * If you load image into {@link ImageView}, you can set ScaleType to this request
     */
    private ImageView.ScaleType mScaleType = null;
    /**
     * The required width
     */
    private int reqWidth;
    /**
     * The required height
     */
    private int reqHeight;
    /**
     * {@link android.graphics.Bitmap.Config}
     */
    private Bitmap.Config mConfig;
    /**
     * {@link android.graphics.Bitmap.CompressFormat}
     */
    private Bitmap.CompressFormat mCompressFormat;
    /**
     * The compress quality
     */
    private int mCompressQuality;
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
    private Action1<Request> internalSubscriber;
    private boolean resized;
    private Func1<Bitmap, Bitmap> mTransformer = bitmap -> bitmap;
    private Transition mTransition;

    public Request() {
        LoaderConfig loaderConfig = LoaderCore.getGlobalConfig();
        mConfig = loaderConfig.mConfig;
        reqWidth = reqHeight = loaderConfig.screenWidth / 4;
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
    public Observable<Bitmap> observable(View view) {
        prepareView(view);
        return LoaderTask.newTask(this).map(mTransformer);
    }

    /**
     * Turn the given bitmap to byte[]
     *
     * @param view just use as a key
     * @return byte[] of bitmap
     */
    public Observable<byte[]> toByte(View view) {
        prepareView(view);
        return LoaderTask.newTask(this)
                .flatMap(bitmap -> {
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    bitmap.compress(mCompressFormat != null ?
                                    mCompressFormat : LoaderCore.getGlobalConfig().mCompressFormat,
                            100, byteArrayOutputStream);
                    Observable observable = Observable.just(byteArrayOutputStream.toByteArray());
                    try {
                        byteArrayOutputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return observable;
                });
    }

    /**
     * Prepare reference of the view
     *
     * @param view the container
     */
    private void prepareView(View view) {
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
            return reqWidth;
        }
        if (!checkNull()) {
            View view = getAttachedView();
            if (view.getMeasuredWidth() != 0) {
                resized = true;
                reqWidth = view.getMeasuredWidth();
            }
        }
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
        View view = mReference.get();
        if (view instanceof ImageView) {
            ((ImageView) view).setImageResource(errorId);
        } else {
            view.setBackgroundResource(errorId);
        }
    }

    @Override
    public void onNext(Bitmap requestResult) {
        if (isUnsubscribed() || checkNull()) {
            return;
        }
        View view = mReference.get();
        view.post(() -> view.setBackgroundResource(0));

        if (mTransition == null) {
            setResult(requestResult, view);
        } else {
            mTransition.getOut().addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);

                    setResult(requestResult, view);
                    view.setVisibility(View.VISIBLE);

                    mTransition.getIn().addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            mTransition.destroy();
                        }
                    });
                    mTransition.getIn().start();
                }
            });
            mTransition.getOut().start();
        }
    }

    /**
     * Set result to the view
     *
     * @param requestResult result bitmap
     * @param view          the container
     */
    private void setResult(Bitmap requestResult, View view) {
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

    /**
     * @return true if we don't have a view
     */
    private boolean checkNull() {
        return mReference == null || mReference.get() == null;
    }
}

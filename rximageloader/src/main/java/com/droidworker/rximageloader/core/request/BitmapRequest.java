package com.droidworker.rximageloader.core.request;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.view.View;
import android.widget.ImageView;

import com.droidworker.rximageloader.core.LoaderTask;
import com.droidworker.rximageloader.core.request.manager.RequestManager;
import com.droidworker.rximageloader.utils.Utils;

import rx.Observable;

/**
 * @author DroidWorkerLYF
 */
public class BitmapRequest extends Request<Bitmap> {

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

    @Override
    public Observable<Bitmap> observable(View view) {
        prepareView(view);
        return LoaderTask.bitmapTask(this).map(mTransformer);
    }

    @Override
    public void onNext(Bitmap bitmap) {
        if (isUnsubscribed() || checkNull()) {
            return;
        }
        View view = mReference.get();
        view.post(() -> view.setBackgroundResource(0));

        if (mTransition == null) {
            setResult(bitmap, view);
        } else {
            mTransition.getOut().addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);

                    setResult(bitmap, view);
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
}

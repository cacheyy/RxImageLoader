package com.droidworker.rximageloader.core.transition;

import android.animation.ObjectAnimator;
import android.view.View;

/**
 * Fade out and Fade in animators
 *
 * @author DroidWorkerLYF
 */
public class CrossFade extends Transition {

    /**
     * Initialize a default CrossFade transition
     *
     * @param view the animated view
     */
    public CrossFade(View view) {
        out = ObjectAnimator.ofFloat(view, "alpha", 0).setDuration(200);
        in = ObjectAnimator.ofFloat(view, "alpha", 1).setDuration(200);
    }

    private CrossFade(ObjectAnimator out, ObjectAnimator in) {
        super(out, in);
    }

    /**
     * Initialize a CrossFade transition according to the given time
     *
     * @param view    the animated view
     * @param outTime time of out animator
     * @param inTime  time of in animator
     */
    public CrossFade(View view, int outTime, int inTime) {
        out = ObjectAnimator.ofFloat(view, "alpha", 0).setDuration(outTime);
        in = ObjectAnimator.ofFloat(view, "alpha", 1).setDuration(inTime);
    }
}

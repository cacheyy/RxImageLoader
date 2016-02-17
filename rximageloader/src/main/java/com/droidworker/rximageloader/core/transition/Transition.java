package com.droidworker.rximageloader.core.transition;

import android.animation.ObjectAnimator;

/**
 * Use {@link ObjectAnimator} to make transition,
 * out represents the loading picture dismiss animation,
 * int represents the result bitmap showing animation
 *
 * @author DroidWorkerLYF
 */
public class Transition {
    protected ObjectAnimator out;
    protected ObjectAnimator in;

    public Transition() {

    }

    public Transition(ObjectAnimator out, ObjectAnimator in) {
        this.out = out;
        this.in = in;
    }

    /**
     * @return ObjectAnimator out
     */
    public ObjectAnimator getOut() {
        return out;
    }

    /**
     * @return ObjectAnimator in
     */
    public ObjectAnimator getIn() {
        return in;
    }

    /**
     * Destroy resources
     */
    public void destroy() {
        clear(out);
        clear(in);
        out = in = null;
    }

    private void clear(ObjectAnimator animator) {
        animator.removeAllListeners();
    }
}

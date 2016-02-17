package com.droidworker.rximageloader.core.transition;

import android.animation.ObjectAnimator;
import android.view.View;

/**
 * @author DroidWorkerLYF
 */
public class CrossFade extends Transition{

    public CrossFade(View view){
        out = ObjectAnimator.ofFloat(view, "alpha", 0).setDuration(200);
        in = ObjectAnimator.ofFloat(view, "alpha", 1).setDuration(200);
    }

    private CrossFade(ObjectAnimator out, ObjectAnimator in) {
        super(out, in);
    }
}

package com.droidworker.rximageloader.core.transition;

import android.animation.ObjectAnimator;

/**
 * @author DroidWorkerLYF
 */
public class Transition {
    protected ObjectAnimator out;
    protected ObjectAnimator in;

    public Transition(){

    }
    
    public Transition(ObjectAnimator out, ObjectAnimator in){
        this.out = out;
        this.in = in;
    }

    public ObjectAnimator getOut(){
        return out;
    }

    public ObjectAnimator getIn(){
        return in;
    }

    public void destroy(){
        clear(out);
        clear(in);
        out = in = null;
    }

    private void clear(ObjectAnimator animator){
        animator.removeAllListeners();
    }
}

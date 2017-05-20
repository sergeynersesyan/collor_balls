package com.example.mrnersesyan.ballor;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.TimeAnimator;
import android.animation.ValueAnimator;
import android.os.CountDownTimer;
import android.view.animation.AnimationSet;
import android.widget.TextView;

import com.example.mrnersesyan.ballor.listener.TimerListener;

/**
 * Created by Mr Nersesyan on 15/02/2017.
 */

public class Timer extends CountDownTimer {

    private TimerListener listener;
    public boolean isWorking;

    /**
     * @param millisInFuture    The number of millis in the future from the call
     *                          to {@link #start()} until the countdown is done and {@link #onFinish()}
     *                          is called.
     * @param countDownInterval The interval along the way to receive
     *                          {@link #onTick(long)} callbacks.
     */
    public Timer(long millisInFuture, long countDownInterval, TimerListener tl) {
        super(millisInFuture, countDownInterval);
        listener = tl;
    }

    @Override
    public void onTick(long millisUntilFinished) {
        listener.onTimeTick(millisUntilFinished);
        isWorking = true;
    }

    @Override
    public void onFinish() {
        listener.onFinishTime();
        isWorking = false;
    }
}

package com.example.mrnersesyan.ballor.listener;

/**
 * Created by Mr Nersesyan on 20/02/2017.
 */

public interface TimerListener {
    void onTimeTick (long millisUntilFinished);
    void onFinishTime ();
}

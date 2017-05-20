package com.example.mrnersesyan.ballor;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.mrnersesyan.ballor.listener.OnPassLineListener;
import com.example.mrnersesyan.ballor.listener.TimerListener;

public class MainActivity extends AppCompatActivity {

    private TextView timerText;
    private TextView resultText;
    private TextView bestResultText;
    private TextView playText;
    private CanvasView playField;
    private Timer timer;
    private SharedPreferences prefs;
    private static final String BEST_RESULT_KEY = "bresKey";
    private ValueAnimator animationGoDown;
    private ValueAnimator animationGoUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        timerText = (TextView) findViewById(R.id.timer_textView);
        resultText = (TextView) findViewById(R.id.result_textview);
        bestResultText = (TextView) findViewById(R.id.best_result_textview);
        playText = (TextView) findViewById(R.id.play_textview);
        playField = (CanvasView) findViewById(R.id.canvas_view);
        playField.setDistanceText((TextView) findViewById(R.id.distance_text));
        playField.setOnPassLineListener(new OnPassLineListener() {
            @Override
            public void onPass() {
                if (timer != null) {
                    timer.cancel();
                }
                startNewTimer();
            }
        });

        playText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateDown();
                playField.isPlayMode = true;
                playField.startNewGame();
            }
        });
    }

    private void animateDown() {
        final Ball mainBall = playField.getMainBall();
        final int radiusInStart = mainBall.getRadius();
//                 if (animationGoDown == null) {
        animationGoDown = ValueAnimator.ofInt(Math.round(mainBall.getY()), playField.getHeight() / 6);
        animationGoDown.setDuration(1000); //one second
        animationGoDown.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mainBall.setY((int) animation.getAnimatedValue());
                mainBall.setRadius((1 - animation.getAnimatedFraction()) * (radiusInStart - playField.getMainRadius()) + playField.getMainRadius());
                playField.invalidate();
            }
        });

        animationGoDown.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                playText.setVisibility(View.GONE);
                resultText.setVisibility(View.GONE);
                bestResultText.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (timer == null || !timer.isWorking) {
                    startNewTimer();
                    playField.setDoNotFinish(false);
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });
//                }
        animationGoDown.start();
    }

    @Override
    protected void onStart() {
        super.onStart();
        playField.post(new Runnable() {
            @Override
            public void run() {
                prepareNewGame(false);
            }
        });
        prefs = getPreferences(MODE_PRIVATE);
    }

    private void startNewTimer() {
        timer = new Timer(Constants.playTimeInSeconds * 1000, 100, new TimerListener() {
            @Override
            public void onTimeTick(long millisUntilFinished) {
                String timeLeft = "" + (millisUntilFinished / 100) / 10f;
                timerText.setText(timeLeft);
            }

            @Override
            public void onFinishTime() {
                prepareNewGame(true);
            }
        });
        timer.start();
    }

    private void prepareNewGame(final boolean showResult) {
        timerText.setText("0.0");

        final Ball mainBall = playField.getMainBall();
        final int radiusInStart = mainBall.getRadius();
//        if (animationGoUp == null) {
        animationGoUp = ValueAnimator.ofInt(Math.round(mainBall.getY()), playField.getHeight() / 2);
        animationGoUp.setDuration(1000); //one second
        animationGoUp.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                if (playField.doNotFinish()) {
                    animationGoUp.cancel();
                    animateDown();
                    System.out.println("heey cancel");
                    playField.setDoNotFinish(false);
                    return;
                }
                mainBall.setY((int) animation.getAnimatedValue());
                mainBall.setRadius(animation.getAnimatedFraction() * (playField.getWidth() / 2 - radiusInStart) + radiusInStart);
                System.out.println("heey setradius " + animation.getAnimatedFraction() * (playField.getWidth() / 2 - radiusInStart) + radiusInStart);
                playField.invalidate();
            }
        });

        animationGoUp.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                int bestResult = prefs.getInt(BEST_RESULT_KEY, 0);
                int currentResult = playField.countOfPassedBalls();
                if (bestResult < currentResult) {
                    bestResult = currentResult;
                    prefs.edit().putInt(BEST_RESULT_KEY, bestResult).apply();
                }
                playText.setVisibility(View.VISIBLE);
                if (showResult) {
                    resultText.setText("Your result: " + currentResult);
                    resultText.setVisibility(View.VISIBLE);
                }
                bestResultText.setText("Best result: " + bestResult);
                bestResultText.setVisibility(View.VISIBLE);
                playField.isPlayMode = false;
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });
//        }
        animationGoUp.start();
    }
}

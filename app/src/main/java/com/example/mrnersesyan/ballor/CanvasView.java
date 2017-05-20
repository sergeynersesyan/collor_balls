package com.example.mrnersesyan.ballor;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.example.mrnersesyan.ballor.listener.OnPassLineListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CanvasView extends View {

    private Paint paintCircle, paintLine;
    private final int RADIUS = 60;
    private Random random;
    private List<Ball> balls;
    private List<Ball> topBalls;
    private int currentColor;
    private TextView distanceText;
    private float similarityDistance = 60;
    private Ball mainBall;
    private boolean doNotFinish;
    public boolean isPlayMode = true;

    public boolean doNotFinish() {
        return doNotFinish;
    }

    public void setDoNotFinish(boolean b) {
        doNotFinish = b;
    }
    public void setDistanceText(TextView distanceText) {
        this.distanceText = distanceText;
        distanceText.setText(getContext().getString(R.string.color_distance) + " < " + Math.round(similarityDistance));
    }

    public void setOnPassLineListener(OnPassLineListener onPassLineListener) {
        this.onPassLineListener = onPassLineListener;
    }

    OnPassLineListener onPassLineListener;

    public CanvasView(Context context) {
        this(context, null);
    }

    public CanvasView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CanvasView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        random = new Random();
        balls = new ArrayList<>();
        topBalls = new ArrayList<>();
        paintCircle = new Paint();
        paintCircle.setColor(Color.RED);
        paintLine = new Paint();
        paintLine.setStrokeWidth(5);
        currentColor = getRandomColor();
        mainBall = new Ball(0, 0, currentColor, getMainRadius());
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mainBall.setX(getWidth() / 2);
        mainBall.setY(getHeight() / 6);
        paintLine.setColor(currentColor);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (Ball ball : balls) {
            paintCircle.setColor(ball.getColor());
            canvas.drawCircle(ball.getX(), ball.getY(), ball.getRadius(), paintCircle);
        }
        for (Ball ball : topBalls) {
            paintCircle.setColor(ball.getColor());
            canvas.drawCircle(ball.getX(), ball.getY(), ball.getRadius(), paintCircle);
        }
        paintCircle.setColor(mainBall.getColor());
        canvas.drawCircle(mainBall.getX(), mainBall.getY(), mainBall.getRadius(), paintCircle);
        canvas.drawLine(0, getHeight() / 3, getWidth(), getHeight() / 3, paintLine);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getActionMasked();
        int actionIndex = event.getActionIndex();
        //int pointerIndex = event.//try using pointerindex
        //Ball point = null;

        switch (action) {
            case MotionEvent.ACTION_DOWN: {
//            case MotionEvent.ACTION_POINTER_DOWN: {
                doNotFinish = false;
                float x = event.getX(actionIndex);
                float y = event.getY(actionIndex);
                if (y < getHeight() / 3) {
                    break;
                }

                Ball b = findBall(x, y);

                if (b == null) {
                    //int pointerID = event.getPointerId(actionIndex);
                    b = new Ball(x, y, getRandomColor(), RADIUS);
                    balls.add(b);
                    b.isFirstClick = true;
                } else {
                    b.setChangeColor(true);
                    b.isFirstClick = false;
                    balls.remove(b);
                    balls.add(b);
//                  b.pointerID = event.getPointerId(actionIndex);
                }

                b.setSelected(true);
                break;
            }

            case MotionEvent.ACTION_MOVE:
                for (Ball ball : balls) {
                    if (ball.isSelected() && !ball.isFirstClick) {
//                        int pIndex = event.findPointerIndex(ball.pointerID);
                        double distance = colourDistanceYUV(currentColor, ball.getColor());
                        if (event.getY(actionIndex) - RADIUS > getHeight() / 3) { // not crossed line
                            ball.setY(event.getY(actionIndex));
                            ball.setPassed(false);
                        } else if (Math.round(distance) <= Math.round(similarityDistance)) {
                            ball.setY(event.getY(actionIndex));
                            if (!ball.isPassed()) {
                                distanceText.setText(getContext().getString(R.string.color_distance) + (" " + Math.round(distance) + " < " + Math.round(similarityDistance)));
                                ball.setPassed(true);
                            }
                        } else {
                            if (ball.getRadius() == RADIUS) {
                                distanceText.setText(getContext().getString(R.string.color_distance) + (" " + Math.round(distance) + " > " + Math.round(similarityDistance)));
                            }
                            ball.setRadius(ball.getRadius() + 5);
                            ball.setY(getHeight() / 3 + ball.getRadius() + 3);
                        }
                        ball.setX(event.getX(actionIndex));
                        if (event.getEventTime() - event.getDownTime() > 40) {
                            ball.setChangeColor(false);
                        }
                        break;
                    }
                }
                break;

//            case MotionEvent.ACTION_POINTER_UP:
            case MotionEvent.ACTION_UP:
                for (int i = balls.size() - 1; i >= 0; i--) {
                    Ball b = balls.get(i);
                    if (b.isSelected()) {
                        if (b.isChangeColor()) {
                            b.setColor(getRandomColor());
                        }
                        if (b.isPassed() && isPlayMode) {
                            changeLevel(b);
                        }
                        b.setSelected(false);
                        distanceText.setText(getContext().getString(R.string.color_distance) + " < " + Math.round(similarityDistance));
                        break;
                    }
                }
        }
        invalidate();
        return true;
    }

    private void changeLevel(@Nullable Ball passedBall) {
        doNotFinish = true;
        if (passedBall != null) {
            passedBall.setRadius(RADIUS - RADIUS / 3);
            topBalls.add(passedBall);
            if (onPassLineListener != null) {
                onPassLineListener.onPass();
            }
        }
        balls.clear();
        currentColor = getRandomColor();
        if (similarityDistance > 50) {
            similarityDistance--;
        } else if (similarityDistance > 40) {
            similarityDistance -= 0.5;
        } else if (similarityDistance > 30) {
            similarityDistance -= 0.25;
        } else {
            similarityDistance -= 0.1;
        }
        for (Ball ball : topBalls) {
            ball.setColor(getSimilarColor(currentColor, Math.round((similarityDistance - 10))));
        }
        mainBall.setColor(currentColor);
        paintLine.setColor(currentColor);
        invalidate();
    }


    private Ball findBall(float cord1, float cord2) {
        float distance;
        Ball p = null;
        for (int i = balls.size() - 1; i >= 0; i--) {
            Ball ball = balls.get(i);
            distance = (float) Math.sqrt((cord1 - ball.getX()) * (cord1 - ball.getX()) + (cord2 - ball.getY()) * (cord2 - ball.getY()));
            if (distance < ball.getRadius()) {
                p = ball;
                break;
            }
        }
        return p;
    }

    private int getRandomColor() {
        return Color.argb(255, random.nextInt(256), random.nextInt(256), random.nextInt(256));
    }

    private int getSimilarColor(int color, int distance) {
        int simColor;
        do {
            simColor = Color.rgb(rgbProxy(Color.red(color), distance), rgbProxy(Color.green(color), distance), rgbProxy(Color.blue(color), distance));
        } while (colourDistanceYUV(color, simColor) >= distance);
        return simColor;
    }

    private double colourDistance(int e1, int e2) {
        long rmean = (Color.red(e1) + Color.red(e2)) / 2;
        long r = Color.red(e1) - Color.red(e2);
        long g = Color.green(e1) - Color.green(e2);
        long b = Color.blue(e1) - Color.blue(e2);
        return Math.sqrt((((512 + rmean) * r * r) >> 8) + 4 * g * g + (((767 - rmean) * b * b) >> 8));
    }

    private double colourDistanceSimple(int c1, int c2) {
        int redDistance = (Color.red(c1) - Color.red(c2));
        int greenDistance = (Color.green(c1) - Color.green(c2));
        int blueDistance = (Color.blue(c1) - Color.blue(c2));
        return (float) Math.sqrt(redDistance * redDistance + greenDistance * greenDistance + blueDistance * blueDistance);
    }

    private double colourDistanceYUV(int c1, int c2) {
        return colourDistanceYUV(rgbToyuv(c1), rgbToyuv(c2));
    }

    private double colourDistanceYUV(YUVcolor yuv1, YUVcolor yuv2) {
        return Math.sqrt((yuv1.y - yuv2.y) * (yuv1.y - yuv2.y) / 10 + (yuv1.u - yuv2.u) * (yuv1.u - yuv2.u) + (yuv1.v - yuv2.v) * (yuv1.v - yuv2.v));
    }

    private YUVcolor rgbToyuv(int color) {
        int r = Color.red(color);
        int g = Color.green(color);
        int b = Color.blue(color);
        double y = 0.299 * r + 0.587 * g + 0.114 * b;  // 0-237
        double u = -0.147 * r - 0.289 * g + 0.436 * b; // -111, 111
        double v = 0.615 * r - 0.515 * g - 0.100 * b;  // -156, 156
        return new YUVcolor(y, u, v);
    }

    private int rgbProxy(int rORgORb, int delta) {
        return Math.min(Math.max(rORgORb + random.nextInt(2 * delta) - delta, 0), 255);
    }

    public Ball getMainBall() {
        return mainBall;
    }

    public int countOfPassedBalls() {
        return topBalls.size();
    }

    public void startNewGame() {
        topBalls.clear();
        balls.clear();
        currentColor = getRandomColor();
        mainBall.setColor(currentColor);
        paintLine.setColor(currentColor);
        similarityDistance = 60;
        distanceText.setText(getContext().getString(R.string.color_distance) + " < " + Math.round(similarityDistance));
    }

    public int getMainRadius() {
        return RADIUS + RADIUS / 3;
    }

}
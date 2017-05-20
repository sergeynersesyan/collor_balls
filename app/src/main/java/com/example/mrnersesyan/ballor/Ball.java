package com.example.mrnersesyan.ballor;

import android.graphics.Color;

import java.util.Random;

/**
 * Created by Mr Nersesyan on 12/02/2017.
 */

public class Ball {
    private float x;
    private float y;
    private int radius;
    private int color;
    private boolean changeColor;
    private boolean selected;
    private boolean passed;
    public boolean isFirstClick;

    public boolean isPassed() {
        return passed;
    }

    public void setPassed(boolean passed) {
        this.passed = passed;
    }

//    int pointerID;

    public Ball(float x, float y, int color, int r) {
        this.x = x;
        this.y = y;
        this.color = color;
        radius = r;
//        pointerID = id;
    }

    public Ball () {
        this(0,0,0,0);
    }

    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    public void setRadius(float radius) {
        this.radius = Math.round(radius);
    }

    public void setX(float x) {
        this.x = x;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public void setChangeColor(boolean a) {
        this.changeColor = a;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public int getColor() {
        return color;
    }

    public boolean isChangeColor() {
        return changeColor;
    }

    public boolean isSelected() {
        return selected;
    }

}

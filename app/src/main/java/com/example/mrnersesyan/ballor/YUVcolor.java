package com.example.mrnersesyan.ballor;

import android.graphics.Color;

/**
 * Created by Mr Nersesyan on 18/02/2017.
 */

public class YUVcolor {
    double y;
    double u;
    double v;

    static final int maxY = 237;
    static final int minY = 0;
    static final int maxU = 111;
    static final int minU = -111;
    static final int maxV = 156;
    static final int minV = -156;

    YUVcolor(double yy, double uu, double vv) {
        y = yy;
        u = uu;
        v = vv;
    }

//    public int toRGB() {
//        Double r = y + 1.140 * (v-128);
//        Double g = y - 0.395 * (u-128) - 0.581 * (v-128);
//        Double b = y + 2.032 * (u-128);
//        return Color.rgb(r.intValue(), g.intValue(), b.intValue());
//    }

}

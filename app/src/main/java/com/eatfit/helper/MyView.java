package com.eatfit.helper;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by Abhinav Suman on 8/22/2016.
 */
public class MyView extends View {

    Paint paint;
    Path path;
    int height, width, radius;

    public MyView(Context context, AttributeSet attrs) {
        super(context, attrs);
        //init();
    }

    public void init(String color) {
        paint = new Paint();
        paint.setColor(Color.parseColor(color));
        paint.setStrokeWidth(10);
        paint.setStyle(Paint.Style.STROKE);

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        height = h;
        width = w;
        radius = 0;

        if (h > w) {
            radius = w / 2;
        } else {
            radius = h / 2;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // TODO Auto-generated method stub
        super.onDraw(canvas);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(width / 2, height / 2, radius, paint);
        //drawCircle(cx, cy, radius, paint)

    }

}

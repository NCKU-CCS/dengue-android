package com.example.dengue.dengue_android;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.view.View;

/**
 * Created by nana on 2016/5/8.
 */
public class DrawView extends View {

    public DrawView(Context context) {
        super(context);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // 创建画笔
        Paint p = new Paint();
        p.setColor(Color.BLUE);// 设置红色

        p.setAntiAlias(true);// 设置画笔的锯齿效果。 true是去除，大家一看效果就明白了
        p.setStyle(Paint.Style.FILL);//设置填满
        canvas.drawCircle(60, 30, 20, p);// 大圆

        Paint pt = new Paint();
        pt.setAntiAlias(true); //設置畫筆為無鋸齒
        pt.setColor(Color.GREEN); //設置畫筆顏色
        pt.setStyle(Paint.Style.FILL); //實心效果
        Rect rectCircle = new Rect(3, 3, 40, 40);
        RectF rectCircleF = new RectF(rectCircle);
        canvas.drawOval(rectCircleF, pt);

        Paint po = new Paint();
        po.setAntiAlias(true); //設置畫筆為無鋸齒
        po.setColor(Color.RED); //設置畫筆顏色
        po.setStrokeWidth((float) 3.0); //線寬
        po.setStyle(Paint.Style.STROKE); //空心效果
        Rect rectCircle2 = new Rect(6, 16, 40, 40);
        RectF rectCircleF2 = new RectF(rectCircle2);
        canvas.drawOval(rectCircleF2,po);

    }
}

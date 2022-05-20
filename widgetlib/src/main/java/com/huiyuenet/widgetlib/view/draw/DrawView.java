package com.huiyuenet.widgetlib.view.draw;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.huiyuenet.widgetlib.R;
import com.huiyuenet.widgetlib.logs.LogUtils;
import com.huiyuenet.widgetlib.model.CanvasPoint;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DrawView extends FrameLayout {
    private Context mContext;
    private ImageView imageView;

    private static final float STROKE_WIDTH = 10f;//画笔大小
    private static final float HALF_STROKE_WIDTH = STROKE_WIDTH / 2;
    private Paint paint = new Paint();
    private List<Integer> colors;
    private int drawCount = 0;//花了几笔
    private int colorCount = 10;//一个有几个颜色
    private Path path = new Path();
    private Canvas canvas;
    private Bitmap copyBitmap;
    private List<List<CanvasPoint>> strokes;//笔画
    private List<CanvasPoint> coordinate;//坐标

    private float lastTouchX;
    private float lastTouchY;

    public DrawView(@NonNull Context context) {
        super(context);
    }

    public DrawView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        strokes = new ArrayList<>();
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.layout_draw, this, true);
        imageView = view.findViewById(R.id.img_canvas);
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        copyBitmap = Bitmap.createBitmap(displayMetrics.widthPixels, displayMetrics.heightPixels, Bitmap.Config.ARGB_8888);
        canvas = new Canvas(copyBitmap);

        imageView.setImageBitmap(copyBitmap);

        paint.setAntiAlias(true);
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeWidth(STROKE_WIDTH);
        colors = new ArrayList<>();
        randomColor();
        initListener();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    private void initListener() {
        imageView.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                float eventX = event.getX();
                float eventY = event.getY();

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        lastTouchX = eventX;
                        lastTouchY = eventY;
                        coordinate = new ArrayList<>();
                        return true;

                    case MotionEvent.ACTION_MOVE:
                        CanvasPoint point;
                        //手指滑动过程中可能产生多个点位，系统会打包进行返回，放了滑动过程更平滑，需要将这些点位取出进行绘制
                        int historySize = event.getHistorySize();
                        if (historySize > 0) {
                            for (int i = 0; i < historySize; i++) {
                                point = new CanvasPoint();
                                float historicalX = event.getHistoricalX(i);
                                float historicalY = event.getHistoricalY(i);
                                point.startX = lastTouchX;
                                point.startY = lastTouchY;
                                point.endX = historicalX;
                                point.endY = historicalY;
                                point.paint = new Paint(paint);
                                canvas.drawLine(lastTouchX, lastTouchY, historicalX, historicalY, paint);
//                                path.moveTo(lastTouchX,lastTouchY);
//                                path.lineTo(historicalX, historicalY);
//                                canvas.drawPath(path, paint);
                                lastTouchX = historicalX;
                                lastTouchY = historicalY;
                                coordinate.add(point);
                                imageView.setImageBitmap(copyBitmap);
                            }
                        } else {
                            point = new CanvasPoint();
                            point.startX = lastTouchX;
                            point.startY = lastTouchY;
                            point.endX = eventX;
                            point.endY = eventY;
                            point.paint = new Paint(paint);
                            canvas.drawLine(lastTouchX, lastTouchY, eventX, eventY, paint);
//                            path.moveTo(lastTouchX,lastTouchY);
//                            path.lineTo(eventX, eventY);
//                            canvas.drawPath(path, paint);
                            lastTouchX = eventX;
                            lastTouchY = eventY;
                            coordinate.add(point);
                            imageView.setImageBitmap(copyBitmap);
                        }


                        break;
                    case MotionEvent.ACTION_UP:
                        lastTouchX = eventX;
                        lastTouchY = eventY;
                        drawCount++;
                        if (drawCount >= colors.size()) {
                            drawCount = colors.size() - 1;
                        }
                        paint.setColor(colors.get(drawCount));
                        strokes.add(coordinate);
                        break;

                    default:
                        return false;
                }


                return true;
            }
        });
    }

    /**
     * 生成随机颜色
     *
     * @return
     */
    public void randomColor() {
        Random random = new Random();
        for (int i = 0; i < colorCount; i++) {
            int red = random.nextInt(254);
            int green = random.nextInt(254);
            int blue = random.nextInt(254);
            colors.add(Color.rgb(red, green, blue));
        }
        drawColors();
    }


    private void drawColors() {
        int startX = 50;
        int startY = 5;
        int endX = startX;
        int endY = 20;
        CanvasPoint point;
        coordinate = new ArrayList<>();
        paint.setStrokeWidth(STROKE_WIDTH * 2);
        for (int i = 0; i < colorCount; i++) {
            point = new CanvasPoint();
            paint.setColor(colors.get(i));
            lastTouchX = startX;
            lastTouchY = startY;
            point.startX = lastTouchX;
            point.startY = lastTouchY;
            point.endX = endX;
            point.endY = endY;
            point.paint = new Paint(paint);
            canvas.drawLine(lastTouchX, lastTouchY, endX, endY, paint);
//            path.moveTo(lastTouchX,lastTouchY);
//            path.lineTo(endX, endY);
//            canvas.drawPath(path, paint);
            startX += 50;
            endX = startX;
            coordinate.add(point);
        }
        paint.setColor(colors.get(drawCount));
        paint.setStrokeWidth(STROKE_WIDTH);
        strokes.add(coordinate);
    }

    /**
     * 清除签名
     */
    public void clear() {
        canvas.drawColor(Color.WHITE);
    }

    public void revoke() {
        if (strokes.size() <= 1)
            return;
        strokes.remove(strokes.size() - 1);
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        copyBitmap = Bitmap.createBitmap(displayMetrics.widthPixels, displayMetrics.heightPixels, Bitmap.Config.ARGB_8888);
        canvas = new Canvas(copyBitmap);
        imageView.setImageBitmap(copyBitmap);
        CanvasPoint point = null;
        for (int i = 0; i < strokes.size(); i++) {
            coordinate = strokes.get(i);
            for (int j = 0; j < coordinate.size(); j++) {
                point = coordinate.get(j);
                float historicalX = point.endX;
                float historicalY = point.endY;
                lastTouchX = point.startX;
                lastTouchY = point.startY;
                canvas.drawLine(lastTouchX, lastTouchY, historicalX, historicalY, point.paint);
//                path.moveTo(lastTouchX,lastTouchY);
//                path.lineTo(historicalX, historicalY);
//                canvas.drawPath(path, point.paint);
            }
        }
        drawCount--;
        if (drawCount < 0) {
            drawCount = 0;
        }
        paint.setColor(colors.get(drawCount));
        imageView.setImageBitmap(copyBitmap);
    }
}

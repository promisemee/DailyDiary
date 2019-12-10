package com.example.daily;

import android.view.View;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.view.MotionEvent;

import java.util.ArrayList;

public class DrawView extends View {


    private Path drawPath;
    private Paint drawPaint, canvasPaint;
    private int paintColor = 0xFF000000;
    private Canvas drawCanvas;
    public Bitmap canvasBitmap;
    private ArrayList<Path> pathList = new ArrayList<Path>();
    private ArrayList<Path> undoList = new ArrayList<Path>();

    public DrawView(Context context){
        super(context);
        setupDrawing();
    }

    public int getListSize(){
        return pathList.size();
    }

    private void setupDrawing(){

        drawPath = new Path();
        drawPaint = new Paint();
        drawPaint.setColor(paintColor);
        drawPaint.setAntiAlias(true);
        drawPaint.setStrokeWidth(30);
        drawPaint.setStyle(Paint.Style.STROKE);
        drawPaint.setStrokeJoin(Paint.Join.ROUND);
        drawPaint.setStrokeCap(Paint.Cap.ROUND);
        canvasPaint = new Paint(Paint.DITHER_FLAG);
    }

    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged( w, h, oldw, oldh);
        canvasBitmap = Bitmap.createBitmap( w, h, Bitmap.Config.ARGB_8888);
        drawCanvas = new Canvas(canvasBitmap);
    }

    protected void onDraw(Canvas canvas) {
        for(Path p:pathList)
            canvas.drawPath(p, drawPaint);
        canvas.drawPath(drawPath, drawPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float touchX = event.getX();
        float touchY = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                undoList.clear();
                drawPath.reset();
                drawPath.moveTo(touchX, touchY);
                break;
            case MotionEvent.ACTION_MOVE:
                drawPath.lineTo(touchX, touchY);
                break;
            case MotionEvent.ACTION_UP:
                drawPath.lineTo(touchX, touchY);
                drawCanvas.drawPath(drawPath, drawPaint);
                pathList.add(drawPath);
                drawPath = new Path();

                break;
            default:
                return false;
        }
        invalidate();
        return true;
    }

    public void onClickUndo(){
        if(pathList.size()>0){
            undoList.add(pathList.remove(pathList.size()-1));
            invalidate();
        }
    }
}
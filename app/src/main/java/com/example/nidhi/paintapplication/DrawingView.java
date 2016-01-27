package com.example.nidhi.paintapplication;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.view.MotionEvent;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.TypedValue;

/**
 * Created by nidhi on 1/9/2016.
 */
public class DrawingView extends View {
    //drawing path
    private Path mDrawPath;
    //drawing and canvas paint
    private Paint mDrawPaint, mCanvasPaint;
    //initial color
    private int mPaintColor = Color.BLUE;
    //canvas
    private Canvas mDrawCanvas;
    //canvas bitmap
    private Bitmap mCanvasBitmap;
    private float mBrushSize, mLastBrushSize;
    private boolean mErase=false;


    public DrawingView(Context context, AttributeSet attrs){
        super(context, attrs);
        setupDrawing();
    }

    private void setupDrawing(){
        mBrushSize = getResources().getInteger(R.integer.medium_size);
        mLastBrushSize = mBrushSize;

        //get drawing area setup for interaction
        mDrawPath = new Path();
        mDrawPaint = new Paint();

        //set the default color
        mDrawPaint.setColor(mPaintColor);

        //set the initial path properties
        mDrawPaint.setAntiAlias(true);
        mDrawPaint.setStrokeWidth(mBrushSize);
        mDrawPaint.setStyle(Paint.Style.STROKE);
        mDrawPaint.setStrokeJoin(Paint.Join.ROUND);
        mDrawPaint.setStrokeCap(Paint.Cap.ROUND);

        mCanvasPaint = new Paint(Paint.DITHER_FLAG);

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        //view given size
        super.onSizeChanged(w, h, oldw, oldh);
        mCanvasBitmap = Bitmap.createBitmap(getMeasuredWidth()  > 0 ? getMeasuredWidth()  : 1,
                getMeasuredHeight() > 0 ? getMeasuredHeight() : 1,
                Bitmap.Config.ARGB_8888);
        mDrawCanvas = new Canvas(mCanvasBitmap);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //draw view
        canvas.drawBitmap(mCanvasBitmap, 0, 0, mCanvasPaint);
        canvas.drawPath(mDrawPath, mDrawPaint);
        super.onDraw(canvas);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //detect user touch
        float touchX = event.getX();
        float touchY = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mDrawPath.moveTo(touchX, touchY);
                break;
            case MotionEvent.ACTION_MOVE:
                mDrawPath.lineTo(touchX, touchY);
                break;
            case MotionEvent.ACTION_UP:
                mDrawCanvas.drawPath(mDrawPath, mDrawPaint);
                mDrawPath.reset();
                break;
            default:
                return false;
        }
        invalidate();
        return true;
    }

    public void setColor(int newColor){
        //set color
        invalidate();
        mPaintColor = newColor;
        mDrawPaint.setColor(mPaintColor);
    }

    public void setBrushSize(float newSize){
        //update size
        float pixelAmount = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                newSize, getResources().getDisplayMetrics());
        mBrushSize=pixelAmount;
        mDrawPaint.setStrokeWidth(mBrushSize);

    }

    public void setLastBrushSize(float lastSize){
        mLastBrushSize=lastSize;
    }
    public float getLastBrushSize(){
        return mLastBrushSize;
    }

    public void setErase(boolean isErase){
       //set erase true or false
        mErase=isErase;
        if(mErase) {

            mDrawPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
            mDrawPaint.setColor(Color.WHITE);//set the color to white
        }
        else {
            mDrawPaint.setColor(mPaintColor); //if erase is set to false, it will use the previous color.
            mDrawPaint.setXfermode(null);
        }

    }

    public void startNew(){
        mDrawCanvas.drawColor(0, PorterDuff.Mode.CLEAR);
        invalidate();
    }

}

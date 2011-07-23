package com.gomongo.app;

import android.graphics.PointF;
import android.view.MotionEvent;
import android.view.GestureDetector.SimpleOnGestureListener;

public class RotateGestureDetector extends SimpleOnGestureListener {
    
    private boolean mGestureInProgress = false;
    private double mLastAngleBetweenPoints;
    
    private OnRotateGestureListener mRotateListener;
    
    public RotateGestureDetector( OnRotateGestureListener listener ) {
        mRotateListener = listener;
    }
    
    public void onTouchEvent( MotionEvent event ) {
        
        
        
        switch( event.getActionMasked() ) {
        case MotionEvent.ACTION_POINTER_DOWN:
            if( !mGestureInProgress && event.getPointerCount() > 1 ) {
                
                PointF pointerPoint1 = new PointF( event.getX(0), event.getY(0) );
                PointF pointerPoint2 = new PointF( event.getX(1), event.getY(1) );
                
                mLastAngleBetweenPoints = getAngleBetweenPoints( pointerPoint1, pointerPoint2 );
                
                mGestureInProgress = true;
            }
            break;
        case MotionEvent.ACTION_MOVE:
            if( mGestureInProgress ) {
                PointF pointerPoint1 = new PointF( event.getX(0), event.getY(0) );
                PointF pointerPoint2 = new PointF( event.getX(1), event.getY(1) );
                
                double currentAngleBetweenPoints = getAngleBetweenPoints( pointerPoint1, pointerPoint2 );
                
                mRotateListener.onRotate(currentAngleBetweenPoints - mLastAngleBetweenPoints);
                
                mLastAngleBetweenPoints = currentAngleBetweenPoints;
            }
            break;
        case MotionEvent.ACTION_POINTER_UP:
            if( mGestureInProgress && event.getPointerCount() -1 < 2 ) {
                mGestureInProgress = false;
            }
            break;
        }
    }
    
    private double getAngleBetweenPoints( PointF point1, PointF point2 ) {
        float deltaX = point1.x - point2.x;
        float deltaY = point1.y = point2.y;
        
        return Math.toDegrees( Math.atan2(deltaX, deltaY) );
    }
}

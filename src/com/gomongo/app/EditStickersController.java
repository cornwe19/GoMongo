package com.gomongo.app;

import java.util.List;

import android.content.Context;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;

import com.gomongo.app.ui.MongoImage;

public class EditStickersController extends BaseStickerController {

    ScaleGestureDetector mScaleGestureDetector;
    RotateGestureDetector mRotateGestureDetector;
    
    Context mContext;
    
    public EditStickersController( Context context, List<MongoImage> images ) {
        super( images );
        mContext = context;
    }
    
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        
        float x = event.getX();
        float y = event.getY();
        
        if( mScaleGestureDetector != null ) {
            mScaleGestureDetector.onTouchEvent(event);
        }
        
        if( mRotateGestureDetector != null ) {
            mRotateGestureDetector.onTouchEvent(event);
        }
        
        switch ( event.getAction() & MotionEvent.ACTION_MASK ) {
        case MotionEvent.ACTION_DOWN:
            
            performHitTestToSelectImage(x, y);
            
            boolean hitTestFoundAnImage = mSelectedImage != null;
            if( hitTestFoundAnImage ) {
                mScaleGestureDetector = new ScaleGestureDetector( mContext, mSelectedImage );
                mRotateGestureDetector = new RotateGestureDetector( mSelectedImage );
            }
            
            break;
        case MotionEvent.ACTION_MOVE:
            if( mSelectedImage != null && !mScaleGestureDetector.isInProgress() ) {
                mSelectedImage.setCurrentPoint( event.getX(), event.getY() );
            }
            break;
        case MotionEvent.ACTION_UP:
            mSelectedImage = null;
            mScaleGestureDetector = null;
            break;
        }
        
        return true;
    }
    
    @Override
    public Paint getStickerDrawPaint() {
        // Don't need a special paint for this context
        return null;
    }

}

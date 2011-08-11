package com.gomongo.app.ui;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.Paint;
import android.graphics.Bitmap.Config;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

import com.gomongo.app.RotateGestureDetector;

public class AnnotationEditorView extends View {
	
	private static final String TAG = "AnnotationEditorView";
	private Context mContext; 
	private ScaleGestureDetector mScaleGestureDetector;
	private RotateGestureDetector mRotateGestureDetector;
	
	public AnnotationEditorView(Context context) {
		super(context);
		
		mContext = context;
	}		
	
	public AnnotationEditorView(Context context, AttributeSet attrs) {
		super(context,attrs);
		
		mContext = context;
	}
	
	@Override
	public void onDraw( Canvas canvas ) {
		Paint imagePaint = null;
		if( mIsDeleting ) {
		    imagePaint = new Paint();
		    int redTintColor = 0xFF3333;
            int lowGreyToDarkenRed = 0x333;
            ColorFilter filter = new LightingColorFilter( redTintColor, lowGreyToDarkenRed );
		    imagePaint.setColorFilter( filter );
		}
	    
	    for( MongoImage image : mImages ) {
			image.drawImage(canvas, imagePaint);
		}
	}
	
	public Bitmap prepareBitmap() {
		Bitmap compositeBitmap = Bitmap.createBitmap(getWidth(), getHeight(), Config.ARGB_8888 );
		
		Canvas virtualCanvas = new Canvas( compositeBitmap );
		
		draw( virtualCanvas );
		
		return compositeBitmap;
	}
	
	List<MongoImage> mImages = new ArrayList<MongoImage>();
	MongoImage mSelectedImage;
	
	public void addAnnotation( MongoImage image ) {
		mImages.add( image );
		invalidate();
	}
	
	boolean mIsDeleting = false;
	public void startDeleting() {
	    mIsDeleting = true;
	    invalidate();
	}

	public void stopDeleting() {
	    mIsDeleting = false;
	    invalidate();
	}
	
	@Override
	public boolean onTouchEvent( MotionEvent event ) {
		
	    float x = event.getX();
        float y = event.getY();
	    
	    if( mIsDeleting ) {
	        performHitTestToSelectImage(x, y);
	        
	        if( mSelectedImage != null ) {
	            mImages.remove(mSelectedImage);
	            invalidate();
	        }
	    }
	    else {
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
    				invalidate();
    			}
    			
    			break;
    		case MotionEvent.ACTION_MOVE:
    			if( mSelectedImage != null && !mScaleGestureDetector.isInProgress() ) {
    				mSelectedImage.setCurrentPoint( event.getX(), event.getY() );
    			}
    			invalidate();
    			break;
    		case MotionEvent.ACTION_UP:
    			mSelectedImage = null;
    			mScaleGestureDetector = null;
    			break;
    		}
	    }
	    
		return true;
	}

	private void performHitTestToSelectImage(float x, float y) {
	    // Read images in reverse so that topmost images will be selected
	    for( int imageIndex = mImages.size()-1; imageIndex >= 0; imageIndex-- ) {
			MongoImage image = mImages.get(imageIndex);
	        
			if( image.containsPoint(x, y) ) {
				Log.d( TAG, String.format( "Selecting image from point (%f,%f)", x, y ) );
				
				mSelectedImage = image;
				break;
			}
		}
	}
}

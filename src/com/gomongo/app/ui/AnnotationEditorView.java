package com.gomongo.app.ui;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Bitmap.Config;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

public class AnnotationEditorView extends View {
	
	private static final String TAG = "AnnotationEditorView";
	private Context mContext; 
	private ScaleGestureDetector mScaleGestureDetector;
	
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
		for( MongoImage image : mImages ) {
			image.drawImage(canvas);
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
	
	@Override
	public boolean onTouchEvent( MotionEvent event ) {
		//Log.d(TAG, String.format( "Touch event with %d pointers", event.getPointerCount() ) );
		
		if( mScaleGestureDetector != null ) {
			mScaleGestureDetector.onTouchEvent(event);
		}
		
		float x = event.getX();
		float y = event.getY();
		
		switch ( event.getAction() & MotionEvent.ACTION_MASK ) {
		case MotionEvent.ACTION_DOWN:
			
			performHitTestToSelectImage(x, y);
			
			boolean hitTestFoundAnImage = mSelectedImage != null;
			if( hitTestFoundAnImage ) {
				mScaleGestureDetector = new ScaleGestureDetector( mContext, mSelectedImage );
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
		
		return true;
	}

	private void performHitTestToSelectImage(float x, float y) {
		for( MongoImage image : mImages ) {
			if( image.containsPoint(x, y) ) {
				Log.d( TAG, String.format( "Selecting image from point (%f,%f)", x, y ) );
				
				mSelectedImage = image;
				break;
			}
		}
	}
}

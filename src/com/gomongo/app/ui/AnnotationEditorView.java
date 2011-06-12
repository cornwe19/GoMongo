package com.gomongo.app.ui;

import java.util.ArrayList;
import java.util.List;

import com.gomongo.app.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class AnnotationEditorView extends View {
	
	private static final String TAG = "AnnotationEditorView";
	private Context mContext; 
	
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
	
	List<MongoImage> mImages = new ArrayList<MongoImage>();
	MongoImage mSelectedImage;
	
	@Override
	public boolean onTouchEvent( MotionEvent event ) {
		//Log.d(TAG, String.format( "Touch event with %d pointers", event.getPointerCount() ) );
		
		float x = event.getX();
		float y = event.getY();
		
		switch ( event.getAction() ) {
		case MotionEvent.ACTION_DOWN:
			
			performHitTestToSelectImage(x, y);
			
			boolean hitTestFoundNoImages = mSelectedImage == null;
			
			if( hitTestFoundNoImages ) {
				createNewImageAtPoint(x, y);
			}
			
			invalidate();
			break;
		case MotionEvent.ACTION_MOVE:
			if( mSelectedImage != null ) {
				mSelectedImage.setCurrentPoint( event.getX(), event.getY() );
				
				invalidate();
			}
			break;
		case MotionEvent.ACTION_UP:
			mSelectedImage = null;
			break;
		}
		
		return true;
	}

	private void createNewImageAtPoint(float x, float y) {
		Bitmap bitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.hat);
		
		MongoImage image = new MongoImage(bitmap);
		image.setCurrentPoint( x, y );
		
		mSelectedImage = image;
		
		mImages.add( image );
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

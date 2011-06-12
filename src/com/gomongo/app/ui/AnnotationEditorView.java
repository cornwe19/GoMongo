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
	
	@Override
	public boolean onTouchEvent( MotionEvent event ) {
		Log.d(TAG, String.format( "Touch event with %d pointers", event.getPointerCount() ) );
		
		switch ( event.getAction() ) {
		case MotionEvent.ACTION_DOWN:
			Bitmap bitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.hat);
			
			MongoImage image = new MongoImage(bitmap);
			image.setCurrentPoint( event.getX(), event.getY() );
			
			mImages.add( image );
			
			invalidate();
			break;
		case MotionEvent.ACTION_MOVE:
			MongoImage imageToMove = getCurrentImage();
			imageToMove.setCurrentPoint( event.getX(), event.getY() );
			
			invalidate();
			break;
		}
		
		return true;
	}
	
	private MongoImage getCurrentImage() {
		return mImages.get( mImages.size() - 1 );
	}
}

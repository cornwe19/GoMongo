package com.gomongo.app.ui;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Bitmap.Config;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.gomongo.app.DeleteStickersController;
import com.gomongo.app.EditStickersController;
import com.gomongo.app.StickerController;

public class AnnotationEditorView extends View {
	
	private StickerController mEditStickersController;
	
	public AnnotationEditorView(Context context) {
		super(context);
		
		mEditStickersController = new EditStickersController(context, mImages);
	}
	
	public AnnotationEditorView(Context context, AttributeSet attrs) {
		super(context,attrs);
		
		mEditStickersController = new EditStickersController(context, mImages);
	}
	
	public boolean isDeleting() {
	    return mEditStickersController instanceof DeleteStickersController;
	}
	
	@Override
	public void onDraw( Canvas canvas ) {
		Paint imagePaint = mEditStickersController.getStickerDrawPaint();
		
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
	
	public void startDeleting() {
	    mEditStickersController = new DeleteStickersController(mImages);
	    invalidate();
	}

	public void stopDeleting() {
	    mEditStickersController = new EditStickersController(getContext(), mImages);
	    invalidate();
	}
	
	@Override
	public boolean onTouchEvent( MotionEvent event ) {
	    boolean handled = mEditStickersController.onTouchEvent(event);
	    
	    invalidate();
		
	    return handled;
	}
}

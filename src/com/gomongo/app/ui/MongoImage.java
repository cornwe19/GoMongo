package com.gomongo.app.ui;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PointF;

public class MongoImage {

	private Matrix mImageTransform = new Matrix();
	
	private PointF mLastPosition = new PointF(0,0);
	public void setCurrentPoint( float x, float y ) {
		PointF newPoint = new PointF(x, y);
		
		mImageTransform.postTranslate( newPoint.x - mLastPosition.x, newPoint.y - mLastPosition.y );
		
		mLastPosition = newPoint;
	}
	
	private void centerTransformForBitmap() {
		float offsetToCenterX = -(mBitmap.getWidth() / 2F);
		float offsetToCenterY = -(mBitmap.getHeight() / 2F);
		
		mImageTransform.postTranslate( offsetToCenterX, offsetToCenterY );
	}
	
	private Bitmap mBitmap;
	public void drawImage( Canvas canvas ) {
		canvas.drawBitmap(mBitmap, mImageTransform, null);
	}
	
	public MongoImage( Bitmap image ) {
		mBitmap = image;
		
		centerTransformForBitmap();
	}
}
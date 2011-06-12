package com.gomongo.app.ui;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.RectF;

public class MongoImage {

	private Matrix mImageTransform = new Matrix();
	private RectF mImageSize;
	private Bitmap mBitmap;
	
	public MongoImage( Bitmap image ) {
		mBitmap = image;
		
		mImageSize = new RectF( 0, 0, (float)image.getWidth(), (float)image.getHeight() );
		
		centerTransformForBitmap();
	}
	
	private PointF mLastPosition = new PointF(0,0);
	public void setCurrentPoint( float x, float y ) {
		PointF newPoint = new PointF(x, y);
		
		mImageTransform.postTranslate( newPoint.x - mLastPosition.x, newPoint.y - mLastPosition.y );
		
		mLastPosition = newPoint;
	}
	
	public boolean containsPoint( float x, float y ) {
		RectF currentImageBounds = new RectF();
		mImageTransform.mapRect(currentImageBounds, mImageSize);
		
		return currentImageBounds.contains( x, y );
	}
	
	private void centerTransformForBitmap() {
		float offsetToCenterX = -(mBitmap.getWidth() / 2F);
		float offsetToCenterY = -(mBitmap.getHeight() / 2F);
		
		mImageTransform.postTranslate( offsetToCenterX, offsetToCenterY );
	}
	
	public void drawImage( Canvas canvas ) {
		canvas.drawBitmap(mBitmap, mImageTransform, null);
	}
}
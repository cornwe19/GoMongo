package com.gomongo.app.ui;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.view.ScaleGestureDetector;

import com.gomongo.app.OnRotateGestureListener;

public class MongoImage extends ScaleGestureDetector.SimpleOnScaleGestureListener implements OnRotateGestureListener {

	private RectF mActualSize;
	private PointF mDistanceToCenter = new PointF();
	private float mCurrentScale = 1f;
	private float mCurrentRotation = 0f;
	private Bitmap mBitmap;
	
	public MongoImage( Bitmap image ) {
		mBitmap = image;
		
		mActualSize = new RectF( 0, 0, (float)image.getWidth(), (float)image.getHeight() );
		
		recalculateCenterOffsets();
	}
	
	private PointF mCurrentPoint = new PointF(0,0);
	public void setCurrentPoint( float x, float y ) {
		mCurrentPoint = new PointF(x, y);
	}
	
	public boolean containsPoint( float x, float y ) {
		RectF currentImageBounds = createImageBoundsFromPositionAndDimensions( mCurrentPoint, mDistanceToCenter );
		
		return currentImageBounds.contains( x, y );
	}

	private RectF createImageBoundsFromPositionAndDimensions( PointF midPoint, PointF distanceToCenter ) {
		RectF imageBounds = new RectF( midPoint.x - distanceToCenter.x, midPoint.y - distanceToCenter.y,
				 					   midPoint.x + distanceToCenter.x, midPoint.y + distanceToCenter.y );
		return imageBounds;
	}
	
	private void recalculateCenterOffsets() {
		float width = mActualSize.width() * mCurrentScale;
		float height = mActualSize.height() * mCurrentScale;
		
		mDistanceToCenter.x = width / 2;
		mDistanceToCenter.y = height / 2;
	}
	
	public void drawImage( Canvas canvas, Paint imagePaint ) {
		Matrix imageTransform = new Matrix();
		
		imageTransform.postScale( mCurrentScale, mCurrentScale );
		imageTransform.postRotate(mCurrentRotation, mDistanceToCenter.x, mDistanceToCenter.y);
		imageTransform.postTranslate( mCurrentPoint.x - mDistanceToCenter.x, mCurrentPoint.y - mDistanceToCenter.y );
		
		canvas.drawBitmap(mBitmap, imageTransform, imagePaint);
	}
	
	@Override
	public boolean onScale( ScaleGestureDetector scaleDetector ) {
		mCurrentScale *= scaleDetector.getScaleFactor();
		
		recalculateCenterOffsets();
		
		boolean handledEvent = true;
		return handledEvent;
	}

    @Override
    public void onRotate(double angleDelta) {
        mCurrentRotation += angleDelta;
    }
}
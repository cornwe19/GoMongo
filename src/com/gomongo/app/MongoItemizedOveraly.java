package com.gomongo.app;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout.LayoutParams;

import com.gomongo.app.ui.MongoMapView;
import com.gomongo.app.ui.OnMapMovedListener;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;

public class MongoItemizedOveraly extends ItemizedOverlay<OverlayItem> implements OnMapMovedListener {

	private List<OverlayItem> mOverlayItems = new ArrayList<OverlayItem>();
	private MapView mMapView;
	private Button mMoreDetailsButton;
	private int mMarkerHeight;
	
	
	public MongoItemizedOveraly( Drawable defaultMarker, Button moreDetailsButton) {
		super(boundCenterBottom(defaultMarker));
		
		mMarkerHeight = defaultMarker.getIntrinsicHeight();
		
		mMoreDetailsButton = moreDetailsButton;
	}

	@Override
	public void draw( Canvas canvas, MapView mapView, boolean showShadow ) {
		super.draw(canvas, mapView, showShadow);
		
		if( mMapView == null ) {
			mMapView = mapView;
			
			MongoMapView mongoMapView = ((MongoMapView)mMapView);
			ensureMongoMapIsUsed(mongoMapView);
			
			mongoMapView.registerOnMoveListener(this);
		}
	}

	private void ensureMongoMapIsUsed(MongoMapView mongoMapView) {
		if( mongoMapView == null ) {
			throw new IllegalArgumentException( "Mongo itemized overlay requires a MongoMapView to behave appropriately." );
		}
	}
	
	@Override
	public boolean onTap( int itemPosition ) {
		OverlayItem overlayItem = getItem(itemPosition);
		final GeoPoint itemGeoPoint = overlayItem.getPoint();
		
		mMoreDetailsButton.setVisibility( View.INVISIBLE );
		
		mMoreDetailsButton.setText( overlayItem.getTitle() );
		
		mMapView.getController().animateTo(itemGeoPoint, getPositionButtonRunnable(itemGeoPoint));
		
		return true;
	}

	private Runnable getPositionButtonRunnable(final GeoPoint itemGeoPoint) {
		return new Runnable(){

			@Override
			public void run() {
				Point projectedPoint = mMapView.getProjection().toPixels(itemGeoPoint, null);
				
				LayoutParams layoutParams = new LayoutParams( LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				
				Point buttonTopLeft = getMoreDetailsButtonCenterTopPlacement(projectedPoint);
				
				layoutParams.setMargins(buttonTopLeft.x,buttonTopLeft.y,0,0);
				
				mMoreDetailsButton.setLayoutParams(layoutParams);
				
				mMoreDetailsButton.setVisibility( View.VISIBLE );
			}

			private Point getMoreDetailsButtonCenterTopPlacement(Point projectedPoint) {
				Point buttonTopLeft = new Point();
				
				buttonTopLeft.x = projectedPoint.x - mMoreDetailsButton.getMeasuredWidth() / 2;
				buttonTopLeft.y = projectedPoint.y - mMoreDetailsButton.getMeasuredHeight() - mMarkerHeight;
				return buttonTopLeft;
			} 
		};
	}
	
	@Override
	protected OverlayItem createItem(int position) {
		return mOverlayItems.get(position);
	}

	public void addOverlay(OverlayItem overlay) {
        mOverlayItems.add(overlay);
        populate();
    }
	
	@Override
	public int size() {
		return mOverlayItems.size();
	}

	@Override
	public void onMoved() {
		mMoreDetailsButton.setVisibility(View.INVISIBLE);
	}
}

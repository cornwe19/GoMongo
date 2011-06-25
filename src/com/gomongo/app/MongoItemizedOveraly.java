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
	
	public MongoItemizedOveraly( Drawable defaultMarker, Button moreDetailsButton) {
		super(boundCenterBottom(defaultMarker));
		
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
		
		mMoreDetailsButton.setVisibility( View.GONE );
		
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
				layoutParams.setMargins(projectedPoint.x,projectedPoint.y,0,0);
				
				mMoreDetailsButton.setLayoutParams(layoutParams);
				
				mMoreDetailsButton.setVisibility( View.VISIBLE );
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
		mMoreDetailsButton.setVisibility(View.GONE);
	}
}

package com.gomongo.app;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout.LayoutParams;

import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;

public class MongoItemizedOveraly extends ItemizedOverlay<OverlayItem> {

	private List<OverlayItem> mOverlayItems = new ArrayList<OverlayItem>();
	private MapView mMapView;
	private View mMoreDetailsButton;
	
	public MongoItemizedOveraly( Drawable defaultMarker, View moreDetailsButton) {
		super(boundCenterBottom(defaultMarker));
		
		mMoreDetailsButton = moreDetailsButton;
	}

	@Override
	public void draw( Canvas canvas, MapView mapView, boolean showShadow ) {
		super.draw(canvas, mapView, showShadow);
		
		if( mMapView == null ) {
			mMapView = mapView;
		}
	}
	
	@Override
	public boolean onTap( int itemPosition ) {
		OverlayItem overlayItem = getItem(itemPosition);
		Point projectedPoint = mMapView.getProjection().toPixels(overlayItem.getPoint(), null);
		
		LayoutParams layoutParams = new LayoutParams( LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		layoutParams.setMargins(projectedPoint.x,projectedPoint.y,0,0);
		
		mMoreDetailsButton.setLayoutParams(layoutParams);
		
		return false;
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

}

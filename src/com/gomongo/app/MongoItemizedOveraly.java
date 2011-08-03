package com.gomongo.app;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
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

	private List<MongoLocation> mMongoLocations = new ArrayList<MongoLocation>();
	private MapView mMapView;
	private Button mMoreDetailsButton;
	private int mMarkerHeight;
	private String TAG = "MongoItemizedOverlay";
	
	private final int ADD_MESSAGE = 0x01;
	private final int CLEAR_MESSAGE = 0x02;
	
	private static final String LOCATION_KEY = "location";
	private Handler mAsyncMapMessageHandler;
	
	public MongoItemizedOveraly( Drawable defaultMarker, Button moreDetailsButton) {
		super(boundCenterBottom(defaultMarker));
		
		mMarkerHeight = defaultMarker.getIntrinsicHeight();
		
		mMoreDetailsButton = moreDetailsButton;
		
		mAsyncMapMessageHandler = getUIThreadHandlerForUpdates();
	}

	private Handler getUIThreadHandlerForUpdates() {
		return new Handler() {
			@Override
			public void handleMessage( Message message ) {
				switch ( message.arg1 ) {
				case ADD_MESSAGE:
				    synchronized( mMongoLocations ) {
	                    mMongoLocations.add( (MongoLocation)message.getData().getParcelable(LOCATION_KEY) );
	                    populate();
	                    
	                    mMapView.invalidate();
	                }
				    break;
				case CLEAR_MESSAGE:
				    synchronized (mMongoLocations) {
                        mMongoLocations.clear();
                    }
				    break;
				}
			    
			}
		};
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
		Log.d( TAG, String.format("Position searched for: %d, list length: %d", itemPosition, mMongoLocations.size() ) );
		
		MongoLocation mongoLocation = mMongoLocations.get(itemPosition);
		final GeoPoint itemGeoPoint = mongoLocation.getPoint();
		
		mMoreDetailsButton.setVisibility( View.INVISIBLE );
		
		mMoreDetailsButton.setText( mongoLocation.getTitle() );
		
		mMoreDetailsButton.setTag( mongoLocation );
		
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
		return mMongoLocations.get(position);
	}

	public void postAddLocation(MongoLocation location) {
		Message addLocationMessage = Message.obtain(mAsyncMapMessageHandler);
		
		addLocationMessage.arg1 = ADD_MESSAGE;
		
		Bundle locationData = new Bundle();
		locationData.putParcelable( LOCATION_KEY, location );
		addLocationMessage.setData( locationData );
		
		addLocationMessage.sendToTarget();
    }
	
	@Override
	public int size() {
		return mMongoLocations.size();
	}

	@Override
	public void onMoved() {
		mMoreDetailsButton.setVisibility(View.INVISIBLE);
	}
	
	public void postClearLocations() {
	    Message clearLocationsMessage = Message.obtain(mAsyncMapMessageHandler);
	    
	    clearLocationsMessage.arg1 = CLEAR_MESSAGE;
	    
	    clearLocationsMessage.sendToTarget();
	}
}

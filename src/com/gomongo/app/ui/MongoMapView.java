package com.gomongo.app.ui;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.google.android.maps.MapView;

public class MongoMapView extends MapView {

	public MongoMapView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	List<OnMapMovedListener> mMovedListeners = new ArrayList<OnMapMovedListener>();
	public void registerOnMoveListener( OnMapMovedListener listener ) {
		mMovedListeners.add(listener);
	}
	
	@Override
	public boolean onTouchEvent( MotionEvent event ) {
		if( event.getAction() == MotionEvent.ACTION_MOVE ) {
			for( OnMapMovedListener listener : mMovedListeners ) {
				listener.onMoved();
			}
		}
		
		return super.onTouchEvent(event);
	}
	
}

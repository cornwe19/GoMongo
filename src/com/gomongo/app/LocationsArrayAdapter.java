package com.gomongo.app;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class LocationsArrayAdapter extends ArrayAdapter<MongoLocation> {

	private Handler mAsyncAdapterActions;
	
	private static final String LOCATION_KEY = "location";
	
	public LocationsArrayAdapter(Context context, List<MongoLocation> objects) {
		super(context, R.id.location_title, objects);
		
		final LocationsArrayAdapter itemToSyncOn = this;
		
		mAsyncAdapterActions = getUIThreadHandlerForUpdates( itemToSyncOn );
	}

	private Handler getUIThreadHandlerForUpdates( final LocationsArrayAdapter itemToSyncOn) {
		return new Handler() {
			@Override
			public void handleMessage( Message message ) {
				synchronized( itemToSyncOn ) {
					add( (MongoLocation)message.getData().getParcelable(LOCATION_KEY) );
				}
			}
		};
	}

	@Override
	public View getView( int position, View convertView, ViewGroup parent ) {
		LayoutInflater inflater = ((Activity)getContext()).getLayoutInflater();
		convertView = inflater.inflate(R.layout.location_list_item, null);
		
		MongoLocation locationToDisplay = getItem(position);
		
		setTextOnView(convertView, R.id.location_title, locationToDisplay.getTitle());
		setTextOnView(convertView, R.id.location_address, locationToDisplay.getSnippet());
		setTextOnView(convertView, R.id.location_distance, locationToDisplay.getDistance());
		
		return convertView;
	}

	public void postAddItem(MongoLocation location) {
		Message addLocationMessage = Message.obtain(mAsyncAdapterActions);
		
		Bundle locationData = new Bundle();
		locationData.putParcelable( LOCATION_KEY, location );
		addLocationMessage.setData( locationData );
		
		addLocationMessage.sendToTarget();
	}
	
	private void setTextOnView(View convertView, int textResourceId, String text) {
		TextView textView = (TextView)convertView.findViewById(textResourceId);
		textView.setText(text);
	}
	
}

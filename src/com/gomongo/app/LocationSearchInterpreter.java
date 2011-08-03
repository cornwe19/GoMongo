package com.gomongo.app;

import java.io.IOException;
import java.util.Locale;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

public class LocationSearchInterpreter implements OnEditorActionListener {

    private String TAG = "LocationSearchInterpreter";
    
    private static int IME_ACTION_HTC_DONE = 0;
    
    Geocoder mGeocoder;
    MongoLocationManager mLocationsManager;
    
    public LocationSearchInterpreter( Context context, MongoLocationManager mapController ) {
        mGeocoder = new Geocoder( context, Locale.getDefault() );
        mLocationsManager = mapController;
    }

    @Override
    public boolean onEditorAction(TextView editor, int actionId, KeyEvent event) {
        
        if( actionId == EditorInfo.IME_ACTION_DONE || actionId == IME_ACTION_HTC_DONE ) {
            try {
                Address address = mGeocoder.getFromLocationName(editor.getText().toString(), 1).get(0);

                mLocationsManager.resetLocationsState();
                
                mLocationsManager.centerMapAndLoadMarkers(address.getLatitude(), address.getLongitude());
                
                Log.d(TAG, String.format( "Found address: %s, focusing on (%f,%f)", address.getAddressLine(0), address.getLatitude(), address.getLongitude() ) );
            } catch (IOException ex) {
                Log.w(TAG, "Problem getting addresses from geocoder.", ex );
            }
        }
        return false;
    }

}

package com.gomongo.app;

import java.io.IOException;
import java.util.Locale;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

public class LocationSearchInterpreter extends DismissKeyboardEditorActionInterpreter implements OnClickListener {

    private String TAG = "LocationSearchInterpreter";
    
    Geocoder mGeocoder;
    MongoLocationManager mLocationsManager;
    Context mContext;
    
    public LocationSearchInterpreter( Context context, MongoLocationManager mapController ) {
        super(context);
        mGeocoder = new Geocoder( context, Locale.getDefault() );
        mLocationsManager = mapController;
    }

    @Override
    public boolean onEnterKeyPressed( TextView editor ) {
        beginNewLocationSearch(editor);
        
        return false;
    }

    private void beginNewLocationSearch(TextView editor) {
        try {                
            mLocationsManager.resetLocationsState();
            
            Address address = mGeocoder.getFromLocationName(editor.getText().toString(), 1).get(0);
            
            mLocationsManager.centerMapAndLoadMarkers(address.getLatitude(), address.getLongitude());
            
            Log.d(TAG, String.format( "Found address: %s, focusing on (%f,%f)", address.getAddressLine(0), address.getLatitude(), address.getLongitude() ) );
        } catch (IOException ex) {
            handleSearchException(ex);
        } catch (IndexOutOfBoundsException ex) {
            handleSearchException(ex);
        }
    }

    private void handleSearchException(Throwable ex) {
        Log.w(TAG, "Problem getting addresses from geocoder.", ex );
        
        Toast.makeText(mContext, R.string.error_location_not_recognized, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onClick(View clickedView) {
        if( clickedView.getId() == R.id.button_search_locations ) {
            TextView associatedSearchText = (TextView)clickedView.getTag();
            
            dismissKeyboard(associatedSearchText);
            
            beginNewLocationSearch(associatedSearchText);
        }
    }

}

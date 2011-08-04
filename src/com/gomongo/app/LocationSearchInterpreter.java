package com.gomongo.app;

import java.io.IOException;
import java.util.Locale;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.TextView.OnEditorActionListener;

public class LocationSearchInterpreter implements OnEditorActionListener, OnClickListener {

    private String TAG = "LocationSearchInterpreter";
    
    Geocoder mGeocoder;
    MongoLocationManager mLocationsManager;
    Context mContext;
    
    public LocationSearchInterpreter( Context context, MongoLocationManager mapController ) {
        mContext = context;
        mGeocoder = new Geocoder( context, Locale.getDefault() );
        mLocationsManager = mapController;
    }

    @Override
    public boolean onEditorAction(TextView editor, int actionId, KeyEvent event) {
        
        if( event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER ) {
            beginNewLocationSearch(editor);
        }
        
        return false;
    }

    private void beginNewLocationSearch(TextView editor) {
        try {                
            dismissKeyboard(editor);
            
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

    private void dismissKeyboard(TextView editor) {
        InputMethodManager inputMethodManager = (InputMethodManager)mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(editor.getWindowToken(), 0);
    }

    private void handleSearchException(Throwable ex) {
        Log.w(TAG, "Problem getting addresses from geocoder.", ex );
        
        Toast.makeText(mContext, R.string.error_location_not_recognized, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onClick(View clickedView) {
        if( clickedView.getId() == R.id.button_search_locations ) {
            TextView associatedSearchText = (TextView)clickedView.getTag();
            
            beginNewLocationSearch(associatedSearchText);
        }
    }

}

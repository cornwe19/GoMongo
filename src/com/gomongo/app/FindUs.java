package com.gomongo.app;

import android.os.Bundle;
import android.widget.Button;

import com.google.android.maps.MapActivity;

public class FindUs extends MapActivity {

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.find_us);
        
        Button homeButton = (Button)findViewById(R.id.button_find_us);
        homeButton.setPressed(true);
    }
	
	// FIXME: dont show any routes for now
	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}

}

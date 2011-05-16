package com.gomongo.app;

import android.os.Bundle;
import android.widget.Button;

import com.google.android.maps.MapActivity;

// Move this to "Find Us" activity
public class Home extends MapActivity {
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        Button homeButton = (Button)findViewById(R.id.button_home);
        homeButton.setPressed(true);
    }

	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}
}
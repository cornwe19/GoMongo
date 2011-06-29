package com.gomongo.app;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class LocationDetails extends Activity {
	@Override
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.location_details);
		
		Bundle intentExtras = getIntent().getExtras();
		
		TextView title = (TextView)findViewById(R.id.location_title);
		title.setText(intentExtras.getString(MongoLocation.FIELD_TITLE));
		
		TextView address = (TextView)findViewById(R.id.location_address);
		address.setText(intentExtras.getString(MongoLocation.FIELD_ADDRESS));
		
		TextView hours = (TextView)findViewById(R.id.hours_body);
		hours.setText(intentExtras.getString(MongoLocation.FIELD_HOURS));
		
		TextView phone = (TextView)findViewById(R.id.phone_number_body);
		phone.setText(intentExtras.getString(MongoLocation.FIELD_PHONE));
	}
}

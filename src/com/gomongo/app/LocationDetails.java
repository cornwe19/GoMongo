package com.gomongo.app;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.util.Linkify;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class LocationDetails extends Activity implements OnClickListener {
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
		
		Linkify.addLinks(phone, Linkify.PHONE_NUMBERS);
		
		Button getDirectionsButton = (Button)findViewById(R.id.button_get_directions);
		getDirectionsButton.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch( v.getId() ) {
		case R.id.button_get_directions:
			String directionsUrl = prepareMapsQueryForThisLocation();
			Intent directionsIntent = new Intent( Intent.ACTION_VIEW, Uri.parse(directionsUrl) );
			startActivity(directionsIntent);
			
			break;
		}
	}

	private String prepareMapsQueryForThisLocation() {
		String directionsUrlFormat = "http://maps.google.com/maps?daddr=%s";
		String lineBrokenAddress = getIntent().getExtras().getString(MongoLocation.FIELD_ADDRESS);
		String directionsUrl = String.format(directionsUrlFormat, lineBrokenAddress.replace("\n", ""));
		return directionsUrl;
	}
}

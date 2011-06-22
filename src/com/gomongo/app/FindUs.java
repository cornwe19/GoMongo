package com.gomongo.app;

import java.io.IOException;
import java.io.StringReader;
import java.net.MalformedURLException;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.gomongo.net.StaticWebService;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;

public class FindUs extends MapActivity {
	
	private String TAG = "FindUs";
	
	private final int NUMBER_OF_LOCAIONS_TO_DISPLAY = 5;
	private String LOCATIONS_XPATH = "/locations/location";
	
	private String LOCATION_SEARCH_URL_FORMAT = "http://www.gomongo.com/iphone/locationsXML.php?lat=%f&lon=%f";
	
	private MongoItemizedOveraly mItemizedOverlay; 
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.find_us);
        
        mItemizedOverlay = new MongoItemizedOveraly( getResources().getDrawable(R.drawable.maps_marker) );
        
        View navigationMenu = (View)findViewById(R.id.nav_menu);
        
        NavigationHelper.setupButtonToLaunchActivity(this, navigationMenu, R.id.button_home, Home.class);
        NavigationHelper.setupButtonToLaunchActivity(this, navigationMenu, R.id.button_create, CreateBowl.class);
        NavigationHelper.setupButtonToLaunchActivity(this, navigationMenu, R.id.button_photo, MongoPhoto.class);
        NavigationHelper.setupButtonToLaunchActivity(this, navigationMenu, R.id.button_about, About.class);
        
        MapView mapView = (MapView)findViewById(R.id.find_us_map);
        mapView.setBuiltInZoomControls(true);
        
        String locationSearchUrl = prepareStoreLocatorRequestBasedOnCurrentLocation();

        try {
			InputSource source = getResponseFromWebService( locationSearchUrl );
			
			XPath xPath = XPathFactory.newInstance().newXPath();
			
			NodeList nodes = (NodeList)xPath.evaluate(LOCATIONS_XPATH, source, XPathConstants.NODESET);
			
			addNearestLocationsToItemizedOverlay( nodes );
		}
        catch (MalformedURLException exception) {
			Log.e( TAG, "Poorly formatted URL in looking up other BD's locations", exception );
		}
        catch (IOException exception) {
			Log.w( TAG, "I/O error in getting to location service." );
			
			Toast.makeText(this, getResources().getString(R.string.error_problem_getting_locations), 
					Toast.LENGTH_LONG).show();
		}
        catch (XPathExpressionException exception) {
			Log.w( TAG, "Problem reading location search response XML." );
			
			Toast.makeText(this, getResources().getString(R.string.error_problem_getting_locations), 
					Toast.LENGTH_LONG).show();
		}
        
        setItemizedOverlayOnMap(mapView);
	}

	private void setItemizedOverlayOnMap(MapView mapView) {
		mapView.getOverlays().add(mItemizedOverlay);
	}

	private void addNearestLocationsToItemizedOverlay(NodeList nodes) throws XPathExpressionException {
		for( int i = 0; i < NUMBER_OF_LOCAIONS_TO_DISPLAY; i++ ) {
			Node currentNode = nodes.item(i);
			if( currentNode != null ) {
				MongoLocation location = MongoLocation.getLocationFromXml(currentNode);
				
				mItemizedOverlay.addOverlay(location);
			}
			else break;
		}
	}

	private InputSource getResponseFromWebService(String locationSearchUrl)
			throws IOException, MalformedURLException {
		String response = StaticWebService.getResponseString( locationSearchUrl );
		StringReader reader = new StringReader(response);
		InputSource source = new InputSource(reader);
		return source;
	}

	private String prepareStoreLocatorRequestBasedOnCurrentLocation() {
		LocationManager locationManager = (LocationManager) getSystemService( Context.LOCATION_SERVICE );
        
        Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        
        String locationSearchUrl = String.format(LOCATION_SEARCH_URL_FORMAT, 
        		lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude() );
        
		return locationSearchUrl;
	}

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}

}

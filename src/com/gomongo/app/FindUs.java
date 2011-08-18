package com.gomongo.app;import java.io.IOException;import java.net.MalformedURLException;import java.util.ArrayList;import javax.xml.xpath.XPath;import javax.xml.xpath.XPathConstants;import javax.xml.xpath.XPathExpressionException;import javax.xml.xpath.XPathFactory;import org.w3c.dom.Node;import org.w3c.dom.NodeList;import org.xml.sax.InputSource;import android.app.ProgressDialog;import android.content.Context;import android.content.Intent;import android.graphics.drawable.Drawable;import android.location.Location;import android.location.LocationManager;import android.os.Bundle;import android.util.Log;import android.view.KeyEvent;import android.view.View;import android.view.Window;import android.view.View.OnClickListener;import android.widget.Button;import android.widget.EditText;import android.widget.ImageButton;import android.widget.ListView;import android.widget.Toast;import com.gomongo.net.WebHelper;import com.google.android.maps.GeoPoint;import com.google.android.maps.MapActivity;import com.google.android.maps.MapController;import com.google.android.maps.MapView;public class FindUs extends MapActivity implements OnClickListener, MongoLocationManager {	private String TAG = "FindUs";	private String LOCATIONS_XPATH = "/locations/location";	private String LOCATION_SEARCH_URL_FORMAT = "http://www.gomongo.com/iphone/locationsXML.php?lat=%f&lon=%f";	private MongoItemizedOveraly mItemizedOverlay;	private ProgressDialog mLoadingMarkersDialog;	private double KANSAS_CITY_LAT = 39.1, KANSAS_CITY_LONG = 94.6;		LocationsArrayAdapter mLocationsArrayAdapter;	private MapView mMapView;    private View mListView;        private Thread mLoadMarkersThread;    private Boolean mCancelLoadingLocations = false;        ImageButton mToggleButton;	    private Context mUiThreadContext;    	@Override	public void onCreate(Bundle savedInstanceState) {		super.onCreate(savedInstanceState);		requestWindowFeature(Window.FEATURE_NO_TITLE);				setContentView(R.layout.find_us);		mUiThreadContext = this;				Drawable marker = getResources().getDrawable(R.drawable.map_marker);		Button moreDetailsButton = (Button) findViewById(R.id.button_location_more_details);		moreDetailsButton.setOnClickListener(this);		mItemizedOverlay = new MongoItemizedOveraly(marker, moreDetailsButton);		View navigationMenu = (View) findViewById(R.id.nav_menu);		NavigationHelper.setupButtonToLaunchActivity(this, navigationMenu, R.id.button_home, Home.class);		ImageButton findUsButton = (ImageButton)navigationMenu.findViewById(R.id.button_find_us);		findUsButton.setBackgroundResource(R.drawable.navigation_tab);        findUsButton.setSelected(true);		NavigationHelper.setupButtonToLaunchActivity(this, navigationMenu, R.id.button_create, CreateBowl.class);		NavigationHelper.setupButtonToLaunchActivity(this, navigationMenu, R.id.button_photo, MongoPhoto.class);		NavigationHelper.setupButtonToLaunchActivity(this, navigationMenu, R.id.button_about, About.class);		mMapView = (MapView) findViewById(R.id.find_us_map);		mMapView.setBuiltInZoomControls(true);		MapController mapController = mMapView.getController();				int cityZoomLevel = 10;		mapController.setZoom(cityZoomLevel);				mToggleButton = (ImageButton)findViewById(R.id.button_map_list_toggle);		mListView = findViewById(R.id.locations_list_pane);		ListView locationsList = (ListView) findViewById(R.id.locations_list);        mLocationsArrayAdapter = new LocationsArrayAdapter( this, mAllLocations);        locationsList.setAdapter( mLocationsArrayAdapter );				double latitude, longitude;		try {		    Location lastKnownLocation = getLastKnownLocation();		    		    latitude = lastKnownLocation.getLatitude();		    longitude = lastKnownLocation.getLongitude();		}		catch ( LocationServiceNotAvailableException exception ) {		    Log.w(TAG, "Couldn't access user's location. Picking arbitrary search spot.", exception);		    		    Toast.makeText(this, getResources().getString(R.string.error_problem_getting_your_location), Toast.LENGTH_LONG).show();		    		    latitude = KANSAS_CITY_LAT;		    longitude = KANSAS_CITY_LONG;		}		        ImageButton mapListToggle = (ImageButton) findViewById(R.id.button_map_list_toggle);        mapListToggle.setOnClickListener(this);				setItemizedOverlayOnMap(mMapView);				setupSearchInterpreter();				centerMapAndLoadMarkers( latitude, longitude );	}    private void setupSearchInterpreter() {        LocationSearchInterpreter searchInterpreter = new LocationSearchInterpreter(this, this);                EditText searchBox = (EditText)findViewById( R.id.search_locations_text );        searchBox.setOnEditorActionListener( searchInterpreter );				ImageButton searchButton = (ImageButton)findViewById( R.id.button_search_locations );		searchButton.setOnClickListener(searchInterpreter);		searchButton.setTag(searchBox);    }	@Override    public void centerMapAndLoadMarkers( double latitude, double longitude ) {        GeoPoint mapCenter = new GeoPoint( MongoLocation.convertToMicroDegrees(latitude), MongoLocation.convertToMicroDegrees(longitude) );                mMapView.getController().setCenter(mapCenter);				final String locationUrlForThread = prepareStoreLocatorRequestBasedOnLocation(latitude, longitude);				mLoadMarkersThread = new Thread(new Runnable() {			@Override			public void run() {				loadNearestMarkersFromWeb(locationUrlForThread);			}		});		mLoadingMarkersDialog = ProgressDialog.show(this, 				getResources().getString(R.string.title_loading_locations),				getResources().getString(R.string.body_loading_locations), true);		mLoadMarkersThread.start();    }	@Override	public void resetLocationsState() {	    if( mLoadMarkersThread.isAlive() ) {    	    synchronized (mCancelLoadingLocations) {                mCancelLoadingLocations = true;    	    }    	        	    try {                mLoadMarkersThread.join();            } catch (InterruptedException e) {                // TODO Auto-generated catch block                e.printStackTrace();            }	    }	    	    mItemizedOverlay.clearLocations();	    	    mLocationsArrayAdapter.clear();	}		private void loadNearestMarkersFromWeb(String locationSearchUrl) {		Integer errorMessageId = null;	    	    try {			Log.d(TAG, "Calling web service");			InputSource source = WebHelper.getResponse(locationSearchUrl);			Log.d(TAG, "Got response from web...parsing now");			XPath xPath = XPathFactory.newInstance().newXPath();			NodeList nodes = (NodeList) xPath.evaluate(LOCATIONS_XPATH, source, XPathConstants.NODESET);			Log.d(TAG, String.format("xPath parsing done. found %d instances", nodes.getLength()));			addNearestLocationsToItemizedOverlay(nodes);			Log.d(TAG, "Locations added to overlay");		} catch (MalformedURLException exception) {			Log.e(TAG, "Poorly formatted URL in looking up other BD's locations", exception);		} catch (IOException exception) {			Log.w(TAG, "I/O error in getting to location service.");			errorMessageId = R.string.error_problem_getting_locations;		} catch (XPathExpressionException exception) {			Log.w(TAG, "Problem reading location search response XML.");			errorMessageId = R.string.error_problem_getting_locations;		}				if( errorMessageId != null ) {		    showSearchErrorMessage(errorMessageId);		}	}    private void showSearchErrorMessage(Integer errorMessageId) {        mLoadingMarkersDialog.dismiss();                final int constErrorMessageId = errorMessageId;        runOnUiThread(new Runnable() {            @Override            public void run() { Toast.makeText( mUiThreadContext, constErrorMessageId, Toast.LENGTH_LONG ).show(); }                    });    }	private void setItemizedOverlayOnMap(MapView mapView) {		mapView.getOverlays().add(mItemizedOverlay);	}	private ArrayList<MongoLocation> mAllLocations = new ArrayList<MongoLocation>();	private void addNearestLocationsToItemizedOverlay(NodeList nodes)			throws XPathExpressionException {		for (int i = 0; i < nodes.getLength(); i++) {		    Node currentNode = nodes.item(i);		    MongoLocation location = MongoLocation.getLocationFromXml(currentNode);		    			synchronized( mCancelLoadingLocations ) {                if( mCancelLoadingLocations ) {                    mCancelLoadingLocations = false;                    break;                }                            mLocationsArrayAdapter.postAddItem( location );                                mItemizedOverlay.postAddLocation(location);			}												if( mLoadingMarkersDialog.isShowing() ) {				mLoadingMarkersDialog.dismiss();			}		}	}	private String prepareStoreLocatorRequestBasedOnLocation( double latitude, double longitude ) {		return String.format(LOCATION_SEARCH_URL_FORMAT, latitude, longitude);	}    private Location getLastKnownLocation() throws LocationServiceNotAvailableException {        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);		Location lastKnownLocation = locationManager				.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);		if( lastKnownLocation == null ) {		    throw new LocationServiceNotAvailableException();		}        return lastKnownLocation;    }	@Override	protected boolean isRouteDisplayed() {		return false;	}	@Override	public boolean onKeyDown( int keyCode, KeyEvent event ) {        boolean handled = false;	    	    if( keyCode == KeyEvent.KEYCODE_BACK && !isShowingMap() ) {            switchToMapView();            handled = true;        }	    else {	        handled  = super.onKeyDown( keyCode, event );	    }        	    return handled;	}		@Override	public void onClick(View clickedView) {		switch (clickedView.getId()) {		case R.id.button_location_more_details:			MongoLocation mongoLocation = (MongoLocation) clickedView.getTag();			if (mongoLocation != null) {				Intent locationDetailsIntent = new Intent(this, LocationDetails.class);				locationDetailsIntent.putExtra(MongoLocation.EXTRA_LOCATION, mongoLocation);				startActivity(locationDetailsIntent);			}			break;		case R.id.button_map_list_toggle:			if (isShowingMap()) {				switchToListView();			} else {				switchToMapView();			}			break;		}	}    private boolean isShowingMap() {        return mListView.getVisibility() == View.INVISIBLE;    }    private void switchToListView() {        mToggleButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_menu_mapmode));        Button moreDetailsButton = (Button) findViewById(R.id.button_location_more_details);        moreDetailsButton.setVisibility(View.INVISIBLE);                mMapView.setVisibility(View.INVISIBLE);        mListView.setVisibility(View.VISIBLE);    }    private void switchToMapView() {        mToggleButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_btn_search));        mMapView.setVisibility(View.VISIBLE);        mListView.setVisibility(View.INVISIBLE);    }}
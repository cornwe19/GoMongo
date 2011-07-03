package com.gomongo.app;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Node;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.OverlayItem;

public class MongoLocation extends OverlayItem implements Parcelable {
	
	public static final String FIELD_TITLE = "location_title";
	public static final String FIELD_ADDRESS = "location_address";
	public static final String FIELD_HOURS = "location_hours";
	public static final String FIELD_PHONE = "location_phone";
	
	private static final String TITLE_XPATH = "location";

	private static final String ADDRESS_XPATH = "address";
	private static final String CITY_XPATH = "city";
	private static final String STATE_XPATH = "state";
	private static final String ZIP_XPATH = "zip";
	
	private static final String DISTANCE_XPATH = "distance";
	
	private static final String HOURS_XPATH = "hours";
	private static final String PHONE_XPATH = "phone";
	
	private static final String LATITUDE_XPATH = "lat";
	private static final String LONGITUDE_XPATH = "lon";
	
	private String mHours;
	private String mPhoneNumber;
	private String mDistance;
	
	// NOTE: Hiding this constructor because loading from XML is the standard way to instansiate this class.
	protected MongoLocation(GeoPoint point, String title, String address, String distance, String hours, String phoneNumber ) {
		super(point, title, address);
		
		mDistance = distance;
		
		mHours = hours;
		mPhoneNumber = phoneNumber;
	}
	
	public String getHours() {
		return mHours;
	}
	
	public String getPhoneNumber() {
		return mPhoneNumber;
	}
	
	public String getDistance() {
		return mDistance;
	}
	
	@Override
	public String toString() {
	    return mTitle;
	}
	
	public static MongoLocation getLocationFromXml( Node xmlRootNode ) throws XPathExpressionException {
		XPath xpath = XPathFactory.newInstance().newXPath();
		
		String title = (String)xpath.evaluate(TITLE_XPATH, xmlRootNode, XPathConstants.STRING);
		String descriptionFormat = "%s \n%s, %s %s";
		String description = String.format(descriptionFormat,
				(String)xpath.evaluate(ADDRESS_XPATH, xmlRootNode, XPathConstants.STRING),
				(String)xpath.evaluate(CITY_XPATH, xmlRootNode, XPathConstants.STRING),
				(String)xpath.evaluate(STATE_XPATH, xmlRootNode, XPathConstants.STRING),
				(String)xpath.evaluate(ZIP_XPATH, xmlRootNode, XPathConstants.STRING));
		
		String distance = (String)xpath.evaluate(DISTANCE_XPATH, xmlRootNode, XPathConstants.STRING);
		
		String hours = (String)xpath.evaluate(HOURS_XPATH, xmlRootNode, XPathConstants.STRING);
		hours  = transformHoursForDisplay( hours );
		
		String phoneNumber = (String)xpath.evaluate(PHONE_XPATH, xmlRootNode, XPathConstants.STRING);
		
		double latitude = (Double)xpath.evaluate(LATITUDE_XPATH, xmlRootNode, XPathConstants.NUMBER);
		double longitude = (Double)xpath.evaluate(LONGITUDE_XPATH, xmlRootNode, XPathConstants.NUMBER);
		
		GeoPoint location = new GeoPoint( toMicroDegrees(latitude), toMicroDegrees(longitude) );
		
		return new MongoLocation(location, title, description, distance, hours, phoneNumber);
	}
	
	private static String transformHoursForDisplay( String hoursString ) {
		Pattern hoursPattern = Pattern.compile( "(?i).*?(A|P)M.*?(A|P)M" );
		Matcher hoursMatcher = hoursPattern.matcher(hoursString);
		
		String modifiedHours = "";
		while( hoursMatcher.find() ) {
			modifiedHours += hoursMatcher.group() + "\n";
		}
		
		return modifiedHours.replace("\t", " ");
	}
	
	private static int toMicroDegrees( double degrees ) {
		return (int)( degrees * 1e6 );
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(mPoint.getLatitudeE6());
		dest.writeInt(mPoint.getLongitudeE6());
		
		dest.writeString(mTitle);
		dest.writeString(mSnippet);
		
		dest.writeString(mHours);
		dest.writeString(mPhoneNumber);
		dest.writeString(mDistance);
		
	}
	
	public static final Parcelable.Creator<MongoLocation> CREATOR = new Parcelable.Creator<MongoLocation>() {
	public MongoLocation createFromParcel(Parcel in) {
	    return new MongoLocation(in);
	}
	
	public MongoLocation[] newArray(int size) {
	    return new MongoLocation[size];
	}
	};
	
	private MongoLocation(Parcel in) {
		this( new GeoPoint(in.readInt(),in.readInt() ), in.readString(), in.readString(), 
				in.readString(), in.readString(), in.readString() );
	}
}

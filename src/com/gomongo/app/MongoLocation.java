package com.gomongo.app;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Node;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.OverlayItem;

public class MongoLocation extends OverlayItem {
	
	private static String TITLE_XPATH = "title";
	private static String DESCRIPTION_XPATH = "location";
	private static String LATITUDE_XPATH = "lat";
	private static String LONGITUDE_XPATH = "lon";
	
	// NOTE: Hiding this constructor because loading from XML is the standard way to instansiate this class.
	protected MongoLocation(GeoPoint point, String title, String snippet) {
		super(point, title, snippet);
	}
	
	public static MongoLocation getLocationFromXml( Node xmlRootNode ) throws XPathExpressionException {
		XPath xpath = XPathFactory.newInstance().newXPath();
		
		String title = (String)xpath.evaluate(TITLE_XPATH, xmlRootNode, XPathConstants.STRING);
		String description = (String)xpath.evaluate(DESCRIPTION_XPATH, xmlRootNode, XPathConstants.STRING);
		
		double latitude = (Double)xpath.evaluate(LATITUDE_XPATH, xmlRootNode, XPathConstants.NUMBER);
		double longitude = (Double)xpath.evaluate(LONGITUDE_XPATH, xmlRootNode, XPathConstants.NUMBER);
		
		GeoPoint location = new GeoPoint( toMicroDegrees(latitude), toMicroDegrees(longitude) );
		
		return new MongoLocation(location, title, description);
	}
	
	private static int toMicroDegrees( double degrees ) {
		return (int)( degrees * 1e6 );
	}
}

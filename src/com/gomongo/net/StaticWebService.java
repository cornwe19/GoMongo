package com.gomongo.net;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.xml.sax.InputSource;

public class StaticWebService {
	
	public static InputSource getResponse(String request) throws IOException, MalformedURLException {
		URL url = new URL(request);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setDoOutput(true);
        connection.setRequestMethod("GET");
        
        InputStreamReader reader = new InputStreamReader(connection.getInputStream());
        InputSource source = new InputSource(reader);
        
        return source;
	}
	
}

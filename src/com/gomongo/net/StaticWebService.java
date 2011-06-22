package com.gomongo.net;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class StaticWebService {
	
	public static String getResponseString(String request) throws IOException, MalformedURLException {
		URL url = new URL(request);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setDoOutput(true);
        connection.setRequestMethod("GET");
        
        String response = "";
        if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
            BufferedReader input = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            
            String responseLine;
            while( ( responseLine = input.readLine()) != null ) {
            	response += responseLine;
            }
        }
        
        return response;
	}
	
}

package com.magi.web;

import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

/**
 * WebParser - Relies on HTTPClient 4.2
 * 
 * @author patkins
 */
public class WebParser {

	private String url;
	
	public WebParser(String url) {
		this.url = url;
	}
	
	public void parse() {
		System.out.println("URL: " + url);
		
		try {
			HttpClient httpclient = new DefaultHttpClient();
			HttpGet httpget = new HttpGet(url);
			HttpResponse response = httpclient.execute(httpget);
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				int bread = 0;
				byte[] buff = new byte[512];
				StringBuffer sbuff = new StringBuffer();
			    InputStream instream = entity.getContent();
			    while ((bread = instream.read(buff)) != -1) {
			    	sbuff.append(new String(buff,0,bread));
			    }
			    // Do not need the rest
			    httpget.abort();
			    
			    parseText(sbuff.toString());
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected void parseText(String text) {
		// Override this
	}
}

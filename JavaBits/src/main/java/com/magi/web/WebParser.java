/* Open Source Licensed under GNU LGPL 3.0
 * See http://www.gnu.org/copyleft/lesser.html for details. */
package com.magi.web;

import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

/**
 * WebParser - Relies on Apache HTTPClient 4.2.3
 * Retrieves the HTML text content of a web page (URL).
 * This class can be extended, or just instantiated and used.
 * 
 * @author patkins
 */
public class WebParser {

	private String url;
	
	public WebParser() { }
	
	public WebParser(String url) {
		this.url = url;
	}
	
	public String parse() throws IOException {
		System.out.println("URL: " + url);
		
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
		    return sbuff.toString();
		}
		
		return null;
	}

	protected void parseText(String text) {
		// Override this
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
}

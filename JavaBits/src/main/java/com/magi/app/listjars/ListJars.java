package com.magi.app.listjars;

import java.io.File;
import java.io.IOException;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import com.magi.io.FileScannerImpl;

/**
 * ListJars
 * 
 * Application to list the jars that a particular class can be found in.
 * This app is useful for searching jar libraries for the existence of a class, and to
 * output which jar library(s) a class appears in.
 * 
 * USAGE:
 * 
 * ListJars <search-for-class> [<search-path>]
 * 
 * The search path is an optional argument, and will default to the current directory.
 * For example:
 * 
 * C:\> ListJars org.apache.http.client.HttpClient C:\maven-home\repository
 * 
 * May result in the following output:
 * 
 * C:\maven-home\repository\org\apache\httpcomponents\httpclient\4.5.9\http-client-4.5.9.jar
 *   |
 *   +-- org/apache/http/client/HttpClient.class
 * 
 * @author patkins
 */
public class ListJars extends FileScannerImpl {

	private static final boolean IS_SORTED = true;
	private static final boolean INCLUDE_SUBDIRS = true;
	
	private String searchFor = null;
	private File lastReported = null;
	
	public ListJars(String path, String searchForClass, boolean sorted, boolean includeSubs) {
		super(path, "*.jar", sorted, includeSubs);
		
		this.searchFor = normaliseSearchFor(searchForClass);
	}

	@Override
	protected void scanFile(File afile) {
		try {
		    JarFile jarFile = new JarFile(afile);
		    
		    jarFile.stream().forEach(entry -> {
		    	if (entry.toString().contains(searchFor)) {
		    		// Print each result to the console
		    		handleSearchResult(afile, entry);
		    	}
		    });
		    
		    jarFile.close();
		}
		catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	protected void handleSearchResult(File jarfile, JarEntry entry) {
		if (this.lastReported == null || !jarfile.equals(this.lastReported)) {
		    System.out.println("\n" + jarfile.getAbsolutePath());
		}
		System.out.println("  |");
		System.out.println("  +-- " + entry);
		this.lastReported = jarfile;
	}

	private String normaliseSearchFor(String searchFor) {
		// Replace dots and backslashes with forward slash
		String normalised = searchFor.replace('.', '/');
		return normalised.replace('\\', '/');
	}

	public static void main(String[] args) {
		String searchFor  = null;
		String searchPath = ".";
		
		if (args.length == 0) {
			// You must have at least one parameter (the class to search for)
			System.out.println("USAGE: ListJars <search-for-class> [<search-path>]");
			return;
		}

		if (args.length > 0) {
		    searchFor = args[0];
		}
		
		if (args.length > 1) {
			searchPath = args[1];
		}
		
		ListJars listJars = new ListJars(searchPath, searchFor, IS_SORTED, INCLUDE_SUBDIRS);
		
		listJars.scan();
	}

}

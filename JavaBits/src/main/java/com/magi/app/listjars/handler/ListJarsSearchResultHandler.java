package com.magi.app.listjars.handler;

import java.io.File;
import java.util.jar.JarEntry;

public interface ListJarsSearchResultHandler {

	public void handleSearchResult(File jarfile, JarEntry entry);
	
}

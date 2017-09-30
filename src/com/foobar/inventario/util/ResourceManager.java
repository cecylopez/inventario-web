package com.foobar.inventario.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URL;

import org.apache.log4j.Logger;

public class ResourceManager {
	protected static Logger logger = Logger.getLogger(ResourceManager.class);

	public static InputStream getResource(String resourceName) throws FileNotFoundException {
		URL resourceURL = ClassLoader.getSystemResource(resourceName);
		logger.debug("resourceURL: " + resourceURL);
		return new FileInputStream(resourceURL.getPath());
	}
}

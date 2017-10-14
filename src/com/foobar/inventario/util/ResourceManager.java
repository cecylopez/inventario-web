package com.foobar.inventario.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URL;

import org.apache.log4j.Logger;

public class ResourceManager {
	 public static final String RECURSO = "menu.json";
	protected static Logger logger = Logger.getLogger(ResourceManager.class);

	public static InputStream getResource(String resourceName) throws FileNotFoundException {
		return ResourceManager.class.getResourceAsStream(resourceName);
	}
	
	
}

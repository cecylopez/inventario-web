package com.foobar.inventario.util;

import java.io.FileNotFoundException;
import java.io.InputStream;

import org.apache.log4j.Logger;

public class ResourceManager {
	 public static final String RECURSO = "menu.json";
	protected static Logger logger = Logger.getLogger(ResourceManager.class);

	public static InputStream getResource(String resourceName) throws FileNotFoundException {
		System.out.println("testStream1: "+ ResourceManager.class.getResourceAsStream(RECURSO) );
		System.out.println("testStream2:"+ ResourceManager.class.getResource(RECURSO));
		System.out.println("testStream5: "+ ResourceManager.class.getResourceAsStream("/com/foobar/inventario/util/" + RECURSO) );

		return ResourceManager.class.getResourceAsStream(resourceName);
	}
	
	
}

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
		URL resourceURL = ClassLoader.getSystemResource(resourceName);
		logger.debug("resourceURL: " + resourceURL);
		System.out.println("testStream1: "+ ResourceManager.class.getResourceAsStream(RECURSO) );

		System.out.println("testStream2:"+ ResourceManager.class.getResource(RECURSO));
		System.out.println("testStream3: "+ ClassLoader.getSystemClassLoader().getResourceAsStream(RECURSO) );
		System.out.println("testStream4: "+ ResourceManager.class.getResourceAsStream("/" + RECURSO) );
		System.out.println("testStream5: "+ ResourceManager.class.getResourceAsStream("/com/foobar/inventario/util/" + RECURSO) );
		System.out.println("testStream6: "+ ResourceManager.class.getResourceAsStream("/inventario-web-0.0.1-SNAPSHOT/WEB-INF/classes/com/foobar/inventario/util/" + RECURSO) );
		System.out.println("testStream7: "+ ResourceManager.class.getResourceAsStream("/target/inventario-web-0.0.1-SNAPSHOT/WEB-INF/classes/com/foobar/inventario/util/" + RECURSO) );

		return ResourceManager.class.getResourceAsStream(resourceName);
	}
	
	
}

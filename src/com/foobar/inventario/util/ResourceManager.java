package com.foobar.inventario.util;

import java.io.InputStream;

public class ResourceManager {

	public static InputStream getResource(String resourceName) {
		return ResourceManager.class.getClassLoader().getResourceAsStream(resourceName);
	}
}

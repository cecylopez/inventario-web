package com.foobar.inventario.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class ResourceManager {

	public static InputStream getResource(String resourceName) throws FileNotFoundException {
		return new FileInputStream(ClassLoader.getSystemResource(resourceName).getPath());
	}
}

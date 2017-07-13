package com.foobar.inventario.util;

import java.io.IOException;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.foobar.inventario.data.Resultado;

public class ErrorHelper {
	private static Logger logger= Logger.getLogger(ErrorHelper.class);
	public static Resultado getError(int codigo){
		Properties props= new Properties();
		Resultado res;
		try {
			props.load(ErrorHelper.class.getResourceAsStream("/org/foobar/inventario/properties/errorCodes.properties"));
			res= new Resultado(codigo, props.getProperty(String.valueOf(codigo), "Error desconocido"));

		} catch (Exception e) {
			logger.error("Error al cargar el archivo errorCodes.properties " + e.getMessage(), e);
			res=new Resultado(9999,"Error desconocido" );
		}
		return res;
	}

}

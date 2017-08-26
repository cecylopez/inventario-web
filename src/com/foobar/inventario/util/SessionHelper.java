package com.foobar.inventario.util;

import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.inventario.data.entities.Usuario;

public class SessionHelper {
	public static final String USUARIO_SESION= "usuario";
	private static Logger logger= Logger.getLogger(SessionHelper.class);

	public static Usuario getUsuarioSession(HttpSession sesion){
		Object userInSession= sesion.getAttribute(USUARIO_SESION);
		logger.debug("userInSession: "+ userInSession);
		if(userInSession!=null && userInSession instanceof Usuario){
			return (Usuario)userInSession;
		}else{
			return null;
		}
	}
}

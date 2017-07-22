package org.foobar.inventario.servlets;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.foobar.inventario.data.Resultado;

import com.foobar.inventario.util.ErrorHelper;

/**
 * Servlet implementation class BaseServlet
 */
@WebServlet("/BaseServlet")
public class BaseServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	protected Logger logger=Logger.getLogger(this.getClass());
	public static final int PAGE_SIZE_DEFAULT=20;

  
    public BaseServlet() {
        super();
    }

	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String opt=req.getParameter("opt");
		logger.debug("opt: "+ opt);
		Resultado result= execMethod(opt, req, resp);
		logger.debug("Devolviendo resultado: " + result);
		
		resp.setContentType("application/json");
		resp.setCharacterEncoding("UTF-8");
		resp.getWriter().write(result.toJson().toString());
	}
	public Resultado execMethod(String methodName, HttpServletRequest req, HttpServletResponse resp){
		Resultado result= Resultado.OK;
		Method method = null;
		try {
			method = this.getClass().getMethod(methodName, HttpServletRequest.class, HttpServletResponse.class);
		} catch (NoSuchMethodException  | SecurityException se) {
			logger.error("Metodo con nombre "+ se.getMessage(), se);
			return ErrorHelper.getError(107);
		}
		
		 try {
			result=(Resultado)method.invoke(this, req, resp);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			logger.error("Error tratando de invocar al metodo con nombre "+ e.getMessage(), e);
			return ErrorHelper.getError(107);
		}
		 
		
		return result;
	}

}

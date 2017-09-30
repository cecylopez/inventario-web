package org.foobar.inventario.filters;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.ws.handler.HandlerResolver;

import org.foobar.inventario.data.Resultado;
import org.foobar.inventario.servlets.LoginServlet;
import org.inventario.data.entities.Usuario;

import com.foobar.inventario.util.SessionHelper;

//
@WebFilter(filterName = "LoginFilter", urlPatterns = { "/site/*", "/sec/*" })
public class LoginFilter implements Filter {

	public void destroy() {
	}

	public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
			throws IOException, ServletException {
		// transformacion/casting de tipo de ServletRequest a HTTPservlet que es
		// la que estamos usando.
		// getSession(true)porque no queremos que la session regrese null. Con
		// true puede construir una session al vuelo
		
		// instanceof revisara que lo que este guardado en usr sea de tipo
		// usuario
		if (SessionHelper.getUsuarioSession(((HttpServletRequest) req).getSession(true))==null) {
			if (((HttpServletRequest) req).getHeader("Accept").contains("application/json")) {
				resp.setContentType("application/json");
				resp.getWriter().write(new Resultado(101, "Usuario no autorizado").toJson().toString());
			} else {
				((HttpServletResponse) resp).sendRedirect("/login.html");
			}

		} else {
			chain.doFilter(req, resp);
		}

	}

	public void init(FilterConfig fConfig) throws ServletException {
	}

}

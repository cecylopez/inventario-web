package org.foobar.inventario.servlets;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.foobar.inventario.data.Resultado;

/**
 * Servlet implementation class SecServlet
 */
@WebServlet("/sec/SecServlet")
public class SecServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.setContentType("application/json");
		resp.getWriter().write(Resultado.OK.toJson().toString());
	}

}

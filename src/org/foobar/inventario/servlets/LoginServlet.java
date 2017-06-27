package org.foobar.inventario.servlets;

import java.io.IOException;
import java.util.Arrays;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.foobar.inventario.data.Resultado;
import org.inventario.data.UsersRepository;
import org.inventario.data.entities.Usuario;

/**
 * Servlet implementation class LoginServlet
 */
@WebServlet("/LoginServlet")
public class LoginServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Logger logger=Logger.getLogger(this.getClass());
	
	public void init(){
		BasicConfigurator.configure();
	}

	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException{
		Resultado result= Resultado.INVALID_OPT;
		String opt= req.getParameter("opt");
		logger.debug("params: " + Arrays.toString(req.getParameterMap().keySet().toArray()));
		if("logIn".equals(opt)){
			result= logIn(req, resp);
		}else if ("logOut".equals(opt)) {
			result=logOut(req, resp);
		}
		resp.setContentType("application/json");
		resp.getWriter().write(result.toJson().toString());
	}

	public Resultado logIn(HttpServletRequest req, HttpServletResponse resp) {
		Resultado r = Resultado.OK;
		UsersRepository repo = new UsersRepository();
		Usuario user = repo.login(req.getParameter("usuario"), req.getParameter("clave"));
		if (user != null) {
			user.setClave("");
			r = new Resultado(0, "OK", user, "usuario");
			req.getSession(true).setAttribute("usuario", user);
		} else {
			r = new Resultado(100, "Usuario o clave invalidos");

		}

		return r;
	}

	public Resultado logOut(HttpServletRequest req, HttpServletResponse resp) {
		if (req.getSession() != null) {
			req.getSession().removeAttribute("usuario");
			req.getSession().invalidate();
		}
		return Resultado.OK;
	}

}

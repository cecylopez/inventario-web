package org.foobar.inventario.servlets;


import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpSession;

import org.foobar.inventario.data.Resultado;
import org.inventario.data.UsersRepository;
import org.inventario.data.entities.Usuario;


/**
 * Servlet implementation class LoginServlet
 */
@WebServlet("/LoginServlet")
public class LoginServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public Resultado login(String usuario, String clave, HttpSession sesion) {
		Resultado r = Resultado.OK;
		UsersRepository repo = new UsersRepository();
		Usuario user = repo.login(usuario, clave);
		if (user != null) {
			user.setClave("");
			r = new Resultado(0, "OK", user, "usuario");
			sesion.setAttribute("usuario", user);
		} else {
			r = new Resultado(100, "Usuario o clave invalidos");

		}

		return r;
	}
	
	public Resultado logOut(HttpSession sesion){
		sesion.removeAttribute("usuario");
		sesion.invalidate();
		return Resultado.OK;
	}

}

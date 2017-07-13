package org.foobar.inventario.servlets;

import java.io.IOException;
import java.util.Arrays;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.foobar.inventario.data.Resultado;
import org.inventario.data.UsersRepository;
import org.inventario.data.entities.Usuario;
import org.inventario.util.SecurityHelper;

import com.foobar.inventario.util.ErrorHelper;

/**
 * Servlet implementation class LoginServlet
 */
//Anotacion funciona en vez del web.xml usando una version de servlet 3.0 
@WebServlet("/LoginServlet")
public class LoginServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Logger logger=Logger.getLogger(this.getClass());
	public static final String USUARIO_SESION= "usuario";
	
	public void init(){
		BasicConfigurator.configure();
	}

	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException{
		Resultado result= ErrorHelper.getError(99);
		String opt= req.getParameter("opt");
		logger.debug("params: " + Arrays.toString(req.getParameterMap().keySet().toArray()));
		if("logIn".equals(opt)){
			result= logIn(req, resp);
		}else if ("logOut".equals(opt)) {
			result=logOut(req, resp);
		}else if ("getUser".equals(opt)){
			result=getUser(req, resp);
		}else if("cambiarClave".equals(opt)){
			result=cambiarClave(req, resp);
		}
		resp.setContentType("application/json");
		resp.setCharacterEncoding("UTF-8");
		resp.getWriter().write(result.toJson().toString());
	}

	public Resultado logIn(HttpServletRequest req, HttpServletResponse resp) {
		Resultado r = Resultado.OK;
		UsersRepository repo = new UsersRepository();
		Usuario user = repo.login(req.getParameter("usuario"), req.getParameter("clave"));
		if (user != null) {
			user.setClave("");
			r = new Resultado(0, "OK", user, "usuario");
			req.getSession(true).setAttribute(USUARIO_SESION, user);
		} else {
			r = ErrorHelper.getError(100);
		}
		repo.close();

		return r;
	}

	public Resultado logOut(HttpServletRequest req, HttpServletResponse resp) {
		if (req.getSession() != null) {
			req.getSession().removeAttribute(USUARIO_SESION);
			req.getSession().invalidate();
		}
		return Resultado.OK;
	}
	
	public Resultado getUser(HttpServletRequest req, HttpServletResponse resp){
		Resultado rest= ErrorHelper.getError(101);
		if(req.getSession()!=null && req.getSession().getAttribute(USUARIO_SESION) !=null && req.getSession().getAttribute(USUARIO_SESION) instanceof Usuario){
			rest.setCodigo(0);
			rest.setRazon("OK");
			rest.setContenido(((Usuario)req.getSession().getAttribute(USUARIO_SESION)).toJson());
		}
		return rest;
	}
	
	public Resultado cambiarClave(HttpServletRequest req, HttpServletResponse resp){
		Resultado rest=Resultado.OK;
		if(req.getSession()!=null && req.getSession().getAttribute(USUARIO_SESION) !=null && req.getSession().getAttribute(USUARIO_SESION) instanceof Usuario){
			String claveActual=req.getParameter("claveActual");
			Usuario	usr=(Usuario)req.getSession().getAttribute(USUARIO_SESION);
			UsersRepository repo= new UsersRepository();
			Usuario usuarioB= repo.get(usr.getId());
			String claveActualVerificar= usuarioB.getClave();
			if(SecurityHelper.verificar(claveActual, claveActualVerificar)){
				String nuevaClave=req.getParameter("nuevaClave");
				String repetirNuevaClave=req.getParameter("repetirNuevaClave");
				if(nuevaClave.equals(repetirNuevaClave)){
					usr.setClave(SecurityHelper.encriptar(nuevaClave));
					repo.update(usr);
					repo.close();
				}else{
					rest= ErrorHelper.getError(103);
				}
			}else{
				rest=ErrorHelper.getError(102);
			}
		}else{
			rest=ErrorHelper.getError(101);
		}
		return rest;
	}

}

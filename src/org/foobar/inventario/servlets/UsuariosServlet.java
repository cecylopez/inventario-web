package org.foobar.inventario.servlets;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.foobar.inventario.data.Estado;
import org.foobar.inventario.data.Resultado;
import org.inventario.data.UsersRepository;
import org.inventario.data.entities.Usuario;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;


@WebServlet("/UsuariosServlet")
public class UsuariosServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Logger logger=Logger.getLogger(this.getClass());

	
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		Resultado result= Resultado.INVALID_OPT;
		String opt= req.getParameter("opt");
		logger.debug("params: " + Arrays.toString(req.getParameterMap().keySet().toArray()));
		if("getUsers".equals(opt)){
			result=getUsers(req,resp);
		}
		resp.setContentType("application/json");
		resp.setCharacterEncoding("UTF-8");
		resp.getWriter().write(result.toJson().toString());
	}

	public Resultado getUsers(HttpServletRequest req, HttpServletResponse resp){
		Resultado res=Resultado.OK;
		List<Usuario> users;
		JsonObject jsonUsuarios = new JsonObject();
		JsonArray arr = new JsonArray();
		
		UsersRepository repo= new UsersRepository();
		users=repo.getAll().stream().filter(u -> !u.getEstado().equals(Estado.ELIMINADO)).collect(Collectors.toList());
		
		for (Usuario usr: users) {
			arr.add(usr.toJson());
		}
		
		jsonUsuarios.add("usuarios", arr);
		res.setContenido(jsonUsuarios);
		
		return res;
	}
}

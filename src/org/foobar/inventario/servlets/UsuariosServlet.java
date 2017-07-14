package org.foobar.inventario.servlets;

import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.foobar.inventario.data.Estado;
import org.foobar.inventario.data.Resultado;
import org.inventario.data.BaseRepository;
import org.inventario.data.UsersRepository;
import org.inventario.data.entities.Departamento;
import org.inventario.data.entities.Rol;
import org.inventario.data.entities.Usuario;
import org.inventario.util.SecurityHelper;

import com.foobar.inventario.util.ErrorHelper;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;


@WebServlet("/UsuariosServlet")
public class UsuariosServlet extends BaseServlet {
	private static final long serialVersionUID = 1L;
	private Logger logger=Logger.getLogger(this.getClass());


	public Resultado getUsers(HttpServletRequest req, HttpServletResponse resp){
		Resultado res=new Resultado(0, "OK");
		List<Usuario> users;
		JsonObject jsonUsuarios = new JsonObject();
		JsonArray arr = new JsonArray();
		
		UsersRepository repo= new UsersRepository();
		users=repo.getAll().stream().filter(u -> !u.getEstado().equals(Estado.ELIMINADO)).collect(Collectors.toList());
		for (Usuario usr: users) {
			if(Estado.ACTIVO.equals(usr.getEstado())){
				usr.setEstado("Activo");
			}else if (Estado.INACTIVO.equals(usr.getEstado())) {
				usr.setEstado("Inactivo");
			}
			arr.add(usr.toJson());
		}
		
		jsonUsuarios.add("usuarios", arr);
		res.setContenido(jsonUsuarios);
		repo.close();
		return res;
	}
	
	public Resultado getRoles(HttpServletRequest req, HttpServletResponse resp){
		Resultado res=new Resultado(0, "OK");
		List<Rol> roles;
		JsonObject jsonRoles = new JsonObject();
		JsonArray arr= new JsonArray();
		BaseRepository<Rol> brepo= new BaseRepository<>(Rol.class);
		roles=brepo.getAll();
		req.getSession(true).setAttribute("roles", roles);
		for(Rol rol: roles){
			arr.add(rol.toJson());
		}
		
		jsonRoles.add("roles",arr );
		res.setContenido(jsonRoles);
		brepo.close();
		return res;
	}
	
	public Resultado getDepartamentos(HttpServletRequest req, HttpServletResponse resp){
		Resultado res=new Resultado(0,"OK");
		List<Departamento> departamentos;
		JsonObject jsonDepartamentos= new JsonObject();
		JsonArray arrDept= new JsonArray();
		BaseRepository<Departamento> baseRepo= new BaseRepository<>(Departamento.class);
		departamentos= baseRepo.getAll();
		req.getSession(true).setAttribute("departamentos", departamentos);
		for(Departamento dept: departamentos){
			arrDept.add(dept.toJson());
		}
		jsonDepartamentos.add("departamentos", arrDept);
		res.setContenido(jsonDepartamentos);
		baseRepo.close();
		return res;
	}
	public Resultado addUsuario(HttpServletRequest req, HttpServletResponse resp){
		Resultado res= Resultado.OK;
		UsersRepository repo= new UsersRepository();
		Usuario user= new Usuario();
		try {
			if(repo.get(req.getParameter("nombre")) !=null){
				res= ErrorHelper.getError(105);
				
			}else {
				List<Rol> roles=(List<Rol>)req.getSession(true).getAttribute("roles");
				List<Departamento> departamentos=(List<Departamento>)req.getSession().getAttribute("departamentos");
				user.setId(repo.getMaxId()+1);
				user.setNombre(req.getParameter("nombre"));
				user.setClave(SecurityHelper.encriptar(req.getParameter("clave")));
				user.setRol(roles.stream().filter(r ->r.getId()==Integer.parseInt(req.getParameter("rol"))).findFirst().get());
				user.setDepartamento(departamentos.stream().filter(d ->d.getId()==Integer.parseInt(req.getParameter("departamento"))).findFirst().get());
				user.setEstado(Estado.ACTIVO);
				repo.add(user);
			}
		
		
		} catch (Exception e) {
			logger.error("excepcion tratanto de agregar usuario "+ e.getMessage(), e);
			 res= ErrorHelper.getError(104); 
		}
		repo.close();
		return res;
	}
	
	public Resultado deleteUsuario(HttpServletRequest req, HttpServletResponse resp){
		Resultado result= Resultado.OK;
		UsersRepository repo= new UsersRepository();
		try {
			repo.delete(repo.get(Integer.parseInt(req.getParameter("userId"))));
		} catch (Exception e) {
			logger.error("excepcion tratando de eliminar usario "+ e.getMessage(), e);
			result= ErrorHelper.getError(106);
		}
		repo.close();
		return result;
	}
	
}

package org.foobar.inventario.servlets;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.foobar.inventario.data.Resultado;
import org.inventario.data.BaseRepository;
import org.inventario.data.Status;
import org.inventario.data.UsersRepository;
import org.inventario.data.entities.Departamento;
import org.inventario.data.entities.Rol;
import org.inventario.data.entities.Usuario;
import org.inventario.util.SecurityHelper;

import com.foobar.inventario.util.ErrorHelper;
import com.foobar.inventario.util.ResourceManager;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;


@WebServlet("/UsuariosServlet")
public class UsuariosServlet extends BaseServlet {
	private static final long serialVersionUID = 1L;


	public Resultado getUsers(HttpServletRequest req, HttpServletResponse resp){
		Resultado res=new Resultado(0, "OK");
		List<Usuario> users;
		JsonObject jsonUsuarios = new JsonObject();
		JsonArray arr = new JsonArray();
		
		UsersRepository repo= new UsersRepository();
		users=repo.getAll().stream().filter(u -> !u.getEstado().equals(Status.ELIMINADO)).collect(Collectors.toList());
		for (Usuario usr: users) {
			if(Status.ACTIVO.equals(usr.getEstado())){
				usr.setEstado("Activo");
			}else if (Status.INACTIVO.equals(usr.getEstado())) {
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
				return res;
				
			}else {
				List<Rol> roles=(List<Rol>)req.getSession(true).getAttribute("roles");
				List<Departamento> departamentos=(List<Departamento>)req.getSession().getAttribute("departamentos");
				user.setId(repo.getMaxId()+1);
				user.setNombre(req.getParameter("nombre"));
				user.setClave(SecurityHelper.encriptar(req.getParameter("clave")));
				user.setRol(roles.stream().filter(r ->r.getId()==Integer.parseInt(req.getParameter("rol"))).findFirst().get());
				user.setDepartamento(departamentos.stream().filter(d ->d.getId()==Integer.parseInt(req.getParameter("departamento"))).findFirst().get());
				user.setEstado(Status.ACTIVO);
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
	public Resultado modifyUsuario(HttpServletRequest req, HttpServletResponse resp){
		Resultado result= Resultado.OK;
		Usuario user= new Usuario();
		UsersRepository repo= new UsersRepository();
		try {
			Usuario usrbd=repo.get(req.getParameter("nombre"));
			if (usrbd !=null && usrbd.getId() !=Integer.parseInt(req.getParameter("id"))) {
				result=ErrorHelper.getError(105);
				return result;
			}
			if(req.getParameter("clave")==null || req.getParameter("clave").isEmpty()){
				user.setClave(usrbd.getClave());
			}else{
				user.setClave(SecurityHelper.encriptar(req.getParameter("clave")));

			}
			List<Rol> roles=(List<Rol>)req.getSession(true).getAttribute("roles");
			List<Departamento> departamentos=(List<Departamento>)req.getSession().getAttribute("departamentos");
			user.setId(Integer.parseInt(req.getParameter("id")));
			user.setNombre(req.getParameter("nombre"));
			user.setEstado(Status.ACTIVO);
			user.setRol(roles.stream().filter(r ->r.getId()==Integer.parseInt(req.getParameter("rol"))).findFirst().get());
			user.setDepartamento(departamentos.stream().filter(d ->d.getId()==Integer.parseInt(req.getParameter("departamento"))).findFirst().get());
			repo.update(user);
			
			
		} catch (Exception e) {
			logger.error("Error al obtener informacion del usuario "+ e.getMessage(), e);
			result=ErrorHelper.getError(108);
		}
		
		return result;
	}
	
	public Resultado getMenu(HttpServletRequest req, HttpServletResponse resp){
		Resultado result=new Resultado(0, "OK");
		String menuJson= "";
		String strLinea=null;
		
		try (InputStream s = ResourceManager.getResource("/menu.json");
				InputStreamReader isr = new InputStreamReader(s);
				BufferedReader br= new BufferedReader(isr)) {
			strLinea=br.readLine();
			
			while (strLinea!=null) {
				menuJson += strLinea;
				strLinea = br.readLine();
			}
			JsonParser parser= new JsonParser();
			result.setContenido(parser.parse(menuJson).getAsJsonObject());
		} catch (IOException e) {
			logger.error("Error al tratar de leer el archivo json "+e.getMessage(), e);
		}
		return result;
	}
	
}

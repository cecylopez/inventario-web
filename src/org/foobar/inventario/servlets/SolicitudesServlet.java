package org.foobar.inventario.servlets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.foobar.inventario.data.Resultado;
import org.inventario.data.BaseRepository;
import org.inventario.data.DepartamentoRepository;
import org.inventario.data.ItemsRepository;
import org.inventario.data.SolicitudesRepository;
import org.inventario.data.Status;
import org.inventario.data.entities.AsignacionItem;
import org.inventario.data.entities.Departamento;
import org.inventario.data.entities.SolicitudAsignacion;
import org.inventario.data.entities.Usuario;

import com.foobar.inventario.util.ErrorHelper;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

@WebServlet("/SolicitudesServlet")
public class SolicitudesServlet extends BaseServlet {
	private static final long serialVersionUID = 1L; 
	
	public Resultado getSolicitudes(HttpServletRequest req, HttpServletResponse resp){
		Resultado result= new Resultado(0, "OK");
		int index=((Integer.parseInt(req.getParameter("page")) - 1) * PAGE_SIZE_DEFAULT);
		long total=0;
		List<SolicitudAsignacion> solicitudes= new ArrayList<SolicitudAsignacion>(0);
		JsonObject solicitudesJson= new JsonObject();
		JsonArray solicitudesArray= new JsonArray();
		SolicitudesRepository solicitudesRepo= new SolicitudesRepository();
		Usuario user=(Usuario)req.getSession(true).getAttribute(LoginServlet.USUARIO_SESION);
		solicitudes= solicitudesRepo.get(Long.valueOf(user.getDepartamento().getId()), req.getParameter("nombreItem"), index, BaseServlet.PAGE_SIZE_DEFAULT);
		for(SolicitudAsignacion solicitud: solicitudes){
			if(Status.ACTIVO.equals(solicitud.getEstado())){
				solicitud.setEstado("Aprobado");
			}else if (Status.PENDIENTE.equals(solicitud.getEstado())) {
				solicitud.setEstado("Pendiente");
			}else if(Status.RECHAZADO.equals(solicitud.getEstado())){
				solicitud.setEstado("Rechazado");
			}
			solicitudesArray.add(solicitud.toJson());
		}
		solicitudesJson.add("solicitudes", solicitudesArray);
		total=solicitudesRepo.getTotal();
		solicitudesJson.addProperty("total", total);
		solicitudesJson.addProperty("pageSize", PAGE_SIZE_DEFAULT);
		result.setContenido(solicitudesJson);
		solicitudesRepo.close();
		return result;
	}
	
	public Resultado aprobarSolicitud(HttpServletRequest req, HttpServletResponse resp){
		Resultado result= new Resultado(0, "OK");
		SolicitudesRepository solicitudRepo= new SolicitudesRepository();
		SolicitudAsignacion solicitud= solicitudRepo.get(Long.valueOf(req.getParameter("idSolicitud")));
		DepartamentoRepository deptoRepo= new DepartamentoRepository();
		Departamento bodega= deptoRepo.get(deptoRepo.BODEGA);
		ItemsRepository itemRepo= new ItemsRepository();
		BaseRepository<AsignacionItem> asignacionItemRepository= new BaseRepository<>(AsignacionItem.class);
		AsignacionItem asignacionItemEnBodega=itemRepo.get(bodega.getId(), solicitud.getAsignacionItem().getItem().getId());
		if(asignacionItemEnBodega.getCantidad()<solicitud.getCantidad()){
			return ErrorHelper.getError(200);
		}else{
			Usuario user= (Usuario)req.getSession(true).getAttribute(LoginServlet.USUARIO_SESION);
			solicitud.setFechaAutorizacion(new Date());
			solicitud.setUsuario2(user);
			solicitud.setEstado(Status.ACTIVO);
			asignacionItemEnBodega.setCantidad(asignacionItemEnBodega.getCantidad()- solicitud.getCantidad());
			solicitud.getAsignacionItem().setCantidad(solicitud.getAsignacionItem().getCantidad() + solicitud.getCantidad());
			solicitudRepo.update(solicitud);
			asignacionItemRepository.update(asignacionItemEnBodega);
			asignacionItemRepository.update(solicitud.getAsignacionItem());

			
			result.setRazon("Solicitud Aprobada");
		}
	
		return result;
	}
	
	public Resultado rechazarSolicitud(HttpServletRequest req, HttpServletResponse resp){
		Resultado result= new Resultado(0, "OK");
		try {
			SolicitudesRepository solicitudRepo= new SolicitudesRepository();
			SolicitudAsignacion solicitud=solicitudRepo.get(Long.valueOf(req.getParameter("idSolicitud")));
			solicitud.setEstado(Status.RECHAZADO);
			solicitud.setUsuario2((Usuario)req.getSession(true).getAttribute(LoginServlet.USUARIO_SESION));
			solicitud.setFechaAutorizacion(new Date());
			solicitudRepo.update(solicitud);
			result.setRazon("la solicitud ha sido rechazada correctamente");
		} catch (Exception e) {
			logger.error(ErrorHelper.getError(109) + e.getMessage(),e);
			result=ErrorHelper.getError(109);
		}
	
		return result;
	}
	

}

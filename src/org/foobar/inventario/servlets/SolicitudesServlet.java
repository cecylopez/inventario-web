package org.foobar.inventario.servlets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
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
import org.inventario.data.UsersRepository;
import org.inventario.data.entities.AsignacionItem;
import org.inventario.data.entities.Departamento;
import org.inventario.data.entities.Item;
import org.inventario.data.entities.SolicitudAsignacion;
import org.inventario.data.entities.Usuario;

import com.foobar.inventario.util.ErrorHelper;
import com.foobar.inventario.util.SessionHelper;
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
		Usuario user=SessionHelper.getUsuarioSession(req.getSession(true));
		solicitudes= solicitudesRepo.get(Long.valueOf(user.getDepartamento().getId()), req.getParameter("nombreItem"), index, BaseServlet.PAGE_SIZE_DEFAULT);
		for(SolicitudAsignacion solicitud: solicitudes){
			if(Status.ACTIVO.equals(solicitud.getEstado())){
				solicitud.setEstado("Aprobado");
			}else if (Status.PENDIENTE.equals(solicitud.getEstado())) {
				solicitud.setEstado("Pendiente");
			}else if(Status.RECHAZADO.equals(solicitud.getEstado())){
				solicitud.setEstado("Rechazado");
			}
			if(!solicitud.getEstado().equals(Status.ELIMINADO)){
				solicitudesArray.add(solicitud.toJson());

			}
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
			Usuario user= (Usuario)SessionHelper.getUsuarioSession(req.getSession(true));
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
			solicitud.setUsuario2((Usuario)SessionHelper.getUsuarioSession(req.getSession(true)));
			solicitud.setFechaAutorizacion(new Date());
			solicitudRepo.update(solicitud);
			result.setRazon("la solicitud ha sido rechazada correctamente");
		} catch (Exception e) {
			logger.error(ErrorHelper.getError(109) + e.getMessage(),e);
			result=ErrorHelper.getError(109);
		}
	
		return result;
	}
	public Resultado deleteSolicitud(HttpServletRequest req, HttpServletResponse resp){
		Resultado result= Resultado.OK;
		SolicitudesRepository solicitudRepo= new SolicitudesRepository();
		SolicitudAsignacion solicitud= solicitudRepo.get(Long.valueOf(req.getParameter("idSolicitud")));

		try {
			solicitud.setEstado(Status.ELIMINADO);
			solicitudRepo.update(solicitud);
		} catch (Exception e) {
			logger.error("Error al tratar de eliminar la solicitud "+ e.getMessage(), e);
			result= ErrorHelper.getError(201);
		}
		solicitudRepo.close();
		return result;
	}
	
	public Resultado addSolicitud(HttpServletRequest req, HttpServletResponse resp){
		Resultado result= new Resultado(0, "OK");
		try {
			Usuario usuarioEnSession= (Usuario)SessionHelper.getUsuarioSession(req.getSession(true));
			SolicitudesRepository solicitudRepo= new SolicitudesRepository();
			ItemsRepository itemRepo= new ItemsRepository();
			SolicitudAsignacion solicitud= new SolicitudAsignacion();
			SolicitudAsignacion solicitudRepetida= new SolicitudAsignacion();
			AsignacionItem itemSolicitud=null;
			try{
				 solicitudRepetida= solicitudRepo.getSolicitudPendiente(usuarioEnSession.getDepartamento().getId(), Long.valueOf(req.getParameter("itemId")));
				 if(solicitudRepetida!=null){
					result= ErrorHelper.getError(205);
					return result;
 
				 }
					 
			}catch(NoResultException ne){
				logger.info("Aun no existe una solicitud para el item con id " + Long.valueOf(req.getParameter("itemId")) + "y departamento con id " + usuarioEnSession.getDepartamento().getId() +" por lo que se procedera a agregar uno");
			} catch(NonUniqueResultException nur){
				result= ErrorHelper.getError(205);
				return result;
			}
			
			try {
				 itemSolicitud= itemRepo.get(usuarioEnSession.getDepartamento().getId(), Long.valueOf(req.getParameter("itemId")));
			
			} catch (NoResultException nre) {
				logger.warn("Error al tratar de adquirir un item para el departamento con id " + itemRepo.get(usuarioEnSession.getDepartamento().getId()) + "y item id "+ itemRepo.get(Long.valueOf(req.getParameter("itemId"))) + nre.getMessage(), nre);
				BaseRepository<AsignacionItem> asignacionRepo= new BaseRepository<>(AsignacionItem.class);
				itemSolicitud= new AsignacionItem();
				itemSolicitud.setId(asignacionRepo.getMaxId() + 1);
				itemSolicitud.setCantidad(0);
				itemSolicitud.setDepartamento(usuarioEnSession.getDepartamento());
				itemSolicitud.setItem(itemRepo.getItem(Long.valueOf(req.getParameter("itemId"))));
				asignacionRepo.add(itemSolicitud);
			
			}
			solicitud.setId(solicitudRepo.getMaxId()+1);
			solicitud.setUsuario1(usuarioEnSession);
			solicitud.setFechaSolicitud(new Date());
			solicitud.setEstado(Status.PENDIENTE);
			solicitud.setAsignacionItem(itemSolicitud);
			solicitud.setCantidad(Integer.parseInt(req.getParameter("cantidad")));
			solicitudRepo.add(solicitud);
			return result;
			
		} catch (Exception e) {
			logger.error("Error al tratar de agregar una nueva solicitud "+ e.getMessage(), e);
			result= ErrorHelper.getError(202);
			return result;
		}
	}
	
	public Resultado modifySolicitud(HttpServletRequest req, HttpServletResponse resp){
		Resultado result= new Resultado(0, "OK");
		
		try {
			SolicitudesRepository solicitudRepo= new SolicitudesRepository();
			SolicitudAsignacion solicitudAModificar=solicitudRepo.get(Long.valueOf(req.getParameter("solicitudId")));
			solicitudAModificar.setCantidad(Integer.parseInt(req.getParameter("cantidad")));
			solicitudRepo.update(solicitudAModificar);
			result.setRazon("La solicitud ha sido modificada satisfactoriamente ");
		} catch (Exception e) {
			logger.error("Ha surgido un error al modficar una solicitud "+ e.getMessage(), e);
			result=ErrorHelper.getError(203);
		}
		return result;
	}
	
	public Resultado descartarCantidadItem(HttpServletRequest req, HttpServletResponse resp){
		Resultado result= new Resultado(0, "OK");
		ItemsRepository itemRepo= new ItemsRepository();
		Usuario usuarioEnSession= (Usuario)SessionHelper.getUsuarioSession(req.getSession(true));
		try {
			AsignacionItem asignacionItem= itemRepo.get(usuarioEnSession.getDepartamento().getId(), Long.valueOf(req.getParameter("itemId")));
			asignacionItem.setCantidad(asignacionItem.getCantidad() - Integer.parseInt(req.getParameter("cantidad")));
			itemRepo.update(asignacionItem);
			result.setRazon("La cantidad ha sido actualizada satisfactoriamente");
		} catch (Exception e) {
			logger.error("Ha ocurrido un error al tratar de descartar un elemento "+ e.getMessage(), e);
			result=ErrorHelper.getError(301);
		}
		
		return result;
	
	}
}

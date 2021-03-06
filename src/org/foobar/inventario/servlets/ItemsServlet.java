package org.foobar.inventario.servlets;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.foobar.inventario.data.Resultado;
import org.inventario.data.ItemsRepository;
import org.inventario.data.Status;
import org.inventario.data.entities.AsignacionItem;
import org.inventario.data.entities.Item;
import org.inventario.data.entities.Usuario;

import com.foobar.inventario.util.ErrorHelper;
import com.foobar.inventario.util.SessionHelper;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

/**
 * Servlet implementation class ItemsServlet
 */
@WebServlet("/ItemsServlet")
public class ItemsServlet extends BaseServlet {
	private static final long serialVersionUID = 1L;

	public Resultado getItems(HttpServletRequest req, HttpServletResponse resp){
		int index=((Integer.parseInt(req.getParameter("page")) - 1) * PAGE_SIZE_DEFAULT);
		logger.debug("index: " + index);
		Resultado result= Resultado.OK;
		long total=0;
		List<Item> items= new ArrayList<Item>(0);
		JsonObject jsonItems= new JsonObject();
		JsonArray arrayItems= new JsonArray();
		ItemsRepository itemRepo=  new ItemsRepository();
		Usuario user=(Usuario)SessionHelper.getUsuarioSession(req.getSession(true));
		items=itemRepo.get(Long.valueOf(user.getDepartamento().getId()), req.getParameter("nombreItem"), index, BaseServlet.PAGE_SIZE_DEFAULT);
		logger.debug("****cantidad de ITEMS encontrados******" + Arrays.toString(items.toArray()));
		for(Item item: items){
			if (Status.ACTIVO.equals(item.getEstado())) {
				item.setEstado("Activo");
			}else if (Status.INACTIVO.equals(item.getEstado())) {
				item.setEstado("Inactivo");
			}
			if(!item.getEstado().equals(Status.ELIMINADO)){
				JsonObject itemJson=item.toJson();
				itemJson.addProperty("cantidadDepto", item.getAsignacionItems().get(0).getCantidad());
				logger.debug("Asignacion items:"+Arrays.toString(item.getAsignacionItems().toArray()) );
				arrayItems.add(itemJson);	
			}
			
		}
		total=itemRepo.getTotal(Long.valueOf(user.getDepartamento().getId()), req.getParameter("nombreItem"));
		jsonItems.add("items", arrayItems);
		jsonItems.addProperty("total", total);
		jsonItems.addProperty("pageSize", PAGE_SIZE_DEFAULT);
		result.setContenido(jsonItems);
		itemRepo.close();
		
		this.logger.debug("==== \t\t Retornando resultado: " + result);
		return result;
	}
	public Resultado getAllItems(HttpServletRequest req, HttpServletResponse resp){
		Resultado result= new Resultado(0, "OK");
		ItemsRepository itemRepo= new ItemsRepository();
		List<Item> items= new ArrayList<Item>(0);
		JsonObject itemObject= new JsonObject();
		JsonArray itemArray= new JsonArray();
		items= itemRepo.getAll();
		for(Item item: items){
			if (Status.ACTIVO.equals(item.getEstado())) {
				JsonObject jsonObject= new JsonObject();
				jsonObject.addProperty("id", item.getId());
				jsonObject.addProperty("nombre", item.getNombre());
				itemArray.add(jsonObject);
			}
		}
		itemObject.add("items", itemArray);
		result.setContenido(itemObject);
		return result;
	}
	public Resultado deleteItem(HttpServletRequest req, HttpServletResponse resp){
		Resultado result= new Resultado(0, "OK");
		ItemsRepository itemRepo= new ItemsRepository();
		Item item= itemRepo.getItem(Long.valueOf(req.getParameter("idItem")));
		try {
			item.setEstado(Status.ELIMINADO);
			itemRepo.update(item);
		} catch (Exception e) {
			logger.error("No se ha podido eliminar el item por la siguiente razon "+ e.getMessage(),e);
			result=ErrorHelper.getError(300);
		}
		itemRepo.close();
		return result;
	}
	
}
	
	//Recibiendo(page):		1, 2, 3, 4, 5
	//Pasar al Repo:	0, 20, 40, 60, 80, 100
	//(


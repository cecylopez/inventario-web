package org.foobar.inventario.servlets;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.foobar.inventario.data.Resultado;
import org.inventario.data.ItemsRepository;
import org.inventario.data.Status;
import org.inventario.data.entities.AsignacionItem;
import org.inventario.data.entities.Item;
import org.inventario.data.entities.Usuario;

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
			JsonObject itemJson=item.toJson();
			itemJson.addProperty("cantidadDepto", item.getAsignacionItems().get(0).getCantidad());
			logger.debug("Asignacion items:"+Arrays.toString(item.getAsignacionItems().toArray()) );
			arrayItems.add(itemJson);
		}
		total=itemRepo.getTotal();
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
				item.setEstado("Activo");
			}else if (Status.INACTIVO.equals(item.getEstado())) {
				item.setEstado("Inactivo");
			}
			itemObject=item.toJson();
			
		return result;
	}
	return result;
	}
}
	
	//Recibiendo(page):		1, 2, 3, 4, 5
	//Pasar al Repo:	0, 20, 40, 60, 80, 100
	//(


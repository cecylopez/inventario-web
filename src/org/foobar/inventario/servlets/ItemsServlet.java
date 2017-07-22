package org.foobar.inventario.servlets;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.foobar.inventario.data.Estado;
import org.foobar.inventario.data.Resultado;
import org.inventario.data.ItemsRepository;
import org.inventario.data.entities.Item;
import org.inventario.data.entities.Usuario;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

/**
 * Servlet implementation class ItemsServlet
 */
@WebServlet("/ItemsServlet")
public class ItemsServlet extends BaseServlet {
	private static final long serialVersionUID = 1L;

	public Resultado getItems(HttpServletRequest req, HttpServletResponse resp){
		Resultado result= Resultado.OK;
		List<Item> items= new ArrayList<Item>(0);
		JsonObject jsonItems= new JsonObject();
		JsonArray arrayItems= new JsonArray();
		ItemsRepository itemRepo=  new ItemsRepository();
		Usuario user=(Usuario)req.getSession(true).getAttribute(LoginServlet.USUARIO_SESION);
		items=itemRepo.get(Long.valueOf(user.getDepartamento().getId()), req.getParameter("nombreItem"), Integer.parseInt(req.getParameter("startIndex")), BaseServlet.PAGE_SIZE_DEFAULT);
		logger.debug("****cantidad de ITEMS encontrados******" + Arrays.toString(items.toArray()));
		for(Item item: items){
			if (Estado.ACTIVO.equals(item.getEstado())) {
				item.setEstado("Activo");
			}else if (Estado.INACTIVO.equals(item.getEstado())) {
				item.setEstado("Inactivo");
			}
			arrayItems.add(item.toJson());
		}
		jsonItems.add("items", arrayItems);
		result.setContenido(jsonItems);
		itemRepo.close();
		
		this.logger.debug("==== \t\t Retornando resultado: " + result);
		return result;
	}
}

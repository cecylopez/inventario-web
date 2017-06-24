package org.foobar.inventario.data;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import lombok.Data;

@Data
public class Resultado {
	private int codigo;
	private String razon;
	private JsonObject contenido;
	private Gson g;
	public static final Resultado OK= new Resultado(0,"OK");
	
	public Resultado(int codigo, String razon){
		this.codigo = codigo;
		this.razon = razon;
		this.contenido = new JsonObject();
		this.g= new Gson();
	}	
	public Resultado(int codigo, String razon, Object objeto, String nombreObjeto){
		this(codigo,razon);
		this.contenido.add(nombreObjeto, this.g.toJsonTree(objeto));
	}
	
	public JsonObject toJson(){
		JsonObject obj= new JsonObject();
		obj.addProperty("codigo", this.codigo);
		obj.addProperty("razon", this.razon);
		obj.add("contenido", this.contenido);
		return obj;
	}
	
}

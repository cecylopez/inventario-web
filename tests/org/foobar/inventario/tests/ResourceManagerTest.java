package org.foobar.inventario.tests;

import static org.junit.Assert.*;

import org.junit.Test;

import com.foobar.inventario.util.ResourceManager;

public class ResourceManagerTest {
	 public static final String RECURSO = "menu.json";
	
	@Test
	public void testStream1() {
		assertNotNull("getResourceAsStream deberia devolver el recurso", ResourceManager.class.getResourceAsStream(RECURSO));
		System.out.println("testStream1: "+ ResourceManager.class.getResourceAsStream(RECURSO) );
	}
	
	@Test
	public void testStream2(){
		assertNotNull("getResource debe devolver el recurso ", ResourceManager.class.getResource(RECURSO));
		System.out.println("testStream2:"+ ResourceManager.class.getResource(RECURSO));
	}
	@Test
	public void testStream3(){
		assertNotNull("ClassLoader deberia de devolver el recurso", ClassLoader.getSystemClassLoader().getResourceAsStream(RECURSO));
		System.out.println("testStream3: "+ ClassLoader.getSystemClassLoader().getResourceAsStream(RECURSO) );
	}
	
	@Test
	public void testStream4() {
		assertNotNull("getResourceAsStream deberia devolver el recurso", ResourceManager.class.getResourceAsStream("/" + RECURSO));
		System.out.println("testStream4: "+ ResourceManager.class.getResourceAsStream("/" + RECURSO) );
	}
	@Test
	public void testStream5() {
		assertNotNull("getResourceAsStream deberia devolver el recurso", ResourceManager.class.getResourceAsStream("/com/foobar/inventario/util/" + RECURSO));
		System.out.println("testStream5: "+ ResourceManager.class.getResourceAsStream("/com/foobar/inventario/util/" + RECURSO) );
	}
	@Test
	public void testStream6() {
		assertNotNull("getResourceAsStream deberia devolver el recurso", ResourceManager.class.getResourceAsStream("/inventario-web-0.0.1-SNAPSHOT/WEB-INF/classes/com/foobar/inventario/util/" + RECURSO));
		System.out.println("testStream6: "+ ResourceManager.class.getResourceAsStream("/inventario-web-0.0.1-SNAPSHOT/WEB-INF/classes/com/foobar/inventario/util/" + RECURSO) );
	}
	
	@Test
	public void testStream7() {
		assertNotNull("getResourceAsStream deberia devolver el recurso", ResourceManager.class.getResourceAsStream("/target/inventario-web-0.0.1-SNAPSHOT/WEB-INF/classes/com/foobar/inventario/util/" + RECURSO));
		System.out.println("testStream7: "+ ResourceManager.class.getResourceAsStream("/target/inventario-web-0.0.1-SNAPSHOT/WEB-INF/classes/com/foobar/inventario/util/" + RECURSO) );
	}

}

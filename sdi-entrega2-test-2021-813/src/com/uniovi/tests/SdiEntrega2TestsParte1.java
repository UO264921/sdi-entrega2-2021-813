package com.uniovi.tests;

import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;

import com.uniovi.tests.pageobjects.PO_BuscarView;
import com.uniovi.tests.pageobjects.PO_NavView;
import com.uniovi.tests.pageobjects.PO_LoginView;
import com.uniovi.tests.pageobjects.PO_OfertaView;
import com.uniovi.tests.pageobjects.PO_RegisterView;
import com.uniovi.tests.pageobjects.PO_View;
import com.uniovi.tests.util.SeleniumUtils;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class SdiEntrega2TestsParte1 {
	static String PathFirefox65 = "C:\\Program Files\\Mozilla Firefox\\firefox.exe";
	static String Geckdriver024 = "C:\\Users\\Usuario\\Desktop\\Curso 2021\\SDI\\Lab\\Sesion5\\PL-SDI-Sesión5-material\\geckodriver024win64.exe";
	static WebDriver driver = getDriver(PathFirefox65, Geckdriver024);
	static String URL = "https://localhost:8081";

	public static WebDriver getDriver(String PathFirefox, String Geckdriver) {
		System.setProperty("webdriver.firefox.bin", PathFirefox);
		System.setProperty("webdriver.gecko.driver", Geckdriver);
		WebDriver driver = new FirefoxDriver();
		return driver;
	} /* Resto del código de la clase */

	// Antes de cada prueba se navega al URL home de la aplicación
	@Before
	public void setUp() {
		driver.navigate().to(URL);
	}

	// Después de cada prueba se borran las cookies del navegador
	@After
	public void tearDown() {
		driver.manage().deleteAllCookies();
	}

	// Antes de la primera prueba
	@BeforeClass
	static public void begin() {
	}

	// Al finalizar la última prueba
	@AfterClass
	static public void end() {
		// Cerramos el navegador al finalizar las pruebas
		driver.quit();
	}

	// Prueba01. Registro de Usuario con datos válidos.
	@Test
	public void Prueba01() {
		PO_NavView.clickOption(driver, "registrarse", "text", "Registro de usuario");
		PO_RegisterView.fillForm(driver, "test1@test1.com", "test1", "test1Apellido", "test1", "test1");
		PO_View.checkElement(driver, "text", "Te has registrado con exito");
	}

	// Prueba02. Registro de Usuario con datos inválidos (email, nombre, apellidos).
	@Test
	public void Prueba02() {
		PO_NavView.clickOption(driver, "registrarse", "text", "Registro de usuario");
		PO_RegisterView.fillForm(driver, "", "", "", "test2", "test2");
		PO_View.checkElement(driver, "text", "Todos los datos deben estar cubiertos");
	}

	// Prueba03. Registro de Usuario con datos inválidos (contraseñas no coinciden).
	@Test
	public void Prueba03() {
		PO_NavView.clickOption(driver, "registrarse", "text", "Registro de usuario");
		PO_RegisterView.fillForm(driver, "test3@test3.com", "test3", "test3Apellidos", "t", "test3");
		PO_View.checkElement(driver, "text", "Las contraseñas no coinciden");
	}

	// Prueba04. Registro de Usuario con datos inválidos (email ya existe).
	@Test
	public void Prueba04() {
		PO_NavView.clickOption(driver, "registrarse", "text", "Registro de usuario");
		PO_RegisterView.fillForm(driver, "test1@test1.com", "test1", "test1Apellido", "test1", "test1");
		PO_View.checkElement(driver, "text", "Ya existe un usuario con ese email");
	}

	// Prueba05. Iniciar sesion con datos válidos
	@Test
	public void Prueba05() {
		PO_NavView.clickOption(driver, "identificarse", "text", "Identificación de usuario");
		PO_LoginView.fillForm(driver, "test1@test1.com", "test1");
		PO_View.checkElement(driver, "text", "Disfruta de nuestros productos y comparte los tuyos propios");
	}

	// Prueba06. Iniciar sesion con datos invalidos
	@Test
	public void Prueba06() {
		PO_NavView.clickOption(driver, "identificarse", "text", "Identificación de usuario");
		PO_LoginView.fillForm(driver, "", "");
		PO_View.checkElement(driver, "text", "Todos los datos deben estar cubiertos");
	}

	// Prueba07. Iniciar sesion con contraseña erronea
	@Test
	public void Prueba07() {
		PO_NavView.clickOption(driver, "identificarse", "text", "Identificación de usuario");
		PO_LoginView.fillForm(driver, "test1@test1.com", "test2");
		PO_View.checkElement(driver, "text", "Email o contraseña incorrectos");
	}

	// Prueba08. Iniciar sesion con email inexistente
	@Test
	public void Prueba08() {
		PO_NavView.clickOption(driver, "identificarse", "text", "Identificación de usuario");
		PO_LoginView.fillForm(driver, "test2@test2.com", "test2");
		PO_View.checkElement(driver, "text", "Email o contraseña incorrectos");
	}

	// Prueba09. Salir de sesion redirigiendo a login
	@Test
	public void Prueba09() {
		PO_NavView.clickOption(driver, "identificarse", "text", "Identificación de usuario");
		PO_LoginView.fillForm(driver, "test1@test1.com", "test1");
		PO_NavView.clickOption(driver, "logout", "text", "Identificación de usuario");
		PO_View.checkElement(driver, "text", "Identificación de usuario");
	}

	// Prueba10. Salir de sesion no disponible sin autenticarse
	@Test
	public void Prueba10() {
		SeleniumUtils.textoNoPresentePagina(driver, "Desconectarse");
	}

	// Prueba11. Comprobar la lista de usuarios
	@Test
	public void Prueba11() {
		PO_NavView.clickOption(driver, "identificarse", "text", "Identificación de usuario");
		PO_LoginView.fillForm(driver, "admin@admin.com", "admin");
		List<WebElement> elementos = PO_View.checkElement(driver, "id", "listUsers");
		elementos.get(0).click();
		elementos = SeleniumUtils.EsperaCargaPagina(driver, "text", "Nombre", PO_View.getTimeout());
		assertTrue(elementos.size() == 4);
	}

	// Prueba12. Borrar el primer usuario de la lista
	@Test
	public void Prueba12() {

		PO_NavView.clickOption(driver, "registrarse", "text", "Registro de usuario");
		PO_RegisterView.fillForm(driver, "Borrado1@Borrado1.com", "Borrado1", "Borrado1Apellido", "Borrado1",
				"Borrado1");
		PO_View.checkElement(driver, "text", "Te has registrado con exito");

		PO_NavView.clickOption(driver, "identificarse", "text", "Identificación de usuario");
		PO_LoginView.fillForm(driver, "admin@admin.com", "admin");
		List<WebElement> elementos = PO_View.checkElement(driver, "id", "listUsers");
		elementos.get(0).click();
		elementos = SeleniumUtils.EsperaCargaPagina(driver, "text", "Apellidos", PO_View.getTimeout());

		assertTrue(elementos.size() == 5);
		PO_View.checkElement(driver, "text", "Borrado1@Borrado1.com");
		driver.findElement(By.id("checkbox0")).click();
		driver.findElement(By.id("eliminar")).click();

		elementos = SeleniumUtils.EsperaCargaPagina(driver, "text", "Éxito en el borrado de usuarios",
				PO_View.getTimeout());
		elementos = SeleniumUtils.EsperaCargaPagina(driver, "text", "Apellidos", PO_View.getTimeout());
		assertTrue(elementos.size() == 4);
		SeleniumUtils.textoNoPresentePagina(driver, "Borrado1@Borrado1.com");
	}

	// Prueba13. Borrar el ultimo usuario de la lista
	@Test
	public void Prueba13() {
		PO_NavView.clickOption(driver, "registrarse", "text", "Registro de usuario");
		PO_RegisterView.fillForm(driver, "zzzz@zzzz.com", "zzzz", "zzzzApellido", "zzzz", "zzzz");
		PO_View.checkElement(driver, "text", "Te has registrado con exito");

		PO_NavView.clickOption(driver, "identificarse", "text", "Identificación de usuario");
		PO_LoginView.fillForm(driver, "admin@admin.com", "admin");
		List<WebElement> elementos = PO_View.checkElement(driver, "id", "listUsers");
		elementos.get(0).click();
		elementos = SeleniumUtils.EsperaCargaPagina(driver, "text", "Apellidos", PO_View.getTimeout());

		assertTrue(elementos.size() == 5);
		PO_View.checkElement(driver, "text", "zzzz@zzzz.com");
		driver.findElement(By.id("checkbox4")).click();
		driver.findElement(By.id("eliminar")).click();

		elementos = SeleniumUtils.EsperaCargaPagina(driver, "text", "Éxito en el borrado de usuarios",
				PO_View.getTimeout());
		elementos = SeleniumUtils.EsperaCargaPagina(driver, "text", "Apellidos", PO_View.getTimeout());
		assertTrue(elementos.size() == 4);
		SeleniumUtils.textoNoPresentePagina(driver, "zzzz@zzzz.com");
	}

	// Prueba14. Borrar 3 usuarios de la lista
	@Test
	public void Prueba14() {
		PO_NavView.clickOption(driver, "registrarse", "text", "Registro de usuario");
		PO_RegisterView.fillForm(driver, "Borrado1@Borrado1.com", "Borrado1", "Borrado1Apellido", "Borrado1",
				"Borrado1");
		PO_View.checkElement(driver, "text", "Te has registrado con exito");
		PO_NavView.clickOption(driver, "registrarse", "text", "Registro de usuario");
		PO_RegisterView.fillForm(driver, "Borrado2@Borrado2.com", "Borrado2", "Borrado2Apellido", "Borrado2",
				"Borrado2");
		PO_View.checkElement(driver, "text", "Te has registrado con exito");
		PO_NavView.clickOption(driver, "registrarse", "text", "Registro de usuario");
		PO_RegisterView.fillForm(driver, "zzzz@zzzz.com", "zzzz", "zzzzApellido", "zzzz", "zzzz");
		PO_View.checkElement(driver, "text", "Te has registrado con exito");

		PO_NavView.clickOption(driver, "identificarse", "text", "Identificación de usuario");
		PO_LoginView.fillForm(driver, "admin@admin.com", "admin");
		List<WebElement> elementos = PO_View.checkElement(driver, "id", "listUsers");
		elementos.get(0).click();
		elementos = SeleniumUtils.EsperaCargaPagina(driver, "text", "Apellidos", PO_View.getTimeout());

		assertTrue(elementos.size() == 7);
		PO_View.checkElement(driver, "text", "Borrado1@Borrado1.com");
		PO_View.checkElement(driver, "text", "Borrado2@Borrado2.com");
		PO_View.checkElement(driver, "text", "zzzz@zzzz.com");
		driver.findElement(By.id("checkbox0")).click();
		driver.findElement(By.id("checkbox1")).click();
		driver.findElement(By.id("checkbox6")).click();
		driver.findElement(By.id("eliminar")).click();

		elementos = SeleniumUtils.EsperaCargaPagina(driver, "text", "Éxito en el borrado de usuarios",
				PO_View.getTimeout());
		elementos = SeleniumUtils.EsperaCargaPagina(driver, "text", "Apellidos", PO_View.getTimeout());
		assertTrue(elementos.size() == 4);
		SeleniumUtils.textoNoPresentePagina(driver, "Borrado1@Borrado1.com");
		SeleniumUtils.textoNoPresentePagina(driver, "Borrado2@Borrado2.com");
		SeleniumUtils.textoNoPresentePagina(driver, "zzzz@zzzz.com");

	}

	// Prueba15. Añadir una oferta valida.
	@Test
	public void Prueba15() {
		PO_NavView.clickOption(driver, "identificarse", "text", "Identificación de usuario");
		PO_LoginView.fillForm(driver, "prueba1@prueba1.com", "prueba1");
		List<WebElement> elementos = PO_View.checkElement(driver, "id", "mPublicaciones");
		elementos.get(0).click();
		elementos = SeleniumUtils.EsperaCargaPagina(driver, "text", "Eliminar", PO_View.getTimeout());

		elementos = PO_View.checkElement(driver, "id", "publicar");
		elementos.get(0).click();

		PO_OfertaView.fillForm(driver, "ProductoPrueba", "Prueba de detalle de un producto", "20");

		PO_View.checkElement(driver, "text", "ProductoPrueba");
	}

	// Prueba16. Añadir una oferta inválida.
	@Test
	public void Prueba16() {
		PO_NavView.clickOption(driver, "identificarse", "text", "Identificación de usuario");
		PO_LoginView.fillForm(driver, "prueba1@prueba1.com", "prueba1");
		List<WebElement> elementos = PO_View.checkElement(driver, "id", "mPublicaciones");
		elementos.get(0).click();
		elementos = SeleniumUtils.EsperaCargaPagina(driver, "text", "Eliminar", PO_View.getTimeout());

		elementos = PO_View.checkElement(driver, "id", "publicar");
		elementos.get(0).click();

		PO_OfertaView.fillForm(driver, "", "detalle", "20");
		PO_View.checkElement(driver, "text", "El titulo debe tener como minimo 2 caracteres");

		PO_OfertaView.fillForm(driver, "titulo", "detalle", "-20");
		PO_View.checkElement(driver, "text", "El precio debe ser mayor de 0");
	}

	// Prueba17. Comprobar la lista de ofertas propias
	@Test
	public void Prueba17() {
		PO_NavView.clickOption(driver, "identificarse", "text", "Identificación de usuario");
		PO_LoginView.fillForm(driver, "prueba1@prueba1.com", "prueba1");
		List<WebElement> elementos = PO_View.checkElement(driver, "id", "mPublicaciones");
		elementos.get(0).click();
		elementos = SeleniumUtils.EsperaCargaPagina(driver, "text", "Eliminar", PO_View.getTimeout());
		assertTrue(elementos.size() == 4);
		PO_View.checkElement(driver, "text", "Bicicleta");
		PO_View.checkElement(driver, "text", "Ambientador olor fresa");
		PO_View.checkElement(driver, "text", "Cargador de movil");
		PO_View.checkElement(driver, "text", "ProductoPrueba");
	}

	// Prueba18. Eliminar de la lista de ofertas propias la primera oferta
	@Test
	public void Prueba18() {
		PO_NavView.clickOption(driver, "identificarse", "text", "Identificación de usuario");
		PO_LoginView.fillForm(driver, "prueba1@prueba1.com", "prueba1");
		List<WebElement> elementos = PO_View.checkElement(driver, "id", "mPublicaciones");
		elementos.get(0).click();
		elementos = SeleniumUtils.EsperaCargaPagina(driver, "text", "Eliminar", PO_View.getTimeout());
		assertTrue(elementos.size() == 4);

		// Borramos la primera oferta
		driver.findElement(By.className("eliminar0")).click();
		elementos = SeleniumUtils.EsperaCargaPagina(driver, "text", "Se ha eliminado correctamente", PO_View.getTimeout());
		elementos = SeleniumUtils.EsperaCargaPagina(driver, "text", "Eliminar", PO_View.getTimeout());
		assertTrue(elementos.size() == 3);
		SeleniumUtils.textoNoPresentePagina(driver, "Ambientador olor fresa");

		// Lo añadimos de nuevo
		elementos = PO_View.checkElement(driver, "id", "publicar");
		elementos.get(0).click();
		PO_OfertaView.fillForm(driver, "Ambientador olor fresa", "Tiene un olor muy fuerte a fresa", "5");
		PO_View.checkElement(driver, "text", "Ambientador olor fresa");
	}

	// Prueba19. Eliminar de las ofertas propias la ultima oferta
	@Test
	public void Prueba19() {
		PO_NavView.clickOption(driver, "identificarse", "text", "Identificación de usuario");
		PO_LoginView.fillForm(driver, "prueba1@prueba1.com", "prueba1");
		List<WebElement> elementos = PO_View.checkElement(driver, "id", "mPublicaciones");
		elementos.get(0).click();
		elementos = SeleniumUtils.EsperaCargaPagina(driver, "text", "Eliminar", PO_View.getTimeout());
		assertTrue(elementos.size() == 4);

		// Borramos la ultima oferta (el productoPrueba añadido previamente)
		driver.findElement(By.className("eliminar3")).click();
		elementos = SeleniumUtils.EsperaCargaPagina(driver, "text", "Se ha eliminado correctamente", PO_View.getTimeout());
		elementos = SeleniumUtils.EsperaCargaPagina(driver, "text", "Eliminar", PO_View.getTimeout());
		assertTrue(elementos.size() == 3);
		SeleniumUtils.textoNoPresentePagina(driver, "ProductoPrueba");

	}

	// Prueba20. Busqueda de campo vacio
	@Test
	public void Prueba20() {
		PO_NavView.clickOption(driver, "identificarse", "text", "Identificación de usuario");
		PO_LoginView.fillForm(driver, "prueba1@prueba1.com", "prueba1");
		List<WebElement> elementos = PO_View.checkElement(driver, "id", "mTienda");
		elementos.get(0).click();

		PO_BuscarView.fillForm(driver, "");
		List<WebElement> paginas = PO_View.checkElement(driver, "free", "//a[contains(@class, 'page-link')]");
		int contador = 0;
		for (int i = 0; i < paginas.size(); i++) {
			paginas.get(i).click();
			paginas = PO_View.checkElement(driver, "free", "//a[contains(@class, 'page-link')]");
			contador += SeleniumUtils
					.EsperaCargaPagina(driver, "free", "//div[contains(@id, 'producto')]", PO_View.getTimeout()).size();
		}
		assertTrue(contador == 9);
	}

	// Prueba21. Busqueda sin resultados
	@Test
	public void Prueba21() {
		PO_NavView.clickOption(driver, "identificarse", "text", "Identificación de usuario");
		PO_LoginView.fillForm(driver, "prueba1@prueba1.com", "prueba1");
		List<WebElement> elementos = PO_View.checkElement(driver, "id", "mTienda");
		elementos.get(0).click();
		SeleniumUtils.EsperaCargaPagina(driver, "text", "Productos", PO_View.getTimeout());
		PO_BuscarView.fillForm(driver, "wwwwwwwwwwwwwwwwwww");
		List<WebElement> paginas = PO_View.checkElement(driver, "free", "//a[contains(@class, 'page-link')]");
		int contador = 0;
		for (int i = 0; i < paginas.size(); i++) {
			try {
				PO_View.checkElement(driver, "free", "//div[contains(@id, 'producto')]");
			} catch (Exception e) {
				break;
			}
			paginas.get(i).click();
			paginas = PO_View.checkElement(driver, "free", "//a[contains(@class, 'page-link')]");
			contador += PO_View.checkElement(driver, "free", "//div[contains(@id, 'producto')]").size();
		}
		assertTrue(contador == 0);
	}

	// Prueba22. Busqueda con minusculas y mayusculas
	@Test
	public void Prueba22() {
		PO_NavView.clickOption(driver, "identificarse", "text", "Identificación de usuario");
		PO_LoginView.fillForm(driver, "prueba1@prueba1.com", "prueba1");
		List<WebElement> elementos = PO_View.checkElement(driver, "id", "mTienda");
		elementos.get(0).click();
		PO_BuscarView.fillForm(driver, "CARGADOR");
		int i = SeleniumUtils
				.EsperaCargaPagina(driver, "free", "//div[contains(@id, 'producto')]", PO_View.getTimeout()).size();
		assertTrue(i == 1);
	}

	// Prueba23. Compra que deja saldo positivo
	@Test
	public void Prueba23() {
		
		PO_NavView.clickOption(driver, "identificarse", "text", "Identificación de usuario");
		PO_LoginView.fillForm(driver, "prueba1@prueba1.com", "prueba1");
		List<WebElement> elementos = PO_View.checkElement(driver, "id", "mPublicaciones");
		elementos.get(0).click();
		elementos = SeleniumUtils.EsperaCargaPagina(driver, "text", "Eliminar", PO_View.getTimeout());

		elementos = PO_View.checkElement(driver, "id", "publicar");
		elementos.get(0).click();

		PO_OfertaView.fillForm(driver, "ProductoPrueba", "Prueba de detalle de un producto", "50");
		PO_View.checkElement(driver, "text", "ProductoPrueba");
		PO_NavView.clickOption(driver, "logout", "text", "Identificación de usuario");
		
		PO_NavView.clickOption(driver, "identificarse", "text", "Identificación de usuario");
		PO_LoginView.fillForm(driver, "test1@test1.com", "test1");
		elementos = PO_View.checkElement(driver, "id", "mTienda");
		elementos.get(0).click();
		PO_BuscarView.fillForm(driver, "ProductoPrueba");
		elementos = PO_View.checkElement(driver, "free", "//a[contains(@href, '/user/tienda/comprar/')]");
		elementos.get(0).click();
		SeleniumUtils.EsperaCargaPagina(driver, "text", "Se ha realizado la compra correctamente", PO_View.getTimeout())
				.size();
		elementos = PO_View.checkElement(driver, "id", "dinero");
		assertTrue(elementos.get(0).getText().equals("Dinero: 50 eur."));
	}

	// Prueba24. Compra que deja saldo a 0
	@Test
	public void Prueba24() {
		PO_NavView.clickOption(driver, "identificarse", "text", "Identificación de usuario");
		PO_LoginView.fillForm(driver, "prueba1@prueba1.com", "prueba1");
		List<WebElement> elementos = PO_View.checkElement(driver, "id", "mPublicaciones");
		elementos.get(0).click();
		elementos = SeleniumUtils.EsperaCargaPagina(driver, "text", "Eliminar", PO_View.getTimeout());
		elementos = PO_View.checkElement(driver, "id", "publicar");
		elementos.get(0).click();
		PO_OfertaView.fillForm(driver, "ProductoPrueba1", "Prueba de detalle de un producto 1", "50");
		PO_View.checkElement(driver, "text", "ProductoPrueba");
		PO_NavView.clickOption(driver, "logout", "text", "Identificación de usuario");
		
		PO_NavView.clickOption(driver, "identificarse", "text", "Identificación de usuario");
		PO_LoginView.fillForm(driver, "test1@test1.com", "test1");
		elementos = PO_View.checkElement(driver, "id", "mTienda");
		elementos.get(0).click();
		PO_BuscarView.fillForm(driver, "ProductoPrueba1");
		elementos = PO_View.checkElement(driver, "free", "//a[contains(@href, '/user/tienda/comprar/')]");
		elementos.get(0).click();
		SeleniumUtils.EsperaCargaPagina(driver, "text", "Se ha realizado la compra correctamente", PO_View.getTimeout())
				.size();
		elementos = PO_View.checkElement(driver, "id", "dinero");
		assertTrue(elementos.get(0).getText().equals("Dinero: 0 eur."));
	}

	// Prueba25. Compra que deja saldo negativo
	@Test
	public void Prueba25() {
		PO_NavView.clickOption(driver, "identificarse", "text", "Identificación de usuario");
		PO_LoginView.fillForm(driver, "test1@test1.com", "test1");
		List<WebElement> elementos = PO_View.checkElement(driver, "id", "mTienda");
		elementos.get(0).click();
		PO_BuscarView.fillForm(driver, "Figura de Spiderman");
		elementos = PO_View.checkElement(driver, "free", "//a[contains(@href, '/user/tienda/comprar/')]");
		elementos.get(0).click();
		SeleniumUtils.EsperaCargaPagina(driver, "text", "No tienes suficiente dinero", PO_View.getTimeout()).size();
		elementos = PO_View.checkElement(driver, "id", "dinero");
		assertTrue(elementos.get(0).getText().equals("Dinero: 0 eur."));
	}

	// Prueba26. Comprobar lista de productos comprados
	@Test
	public void Prueba26() {
		PO_NavView.clickOption(driver, "identificarse", "text", "Identificación de usuario");
		PO_LoginView.fillForm(driver, "test1@test1.com", "test1");
		List<WebElement> elementos = PO_View.checkElement(driver, "id", "mCompras");
		elementos.get(0).click();
		PO_View.checkElement(driver, "text", "ProductoPrueba");
		PO_View.checkElement(driver, "text", "ProductoPrueba1");
	}

	// Eliminacón del test1 creado para las pruebas y las dos ofertas
	@Test
	public void PruebaFinal() {
		PO_NavView.clickOption(driver, "identificarse", "text", "Identificación de usuario");
		PO_LoginView.fillForm(driver, "admin@admin.com", "admin");
		List<WebElement> elementos = PO_View.checkElement(driver, "id", "listUsers");
		elementos.get(0).click();
		elementos = SeleniumUtils.EsperaCargaPagina(driver, "text", "Apellidos", PO_View.getTimeout());

		// Comprobamos que hay 4 usuarios y el ultimo es test1
		assertTrue(elementos.size() == 4);
		PO_View.checkElement(driver, "text", "test1@test1.com");
		driver.findElement(By.id("checkbox3")).click();
		driver.findElement(By.id("eliminar")).click();

		// Comprobamos que hay 3 usuarios y ya no está test1
		elementos = SeleniumUtils.EsperaCargaPagina(driver, "text", "Éxito en el borrado de usuarios", PO_View.getTimeout());
		PO_NavView.clickOption(driver, "logout", "text", "Identificación de usuario");
		
		// Eliminamos las dos ofertas de prueba1
		PO_NavView.clickOption(driver, "identificarse", "text", "Identificación de usuario");
		PO_LoginView.fillForm(driver, "prueba1@prueba1.com", "prueba1");
		elementos = PO_View.checkElement(driver, "id", "mPublicaciones");
		elementos.get(0).click();
		elementos = SeleniumUtils.EsperaCargaPagina(driver, "text", "Eliminar", PO_View.getTimeout());
		assertTrue(elementos.size() == 5);

		driver.findElement(By.className("eliminar3")).click();
		elementos = SeleniumUtils.EsperaCargaPagina(driver, "text", "Se ha eliminado correctamente", PO_View.getTimeout());
		elementos = SeleniumUtils.EsperaCargaPagina(driver, "text", "Eliminar", PO_View.getTimeout());
		assertTrue(elementos.size() == 4);
		driver.findElement(By.className("eliminar3")).click();
		elementos = SeleniumUtils.EsperaCargaPagina(driver, "text", "Se ha eliminado correctamente", PO_View.getTimeout());
		elementos = SeleniumUtils.EsperaCargaPagina(driver, "text", "Eliminar", PO_View.getTimeout());
		assertTrue(elementos.size() == 3);
	}

}
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

import com.uniovi.tests.pageobjects.PO_LoginView;
import com.uniovi.tests.pageobjects.PO_MessageView;
import com.uniovi.tests.pageobjects.PO_NavView;
import com.uniovi.tests.pageobjects.PO_OfertaView;
import com.uniovi.tests.pageobjects.PO_RegisterView;
import com.uniovi.tests.pageobjects.PO_View;
import com.uniovi.tests.util.SeleniumUtils;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class SdiEntrega2TestsParte2 {
	static String PathFirefox65 = "C:\\Program Files\\Mozilla Firefox\\firefox.exe";
	static String Geckdriver024 = "C:\\Users\\Usuario\\Desktop\\Curso 2021\\SDI\\Lab\\Sesion5\\PL-SDI-Sesi�n5-material\\geckodriver024win64.exe";
	static WebDriver driver = getDriver(PathFirefox65, Geckdriver024);
	static String URL = "https://localhost:8081/cliente.html";

	public static WebDriver getDriver(String PathFirefox, String Geckdriver) {
		System.setProperty("webdriver.firefox.bin", PathFirefox);
		System.setProperty("webdriver.gecko.driver", Geckdriver);
		WebDriver driver = new FirefoxDriver();
		return driver;
	} /* Resto del c�digo de la clase */

	// Antes de cada prueba se navega al URL home de la aplicaci�n
	@Before
	public void setUp() {
		// Crear un usuario de prueba para los mensajes
		driver.navigate().to(URL);
	}

	// Despu�s de cada prueba se borran las cookies del navegador
	@After
	public void tearDown() {
		driver.manage().deleteAllCookies();
	}

	// Antes de la primera prueba
	@BeforeClass
	static public void begin() {
		driver.navigate().to("https://localhost:8081/");
		PO_NavView.clickOption(driver, "registrarse", "text", "Registro de usuario");
		PO_RegisterView.fillForm(driver, "test1@test1.com", "test1", "test1Apellido", "test1",
				"test1");
		PO_View.checkElement(driver, "text", "Te has registrado con exito");
		PO_NavView.clickOption(driver, "registrarse", "text", "Registro de usuario");
		PO_RegisterView.fillForm(driver, "test2@test2.com", "test2", "test2Apellido", "test2",
				"test2");
		PO_View.checkElement(driver, "text", "Te has registrado con exito");
		PO_NavView.clickOption(driver, "identificarse", "text", "Identificaci�n de usuario");
		PO_LoginView.fillForm(driver, "test1@test1.com", "test1");
		List<WebElement> elementos = PO_View.checkElement(driver, "id", "mPublicaciones");
		elementos.get(0).click();

		elementos = PO_View.checkElement(driver, "id", "publicar");
		elementos.get(0).click();

		PO_OfertaView.fillForm(driver, "ProductoPrueba", "Prueba de detalle de un producto", "20");

		PO_View.checkElement(driver, "text", "ProductoPrueba");
	}

	// Al finalizar la �ltima prueba
	@AfterClass
	static public void end() {
		driver.navigate().to("https://localhost:8081/");
		PO_NavView.clickOption(driver, "identificarse", "text", "Identificaci�n de usuario");
		PO_LoginView.fillForm(driver, "admin@admin.com", "admin");
		List<WebElement> elementos = PO_View.checkElement(driver, "id", "listUsers");
		elementos.get(0).click();
		elementos = SeleniumUtils.EsperaCargaPagina(driver, "text", "Apellidos", PO_View.getTimeout());

		PO_View.checkElement(driver, "text", "test1@test1.com");
		driver.findElement(By.id("checkbox3")).click();
		driver.findElement(By.id("checkbox4")).click();
		driver.findElement(By.id("eliminar")).click();
		// Cerramos el navegador al finalizar las pruebas
		driver.quit();
	}

	// Prueba30. Inicio de sesi�n con datos v�lidos.
	@Test
	public void Prueba30() {
		SeleniumUtils.EsperaCargaPagina(driver, "text", "Email", PO_View.getTimeout());
		PO_LoginView.fillForm(driver, "prueba1@prueba1.com", "prueba1");
		SeleniumUtils.EsperaCargaPagina(driver, "text", "Ofertas", PO_View.getTimeout());
	}

	// Prueba31. Inicio de sesi�n con datos inv�lidos (contrase�a incorrecta).
	@Test
	public void Prueba31() {
		SeleniumUtils.EsperaCargaPagina(driver, "text", "Email", PO_View.getTimeout());
		PO_LoginView.fillForm(driver, "prueba1@prueba1.com", "prueba2");
		SeleniumUtils.EsperaCargaPagina(driver, "text", "Usuario o password incorrectos", PO_View.getTimeout());
	}

	// Prueba32. Inicio de sesi�n con datos inv�lidos (campo contrase�a vacio).
	@Test
	public void Prueba32() {
		SeleniumUtils.EsperaCargaPagina(driver, "text", "Email", PO_View.getTimeout());
		PO_LoginView.fillForm(driver, "prueba1@prueba1.com", "");
		SeleniumUtils.EsperaCargaPagina(driver, "text", "Se deben rellenar los dos campos", PO_View.getTimeout());
	}

	// Prueba33. Comprobar la lista de ofertas
	@Test
	public void Prueba33() {
		SeleniumUtils.EsperaCargaPagina(driver, "text", "Email", PO_View.getTimeout());
		PO_LoginView.fillForm(driver, "prueba1@prueba1.com", "prueba1");
		List<WebElement> elementos = PO_View.checkElement(driver, "id", "ofertas");
		elementos.get(0).click();
		SeleniumUtils.EsperaCargaPagina(driver, "text", "Enviar mensaje", PO_View.getTimeout());
		elementos = driver.findElements(By.xpath("//table[@class='table table-hover']/tbody/tr"));
		assertTrue(elementos.size() == 7);
		PO_View.checkElement(driver, "text", "Figura de Spiderman");
		PO_View.checkElement(driver, "text", "Movil iPhone X");
		PO_View.checkElement(driver, "text", "Puzle de juego de tronos");
		PO_View.checkElement(driver, "text", "Bicicleta");
		PO_View.checkElement(driver, "text", "Cuberter�a de metal");
		PO_View.checkElement(driver, "text", "Maquina de afeitar");
		PO_View.checkElement(driver, "text", "ProductoPrueba");
	}

	// Prueba34. Enviar un mensaje a una oferta concreta
	@Test
	public void Prueba34() {
		SeleniumUtils.EsperaCargaPagina(driver, "text", "Email", PO_View.getTimeout());
		PO_LoginView.fillForm(driver, "test2@test2.com", "test2");
		List<WebElement> elementos = PO_View.checkElement(driver, "id", "ofertas");
		elementos.get(0).click();
		elementos = SeleniumUtils.EsperaCargaPagina(driver, "free", "//table[@class='table table-hover']/tbody/tr/td/button[@id='9']", PO_View.getTimeout());
		elementos.get(0).click();
		
		SeleniumUtils.EsperaCargaPagina(driver, "text", "Enviar", PO_View.getTimeout());
		PO_MessageView.fillForm(driver, "Nuevo mensaje de prueba");

		elementos = SeleniumUtils.EsperaCargaPagina(driver, "free", "//table[@class='table table-hover']/tbody/tr",
				PO_View.getTimeout());
		assertTrue(elementos.size() == 1);
		PO_View.checkElement(driver, "text", "Nuevo mensaje de prueba");
	}

	// Prueba35. Enviar un mensaje a una oferta abierta
	@Test
	public void Prueba35() {
		SeleniumUtils.EsperaCargaPagina(driver, "text", "Email", PO_View.getTimeout());
		PO_LoginView.fillForm(driver, "test1@test1.com", "test1");
		SeleniumUtils.EsperaCargaPagina(driver, "text", "Enviar mensaje", PO_View.getTimeout());
		List<WebElement> elementos = PO_View.checkElement(driver, "id", "productos");
		elementos.get(0).click();
		elementos = SeleniumUtils.EsperaCargaPagina(driver, "text", "Ver mensaje", PO_View.getTimeout());
		elementos.get(0).click();
		SeleniumUtils.EsperaCargaPagina(driver, "text", "Enviar", PO_View.getTimeout());
		PO_MessageView.fillForm(driver, "Nuevo mensaje de prueba como respuesta");

		SeleniumUtils.EsperaCargaPagina(driver, "text", "test1@test1.com",
				PO_View.getTimeout());
		elementos = SeleniumUtils.EsperaCargaPagina(driver, "free", "//table[@class='table table-hover']/tbody/tr",
				PO_View.getTimeout());
		assertTrue(elementos.size() == 2);
		PO_View.checkElement(driver, "text", "Nuevo mensaje de prueba como respuesta");
	}

}
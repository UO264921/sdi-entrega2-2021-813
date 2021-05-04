package com.uniovi.tests.pageobjects;


import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class PO_BuscarView extends PO_NavView {
	static public void fillForm(WebDriver driver, String texto) {
		WebElement buscar = driver.findElement(By.name("busqueda"));
		buscar.click();
		buscar.clear();
		buscar.sendKeys(texto);
		
		//Pulsar el boton de Alta.
		By boton = By.className("btn");
		driver.findElement(boton).click();
	}
}

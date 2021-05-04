package com.uniovi.tests.pageobjects;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class PO_MessageView extends PO_NavView {

	static public void fillForm(WebDriver driver, String mes) {
		WebElement mess = driver.findElement(By.name("mensaje"));
		mess.click();
		mess.clear();
		mess.sendKeys(mes);
		//Pulsar el boton de enviar.
		By boton = By.id("boton-mensaje");
		driver.findElement(boton).click();	
	}

}

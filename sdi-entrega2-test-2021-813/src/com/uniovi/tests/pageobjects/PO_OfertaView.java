package com.uniovi.tests.pageobjects;


import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class PO_OfertaView extends PO_NavView {
	static public void fillForm(WebDriver driver, String titu, String desc, String p) {
		WebElement titulo = driver.findElement(By.name("titulo"));
		titulo.click();
		titulo.clear();
		titulo.sendKeys(titu);
		WebElement password = driver.findElement(By.name("detalle"));
		password.click();
		password.clear();
		password.sendKeys(desc);
		WebElement precio = driver.findElement(By.name("precio"));
		precio.click();
		precio.clear();
		precio.sendKeys(p);

		//Pulsar el boton de Alta.
		By boton = By.className("btn");
		driver.findElement(boton).click();
	}
}

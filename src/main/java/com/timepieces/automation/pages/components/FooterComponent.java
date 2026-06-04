package com.timepieces.automation.pages.components;

import org.openqa.selenium.WebDriver;

import lombok.extern.log4j.Log4j2;

@Log4j2 // Lombok logger annotation, Takes care of the logger instances
public class FooterComponent {

    private WebDriver driver;

    //Contructor
    public FooterComponent(WebDriver driver) {
        this.driver = driver;
    }
}

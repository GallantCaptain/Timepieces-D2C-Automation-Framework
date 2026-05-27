package com.timepieces.automation.pages.web.account;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import com.timepieces.automation.pages.BasePage;
import com.timepieces.automation.utils.BrowserUtils;
import com.timepieces.automation.utils.ConfigReader;
import com.timepieces.automation.utils.WindowUtils;

import lombok.extern.log4j.Log4j2;

@Log4j2 // Lombok logger annotation, Takes care of the logger instances 
public class AccountPage extends LoginPage {
    // /********* Constructor *********/
    public AccountPage(WebDriver driver) {
        super(driver);
    }

    // /********* Locators *********/
 
    //Filling this after P0 Business Library is implemented in LoginPage, as most of the locators will be inherited from there.
    //Will add more locators here as we implement more business library methods specific to AccountPage
}

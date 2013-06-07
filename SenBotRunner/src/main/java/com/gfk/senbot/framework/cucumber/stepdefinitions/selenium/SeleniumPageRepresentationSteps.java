package com.gfk.senbot.framework.cucumber.stepdefinitions.selenium;

import static org.junit.Assert.*;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;

import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.BeanUtils;

import com.gfk.senbot.framework.context.SenBotContext;
import com.gfk.senbot.framework.cucumber.stepdefinitions.BaseStepDefinition;

import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import cucumber.runtime.java.StepDefAnnotation;

@StepDefAnnotation
public class SeleniumPageRepresentationSteps extends BaseStepDefinition{
	
	@When("^I click \"([^\"]*)\" on the \"([^\"]*)\" view$")
	public void I_click_on_the(String elementName, String viewName) throws Throwable {
		WebElement found = the_view_should_contain(viewName, elementName);
		found.click();
	}
	
	/**
	 * A generic way to assert of a view/page contains a certain element. The element lookup is done though a naming convention. 
	 * variable_Name is matched up on argument "Variable name". So case insensitive and spaces are replaced by underscores
	 * 
	 * @param viewName
	 * @param elementName
	 * @throws Throwable
	 */
	@Then("^the \"([^\"]*)\" view should show the element \"([^\"]*)\"$")
	public WebElement the_view_should_show(String viewName, String elementName) throws Throwable {
		WebElement found = seleniumElementService.getElementFromReferencedView(viewName, elementName);
		seleniumElementService.waitForLoaders();
		found = new WebDriverWait(getWebDriver(), getSeleniumManager().getTimeout()).until(ExpectedConditions.visibilityOf(found));
		
		return found;
		
	}

	/**
	 * A generic way to assert of a view/page contains a certain element. The element lookup is done though a naming convention. 
	 * variable_Name is matched up on argument "Variable name". So case insensitive and spaces are replaced by underscores
	 * 
	 * @param viewName
	 * @param elementName
	 * @throws Throwable
	 */
	@Then("^the \"([^\"]*)\" view should contain the element \"([^\"]*)\"$")
	public WebElement the_view_should_contain(String viewName, String elementName) throws Throwable {
		WebElement found = seleniumElementService.getElementFromReferencedView(viewName, elementName);
		boolean notFound = true;
		
		try{
			notFound = found == null;
			seleniumElementService.waitForLoaders();
			found = new WebDriverWait(getWebDriver(), getSeleniumManager().getTimeout()).until(ExpectedConditions.visibilityOf(found));
		}
		catch (NoSuchElementException e) {
			//leave fail = true
		}
		if(notFound) {			
			fail("The element \"" + elementName + "\" on view/page \"" + viewName + "\" is not found.");
		}
		return found;
		
	}
	
	@Then("^the \"([^\"]*)\" view should not contain the element \"([^\"]*)\"$")
	public void the_view_should_not_contain_element(String viewName, String elementName) throws Throwable {
		WebElement found = seleniumElementService.getElementFromReferencedView(viewName, elementName);
		boolean fail = true;
		
		try{
			fail = found != null && found.isDisplayed();			
		}
		catch (NoSuchElementException e) {
			fail = false;
		}
		if(fail) {
			fail("The element \"" + elementName + "\" on view/page \"" + viewName + "\" is found where it is not expected.");
		}
	}	
	
	@Then("^the \"([^\"]*)\" view should not show the element \"([^\"]*)\"$")
	public void the_view_should_not_show_the_element(String viewName, String elementName) throws Throwable {
		WebElement found = seleniumElementService.getElementFromReferencedView(viewName, elementName);
		try{
			Boolean isHidden = new WebDriverWait(SenBotContext.getSeleniumDriver(), 4).until(ExpectedConditions.not(ExpectedConditions.visibilityOf(found)));
			assertFalse("The element should not be displayed", isHidden);
		}
		catch (NoSuchElementException nsee){
			//not found is also hidden
		}
	}

}

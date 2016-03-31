package org.openmrs.module.radiology.test

import geb.spock.GebReportingSpec
import org.openqa.selenium.By
import org.openqa.selenium.WebElement
import org.openqa.selenium.interactions.Actions

class DefaultSpec extends GebReportingSpec {
    Configuration config = new Configuration()

    /**
     * Pause in milliseconds.
     * @param milliseconds the duration of the pause in milliseconds
     */
    def pause(long milliseconds)
    {
        Thread.sleep(milliseconds)
    }

    /**
     * Get the text of an element with by the xpath
     * @param xpath the xpath of the element
     * @return the content of the element as a string
     */
    def textOfXpathElem(String xpath)
    {
        return driver.findElement(By.xpath(xpath)).getText();
    }

    /**
     * Mouse-Hover event on a certain WebElement
     * @param element The element that will be hovered on
     */
    def hoverOnWebElement(WebElement element)
    {
        Actions builder = new Actions(driver);
        Actions hoverOverRegistrar = builder.moveToElement(element);
        hoverOverRegistrar.perform();
    }
}

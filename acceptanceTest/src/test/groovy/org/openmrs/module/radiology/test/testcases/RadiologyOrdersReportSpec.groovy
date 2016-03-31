package org.openmrs.module.radiology.test.testcases

import geb.module.Textarea
import org.openmrs.module.radiology.test.DefaultSpec
import org.openmrs.module.radiology.test.pages.LoginPage
import org.openmrs.module.radiology.test.pages.RadiologyOrdersPage
import org.openqa.selenium.By
import org.openqa.selenium.WebElement
import spock.lang.Stepwise

@Stepwise
class RadiologyOrdersReportSpec extends DefaultSpec {

    def "Login"()
    {
        given: "Go to Login-Page"
        to LoginPage

        when: "Login"
        performLogin(config.adminName, config.pwString)

        then: "View main page"
        title.startsWith("OpenMRS - Home")
    }

    def "View Orders"()
    {
        when: "Go to Radiology-Orders-View"
        $("a", text: "Administration").click()
        def menu_mro = $("a", text: "Manage Radiology Orders")
        waitFor {menu_mro.isDisplayed()}
        menu_mro.click()

        $("input", value: "Find").click()

        then: "Check if there are any orders"
        waitFor {$("tbody#radiologyOrdersTableBody > tr")}

        and:
        $("tbody#radiologyOrdersTableBody > tr").size()!=0
    }

    def "Check if the no-'completed' order is visible"()
    {
        given: "Go to Radiology-Orders-View"
        to RadiologyOrdersPage

        when: "Check for a no-'completed' order"
        setStartDate("13","Mar", "2016")
        orderID = "11"

        then: "The order with the id 11 should be visible"
        waitFor {$("a", text: orderID).isDisplayed()}
    }

    def "The performed status of the no-'completed'-order should not be 'COMPLETED' "()
    {
        when: "Click on the order"
        $("a", text: orderID).click()

        then: "The performed status shouldn't be 'COMPLETED'"
        textOfXpathElem("//*[@id=\"radiologyOrder\"]/table/tbody/tr[8]/td[2]").contains("COMPLETED") == false
    }

    def "Check if the 'completed' order is visible"()
    {
        given: "Go to Radiology-Orders-View"
        to RadiologyOrdersPage

        when: "Check for the 'completed' order"
        setStartDate("13","Mar", "2016")
        orderID = "10"

        then: "The order with the id 10 should be visible"
        waitFor {$("a", text: orderID).isDisplayed()}
    }

    def "The performed status of the 'completed'-order should be 'COMPLETED' "()
    {
        when: "Click on the order"
        $("a", text: orderID).click()

        then: "The performed status should be 'COMPLETED'"
        textOfXpathElem("//*[@id=\"radiologyOrder\"]/table/tbody/tr[8]/td[2]").contains("COMPLETED")
    }

    def "Check if the 'completed'-order can be claimed"()
    {

        when: "Click on 'Claim Report'"
        $("a", text: "Claim Report").click()

        then: "The values of the order should be the same"
        textOfXpathElem("//*[@id=\"radiologyOrder\"]/table/tbody/tr[1]/td[2]").contains("10")

        and:
        textOfXpathElem("//*[@id=\"radiologyOrder\"]/table/tbody/tr[8]/td[2]").contains("COMPLETED")
    }

    def "Unclaim the 'completed'-order"()
    {
        when: "Unlcaim the report"
        $("input", name: "unclaimRadiologyReport").click()

        then: "The order can be claimed again"
        waitFor {$("a", text: "Claim Report").isDisplayed()}
    }

    def "Claim the 'completed'-order and enter information" ()
    {
        when: "Claim a report, enter diagnosis and select the provider"
        $("a", text: "Claim Report").click()
        waitFor {$("textarea", name: "reportBody").isDisplayed()}

        def diagnosisTextarea = $(name: "reportBody").module(Textarea)
        diagnosisTextarea.text = "-"
        driver.findElement(By.id("principalResultsInterpreter_id_selection")).clear();
        driver.findElement(By.id("principalResultsInterpreter_id_selection")).sendKeys("nurse");
        pause(1000)
        WebElement whereToClick = driver.findElement(By.cssSelector("span.autocompleteresult"));
        hoverOnWebElement(whereToClick);
        whereToClick.click();

        then: "ProviderID should be the ID of 'Nurse Bob'"
        $("#principalResultsInterpreter_id").value().toString().contains("2")
    }

    def "Save the new report as a draft and check if provider cannot be changed" ()
    {
        when: "Saving the report as a draft"
        $("input", name: "saveRadiologyReport").click()

        then: "Provider should be a text"
        textOfXpathElem("//*[@id=\"radiologyReport\"]/div/table/tbody/tr[5]/td[2]").contains("Nurse Bob")
    }

    def "Resume to work on the report"()
    {
        given: "Go to Radiology-Orders-View"
        to RadiologyOrdersPage

        when: "Check for the 'completed' order"
        setStartDate("13","Mar", "2016")
        orderID = "10"
        waitFor {$("a", text: orderID).isDisplayed()}
        $("a", text: orderID).click()
        def resumeWorkOnReport = $("a", text: "Resume work on Report")
        waitFor {resumeWorkOnReport.isDisplayed()}
        resumeWorkOnReport.click()

        then: "Provider should still be a text"
        textOfXpathElem("//*[@id=\"radiologyReport\"]/div/table/tbody/tr[5]/td[2]").contains("Nurse Bob")
    }

    def "Complete Radiology Report"()
    {
        when: "Complete the report"
        $("input", name: "completeRadiologyReport").click()

        WebElement diagnosisField = driver.findElement(By.id("reportBody"));

        then: "The diagnosis shouldn't be changeable"
        diagnosisField.getAttribute("disabled") != null
    }

    def "Look at the completed radiology report"()
    {
        given: "Go to Radology-Orders-View"
        to RadiologyOrdersPage

        when: "Check for the 'completed' order"
        setStartDate("13","Mar", "2016")
        orderID = "10"
        waitFor {$("a", text: orderID).isDisplayed()}
        $("a", text: orderID).click()
        def showCompletedReport = $("a", text: "Show completed Report")
        waitFor {showCompletedReport.isDisplayed()}
        showCompletedReport.click()

        WebElement diagnosisField = driver.findElement(By.id("reportBody"));

        then: "The diagnosis shouldn't be changeable"
        diagnosisField.getAttribute("disabled") != null
    }

    def "Logout"()
    {
        when:
        $("a", text: "Log out").click()

        def usernameLoginField = $("input", name: "uname")

        then: "The username login field is displayed"
        waitFor {usernameLoginField.isDisplayed()}
    }

}


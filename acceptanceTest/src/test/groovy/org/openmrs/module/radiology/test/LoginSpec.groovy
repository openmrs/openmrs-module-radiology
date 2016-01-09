package org.openmrs.module.radiology.test
import org.openmrs.module.radiology.test.pages.LoginPage


import geb.spock.GebReportingSpec
import spock.lang.Stepwise

@Stepwise
class LoginSpec extends GebReportingSpec {

    Configuration config = new Configuration()

    def "Login"() {

        given: "Going to Login"
        to LoginPage

        when: "Login"
        usernameBox.value(config.adminName)
        passwordBox.value(config.pwString)
        loginButton.click()

        then: "View main page"
        title.startsWith("OpenMRS - Home")
    }

    def "Logout"() {
        when:
        $("a", text: "Log out").click()

        then: "the username-textbox is displayed"
        waitFor {$("input", name: "uname").isDisplayed()}
    }

}


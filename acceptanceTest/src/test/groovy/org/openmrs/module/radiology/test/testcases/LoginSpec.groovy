package org.openmrs.module.radiology.test.testcases

import org.openmrs.module.radiology.test.DefaultSpec
import org.openmrs.module.radiology.test.pages.LoginPage
import spock.lang.Stepwise

@Stepwise
class LoginSpec extends DefaultSpec {

    def "Login"() {

        given: "Go to Login-Page"
        to LoginPage

        when: "Login"
        performLogin(config.adminName, config.pwString)

        then: "View main page"
        title.startsWith("OpenMRS - Home")
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


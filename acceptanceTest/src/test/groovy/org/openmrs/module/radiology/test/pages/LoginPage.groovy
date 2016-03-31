package org.openmrs.module.radiology.test.pages
import geb.Page

class LoginPage extends Page{

    static url = "/openmrs/index.htm"

    static at = {loginButton.isDisplayed()}
    static content = {
        loginButton {$("input", type: "submit")}
        usernameBox {$("input", name: "uname")}
        passwordBox {$("input", name: "pw")}
    }

    /**
     * Performs a login on the openmrs web instance.
     * @param username The username of the user.
     * @param password The password of the user.
     */
    def performLogin(String username, String password) {
        usernameBox.value(username)
        passwordBox.value(password)
        loginButton.click()
    }
}

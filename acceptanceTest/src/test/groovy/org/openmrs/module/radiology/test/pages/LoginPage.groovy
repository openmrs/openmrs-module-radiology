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
}

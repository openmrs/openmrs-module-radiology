package org.openmrs.module.radiology.test.pages

import geb.Page
import geb.module.Select

class RadiologyOrdersPage extends Page{

    static url = "/openmrs/module/radiology/radiologyOrder.list"
    static at = {findButton.isDisplayed()}

    def orderID = ""

    static content = {
        findButton {$("input", value: "Find")}
        baseTable {$("tbody#radiologyOrdersTableBody")}
        tableRows {$("tbody#radiologyOrdersTableBody > tr")}
    }

    /**
     * Returns the number of listed radiology orders in the view
     * @return the number of listed radiology orders in the view
     */
    def numberOfResults() {
        waitFor {baseTable.isDisplayed()}
        return tableRows.size()
    }

    /**
     * Sets the start date for filtering
     * @param day the day
     * @param month the month as ["Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"]
     * @param year the year
     */
    def setStartDate(String day, String month, String year)
    {
        $("input", name: "startDate").click()
        def selectMonth = $(".select.ui-datepicker-month").module(Select)
        selectMonth.selected = month
        def selectYear = $(".select.ui-datepicker-month").module(Select)
        selectYear.selected = year
        $("a", text: day).click()
        findButton.click()

        waitFor {tableRows.isDisplayed()}
        and:
        tableRows.size()!=0
    }


}

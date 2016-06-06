<%@ include file="/WEB-INF/template/include.jsp"%>
<c:set var="DO_NOT_INCLUDE_JQUERY" value="true" />
<%@ include file="/WEB-INF/template/header.jsp"%>
<%@ include file="/WEB-INF/view/module/radiology/template/includeScripts.jsp"%>

<openmrs:require privilege="View Orders" otherwise="/login.htm" redirect="/module/radiology/radiologyDashboard.form" />

<h2>
  <spring:message code="radiology.dashboard.title" />
</h2>

<script type="text/javascript">
  var timeOut = null;

  <openmrs:authentication>
  var userId = "${authenticatedUser.userId}";
  </openmrs:authentication>

  var $j = jQuery.noConflict();
  $j(document).ready(
          function() {
            //initTabs
            var tabIndex = localStorage.getItem("selectedRadiologyTab");
            if (tabIndex === null) {
              tabIndex = 0;
            }
            $j("#radiologyTabs").tabs(
                    {
                      selected: tabIndex,
                      show: function(event, ui) {
                        localStorage.setItem("selectedRadiologyTab", $j(this)
                                .tabs("option", "selected"));
                      }
                    });
            $j('.ui-corner-all,.ui-corner-top').removeClass(
                    'ui-corner-all ui-corner-top');
            $j('#radiologyTabsList').css('display', 'block');
          });
</script>

<div id="radiologyTabs">
  <ul id="radiologyTabsList" style="display: none;">
    <openmrs:hasPrivilege privilege="View Orders">
      <li><a id="radiologyOrdersTab" href="#radiologyOrders"><openmrs:message code="radiology.dashboard.tabs.orders" /></a></li>
    </openmrs:hasPrivilege>
    <openmrs:hasPrivilege privilege="Get Radiology Reports">
      <li><a id="radiologyReportsTab" href="#radiologyReports"><openmrs:message
            code="radiology.dashboard.tabs.reports" /></a></li>
    </openmrs:hasPrivilege>
    <openmrs:hasPrivilege privilege="View Radiology Report Templates">
      <li><a id="radiologyReportTemplatesTab" href="#" onclick="return changeTab(this);" hidefocus="hidefocus"><openmrs:message
            code="radiology.dashboard.tabs.reportTemplates" /></a></li>
    </openmrs:hasPrivilege>
  </ul>
  <openmrs:hasPrivilege privilege="View Orders">
    <div id="radiologyOrders">
      <openmrs:portlet url="radiologyOrdersTab" id="ordersTab" moduleId="radiology" />
    </div>
  </openmrs:hasPrivilege>
  <openmrs:hasPrivilege privilege="Get Radiology Reports">
    <div id="radiologyReports">
      <openmrs:portlet url="radiologyReportsTab" id="reportsTab" moduleId="radiology" />
    </div>
  </openmrs:hasPrivilege>
  <openmrs:hasPrivilege privilege="View Radiology Report Templates">
    <div id="radiologyReportTemplates" style="display: none;">

      <openmrs:portlet url="radiologyReportTemplatesTab" id="reportTemplatesTab" moduleId="radiology" />
   </div>
  </openmrs:hasPrivilege>
  <openmrs:hasPrivilege privilege="View Radiology Report Templates">
    <div id="radiologyReportTemplates" style="display: none;">
      <openmrs:portlet url="radiologyReportTemplatesTab" id="reportTemplatesTab" moduleId="radiology" />
    </div>
  </openmrs:hasPrivilege>
</div>

<%@ include file="/WEB-INF/template/footer.jsp"%>
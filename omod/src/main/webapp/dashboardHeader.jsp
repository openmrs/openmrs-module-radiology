<%@ include file="/WEB-INF/view/module/radiology/template/includeTags.jsp"%>
<c:set var="DO_NOT_INCLUDE_JQUERY" value="true" />
<%@ include file="/WEB-INF/template/header.jsp"%>
<%@ include file="/WEB-INF/view/module/radiology/template/includeScripts.jsp"%>
<%@ include file="/WEB-INF/view/module/radiology/template/includeDatatablesWithDefaults.jsp"%>

<openmrs:require privilege="View Orders" otherwise="/login.htm" redirect="/module/radiology/radiologyDashboardOrdersTab.htm" />

<script type="text/javascript">
  <openmrs:authentication>
  var userId = "${authenticatedUser.userId}";
  </openmrs:authentication>
  //configure current locale as momentjs default, fall back to "en" if locale not found
  moment.locale([jsLocale, 'en']);
</script>

<h2>
  <spring:message code="radiology.dashboard.title" />
</h2>

<div id="radiologyTabs" class="ui-tabs ui-widget ui-widget-content">
  <ul id="radiologyTabsList" class="ui-tabs-nav ui-helper-reset ui-helper-clearfix ui-widget-header">
    <openmrs:hasPrivilege privilege="View Orders">
      <li class="ui-state-default"><a id="radiologyOrdersTab" href="${pageContext.request.contextPath}/module/radiology/radiologyDashboardOrdersTab.htm?switchTab=true"><openmrs:message
            code="radiology.dashboard.tabs.orders" /></a></li>
    </openmrs:hasPrivilege>
    <openmrs:hasPrivilege privilege="Get Radiology Reports">
      <li class="ui-state-default"><a id="radiologyReportsTab" href="${pageContext.request.contextPath}/module/radiology/radiologyDashboardReportsTab.htm"><openmrs:message
            code="radiology.dashboard.tabs.reports" /></a></li>
    </openmrs:hasPrivilege>
    <openmrs:hasPrivilege privilege="View Radiology Report Templates">
      <li class="ui-state-default"><a id="radiologyReportTemplatesTab" href="${pageContext.request.contextPath}/module/radiology/radiologyDashboardReportTemplatesTab.htm"><openmrs:message
            code="radiology.dashboard.tabs.reportTemplates" /></a></li>
    </openmrs:hasPrivilege>
    <openmrs:hasPrivilege privilege="Get Radiology Modalities">
      <li class="ui-state-default"><a id="radiologyModalitiesTab" href="${pageContext.request.contextPath}/module/radiology/radiologyDashboardModalitiesTab.htm"><openmrs:message
            code="radiology.dashboard.tabs.radiologyModalities" /></a></li>
    </openmrs:hasPrivilege>
  </ul>

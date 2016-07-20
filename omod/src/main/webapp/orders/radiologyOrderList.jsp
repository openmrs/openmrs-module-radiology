<%@ include file="/WEB-INF/template/include.jsp"%>
<c:set var="DO_NOT_INCLUDE_JQUERY" value="true" />
<%@ include file="/WEB-INF/template/header.jsp"%>
<%@ include file="/WEB-INF/view/module/radiology/template/includeScripts.jsp"%>
<%@ include file="/WEB-INF/view/module/radiology/localHeader.jsp"%>

<openmrs:require privilege="View Orders" otherwise="/login.htm" redirect="/module/radiology/radiologyOrder.list" />

<h2>
  <spring:message code="radiology.manageOrders" />
</h2>
<openmrs:portlet url="radiologyOrdersTab" id="ordersTab" moduleId="radiology" />
<br />
<%@ include file="/WEB-INF/template/footer.jsp"%>
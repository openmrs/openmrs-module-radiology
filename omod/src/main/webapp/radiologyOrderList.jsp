<%@ include file="/WEB-INF/template/include.jsp"%>

<openmrs:require privilege="View Orders" otherwise="/login.htm" redirect="/module/radiology/radiologyOrder.list" />

<%@ include file="/WEB-INF/template/header.jsp"%>
<%@ include file="./localHeader.jsp"%>

<h2>
  <spring:message code="radiology.manageOrders" />
</h2>
<openmrs:portlet url="radiologyOrdersTab" id="ordersTab" moduleId="radiology" />
<br />
<%@ include file="/WEB-INF/template/footer.jsp"%>

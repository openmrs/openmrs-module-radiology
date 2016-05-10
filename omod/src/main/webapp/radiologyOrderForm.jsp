
<%@ include file="/WEB-INF/template/include.jsp"%>

<openmrs:require
	allPrivileges="Add Encounters,Add Orders,Add Radiology Orders,Add Radiology Reports,Add Visits,Delete Radiology Orders,Delete Radiology Reports,Edit Encounters,Edit Orders,Edit Radiology Reports,Edit Visits,Get Care Settings,Get Concepts,Get Encounter Roles,Get Encounters,Get Orders,Get Patients,Get Providers,Get Radiology Orders,Get Radiology Reports,Get Users,Get Visit Attribute Types,Get Visit Types,Get Visits,View Orders"
	otherwise="/login.htm" redirect="/module/radiology/radiologyOrder.form" />

<%@ include file="/WEB-INF/template/header.jsp"%>
<%@ include file="localHeader.jsp"%>

<c:choose>
	<c:when
		test="${not empty radiologyOrder && empty radiologyOrder.orderId}">
		<!--  Create a new RadiologyOrder -->
		<%@ include file="portlets/radiologyOrderCreationPortlet.jsp"%>
	</c:when>

	<c:otherwise>
		<!--  Show existing RadiologyOrder/discontinued Order -->

		<br>
		<openmrs:portlet url="patientHeader" id="patientDashboardHeader"
			patientId="${order.patient.patientId}" />
		<br>

		<c:choose>
			<c:when test="${not empty radiologyOrder}">
				<!--  Show existing RadiologyOrder -->
				<%@ include file="portlets/radiologyOrderDisplayPortlet.jsp"%>
				<c:if test="${radiologyOrder.completed}">
					<!--  Show form for radiology report -->
					<%@ include file="portlets/radiologyReportPortlet.jsp"%>
				</c:if>
				<c:if test="${radiologyOrder.discontinuationAllowed}">
					<!--  Show form to discontinue an active non in progress/completed RadiologyOrder -->
					<%@ include
						file="portlets/radiologyOrderDiscontinuationPortlet.jsp"%>
				</c:if>
			</c:when>
			<c:otherwise>
				<!--  Show read-only view of discontinuation Order -->
				<%@ include file="portlets/discontinuationOrderDisplayPortlet.jsp"%>
			</c:otherwise>
		</c:choose>
	</c:otherwise>
</c:choose>

<div id="moreInfoPopup"></div>
<%@ include file="/WEB-INF/template/footer.jsp"%>
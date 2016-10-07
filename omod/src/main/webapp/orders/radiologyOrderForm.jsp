<%@ include file="/WEB-INF/template/include.jsp"%>
<c:set var="DO_NOT_INCLUDE_JQUERY" value="true" />
<%@ include file="/WEB-INF/template/header.jsp"%>
<c:set var="INCLUDE_TIME_ADJUSTMENT" value="true" />
<%@ include file="/WEB-INF/view/module/radiology/template/includeScripts.jsp"%>

<openmrs:require
  allPrivileges="Get Care Settings,Get Concepts,Get Encounter Roles,Get Encounters,Get Orders,Get Patients,Get Providers,Get Radiology Orders,Get Users,Get Visit Attribute Types,Get Visit Types,Get Visits,View Orders"
  otherwise="/login.htm" redirect="/module/radiology/radiologyOrder.form" />

<!--  This form shows existing RadiologyOrder/discontinued Order -->

<openmrs:hasPrivilege privilege="View Patients">
  <openmrs:portlet url="patientHeader" id="patientDashboardHeader" patientId="${order.patient.patientId}" parameters="showPatientDashboardLink=true" />
  <br>
</openmrs:hasPrivilege>
<c:choose>
  <c:when test="${not empty radiologyOrder}">
    <!--  Show existing RadiologyOrder -->
    <%@ include file="radiologyOrderDisplaySegment.jsp"%>
    <c:if test="${radiologyOrder.completed}">
      <!--  Show form for radiology report -->
      <openmrs:hasPrivilege hasAll="true" privilege="Add Radiology Reports,Delete Radiology Reports,Edit Radiology Reports,Get Radiology Reports">
        <%@ include file="radiologyReportSegment.jsp"%>
      </openmrs:hasPrivilege>
    </c:if>
    <c:if test="${radiologyOrder.discontinuationAllowed}">
      <!--  Show form to discontinue an active non in progress/completed RadiologyOrder -->
      <openmrs:hasPrivilege hasAll="true" privilege="Delete Radiology Orders,Edit Orders">
        <%@ include file="radiologyOrderDiscontinuationSegment.jsp"%>
      </openmrs:hasPrivilege>
    </c:if>
  </c:when>
  <c:otherwise>
    <!--  Show read-only view of discontinuation Order -->
    <%@ include file="discontinuationOrderDisplaySegment.jsp"%>
  </c:otherwise>
</c:choose>
<%@ include file="/WEB-INF/template/footer.jsp"%>

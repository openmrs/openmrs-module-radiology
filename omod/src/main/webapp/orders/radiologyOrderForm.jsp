<%@ include file="/WEB-INF/template/include.jsp"%>

<openmrs:require
  allPrivileges="Get Care Settings,Get Concepts,Get Encounter Roles,Get Encounters,Get Orders,Get Patients,Get Providers,Get Radiology Orders,Get Users,Get Visit Attribute Types,Get Visit Types,Get Visits,View Orders"
  otherwise="/login.htm" redirect="/module/radiology/radiologyOrder.form" />

<%@ include file="/WEB-INF/template/header.jsp"%>
<%@ include file="/WEB-INF/view/module/radiology/localHeader.jsp"%>

<c:choose>
  <c:when test="${not empty radiologyOrder && empty radiologyOrder.orderId}">
    <!--  Create a new RadiologyOrder -->
    <openmrs:hasPrivilege privilege="Add Encounters">
      <openmrs:hasPrivilege privilege="Add Orders">
        <openmrs:hasPrivilege privilege="Add Radiology Orders">
          <openmrs:hasPrivilege privilege="Add Visits">
            <openmrs:hasPrivilege privilege="Edit Encounters">
              <openmrs:hasPrivilege privilege="Edit Visits">
                <%@ include file="radiologyOrderCreationSegment.jsp"%>
              </openmrs:hasPrivilege>
            </openmrs:hasPrivilege>
          </openmrs:hasPrivilege>
        </openmrs:hasPrivilege>
      </openmrs:hasPrivilege>
    </openmrs:hasPrivilege>

  </c:when>

  <c:otherwise>
    <!--  Show existing RadiologyOrder/discontinued Order -->

    <openmrs:portlet url="patientHeader" id="patientDashboardHeader" patientId="${order.patient.patientId}" />
    <br>

    <c:choose>
      <c:when test="${not empty radiologyOrder}">
        <!--  Show existing RadiologyOrder -->
        <%@ include file="radiologyOrderDisplaySegment.jsp"%>
        <c:if test="${radiologyOrder.completed}">
          <!--  Show form for radiology report -->
          <openmrs:hasPrivilege privilege="Add Radiology Reports">
            <openmrs:hasPrivilege privilege="Delete Radiology Reports">
              <openmrs:hasPrivilege privilege="Edit Radiology Reports">
                <openmrs:hasPrivilege privilege="Get Radiology Reports">
                  <%@ include file="radiologyReportSegment.jsp"%>
                </openmrs:hasPrivilege>
              </openmrs:hasPrivilege>
            </openmrs:hasPrivilege>
          </openmrs:hasPrivilege>

        </c:if>
        <c:if test="${radiologyOrder.discontinuationAllowed}">
          <!--  Show form to discontinue an active non in progress/completed RadiologyOrder -->
          <openmrs:hasPrivilege privilege="Delete Radiology Orders">
            <openmrs:hasPrivilege privilege="Edit Orders">
              <%@ include file="radiologyOrderDiscontinuationSegment.jsp"%>
            </openmrs:hasPrivilege>
          </openmrs:hasPrivilege>
        </c:if>
      </c:when>
      <c:otherwise>
        <!--  Show read-only view of discontinuation Order -->
        <%@ include file="discontinuationOrderDisplaySegment.jsp"%>
      </c:otherwise>
    </c:choose>
  </c:otherwise>
</c:choose>
<%@ include file="/WEB-INF/template/footer.jsp"%>
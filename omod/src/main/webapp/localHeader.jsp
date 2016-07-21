<ul id="menu">
  <c:if test="${not empty radiologyOrder && not empty radiologyOrder.patient}">
    <openmrs:hasPrivilege privilege="View Patients">
      <li class="first"><a
        href="${pageContext.request.contextPath}/patientDashboard.form?patientId=${radiologyOrder.patient.patientId}"><spring:message
            code="radiology.localHeader.links.patientDashboard" /></a></li>
      <c:set var="VIEW_PATIENTS_PRIVILEGE" value="true" />
    </openmrs:hasPrivilege>
    <c:if test='<%=request.getRequestURI()
                            .contains("radiologyReport")%>'>
      <openmrs:hasPrivilege privilege="View Orders">
        <li <c:if test="${empty VIEW_PATIENTS_PRIVILEGE}">class="first"</c:if>><a
          href="${pageContext.request.contextPath}/module/radiology/radiologyOrder.form?orderId=${radiologyOrder.orderId}"><spring:message
              code="radiology.localHeader.links.radiologyOrder" /></a></li>
      </openmrs:hasPrivilege>
    </c:if>
  </c:if>
</ul>

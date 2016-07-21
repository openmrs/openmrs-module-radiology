<ul id="menu">
  <c:if test="${not empty radiologyOrder}">
    <c:choose>
      <c:when test="${not empty radiologyOrder.patient}">
        <openmrs:hasPrivilege privilege="View Patients">
          <li class="first"><a
            href="${pageContext.request.contextPath}/patientDashboard.form?patientId=${radiologyOrder.patient.patientId}"><spring:message
                code="radiology.localHeader.links.patientDashboard" /></a></li>
        </openmrs:hasPrivilege>
        <c:choose>
          <c:when test="${empty radiologyOrder.orderId}">
            <openmrs:hasPrivilege privilege="Add Radiology Order">
              <c:if
                test='<%=request.getRequestURI()
                                                .contains("radiologyOrderForm")%>'>
                <li class="active"><a
                  href="${pageContext.request.contextPath}/module/radiology/radiologyOrder.form?patientId=${radiologyOrder.patient.patientId}"><spring:message
                      code="radiology.localHeader.links.radiologyOrder" /></a></li>
              </c:if>
            </openmrs:hasPrivilege>
          </c:when>

          <c:otherwise>

            <openmrs:hasPrivilege privilege="View Orders">
              <c:if
                test='<%=request.getRequestURI()
                                                .contains("radiologyOrderForm")%>'>
                <li class="active"><a
                  href="${pageContext.request.contextPath}/module/radiology/radiologyOrder.form?orderId=${radiologyOrder.orderId}"><spring:message
                      code="radiology.localHeader.links.radiologyOrder" /></a></li>
              </c:if>
            </openmrs:hasPrivilege>
            <c:if
              test='<%=request.getRequestURI()
                                            .contains("radiologyReport")%>'>
              <openmrs:hasPrivilege privilege="View Orders">
                <li><a
                  href="${pageContext.request.contextPath}/module/radiology/radiologyOrder.form?orderId=${radiologyOrder.orderId}"><spring:message
                      code="radiology.localHeader.links.radiologyOrder" /></a></li>
              </openmrs:hasPrivilege>
              <openmrs:hasPrivilege privilege="Get Radiology Reports">
                <li class="active"><a
                  href="${pageContext.request.contextPath}/module/radiology/radiologyReport.form?radiologyReportId=${radiologyReport.id}"><spring:message
                      code="radiology.localHeader.links.radiologyReport" /></a></li>
              </openmrs:hasPrivilege>
            </c:if>
          </c:otherwise>
        </c:choose>
      </c:when>

      <c:otherwise>
        <openmrs:hasPrivilege privilege="Add Radiology Order">
          <c:if test='<%=request.getRequestURI()
                                        .contains("radiologyOrderForm")%>'>
            <li class="active first"><a href="${pageContext.request.contextPath}/module/radiology/radiologyOrder.form"><spring:message
                  code="radiology.localHeader.links.radiologyOrder" /></a></li>
          </c:if>
        </openmrs:hasPrivilege>
      </c:otherwise>
    </c:choose>
  </c:if>

</ul>

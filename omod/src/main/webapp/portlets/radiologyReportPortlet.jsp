<br>
<div>
  <span class="boxHeader"> <b><spring:message code="radiology.radiologyReportTitle" /></b>
  </span>
  <c:choose>
    <c:when test="${radiologyReportNeedsToBeCreated}">
      <form:form method="post" modelAttribute="radiologyOrder" cssClass="box">
        <tr>
          <td><spring:bind path="orderId">
              <a href="/openmrs/module/radiology/radiologyReport.form?orderId=${status.value}"> <spring:message
                  code="radiology.radiologyReportClaim" />
              </a>
            </spring:bind></td>
        </tr>
      </form:form>
    </c:when>
    <c:otherwise>
      <form:form method="post" modelAttribute="radiologyReport" cssClass="box">
        <tr>
          <td><a href="/openmrs/module/radiology/radiologyReport.form?radiologyReportId=${radiologyReport.id}"> <c:choose>
                <c:when test="${radiologyReport.reportStatus == 'CLAIMED'}">
                  <spring:message code="radiology.radiologyReportResume" />
                </c:when>
                <c:otherwise>
                  <spring:message code="radiology.radiologyReportShow" />
                </c:otherwise>
              </c:choose>
          </a></td>
        </tr>
      </form:form>
    </c:otherwise>
  </c:choose>
</div>
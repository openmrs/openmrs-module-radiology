<c:if test="${radiologyOrder.discontinuedRightNow}">
  <div class="retiredMessage">
    <div>
      <spring:message code="radiology.isNotActiveOrder" />
      <spring:message code="general.dateDiscontinued" />
      <openmrs:formatDate date="${radiologyOrder.dateStopped}" type="medium" />
    </div>
  </div>
</c:if>

<div>
  <span class="boxHeader"> <b><spring:message code="radiology.radiologyOrder" /></b>
  </span>
  <div class="box">
    <%@ include file="radiologyOrderDetailsSegment.jsp"%>
  </div>
</div>

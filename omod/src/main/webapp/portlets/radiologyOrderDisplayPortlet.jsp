<c:if test="${radiologyOrder.discontinuedRightNow}">
	<div class="retiredMessage">
		<div>
			<spring:message code="radiology.isNotActiveOrder" />
			<spring:message code="general.dateDiscontinued" />
			<openmrs:formatDate date="${radiologyOrder.dateStopped}"
				type="medium" />
		</div>
	</div>
</c:if>
<spring:hasBindErrors name="radiologyOrder">
	<spring:message code="fix.error" />
	<br />
</spring:hasBindErrors>
<spring:hasBindErrors name="study">
	<spring:message code="fix.error" />
	<br />
</spring:hasBindErrors>

<div>
	<span class="boxHeader"> <b><spring:message
				code="radiology.radiologyOrder" /></b>
	</span>
	<%@ include file="radiologyOrderDetailsPortlet.jsp"%>
</div>

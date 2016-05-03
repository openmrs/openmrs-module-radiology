
<%@ include file="/WEB-INF/template/include.jsp"%>

<openmrs:require allPrivileges="Add Encounters,Add Orders,Add Radiology Orders,Add Radiology Reports,Add Visits,Delete Radiology Orders,Delete Radiology Reports,Edit Encounters,Edit Orders,Edit Radiology Reports,Edit Visits,Get Care Settings,Get Concepts,Get Encounter Roles,Get Encounters,Get Orders,Get Patients,Get Providers,Get Radiology Reports,Get Users,Get Visit Attribute Types,Get Visit Types,Get Visits,View Orders"
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
		<!--  Show existing Order/RadiologyOrder -->

		<br>
		<openmrs:portlet url="patientHeader" id="patientDashboardHeader"
			patientId="${order.patient.patientId}" />
		<br>

		<c:if test="${empty radiologyOrder}">
			<!--  Show read-only view of discontinuation Order -->
			<div>
				<span class="boxHeader"> <b><spring:message
							code="Order.title" /></b>
				</span>
				<form:form method="post" modelAttribute="order" cssClass="box">
					<table>
						<tr>
							<td><spring:message code="general.id" /></td>
							<td><spring:bind path="orderId">${status.value}</spring:bind></td>
						</tr>
						<tr>
							<td><spring:message code="Order.patient" /></td>
							<td><a
								href="/openmrs/patientDashboard.form?patientId=<spring:bind path="patient.id">
								${status.value}
							</spring:bind>">
									<spring:bind path="patient.personName.fullName">
								${status.value}
							</spring:bind>
							</a></td>
						</tr>
						<tr>
							<td><spring:message code="radiology.imagingProcedure" /></td>
							<td><spring:bind path="concept.name.name">
								${status.value}
					</spring:bind></td>
						</tr>
						<tr>
							<td><spring:message code="Order.orderer" /></td>
							<td><spring:bind path="orderer.name">
								${status.value}
					</spring:bind></td>
						</tr>
						<tr>
							<td><spring:message code="general.dateDiscontinued" /></td>
							<td><spring:bind path="dateActivated">
								${status.value}
					</spring:bind></td>
						</tr>
						<tr>
							<td><spring:message code="general.discontinuedReason" /></td>
							<td><spring:bind path="orderReasonNonCoded">
								${status.value}
					</spring:bind></td>
						</tr>
						<tr>
							<td><spring:message code="radiology.discontinuedOrder" /></td>
							<td><spring:bind path="previousOrder">
									<a href="radiologyOrder.form?orderId=${status.value}">${status.value}</a>
								</spring:bind></td>
						</tr>
						<tr>
							<td><spring:message code="general.createdBy" /></td>
							<td><spring:bind path="creator.personName">
								${status.value}
							</spring:bind> - <spring:bind path="dateCreated">
								${status.value}
							</spring:bind></td>
						</tr>
					</table>
					<br />
				</form:form>
			</div>
		</c:if>
		<c:if test="${not empty radiologyOrder}">
			<!--  Show existing RadiologyOrder -->
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
				<%@ include file="portlets/radiologyOrderDetailsPortlet.jsp"%>
			</div>

			<c:if test="${radiologyOrder.completed}">
				<%@ include file="portlets/radiologyReportPortlet.jsp"%>
			</c:if>
			<c:if test="${radiologyOrder.discontinuationAllowed}">
				<!--  Show form to discontinue an active non in progress/completed RadiologyOrder -->
				<%@ include file="portlets/radiologyOrderDiscontinuationPortlet.jsp"%>
			</c:if>
			</div>
		</c:if>
	</c:otherwise>
</c:choose>

<div id="moreInfoPopup"></div>
<%@ include file="/WEB-INF/template/footer.jsp"%>
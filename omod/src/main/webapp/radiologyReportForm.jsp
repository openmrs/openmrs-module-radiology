<%@ include file="/WEB-INF/template/include.jsp"%>
<%@ include file="/WEB-INF/template/header.jsp"%>
<%@ include
	file="/WEB-INF/view/module/radiology/resources/js/moreInfo.js"%>

<%@ include file="localHeader.jsp"%>

<spring:hasBindErrors name="radiologyReport">
	<spring:message code="fix.error" />
</spring:hasBindErrors>
<br>
<openmrs:portlet url="patientHeader" id="patientDashboardHeader"
	patientId="${order.patient.patientId}" />
<br>
<div>
	<span class="boxHeader"> <b><spring:message
				code="radiology.radiologyOrder" /></b>
	</span>
	<%@ include file="portlets/radiologyOrderDetailsPortlet.jsp"%>
</div>
<br>
<span class="boxHeader"> <b><spring:message
			code="radiology.radiologyReportTitle" /></b>
</span>
<form:form modelAttribute="radiologyReport" method="post">
	<div class="box">
		<table>
			<tr>
				<td><spring:message code="radiology.radiologyReportId" /></td>
				<td>${radiologyReport.id}</td>
				<form:hidden path="id" />
			</tr>
			<tr>
				<form:hidden path="radiologyOrder" />
			</tr>
			<tr>
				<td><spring:message code="radiology.reportStatus" /></td>
				<td>${radiologyReport.reportStatus}</td>
				<form:hidden path="reportStatus" />
			</tr>
			<c:if test="${radiologyReport.reportStatus == 'COMPLETED'}">
				<tr>
					<td><spring:message code="radiology.radiologyReportDate" /></td>
					<td>${radiologyReport.reportDate}</td>
					<form:hidden path="reportDate" />
				</tr>
			</c:if>
			<tr>
				<td><spring:message code="radiology.radiologyReportDiagnosis" /></td>
				<td><c:choose>
						<c:when test="${radiologyReport.reportStatus == 'COMPLETED'}">
							<form:textarea path="reportBody" disabled="true"></form:textarea>
						</c:when>
						<c:otherwise>
							<form:textarea path="reportBody"></form:textarea>
						</c:otherwise>
					</c:choose></td>
			</tr>
			<tr>
				<td><spring:message code="radiology.radiologyReportProvider" /></td>
				<td><c:choose>
						<c:when
							test="${not empty radiologyReport.principalResultsInterpreter.id}">
                            ${radiologyReport.principalResultsInterpreter.name}
                            <form:hidden
								path="principalResultsInterpreter" />
						</c:when>
						<c:otherwise>
							<spring:bind path="principalResultsInterpreter">
								<openmrs:fieldGen type="org.openmrs.Provider"
									formFieldName="${status.expression}"
									val="${status.editor.value}" />
								<c:if test="${status.errorMessage != ''}">
									<span class="error">${status.errorMessage}</span>
								</c:if>
							</spring:bind>
						</c:otherwise>
					</c:choose></td>
			</tr>
			<tr>
				<td><spring:message code="general.createdBy" /></td>
				<td><spring:bind path="creator.personName">
					${status.value}
					<form:hidden path="creator" />
					</spring:bind> - <spring:bind path="dateCreated">
					${status.value}
					<form:hidden path="dateCreated" />
					</spring:bind></td>
			</tr>
		</table>
		<br>
		<c:if test="${radiologyReport.reportStatus != 'COMPLETED'}">
			<c:if test="${radiologyReport.reportStatus != 'DISCONTINUED'}">
				<input type="submit"
					value="<spring:message code="radiology.radiologyReportUnclaim"/>"
					name="unclaimRadiologyReport" />
				<input type="submit"
					value="<spring:message code="radiology.radiologyReportSave"/>"
					name="saveRadiologyReport" />
				<input type="submit"
					value="<spring:message code="radiology.radiologyReportComplete"/>"
					name="completeRadiologyReport" />
			</c:if>
		</c:if>
	</div>
</form:form>


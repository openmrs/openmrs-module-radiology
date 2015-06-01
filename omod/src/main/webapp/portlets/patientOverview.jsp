
<%@ include file="/WEB-INF/template/include.jsp"%>
<c:if test="${not empty param.patientIdReq}">
	<openmrs:portlet url="patientHeader" id="patientDashboardHeader"
		patientId="${param.patientIdReq}" />
	<br />

	<openmrs:hasPrivilege privilege="View Allergies">
		<div id="patientActiveListsAllergyBoxHeader"
			class="boxHeader${model.patientVariation}">
			<spring:message code="ActiveLists.allergy.title" />
		</div>
		<div id="patientActiveListsAllergyBox"
			class="box${model.patientVariation}">
			<openmrs:portlet url="activeListAllergy"
				patientId="${param.patientIdReq}" parameters="type=allergy" />
		</div>
		<br />
	</openmrs:hasPrivilege>

	<openmrs:hasPrivilege privilege="View Problems">
		<div id="patientActiveListsProblemBoxHeader"
			class="boxHeader${model.patientVariation}">
			<spring:message code="ActiveLists.problem.title" />
		</div>
		<div id="patientActiveListsProblemBox"
			class="box${model.patientVariation}">
			<openmrs:portlet url="activeListProblem"
				patientId="${param.patientIdReq}" parameters="type=problem" />
		</div>
		<br />
	</openmrs:hasPrivilege>
	<a
		href="/openmrs/patientDashboard.form?patientId=${param.patientIdReq}"><spring:message
			code="patientDashboard.viewDashboard" /></a>
</c:if>
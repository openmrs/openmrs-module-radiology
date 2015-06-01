<%@ include file="/WEB-INF/template/include.jsp"%>
<div>
	<h2>
		<spring:message code="Obs.title" />
	</h2>
	<spring:hasBindErrors name="obs">
		<spring:message code="fix.error" />
		<div class="error">
			<c:forEach items="${errors.globalErrors}" var="error">
				<spring:message code="${error.defaultMessage}"
					text="${error.defaultMessage}" />
				<br />
				<!-- ${error} -->
			</c:forEach>
		</div>
		<br />
	</spring:hasBindErrors>
	<c:if test="${obs.voided}">
		<div class="retiredMessage">
			<div>
				<spring:message code="general.voidedBy" />
				${obs.voidedBy.personName}
				<openmrs:formatDate date="${obs.dateVoided}" type="medium" />
				- ${obs.voidReason}
			</div>
		</div>
	</c:if>
	<table>
		<spring:nestedPath path="obs">
			<tr>
				<th><spring:message code="general.id" /></th>
				<td>${obs.obsId}</td>
			</tr>
			<tr>
				<th><spring:message code="Obs.encounter" /></th>
				<td><c:choose>
						<c:when test="${obs.encounter == null}">
                                            None
                                    </c:when>
						<c:otherwise>
                                            ${obs.encounter.location.name} - <openmrs:formatDate
								date="${obs.encounter.encounterDatetime}" type="medium" />
							<a
								href="${pageContext.request.contextPath}/admin/encounters/encounter.form?encounterId=${obs.encounter.encounterId}"><spring:message
									code="general.view" />/<spring:message code="general.edit" /></a>
						</c:otherwise>
					</c:choose></td>
			</tr>
			<tr>
				<th><spring:message code="Obs.location" /></th>
				<td>${obs.location}</td>
			</tr>
			<tr>
				<th><spring:message code="Obs.datetime" /></th>
				<td><openmrs:formatDate date="${obs.obsDatetime}" type="medium" />
				</td>
			</tr>

			<tr>
				<th><spring:message code="Obs.concept" /></th>
				<td>${obs.concept.name.name}</td>
			</tr>

			<tr>
				<th><spring:message code="general.value" /></th>
				<td>${obsAnswer}</td>
			</tr>

			<tr>
				<th><spring:message code="Obs.comment" /></th>
				<td><textarea rows="2" cols="45" readonly>${obs.comment}</textarea>
			</tr>
			<c:if test="${obs.creator != null}">
				<tr>
					<th><spring:message code="general.createdBy" /></th>
					<td>${obs.creator.personName}- <openmrs:formatDate
							date="${obs.dateCreated}" type="medium" />
					</td>
				</tr>
			</c:if>
		</spring:nestedPath>
	</table>

</div>


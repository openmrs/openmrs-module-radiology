<openmrs:htmlInclude file="/scripts/calendar/calendar.js" />
<openmrs:htmlInclude file="/scripts/timepicker/timepicker.js" />
<openmrs:htmlInclude file="/moduleResources/radiology/js/moreInfo.js" />
<script type="text/javascript">
	function onQuestionSelect(concept) {
		$j("#conceptDescription").show();
		$j("#conceptDescription").html(concept.description);
	}
</script>

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
				code="radiology.addOrder" /></b>
	</span>
	<form:form method="post" modelAttribute="radiologyOrder" cssClass="box">
		<table>
			<tr>
				<td><spring:message code="Order.patient" /></td>
				<td><spring:bind path="patient">
						<openmrs:fieldGen type="org.openmrs.Patient"
							formFieldName="${status.expression}" val="${status.editor.value}" />
						<a style="cursor: pointer;" id="moreInfo"><spring:message
								code="radiology.moreInfo" /></a>
						<c:if test="${status.errorMessage != ''}">
							<span class="error">${status.errorMessage}</span>
						</c:if>
					</spring:bind></td>
			</tr>

			<tr>
				<td><spring:message code="radiology.imagingProcedure" /></td>
				<td><spring:bind path="concept">
						<openmrs_tag:conceptField formFieldName="concept"
							formFieldId="conceptId"
							initialValue="${status.editor.value.conceptId}"
							onSelectFunction="onQuestionSelect"
							includeClasses="${radiologyConceptClassNames}" />
						<c:if test="${status.errorMessage != ''}">
							<span class="error">${status.errorMessage}</span>
						</c:if>
						<div class="description" id="conceptDescription"></div>
					</spring:bind></td>
			</tr>
			<tr>
				<td><spring:message code="radiology.urgency" /></td>
				<td><spring:bind path="urgency">
						<select name="${status.expression}" id="urgencySelect">
							<c:forEach var="urgency" items="${urgencies}">
								<option value="${urgency}"
									${status.value == urgency ? 'selected="selected"' : ''}><spring:message
										code="radiology.${urgency}" text="${urgency}" /></option>
							</c:forEach>
						</select>
						<c:if test="${status.errorMessage != ''}">
							<span class="error">${status.errorMessage}</span>
						</c:if>
					</spring:bind></td>
			</tr>
			<tr>
				<td><spring:message code="radiology.scheduledDate" /></td>
				<td><spring:bind path="scheduledDate">
						<input name="${status.expression}" type="text"
							onclick="showDateTimePicker(this)" value="${status.value}">
						<c:if test="${status.errorMessage != ''}">
							<span class="error">${status.errorMessage}</span>
						</c:if>
					</spring:bind></td>
			</tr>
			<tr>
				<td><spring:message code="radiology.scheduledStatus" /></td>
				<td><spring:bind path="study.scheduledStatus">
						<select name="${status.expression}"
							id="scheduledProcedureStepStatusSelect">
							<c:forEach var="scheduledProcedureStepStatus"
								items="${scheduledProcedureStepStatuses}">
								<option value="${scheduledProcedureStepStatus.key}"
									${status.value == scheduledProcedureStepStatus.key ? 'selected="selected"' : ''}><spring:message
										code="radiology.${scheduledProcedureStepStatus.value}"
										text="${scheduledProcedureStepStatus.value}" /></option>
							</c:forEach>
						</select>
						<c:if test="${status.errorMessage != ''}">
							<span class="error">${status.errorMessage}</span>
						</c:if>
					</spring:bind></td>
			</tr>
			<tr>
				<td><spring:message code="radiology.performedStatus" /></td>
				<td><spring:bind path="study.performedStatus">
						<select name="${status.expression}" id="performedStatusSelect">
							<c:forEach var="performedStatus" items="${performedStatuses}">
								<option value="${performedStatus.key}"
									${status.value == performedStatus.key ? 'selected="selected"' : ''}><spring:message
										code="radiology.${performedStatus.key}"
										text="${performedStatus.value}" /></option>
							</c:forEach>
						</select>
						<c:if test="${status.errorMessage != ''}">
							<span class="error">${status.errorMessage}</span>
						</c:if>
					</spring:bind></td>
			</tr>
			<tr>
				<td><spring:message code="radiology.modality" /></td>
				<td><spring:bind path="study.modality">
						<select name="${status.expression}" id="modalitySelect">
							<c:forEach var="modality" items="${modalities}">
								<option value="${modality.key}"
									${status.value == modality.key ? 'selected="selected"' : ''}><spring:message
										code="radiology.${modality.key}" text="${modality.value}" /></option>
							</c:forEach>
						</select>
						<c:if test="${status.errorMessage != ''}">
							<span class="error">${status.errorMessage}</span>
						</c:if>
					</spring:bind></td>
			</tr>
			<tr>
				<td><spring:message code="general.instructions" /></td>
				<td><spring:bind path="instructions">
						<textarea name="${status.expression}">${status.value}</textarea>
						<c:if test="${status.errorMessage != ''}">
							<span class="error">${status.errorMessage}</span>
						</c:if>
					</spring:bind></td>
			</tr>
			<tr>
				<td><spring:message code="Order.orderer" /></td>
				<td><spring:bind path="orderer">
						<openmrs:fieldGen type="org.openmrs.Provider"
							formFieldName="${status.expression}" val="${status.editor.value}" />
						<c:if test="${status.errorMessage != ''}">
							<span class="error">${status.errorMessage}</span>
						</c:if>
					</spring:bind></td>
			</tr>
		</table>
		<br />
		<input type="submit" name="saveRadiologyOrder"
			value="<spring:message code="Order.save"/>">
	</form:form>
</div>
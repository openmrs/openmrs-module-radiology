
<%@ include file="/WEB-INF/template/include.jsp"%>

<openmrs:require privilege="Edit Orders" otherwise="/login.htm"
	redirect="/module/radiology/radiologyOrder.form" />

<%@ include file="/WEB-INF/template/header.jsp"%>
<%@ include file="localHeader.jsp"%>
<openmrs:htmlInclude file="/scripts/calendar/calendar.js" />
<%@ include
	file="/WEB-INF/view/module/radiology/resources/js/moreInfo.js"%>
<script type="text/javascript">
	function onQuestionSelect(concept) {
		$j("#conceptDescription").show();
		$j("#conceptDescription").html(concept.description);
	}
</script>
<h2>
	<spring:message code="Order.title" />
</h2>

<spring:hasBindErrors name="radiologyOrder">
	<spring:message code="fix.error" />
	<br />
</spring:hasBindErrors>
<spring:hasBindErrors name="study">
	<spring:message code="fix.error" />
	<br />
</spring:hasBindErrors>

<c:choose>
	<c:when
		test="${not empty radiologyOrder && empty radiologyOrder.orderId}">
		<!--  Create a new RadiologyOrder -->
		<form:form method="post" modelAttribute="radiologyOrder"
			cssClass="box">
			<table>
				<tr>
					<td><spring:message code="Order.patient" /></td>
					<td><spring:bind path="patient">
							<openmrs:fieldGen type="org.openmrs.Patient"
								formFieldName="${status.expression}"
								val="${status.editor.value}" />
							<a style="cursor: pointer;" id="moreInfo"><spring:message
									code="radiology.moreInfo" /></a>
							<c:if test="${status.errorMessage != ''}">
								<span class="error">${status.errorMessage}</span>
							</c:if>
						</spring:bind></td>
				</tr>

				<tr>
					<td><spring:message code="Order.concept" /></td>
					<td><spring:bind path="concept">
						<openmrs_tag:conceptField formFieldName="concept"
						                          formFieldId="conceptId"
						                          initialValue="${status.editor.value.conceptId}"
						                          onSelectFunction="onQuestionSelect"
						                          includeClasses="${radiologyConceptClassNames}" />

						<div class="description" id="conceptDescription"></div>
					</spring:bind></td>
				</tr>
				<tr>
					<td><spring:message code="radiology.urgency" /></td>
					<td><spring:bind path="urgency">
							<select name="${status.expression}" id="urgencySelect">
								<c:forEach var="urgency" items="${urgencies}">
									<option value="${urgency}"
										${status.value == urgency ? 'selected="selected"' : ''}>${urgency}</option>
								</c:forEach>
							</select>
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
										${status.value == scheduledProcedureStepStatus.key ? 'selected="selected"' : ''}>${scheduledProcedureStepStatus.value}</option>
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
										${status.value == modality.key ? 'selected="selected"' : ''}>${modality.value}</option>
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
								formFieldName="${status.expression}"
								val="${status.editor.value}" />
							<c:if test="${status.errorMessage != ''}">
								<span class="error">${status.errorMessage}</span>
							</c:if>
						</spring:bind></td>
				</tr>
				<tr>
					<td><spring:message code="radiology.scheduledDate" /></td>
					<td><spring:bind path="scheduledDate">
							<openmrs:fieldGen type="java.util.Date"
								formFieldName="${status.expression}"
								val="${status.editor.value}" />
							<c:if test="${status.errorMessage != ''}">
								<span class="error">${status.errorMessage}</span>
							</c:if>
						</spring:bind></td>
				</tr>
				<tr>
					<td><spring:message code="general.dateAutoExpire" /></td>
					<td><spring:bind path="autoExpireDate">
							<openmrs:fieldGen type="java.util.Date"
								formFieldName="${status.expression}"
								val="${status.editor.value}" />
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
	</c:when>

	<c:otherwise>
		<c:if test="${empty radiologyOrder}">
			<!--  Show existing RadiologyOrder's and discontinuation Order's -->
			<form:form method="post" modelAttribute="order" cssClass="box">
				<table>
					<tr>
						<td><spring:message code="Order.title" /> <spring:message
								code="general.id" /></td>
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
						<td><spring:message code="Order.concept" /></td>
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
		</c:if>
		<c:if test="${not empty radiologyOrder}">
			<%@ include file="portlets/orderDetailsPortlet.jsp" %>
			<c:if test="${isOrderActive}">
				<br />
				<form:form method="post" modelAttribute="discontinuationOrder"
					cssClass="box">
					<table>
						<tr>
							<td><spring:message code="Order.orderer" /></td>
							<td><spring:bind path="orderer">
									<openmrs:fieldGen type="org.openmrs.Provider"
										formFieldName="${status.expression}"
										val="${status.editor.value}" />
									<c:if test="${status.errorMessage != ''}">
										<span class="error">${status.errorMessage}</span>
									</c:if>
								</spring:bind></td>
						</tr>
						<tr>
							<td><spring:message code="general.dateDiscontinued" /></td>
							<td><spring:bind path="dateActivated">
									<openmrs:fieldGen type="java.util.Date"
										formFieldName="${status.expression}"
										val="${status.editor.value}" />
									<c:if test="${status.errorMessage != ''}">
										<span class="error">${status.errorMessage}</span>
									</c:if>
								</spring:bind></td>
						</tr>
						<tr>
							<td><spring:message code="general.discontinuedReason" /></td>
							<td><spring:bind path="orderReasonNonCoded">
									<textarea name="${status.expression}">${status.value}</textarea>
									<c:if test="${status.errorMessage != ''}">
										<span class="error">${status.errorMessage}</span>
									</c:if>
								</spring:bind></td>
						</tr>
					</table>
					<input type="submit" name="discontinueOrder"
						value='<spring:message code="Order.discontinueOrder"/>' />
				</form:form>
			</c:if>
		</c:if>
	</c:otherwise>
</c:choose>


<div id="moreInfoPopup"></div>
<%@ include file="/WEB-INF/template/footer.jsp"%>
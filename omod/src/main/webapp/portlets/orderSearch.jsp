<%@ include file="/WEB-INF/template/include.jsp"%>

<openmrs:require privilege="View Orders" otherwise="/login.htm"
	redirect="/module/radiology/radiologyOrder.list" />

<c:if test="${not empty radiologyOrders}">
	<c:if test="${empty exceptionText}">
		<table id="radiologyOrdersTable" cellspacing="0" width="100%"
			class="display nowrap">
			<thead>
				<tr>
					<th></th>
					<th><spring:message code="general.edit" /></th>
					<th><spring:message code="radiology.patientId" /></th>
					<th><spring:message code="radiology.patientFullName" /></th>
					<th><spring:message code="radiology.priority" /></th>
					<th><spring:message code="radiology.appoinmentDate" /></th>
					<th><spring:message code="radiology.modality" /></th>
					<th><spring:message code="radiology.performedStatus" /></th>
					<th><spring:message code="radiology.referringPhysician" /></th>
					<th><spring:message code="radiology.scheduledStatus" /></th>
					<th><spring:message code="general.instructions" /></th>
				</tr>
			</thead>
			<tbody id="radiologyOrdersTableBody">
				<c:forEach items="${radiologyOrders}" var="radiologyOrder">
					<tr data-child-order_id="${radiologyOrder.orderId}"
						data-child-physician="${radiologyOrder.orderer.name}"
						data-child-status="<spring:message
								code="radiology.${radiologyOrder.study.scheduledStatus}"
								text="${radiologyOrder.study.scheduledStatus}" />"
						data-child-instructions="${radiologyOrder.instructions}">
						<td class="details-control"></td>
						<td><a
							href="radiologyOrder.form?orderId=${radiologyOrder.orderId}">${radiologyOrder.orderId}</a></td>
						<td style="text-align: center"><a
							href="/openmrs/patientDashboard.form?patientId=${radiologyOrder.patient.patientId}">${radiologyOrder.patient.patientIdentifier}</a></td>
						<td><a
							href="/openmrs/patientDashboard.form?patientId=${radiologyOrder.patient.patientId}">${radiologyOrder.patient.personName}</a></td>
						<td><spring:message
								code="radiology.${radiologyOrder.urgency}"
								text="${radiologyOrder.urgency}" /></td>
						<td>${radiologyOrder.effectiveStartDate}</td>
						<td><spring:message
								code="radiology.${radiologyOrder.study.modality}"
								text="${radiologyOrder.study.modality}" /></td>
						<td><spring:message
								code="radiology.${radiologyOrder.study.performedStatus}"
								text="${radiologyOrder.study.performedStatus}" /></td>
						<td>${radiologyOrder.orderer.name}</td>
						<td><spring:message
								code="radiology.${radiologyOrder.study.scheduledStatus}"
								text="${radiologyOrder.study.scheduledStatus}" /></td>
						<td>${radiologyOrder.instructions}</td>
					</tr>
				</c:forEach>
			</tbody>
		</table>
	</c:if>
</c:if>
<c:if test="${not empty exceptionText}">
	<span id="errorSpan" class="error"> <spring:message
			code="${exceptionText}" arguments="${invalidValue}" />
	</span>
</c:if>
<c:if test="${empty radiologyOrders}">
	<br />
	<p>
		<spring:message code="radiology.OrderListEmpty" />
	</p>
</c:if>

<%@ include file="/WEB-INF/template/include.jsp"%>

<openmrs:require privilege="View Orders" otherwise="/login.htm"
	redirect="/module/radiology/radiologyOrderList.jsp" />


<c:if test="${not empty orderList}">
	<c:if test="${empty exceptionText}">
		<table id="matchedOrders" cellspacing="0" width="100%"
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
					<th><spring:message code="radiology.radiologyReportId" /></th>
					<th><spring:message code="radiology.referringPhysician" /></th>
					<th><spring:message code="radiology.scheduledStatus" /></th>
					<th><spring:message code="general.instructions" /></th>
					<th><spring:message code="radiology.mwlStatus" /></th>
				</tr>
			</thead>
			<tbody id="matchedOrdersBody">
				<c:forEach items="${orderList}" var="report">
					<tr data-child-order_id="${report.radiologyOrder.orderId}"
						data-child-report_id="${report.id}"
						data-child-physician="${report.radiologyOrder.orderer.name}"
						data-child-status="${report.radiologyOrder.study.scheduledStatus}"
						data-child-instructions="${report.radiologyOrder.instructions}"
						data-child-mwl="<spring:message code="radiology.${report.radiologyOrder.study.mwlStatus}"/>">
						<td class="details-control"></td>
						<td><a
							href="radiologyOrder.form?orderId=${report.radiologyOrder.orderId}">${report.radiologyOrder.orderId}</a></td>
						<td style="text-align: center">${report.radiologyOrder.patient.patientIdentifier}</td>
						<td>${report.radiologyOrder.patient.personName}</td>
						<td>${report.radiologyOrder.urgency}</td>
						<td name="appointmentDate">${report.radiologyOrder.effectiveStartDate}</td>
						<td>${report.radiologyOrder.study.modality.fullName}</td>
						<td>${report.radiologyOrder.study.performedStatus}</td>
						<td><c:if test="${report.id != '0'}">
								<a
									href="/openmrs/module/radiology/radiologyReport.form?orderId=${report.radiologyOrder.orderId}">${report.id}</a>
							</c:if></td>
						<td>${report.radiologyOrder.orderer.name}</td>
						<td>${report.radiologyOrder.study.scheduledStatus}</td>
						<td style="max-width: 90px; overflow: hidden;"><a
							style="cursor: pointer"
							onclick="$j('<p>'+this.innerHTML+'</p>').dialog({autoOpen:true,modal:true});"
							title="<spring:message code="general.view"/>">${report.radiologyOrder.instructions}
						</a></td>
						<td><spring:message code="radiology.${report.radiologyOrder.study.mwlStatus}"
								text="${report.radiologyOrder.study.mwlStatus}" /></td>
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
<c:if test="${empty orderList}">
	<br />
	<p>
		<spring:message code="radiology.OrderListEmpty" />
	</p>

</c:if>

<%@ include file="/WEB-INF/template/include.jsp"%>

<openmrs:require privilege="View Orders" otherwise="/login.htm"
	redirect="/module/radiology/radiologyOrderList.jsp" />


<c:if test="${not empty orderList}">
	<c:if test="${empty exceptionText}">
		<table id="matchedOrders" cellpadding="2" cellspacing="0" width="100%">
			<thead>
				<tr>
					<th><c:if test="${empty obsId}">
							<spring:message code="general.edit" />
						</c:if> <c:if test="${not empty obsId}">
							Obs.
						</c:if></th>
					<th><spring:message code="radiology.patientId" /></th>
					<th><spring:message code="radiology.patientFullName" /></th>
					<th><spring:message code="radiology.priority" /></th>
					<th><spring:message code="radiology.referringPhysician" /></th>
					<th><spring:message code="radiology.appoinmentDate" /></th>
					<th><spring:message code="radiology.modality" /></th>
					<th><spring:message code="radiology.scheduledStatus" /></th>
					<th><spring:message code="radiology.performedStatus" /></th>
					<th><spring:message code="general.instructions" /></th>
					<th><spring:message code="radiology.mwlStatus" /></th>
				</tr>
			</thead>
			<tbody id="matchedOrdersBody">
				<c:forEach items="${orderList}" var="order">
					<tr>
						<td><c:if test="${empty obsId}">
								<a href="radiologyOrder.form?orderId=${order.orderId}">${order.orderId}</a>
							</c:if> <c:if test="${not empty obsId}">
								<!-- TODO  select observation-->
								<a href="radiologyObs.form?orderId=${order.orderId}${obsId}">${order.orderId}</a>
							</c:if></td>
						<td style="text-align: center">${order.patient.patientIdentifier}</td>
						<td>${order.patient.personName}</td>
						<td>${order.urgency}</td>
						<td>${order.orderer.name}</td>
						<td name="appointmentDate">${order.effectiveStartDate}</td>
						<td>${order.study.modality.fullName}</td>
						<td>${order.study.scheduledStatus}</td>
						<td>${order.study.performedStatus}</td>
						<td style="max-width: 90px; overflow: hidden;"><a
							style="cursor: pointer"
							onclick="$j('<p>'+this.innerHTML+'</p>').dialog({autoOpen:true,modal:true});"
							title="<spring:message code="general.view"/>">${order.instructions}
						</a></td>
						<td><spring:message code="radiology.${order.study.mwlStatus}"
								text="${order.study.mwlStatus}" /></td>
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

<%@ include file="/WEB-INF/template/include.jsp"%>

<openmrs:require privilege="View Orders" otherwise="/login.htm"
	redirect="/module/radiology/radiologyOrderList.jsp" />

<c:if test="${not empty orderList}">
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
				<th><spring:message code="radiology.scheduler" /></th>
				<th><spring:message code="radiology.performingPhysician" /></th>
				<th><spring:message code="radiology.readingPhysician" /></th>
				<th><spring:message code="radiology.appoinmentDate" /></th>
				<th><spring:message code="radiology.modality" /></th>
				<th title="<spring:message code="radiology.accordingModality"/>">
					<spring:message code="radiology.status" />
				</th>
				<th><spring:message code="general.instructions" /></th>
				<th><spring:message code="radiology.mwlStatus" /></th>
			</tr>
		</thead>
		<tbody id="matchedOrdersBody">
			<c:forEach items="${orderList}" begin="0" end="${matchedOrdersSize}"
				var="order" varStatus="status">
				<tr>
					<td><c:if test="${empty obsId}">
							<a href="radiologyOrder.form?orderId=${order.orderId}">${status.count}</a>
						</c:if> <c:if test="${not empty obsId}">
							<!-- TODO  select observation-->
							<a href="radiologyObs.form?orderId=${order.orderId}${obsId}">${status.count}</a>
						</c:if></td>
					<td style="text-align: center">${order.patient.patientIdentifier}</td>
					<td>${order.patient.personName}</td>
					<td>${priorities[status.count-1]}</td>
					<td>${order.orderer.personName}</td>
					<td>${schedulers[status.count-1]}</td>
					<td>${performings[status.count-1]}</td>
					<td>${readings[status.count-1]}</td>
					<td name="appointmentDate">${order.startDate}</td>
					<td>${modalities[status.count-1]}</td>
					<td>${statuses[status.count-1]}</td>
					<td style="max-width: 90px; overflow: hidden;"><a
						style="cursor: pointer"
						onclick="$j('<p>'+this.innerHTML+'</p>').dialog({autoOpen:true,modal:true});"
						title="<spring:message code="general.view"/>">${order.instructions}
					</a></td>
					<td><spring:message
							code="radiology.${mwlStatuses[status.count-1]}"
							text="${mwlStatuses[status.count-1]}" /></td>
				</tr>
			</c:forEach>
		</tbody>
	</table>
</c:if>
<c:if test="${error == 'crossDate'}">
	<span id="crossDate" class="error"><spring:message
			code="radiology.crossDate" /> </span>
</c:if>
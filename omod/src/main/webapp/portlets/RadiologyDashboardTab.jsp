<%@ include file="/WEB-INF/template/include.jsp"%>

<openmrs:htmlInclude
	file="/scripts/jquery-ui/js/jquery-ui-1.7.2.custom.min.js" />
<openmrs:htmlInclude file="/moduleResources/radiology/radiology.css" />
<openmrs:htmlInclude
	file="/moduleResources/radiology/js/jquery.dataTables.min.js" />

<%@ include
	file="/WEB-INF/view/module/radiology/resources/js/orderList.js"%>
<openmrs:htmlInclude file="/moduleResources/radiology/js/sortNumbers.js" />
<openmrs:htmlInclude
	file="/moduleResources/radiology/css/jquery.dataTables.min.css" />
<openmrs:htmlInclude
	file="/moduleResources/radiology/css/details-control.dataTables.css" />

<openmrs:hasPrivilege privilege="Add Orders">
	<p>
		<a
			href="module/radiology/radiologyOrder.form?patientId=${patient.patientId}"><spring:message
				code="radiology.addOrder" /></a> <br />
	</p>
</openmrs:hasPrivilege>

<div id="radiologyOrders">
	<div id="radiologyHeader" class="boxHeader">
		<spring:message code="radiology.radiologyOrders" />
	</div>
	<div id="radiologyTable" class="box">
		<br>
		<c:if test="${not empty orderList}">
			<table id="matchedOrders" cellspacing="0" width="100%"
				class="display nowrap">
				<thead>
					<tr>
						<th><spring:message code="general.edit" /></th>
						<th><spring:message code="radiology.priority" /></th>
						<th><spring:message code="radiology.referringPhysician" /></th>
						<th><spring:message code="radiology.appoinmentDate" /></th>
						<th><spring:message code="radiology.modality" /></th>
						<th><spring:message code="radiology.scheduledStatus" /></th>
						<th><spring:message code="radiology.performedStatus" /></th>
						<th><spring:message code="general.instructions" /></th>
					</tr>
				</thead>
				<tbody id="matchedOrdersBody">
					<c:forEach items="${orderList}" begin="0"
						end="${matchedOrdersSize}" var="order" varStatus="status">
						<tr>
							<td style="text-align: center"><a
								href="module/radiology/radiologyOrder.form?orderId=${order.orderId}">${order.orderId}
							</a></td>
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
						</tr>
					</c:forEach>
				</tbody>
			</table>
		</c:if>
		<c:if test="${empty orderList}">
			<p>
				<spring:message code="radiology.OrderListEmpty" />
			</p>
		</c:if>
	</div>
</div>

<script type="text/javascript">
	var $j = jQuery.noConflict();
	$j(document)
			.ready(
					function() {
						$j('table#matchedOrders')
								.dataTable(
										{
											"order" : [ [ 1, 'asc' ] ],
											"oLanguage" : {
												"sLengthMenu" : '<spring:message code="radiology.show"/>'
														+ ' _MENU_ <spring:message code="radiology.entries"/>',
												"sSearch" : '<spring:message code="general.search"/>:',
												"sInfo" : '<spring:message code="radiology.viewing"/> _START_ '
														+ '- _END_ '
														+ '<spring:message code="radiology.of"/> _TOTAL_',
												"oPaginate" : {
													"sFirst" : '<spring:message code="radiology.first"/>',
													"sPrevious" : '<spring:message code="general.previous"/>',
													"sNext" : '<spring:message code="general.next"/>',
													"sLast" : '<spring:message code="radiology.last"/>',
												},
												"sProcessing" : '<spring:message code="general.loading"/>'
											},
											"aoColumnDefs" : [ {
												"sType" : "num-html",
												"bSortable" : true,
												"aTargets" : [ 0 ]
											} ],
										});
					});
</script>
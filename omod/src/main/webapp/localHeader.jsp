<!-- Add, Edit, Delete, View Orders -->
<!-- Add, Edit, Delete, View Reports -->
<!-- Configure Devices -->


<ul id="menu">
	<li class="first"><a
		href="${pageContext.request.contextPath}/admin"><spring:message
				code="admin.title.short" /></a></li>
	<openmrs:hasPrivilege privilege="View Orders">
		<li
			<c:if test='<%=request.getRequestURI().contains("radiologyOrderList")%>'>class="active"</c:if>>
			<a
			href="${pageContext.request.contextPath}/module/radiology/radiologyOrder.list">
				<spring:message code="radiology.manageOrders" />
		</a>
		</li>
		<c:if
			test='<%=request.getRequestURI().contains("radiologyOrderForm")%>'>
			<li class="active"><a
				href="${pageContext.request.contextPath}/module/radiology/radiologyOrder.form?orderId=${radiologyOrder.orderId}">
					Radiology Order </a></li>
		</c:if>
		<c:if
			test='<%=request.getRequestURI().contains("radiology/radiologyReport")%>'>
			<li><a
				href="${pageContext.request.contextPath}/module/radiology/radiologyOrder.form?orderId=${radiologyOrder.orderId}">
					Radiology Order </a></li>
			<li class="active"><a
				href="${pageContext.request.contextPath}/module/radiology/radiologyReport
				.form?radiologyReportId=${radiologyReport.id}">
					Radiology Report </a></li>
		</c:if>
	</openmrs:hasPrivilege>

</ul>
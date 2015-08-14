<%@ include file="/WEB-INF/template/include.jsp"%>

<openmrs:require privilege="View Orders" otherwise="/login.htm"
	redirect="/module/radiology/radiologyOrderList.jsp" />

<%@ include file="/WEB-INF/template/header.jsp"%>
<%@ include file="./localHeader.jsp"%>

<openmrs:htmlInclude file="/scripts/calendar/calendar.js" />
<%--<openmrs:htmlInclude file="/scripts/jquery/dataTables/css/dataTables.css" />--%>
<openmrs:htmlInclude
	file="/scripts/jquery-ui/js/jquery-ui-1.7.2.custom.min.js" />
<openmrs:htmlInclude
	file="/scripts/jquery-ui/css/redmond/jquery-ui-1.7.2.custom.css" />

<openmrs:htmlInclude file="/moduleResources/radiology/radiology.css" />
<openmrs:htmlInclude
	file="/moduleResources/radiology/js/jquery.dataTables.min.js" />
<%@ include
	file="/WEB-INF/view/module/radiology/resources/js/orderList.js"%>
<openmrs:htmlInclude file="/moduleResources/radiology/js/sortNumbers.js" />
<openmrs:htmlInclude file="/moduleResources/radiology/css/ColVis.css" />
<openmrs:htmlInclude file="/moduleResources/radiology/js/ColVis.min.js" />

<div id="calendar"></div>
<div id="voidReasonPopup">
	<label><spring:message code="general.voidReason" />:</label><br /> <input
		name="voidReason" type="text" style="width: 20em" /><br /> <input
		id="voidOrderButton" type="button"
		value='<spring:message code="general.void"/>' />
</div>

<h2>
	<spring:message code="radiology.manageOrders" />
</h2>
<div id="openmrs_msg" name="loading">
	<spring:message code="general.loading" />
</div>
<openmrs:hasPrivilege privilege="Add Orders">
	<a href="radiologyOrder.form"><spring:message
			code="radiology.addOrder" /></a>
	<br />
</openmrs:hasPrivilege>
<br />
<span class="boxHeader"> <b><spring:message
			code="radiology.worklist" /></b> <a id="clearResults" href="#"
	style="float: right"> <spring:message code="radiology.clearResults" />
</a>
</span>
<div class="box">
	<table id="searchForm" cellspacing="10">
		<tr>
			<td><label><spring:message code="radiology.patient" />:</label>
				<input name="patientQuery" type="text" style="width: 20em"
				title="<spring:message
						code="radiology.minChars" />" /></td>
			<td><label><spring:message code="radiology.startDate" />:</label>
				<input name="startDate" type="text" onclick="showCalendar(this)" /></td>
			<td><label><spring:message code="radiology.endDate" />:</label>
				<input name="endDate" type="text" onclick="showCalendar(this)" /></td>
		</tr>
		<tr>
			<td><input id="findButton" type="button"
				value="<spring:message code="radiology.find"/>" /></td>
			<td id="errorSpan"></td>
		</tr>
	</table>
	<div id="results"></div>

</div>
<br />
<c:if test="${not empty initialized}">
	<div id="init">
		<spring:message code="radiology.badInit" />
		<br />
		<spring:message code="radiology.goto" />
		<a href="config.list"><spring:message code="radiology.init" /></a>
	</div>
	<script type="text/javascript">
		$j('#init')
				.dialog(
						{
							autoOpen : true,
							modal : true,
							position : 'center',
							title : '<spring:message code="radiology.init" javaScriptEscape="true"/>',
							width : '50%'
						});
	</script>
</c:if>
<%@ include file="/WEB-INF/template/footer.jsp"%>
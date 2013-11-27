<%@ include file="/WEB-INF/template/include.jsp"%>
<div class="boxHeader" id="dashboardRadiologyObs">
    Radiology Observations
</div>
<div class="box">
    <table>                   
                <tr>
                    <th>  <spring:message code="Obs.person" /> </th> <td>: ${personName}</td>                                        
                </tr>
                <tr>
                    <th> <spring:message code="Obs.order" /></th> <td>: ${orderId}</td>                                        
                </tr>
                <c:if test="${not empty studyUID}">
                    <tr>
                        <th><spring:message code="radiology.studyResults" /> </th>
                        <%--<td><a href="/openmrs/moduleServlet/radiology/viewer.jnlp?studyUID=${studyUID}"><spring:message code="general.download" /></a></td>--%>
                        <td>:  <a href="${oviyamLink}" target="_tab">View Study</a></td>
                    </tr>
                </c:if> 
    </table>
                    
    <c:if test="${not empty studyUID}">
       <p><a href="module/radiology/radiologyObs.form?orderId=${obs.order.orderId }&obsId">Add/Edit Observations</a></p> 
    </c:if>
    
    <c:if test="${empty prevs}">
        <p>No Radiology Observations Present.</p>
    </c:if>                    
    <c:if test="${not empty prevs}">        	
	<h2>
		<spring:message code="radiology.previousObs" />
	</h2>		
	<table id="prevs">
		<tr class="boxHeader" style="display: table-row;">
			<th><spring:message code="general.id" /></th>
			<th><spring:message code="radiology.readingPhysician" /></th>
			<th><spring:message code="Obs.location" /></th>
			<th><spring:message code="Obs.datetime" /></th>
			<th><spring:message code="Obs.concept" /></th>
			<th><spring:message code="general.value" /></th>
			<th><spring:message code="Obs.comment" /></th>
		</tr>
		<c:forEach items="${prevs}" begin="0" end="${prevsSize}" var="obs"
			varStatus="status">
			<tr>
				<td>${obs.obsId }</td>
				<td>${obs.creator.personName }</td>
				<td>${obs.location }</td>
				<td>${obs.obsDatetime}</td>
				<td>${obs.concept.name.name }</td>
				<td><a
					href="module/radiology/portlets/radiologyObsDetailsDashboard.form?orderId=${obs.order.orderId }&obsId=${obs.obsId }" class="obsList"><spring:message
							code="general.view" /></a></td>
				<td>${obs.comment}</td>
			</tr>
		</c:forEach>
	</table>
    </c:if>
    <div id="obsDetails"></div>                    
</div>

<script type="text/javascript">
    var $j=jQuery.noConflict();                
	$j(document).ready(function() {
//		$j('#obsDetails').dialog({
//			autoOpen: false,
//			modal: true,
//			position: top,
//			title: '<spring:message code="radiology.radiologyObservations" javaScriptEscape="true"/>',
//			width: '90%'
//		});
                $j('.obsList').click(function(e) {
                         e.preventDefault();
                         var url=$j(this).attr('href');                            
                         $j('#obsDetails').load(url);
		//	 $j('#obsDetails').dialog('open');                                                 
		});
  });  
</script>    




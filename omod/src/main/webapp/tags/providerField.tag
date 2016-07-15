<%@ include file="/WEB-INF/template/include.jsp"%>

<%@ attribute name="formFieldName" required="true"%>
<%@ attribute name="formFieldId" required="false"%>

<openmrs:htmlInclude file="/scripts/jquery/autocomplete/jquery.ui.autocomplete.autoSelect.js" />

<c:if test="${empty formFieldId}">
	<c:set var="formFieldId" value="${formFieldName}_id" />
</c:if>
<c:set var="displayNameInputId" value="${formFieldId}_selection" />

<script type="text/javascript">
	
	$j(document).ready( function() {
	    $j("#${displayNameInputId}").autocomplete({
			minLength: 2,
			source: function(request,response) {
				$j.ajax({
					headers: {
					Accept: "application/json; charset=utf-8",
						"Content-Type": "text/plain; charset=utf-8",
					},
					cache: true,
					dataType: "json",
					url: "${pageContext.request.contextPath}/ws/rest/v1/provider/",
					data: {
						q: request.term
					},
					success: function (data){
						var datalist=[];
						$j.each(data.results, function (index, value) {
							datalist.push({label: value.display, value: value.uuid});
						});
						response(datalist);
					}
				});
			},
	      	select: function(event, ui) {
	      	    event.preventDefault();
	      	    $j("#${formFieldId}").val(ui.item.value);
	      	    $j("#${displayNameInputId}").val(ui.item.label);
	      	}
	    });
	    
		//Clear hidden value on losing focus with no valid entry
		$j("#${displayNameInputId}").autocomplete().blur(function(event, ui) {
			if (!event.target.value) {
				$j('#${formFieldId}').val('');
			}
		});
	    
	    
	    $j("#${displayNameInputId}").tooltip();
		
	})
</script>

<input type="text" id="${displayNameInputId}" title="<spring:message code="radiology.dashboard.tabs.reports.filters.principalResultsInterpreter.title" />"/>
<input type="hidden" name="${formFieldName}" id="${formFieldId}" />
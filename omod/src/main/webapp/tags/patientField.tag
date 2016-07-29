<%@ include file="/WEB-INF/template/include.jsp"%>

<%@ attribute name="formFieldName" required="true"%>
<%@ attribute name="formFieldId" required="false"%>
<%@ attribute name="initialValue" required="false"%>
<%-- This should be a uuid --%>
<%@ attribute name="callback" required="false"%>
<%-- gets the relType, PatientListItem sent back --%>

<openmrs:htmlInclude file="/dwr/interface/DWRPatientService.js" />
<openmrs:htmlInclude file="/scripts/jquery/autocomplete/OpenmrsAutoComplete.js" />
<openmrs:htmlInclude file="/scripts/jquery/autocomplete/jquery.ui.autocomplete.autoSelect.js" />

<c:if test="${empty formFieldId}">
  <c:set var="formFieldId" value="${formFieldName}_id" />
</c:if>
<c:set var="displayNameInputId" value="${formFieldId}_selection" />

<script type="text/javascript">
	
	jQuery(document).ready( function() {

		// set up the autocomplete
		new AutoComplete("${displayNameInputId}", new CreateCallback().patientCallback(), {
			select: function(event, ui) {
				jquerySelectEscaped("${formFieldId}").val(ui.item.object.uuid);
					
				<c:if test="${not empty callback}">
				if (ui.item.object) {
					// only call the callback if we got a true selection, not a click on an error field
					${callback}("${formFieldName}", ui.item.object, false);
				}
				</c:if>
			}
		});

		//Clear hidden value on losing focus with no valid entry
		jQuery("#${displayNameInputId}").autocomplete().blur(function(event, ui) {
			if (!event.target.value) {
				jquerySelectEscaped('${formFieldId}').val('');
			}
		});
		
		// get the name of the person that they passed in the id for
		<c:if test="${not empty initialValue}">
			jquerySelectEscaped("${formFieldId}").val("${initialValue}");
			DWRPatientService.getPatient("${initialValue}", function(patient) {
				jquerySelectEscaped("${displayNameInputId}").val(patient.personName);
				jquerySelectEscaped("${displayNameInputId}").autocomplete("option", "initialValue", patient.personName);
				<c:if test="${not empty callback}">
					${callback}("${formFieldName}", patient, true);
				</c:if>
			});
		</c:if>
		
	})
</script>

<input type="text" id="${displayNameInputId}" placeholder='<openmrs:message code="Patient.searchBox.placeholder"/>' style="width: 15em"/>
<input type="hidden" name="${formFieldName}" id="${formFieldId}" />

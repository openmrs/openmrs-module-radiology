<%@ include file="/WEB-INF/template/include.jsp"%>
<c:set var="DO_NOT_INCLUDE_JQUERY" value="true" />
<%@ include file="/WEB-INF/template/header.jsp"%>
<c:set var="INCLUDE_TIME_ADJUSTMENT" value="true" />
<%@ include file="/WEB-INF/view/module/radiology/template/includeScripts.jsp"%>

<openmrs:htmlInclude file="/moduleResources/radiology/vendor/jquery-date-range-picker/daterangepicker.min.css" />
<openmrs:htmlInclude file="/moduleResources/radiology/vendor/jquery-date-range-picker/jquery.daterangepicker.min.js" />

<openmrs:require
  allPrivileges="Add Encounters,Add Orders,Add Radiology Orders,Add Visits,Edit Encounters,Edit Visits,Get Care Settings,Get Concepts,Get Encounter Roles,Get Encounters,Get Orders,Get Patients,Get Providers,Get Radiology Orders,Get Users,Get Visit Attribute Types,Get Visit Types,Get Visits,View Orders"
  otherwise="/login.htm" redirect="/module/radiology/radiologyOrder.form" />

<!--  This form is for creating new RadiologyOrders -->

<script type="text/javascript">
  var $j = jQuery.noConflict();

  function onQuestionSelect(concept) {
    $j("#conceptDescription").html(concept.description);
  }

  function onReasonSelect(concept) {
    $j("#reasonDescription").html(concept.description);
  }
  
  $j(document).ready(
          function() {
            var scheduledDate = $j("#scheduledDateId");
            var scheduledDateInput = $j("#scheduledDateInputId");
            var scheduledDateErrorSpan = $j("#scheduledDateErrorSpan");
            var urgencySelect = $j("#urgencySelect");
            
            scheduledDateInput.dateRangePicker({
              startOfWeek: "monday",
              customTopBar: '<b style="font-size: 0.9em" class="start-day">...</b>',
              format: 'L LT',
              singleDate: true,
              singleMonth: true,
              time: {
                enabled: true
              }
            }).bind(
                    'datepicker-change',
                    function(event, obj) {
                      setUtcScheduledDate();
                    });
            
            scheduledDateInput.change(function() {
              setUtcScheduledDate();
            });
            
            var setUtcScheduledDate = function() {
              if ($j.trim(scheduledDateInput.val())) {
                scheduledDate.val(moment(scheduledDateInput.val(),
                        "L LT").utc().format("L LT"));
              } else {
                scheduledDate.val("");
              }
            }
            
            if (urgencySelect.val() === "ON_SCHEDULED_DATE") {
              scheduledDateInput.show();
              if ($j.trim(scheduledDate.val())){
                scheduledDateInput.val(moment.utc(scheduledDate.val(), "L LT").local()
                                          .format("L LT"));
              }
            }
           
            var showOrHideScheduledDate = function() {
              if (urgencySelect.val() === "ON_SCHEDULED_DATE") {
                scheduledDateInput.show();
              } else {
                scheduledDateInput.hide();
                scheduledDateErrorSpan.hide();
                scheduledDateInput.val("");
              }
            }
  
            urgencySelect.change(function() {
              showOrHideScheduledDate();
            });

            $j("#radiologyOrder input:visible:enabled:first").focus();
          });
</script>

<c:if test="${not empty param.patientId}">
  <openmrs:hasPrivilege privilege="View Patients">
    <openmrs:portlet url="patientHeader" id="patientDashboardHeader" patientId="${param.patientId}" parameters="showPatientDashboardLink=true" />
    <br>
  </openmrs:hasPrivilege>
</c:if>

<spring:hasBindErrors name="radiologyOrder">
  <div class="error">
    <spring:message code="fix.error" />
  </div>
  <br />
</spring:hasBindErrors>
<spring:hasBindErrors name="study">
  <div class="error">
    <spring:message code="fix.error" />
  </div>
  <br />
</spring:hasBindErrors>

<div>
  <span class="boxHeader"> <b><spring:message code="radiology.addOrder" /></b>
  </span>
  <form:form id="radiologyOrder" method="post" modelAttribute="radiologyOrder" cssClass="box">
    <table>
      <tr>
        <td><spring:message code="Order.patient" /><span class="required">*</span></td>
        <td><spring:bind path="patient">
            <c:choose>
              <c:when test="${not empty param.patientId}">
                <input type="text" value="${status.editor.value.personName}" disabled />
                <input type="hidden" name="${status.expression}" value="${status.value}" />
              </c:when>
              <c:otherwise>
                <openmrs:fieldGen type="org.openmrs.Patient" formFieldName="${status.expression}"
                  val="${status.editor.value}" />
              </c:otherwise>
            </c:choose>
          </spring:bind> <form:errors path="patient" cssClass="error" /></td>
      </tr>
      <tr>
        <td><spring:message code="radiology.imagingProcedure" /><span class="required">*</span></td>
        <td><spring:bind path="concept">
            <openmrs_tag:conceptField formFieldName="concept" formFieldId="conceptId"
              initialValue="${status.editor.value.conceptId}" onSelectFunction="onQuestionSelect"
              includeClasses="${radiologyConceptClassNames}" />
          </spring:bind> <form:errors path="concept" cssClass="error" />
          <div class="description" id="conceptDescription"></div></td>
      </tr>
      <tr>
        <td><spring:message code="radiology.radiologyOrder.orderReason" /></td>
        <td><spring:bind path="orderReason">
            <openmrs_tag:conceptField formFieldName="orderReason" formFieldId="orderReasonId"
              initialValue="${status.editor.value.conceptId}" onSelectFunction="onReasonSelect" 
              includeClasses="${radiologyOrderReasonConceptClassNames}" />
          </spring:bind> <form:errors path="orderReason" cssClass="error" />
          <div class="description" id="reasonDescription"></div></td>
      </tr>
      <tr>
        <td><spring:message code="radiology.radiologyOrder.orderReasonNonCoded" /></td>
        <td><form:textarea path="orderReasonNonCoded" id="orderReasonNonCodedId" /> <form:errors
            path="orderReasonNonCoded" cssClass="error" /></td>
      </tr>
      <tr>
        <td><spring:message code="radiology.radiologyOrder.clinicalHistory" /></td>
        <td><form:textarea path="clinicalHistory" id="clinicalHistoryId" /> <form:errors path="clinicalHistory"
            cssClass="error" /></td>
      </tr>
      <tr>
        <td><spring:message code="radiology.urgency" /><span class="required">*</span></td>
        <td><spring:bind path="urgency">
            <select name="${status.expression}" id="urgencySelect">
              <c:forEach var="urgency" items="${urgencies}">
                <option value="${urgency}" ${status.value == urgency ? 'selected="selected"' : ''}><spring:message
                    code="radiology.order.urgency.${urgency}" text="${urgency}" /></option>
              </c:forEach>
            </select>
          </spring:bind> <spring:bind path="scheduledDate">
            <input name="${status.expression}" id="${status.expression}Id" type="hidden" value="${status.value}">
            <input name="${status.expression}Input" id="${status.expression}InputId" type="text"
              style="display: none; width: 150px;" value=""
              placeholder="<spring:message code="radiology.order.creation.scheduledDate.placeholder"/>">
          </spring:bind> <form:errors path="scheduledDate" cssClass="error" /></td>
      </tr>
      <tr>
        <td><spring:message code="radiology.performedStatus" /></td>
        <td><spring:bind path="study.performedStatus">
            <select name="${status.expression}" id="performedStatusSelect">
              <c:forEach var="performedStatus" items="${performedStatuses}">
                <option value="${performedStatus.key}" ${status.value == performedStatus.key ? 'selected="selected"' : ''}><spring:message
                    code="radiology.${performedStatus.key}" text="${performedStatus.value}" /></option>
              </c:forEach>
            </select>
          </spring:bind> <form:errors path="study.performedStatus" cssClass="error" /></td>
      </tr>
      <tr>
        <td><spring:message code="general.instructions" /></td>
        <td><form:textarea path="instructions" id="instructionsId" /> <form:errors path="instructions" cssClass="error" /></td>
      </tr>
      <tr>
        <td><spring:message code="Order.orderer" /><span class="required">*</span></td>
        <td><spring:bind path="orderer">
            <openmrs:fieldGen type="org.openmrs.Provider" formFieldName="${status.expression}" val="${status.editor.value}" />
          </spring:bind> <form:errors path="orderer" cssClass="error" /></td>
      </tr>
      <tr>
        <td></td>
        <td><input type="submit" name="saveRadiologyOrder" value="<spring:message code="Order.save"/>"></td>
      </tr>
    </table>
  </form:form>
</div>
<%@ include file="/WEB-INF/template/footer.jsp"%>

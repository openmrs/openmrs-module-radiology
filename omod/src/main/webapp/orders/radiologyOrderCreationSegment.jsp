<openmrs:htmlInclude file="/scripts/calendar/calendar.js" />
<openmrs:htmlInclude file="/scripts/timepicker/timepicker.js" />
<script type="text/javascript">
  function onQuestionSelect(concept) {
    $j("#conceptDescription").show();
    $j("#conceptDescription").html(concept.description);
  }
</script>
<script type="text/javascript">
  var $j = jQuery.noConflict();
  $j(document).ready(function() {
    var scheduledDate = $j("#scheduledDateId");
    var scheduledDateErrorSpan = $j("#scheduledDateErrorSpan");
    var urgencySelect = $j("#urgencySelect");

    var showOrHideScheduledDate = function() {
      if (urgencySelect.val() === "ON_SCHEDULED_DATE") {
        scheduledDate.show();
        scheduledDate.click();
      } else {
        scheduledDate.hide();
        scheduledDateErrorSpan.hide();
        scheduledDate.val("");
      }
    }

    showOrHideScheduledDate();

    urgencySelect.on("change", function() {
      showOrHideScheduledDate();
    });
  });
</script>

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
  <form:form method="post" modelAttribute="radiologyOrder" cssClass="box">
    <table>
      <tr>
        <td><spring:message code="Order.patient" /></td>
        <td><spring:bind path="patient">
            <openmrs:fieldGen type="org.openmrs.Patient" formFieldName="${status.expression}" val="${status.editor.value}" />
            <c:if test="${status.errorMessage != ''}">
              <span class="error">${status.errorMessage}</span>
            </c:if>
          </spring:bind></td>
      </tr>
      <tr>
        <td><spring:message code="radiology.imagingProcedure" /></td>
        <td><spring:bind path="concept">
            <openmrs_tag:conceptField formFieldName="concept" formFieldId="conceptId"
              initialValue="${status.editor.value.conceptId}" onSelectFunction="onQuestionSelect"
              includeClasses="${radiologyConceptClassNames}" />
            <c:if test="${status.errorMessage != ''}">
              <span class="error">${status.errorMessage}</span>
            </c:if>
            <div class="description" id="conceptDescription"></div>
          </spring:bind></td>
      </tr>
      <tr>
        <td><spring:message code="radiology.radiologyOrder.orderReason" /></td>
        <td><spring:bind path="orderReason">
            <openmrs_tag:conceptField formFieldName="orderReason" formFieldId="orderReasonId"
              initialValue="${status.editor.value.conceptId}" />
            <c:if test="${status.errorMessage != ''}">
              <span class="error">${status.errorMessage}</span>
            </c:if>
          </spring:bind></td>
      </tr>
      <tr>
        <td><spring:message code="radiology.radiologyOrder.orderReasonNonCoded" /></td>
        <td><spring:bind path="orderReasonNonCoded">
            <textarea name="${status.expression}">${status.value}</textarea>
            <c:if test="${status.errorMessage != ''}">
              <span class="error">${status.errorMessage}</span>
            </c:if>
          </spring:bind></td>
      </tr>
      <tr>
        <td><spring:message code="radiology.radiologyOrder.clinicalHistory" /></td>
        <td><spring:bind path="clinicalHistory">
            <textarea name="${status.expression}">${status.value}</textarea>
            <c:if test="${not empty status.errorMessage}">
              <span class="error">${status.errorMessage}</span>
            </c:if>
          </spring:bind></td>
      </tr>
      <tr>
        <td><spring:message code="radiology.urgency" /></td>
        <td><spring:bind path="urgency">
            <select name="${status.expression}" id="urgencySelect">
              <c:forEach var="urgency" items="${urgencies}">
                <option value="${urgency}" ${status.value == urgency ? 'selected="selected"' : ''}><spring:message
                    code="radiology.order.urgency.${urgency}" text="${urgency}" /></option>
              </c:forEach>
            </select>
          </spring:bind> <spring:bind path="scheduledDate">
            <input name="${status.expression}" id="${status.expression}Id" type="text" style="display: none;"
              onclick="showDateTimePicker(this)" value="${status.value}">
            <c:if test="${status.errorMessage != ''}">
              <span id="scheduledDateErrorSpan" class="error">${status.errorMessage}</span>
            </c:if>
          </spring:bind></td>
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
            <c:if test="${status.errorMessage != ''}">
              <span class="error">${status.errorMessage}</span>
            </c:if>
          </spring:bind></td>
      </tr>
      <tr>
        <td><spring:message code="general.instructions" /></td>
        <td><spring:bind path="instructions">
            <textarea name="${status.expression}">${status.value}</textarea>
            <c:if test="${status.errorMessage != ''}">
              <span class="error">${status.errorMessage}</span>
            </c:if>
          </spring:bind></td>
      </tr>
      <tr>
        <td><spring:message code="Order.orderer" /></td>
        <td><spring:bind path="orderer">
            <openmrs:fieldGen type="org.openmrs.Provider" formFieldName="${status.expression}" val="${status.editor.value}" />
            <c:if test="${status.errorMessage != ''}">
              <span class="error">${status.errorMessage}</span>
            </c:if>
          </spring:bind></td>
      </tr>
    </table>
    <br />
    <input type="submit" name="saveRadiologyOrder" value="<spring:message code="Order.save"/>">
  </form:form>
</div>

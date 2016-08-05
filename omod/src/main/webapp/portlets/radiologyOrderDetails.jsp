<%@ include file="/WEB-INF/template/include.jsp"%>

<c:set var="rOrder" value="${model.radiologyOrder}" />
<c:if test="${not empty rOrder}">
    <table>
      <tr>
        <td><spring:message code="radiology.radiologyOrder.accessionNumber" /></td>
        <td>${rOrder.accessionNumber}</td>
      </tr>
      <tr>
        <td><spring:message code="radiology.imagingProcedure" /></td>
        <td>${rOrder.concept.name.name}</td>
      </tr>
      <tr>
        <td><spring:message code="radiology.radiologyOrder.orderReason" /></td>
        <td><c:if test="${not empty rOrder.orderReason}">${rOrder.orderReason.name.name}</c:if></td>
      </tr>
      <tr>
        <td><spring:message code="radiology.radiologyOrder.orderReasonNonCoded" /></td>
        <td>${rOrder.orderReasonNonCoded}</td>
      </tr>
      <tr>
        <td><spring:message code="radiology.radiologyOrder.clinicalHistory" /></td>
        <td>${rOrder.clinicalHistory}</td>
      </tr>
      <tr>
        <td><spring:message code="Order.orderer" /></td>
        <td> ${rOrder.orderer.name}</td>
      </tr>
      <tr>
        <td><spring:message code="radiology.urgency" /></td>
        <td><spring:message code="radiology.order.urgency.${rOrder.urgency}" text="${rOrder.urgency}" /></td>
      </tr>
      <tr>
        <td><spring:message code="radiology.scheduledDate" /></td>
        <td class="datetime">${rOrder.effectiveStartDate}</td>
      </tr>
      <tr>
        <td><spring:message code="radiology.stopDate" /></td>
        <td class="datetime">${rOrder.effectiveStopDate}</td>
      </tr>
      <tr>
        <td><spring:message code="radiology.performedStatus" /></td>
        <td><spring:message code="radiology.${rOrder.study.performedStatus}" text="${rOrder.study.performedStatus}" />
        </td>
      </tr>
      <tr>
        <td><spring:message code="general.instructions" /></td>
        <td>${rOrder.instructions}</td>
      </tr>
      <tr>
        <td><spring:message code="general.createdBy" /></td>
        <td>${rOrder.creator.personName}-<span class="datetime"> ${rOrder.dateCreated} </span></td>
      </tr>
      <openmrs:hasPrivilege privilege="Get Radiology Studies">
        <c:if test="${not empty model.dicomViewerUrl}">
          <tr>
            <td><spring:message code="radiology.studyResults" /></td>
            <td><a href="${model.dicomViewerUrl}" target="_tab">View Study</a></td>
          </tr>
        </c:if>
      </openmrs:hasPrivilege>
    </table>
</c:if>
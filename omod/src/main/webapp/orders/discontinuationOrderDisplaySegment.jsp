<div>
  <span class="boxHeader"> <b><spring:message code="Order.title" /></b>
  </span>
  <form:form method="post" modelAttribute="order" cssClass="box">
    <table>
      <tr>
        <td><spring:message code="general.id" /></td>
        <td><spring:bind path="orderId">${status.value}</spring:bind></td>
      </tr>
      <tr>
        <td><spring:message code="Order.patient" /></td>
        <td><spring:bind path="patient.personName.fullName">${status.value}</spring:bind>
        </a></td>
      </tr>
      <tr>
        <td><spring:message code="radiology.imagingProcedure" /></td>
        <td><spring:bind path="concept.name.name">${status.value}</spring:bind></td>
      </tr>
      <tr>
        <td><spring:message code="radiology.radiologyOrder.orderReason" /></td>
        <td><spring:bind path="concept.name.name">${status.value}</spring:bind></td>
      </tr>
      <tr>
        <td><spring:message code="radiology.radiologyOrder.clinicalHistory" /></td>
        <td><spring:bind path="clinicalHistory">${status.value}</spring:bind></td>
      </tr>
      <tr>
        <td><spring:message code="radiology.radiologyOrder.orderReasonNonCoded" /></td>
        <td><spring:bind path="orderReasonNonCoded">${status.value}</spring:bind></td>
      </tr>
      <tr>
        <td><spring:message code="Order.orderer" /></td>
        <td><spring:bind path="orderer.name">${status.value}</spring:bind></td>
      </tr>
      <tr>
        <td><spring:message code="general.dateDiscontinued" /></td>
        <td class="datetime"><spring:bind path="dateActivated">${status.value}</spring:bind></td>
      </tr>
      <tr>
        <td><spring:message code="general.discontinuedReason" /></td>
        <td><spring:bind path="orderReasonNonCoded">${status.value}</spring:bind></td>
      </tr>
      <tr>
        <td><spring:message code="radiology.discontinuedOrder" /></td>
        <td><spring:bind path="previousOrder">
            <a href="radiologyOrder.form?orderId=${status.value}">${status.value}</a>
          </spring:bind></td>
      </tr>
      <tr>
        <td><spring:message code="general.createdBy" /></td>
        <td><spring:bind path="creator.personName">${status.value}</spring:bind> - <span class="datetime"><spring:bind
              path="dateCreated">${status.value}</spring:bind></span></td>
      </tr>
    </table>
  </form:form>
</div>

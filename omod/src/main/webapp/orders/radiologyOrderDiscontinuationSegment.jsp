<br />

<form:form method="post" modelAttribute="discontinuationOrderRequest" cssClass="box">
  <table>
    <tr>
      <td><spring:message code="Order.orderer" /><span class="required">*</span></td>
      <td><spring:bind path="orderer">
          <openmrs:fieldGen type="org.openmrs.Provider" formFieldName="${status.expression}" val="${status.editor.value}" />
        </spring:bind> <form:errors path="orderer" cssClass="error" /></td>
    </tr>
    <tr>
      <td><spring:message code="general.discontinuedReason" /><span class="required">*</span></td>
      <td><spring:bind path="reasonNonCoded">
          <textarea name="${status.expression}">${status.value}</textarea>
        </spring:bind> <form:errors path="reasonNonCoded" cssClass="error" /></td>
    </tr>
  </table>
  <input type="submit" name="discontinueOrder" value='<spring:message code="Order.discontinueOrder"/>' />
</form:form>

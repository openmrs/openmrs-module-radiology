<br />

<form:form method="post" modelAttribute="discontinuationOrder"
	cssClass="box">
	<table>
		<tr>
			<td><spring:message code="Order.orderer" /></td>
			<td><spring:bind path="orderer">
					<openmrs:fieldGen type="org.openmrs.Provider"
						formFieldName="${status.expression}" val="${status.editor.value}" />
					<c:if test="${status.errorMessage != ''}">
						<span class="error">${status.errorMessage}</span>
					</c:if>
				</spring:bind></td>
		</tr>
		<tr>
			<form:hidden path="dateActivated" />
		</tr>
		<tr>
			<td><spring:message code="general.discontinuedReason" /></td>
			<td><spring:bind path="orderReasonNonCoded">
					<textarea name="${status.expression}">${status.value}</textarea>
					<c:if test="${status.errorMessage != ''}">
						<span class="error">${status.errorMessage}</span>
					</c:if>
				</spring:bind></td>
		</tr>
	</table>
	<input type="submit" name="discontinueOrder"
		value='<spring:message code="Order.discontinueOrder"/>' />
</form:form>

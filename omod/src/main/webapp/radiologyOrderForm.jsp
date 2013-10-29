    <%@ include file="/WEB-INF/template/include.jsp"%>

<openmrs:require privilege="Manage Orders" otherwise="/login.htm"
	redirect="/module/radiology/radiologyOrder.form" />

<%@ include file="/WEB-INF/template/header.jsp"%>
<%@ include file="localHeader.jsp"%>
<openmrs:htmlInclude file="/scripts/calendar/calendar.js" />
<%@ include
	file="/WEB-INF/view/module/radiology/resources/js/moreInfo.js"%>

<h2>
	<spring:message code="Order.title" />
</h2>

<spring:hasBindErrors name="order">
	<spring:message code="fix.error" />
	<br />
</spring:hasBindErrors>
<spring:hasBindErrors name="study">
	<spring:message code="fix.error" />
	<br />
</spring:hasBindErrors>

<c:if test="${order.voided}">
	<form method="post">
		<div class="retiredMessage">
			<div>
				<spring:message code="general.voidedBy" />
				${order.voidedBy.personName}
				<openmrs:formatDate date="${order.dateVoided}" type="medium" />
				- ${order.voidReason} <input type="submit" name="unvoidOrder"
					value='<spring:message code="Order.unvoidOrder"/>' />
			</div>
		</div>
	</form>
</c:if>
<form method="post" class="box">       
	<input type="hidden" name="study_id" value="${study.id }" /> 
         <spring:bind path="study.mwlStatus">
                <input type="hidden" name="${status.expression}" value="${status.value}" />
                <c:if test="${status.errorMessage != ''}">
				<span class="error">${status.errorMessage}</span>
                </c:if>
         </spring:bind>
            <%--  <spring:bind path="study.id">--%>
                <%--<div><p>Value : ${status.expression }</p></div>
                <div><p>Value : ${status.value }</p></div>
            </spring:bind>--%>
	<table>
		<tr>
			<td valign="top"><spring:message code="Order.patient" /></td>
			<td valign="top"><spring:bind path="order.patient">
					<c:choose>
						<c:when test="${!referring && !super}">
							<input type="hidden" name="${status.expression }"
								value="${status.editor.value.id }" />
							<input readonly="readonly" value="${order.patient.personName}" />
						</c:when>
						<c:otherwise>

							<openmrs:fieldGen type="org.openmrs.Patient"
								formFieldName="${status.expression}"
								val="${status.editor.value}" />
								<a style="cursor:pointer;" id="moreInfo"><spring:message code="radiology.moreInfo" /></a>
							<c:if test="${status.errorMessage != ''}">
								<span class="error">${status.errorMessage}</span>
							</c:if>

						</c:otherwise>
					</c:choose>
				</spring:bind></td>
		</tr>
		<tr>
			<td valign="top"><spring:message code="Order.concept" /></td>
			<td valign="top"><spring:bind path="order.concept">
					<c:choose>
						<c:when test="${!referring && !super}">
							<input type="hidden" name="${status.expression }"
								value="${status.editor.value.id }" />
							<input readonly="readonly" value="${order.concept.name.name}" />
						</c:when>
						<c:otherwise>

							<openmrs:fieldGen type="org.openmrs.Concept"
								formFieldName="${status.expression}"
								val="${status.editor.value}" />
							<c:if test="${status.errorMessage != ''}">
								<span class="error">${status.errorMessage}</span>
							</c:if>

						</c:otherwise>
					</c:choose>
				</spring:bind></td>
		</tr>
		<tr>
			<td valign="top"><spring:message code="radiology.priority" />
			</td>
			<td valign="top"><spring:bind path="study.priority">
					<c:choose>
						<c:when test="${!referring && !super}">
							<input type="hidden" name="${status.expression }"
								value="${status.value }" />
							<input readonly="readonly" value="${priorities[status.value+1] }" />
						</c:when>
						<c:otherwise>
							<select name="${status.expression}">
								<c:forEach items="${priorities}" begin="0" end="${n_priorities}"
									var="p" varStatus="status1">
									<option value="${status1.count-2}"
										<c:if test="${ status1.count-2 == status.value}">selected</c:if>>
										${p}</option>
								</c:forEach>
							</select>

							<c:if test="${status.errorMessage != ''}">
								<span class="error">${status.errorMessage}</span>
							</c:if>
						</c:otherwise>
					</c:choose>

				</spring:bind></td>
		</tr>
		<tr <c:if test="${!super}">style="display:none"</c:if>>
			<td valign="top"><spring:message
					code="radiology.scheduledStatus" /></td>
			<td valign="top"><spring:bind path="study.scheduledStatus">
					<c:choose>
						<c:when test="${!scheduler && !super}">
							<input type="hidden" name="${status.expression }"
								value="${status.value }" />
							<input readonly="readonly" value="${sStatuses[status.value+1] }" />
						</c:when>
						<c:otherwise>
							<select name="${status.expression}">
								<c:forEach items="${sStatuses}" begin="0" end="${n_sStatuses}"
									var="p" varStatus="status1">
									<option value="${status1.count-2}"
										<c:if test="${ status1.count-2 == status.value}">selected</c:if>>
										${p}</option>
								</c:forEach>
							</select>
						</c:otherwise>
					</c:choose>
					<c:if test="${status.errorMessage != ''}">
						<span class="error">${status.errorMessage}</span>
					</c:if>
				</spring:bind></td>
		</tr>
		<tr <c:if test="${!super}">style="display:none"</c:if>>
			<td valign="top"><spring:message
					code="radiology.performedStatus" /></td>
			<td valign="top"><spring:bind path="study.performedStatus">
					<c:choose>
						<c:when test="${!performing && !super}">
							<input type="hidden" name="${status.expression }"
								value="${status.value }" />
							<input readonly="readonly" value="${pStatuses[status.value+1] }" />
						</c:when>
						<c:otherwise>
							<select name="${status.expression}">
								<c:forEach items="${pStatuses}" begin="0" end="${n_pStatuses}"
									var="p" varStatus="status1">
									<option value="${status1.count-2}"
										<c:if test="${ status1.count-2 == status.value}">selected</c:if>>
										${p}</option>
								</c:forEach>
							</select>
						</c:otherwise>
					</c:choose>
					<c:if test="${status.errorMessage != ''}">
						<span class="error">${status.errorMessage}</span>
					</c:if>
				</spring:bind></td>
		</tr>
		<tr>
			<td valign="top"><spring:message code="radiology.modality" />
			</td>
			<td valign="top"><spring:bind path="study.modality">
					<c:choose>
						<c:when test="${!referring && !super}">
							<input type="hidden" name="${status.expression }"
								value="${status.value }" />
							<input readonly="readonly" value="${modalities[status.value] }" />
						</c:when>
						<c:otherwise>
							<select name="${status.expression}">
								<c:forEach items="${modalities}" begin="0" end="${n_modalities}"
									var="p" varStatus="status1">
									<option value="${status1.count-1}"
										<c:if test="${ status1.count-1 == status.value}">selected</c:if>>
										${p}</option>
								</c:forEach>
							</select>
						</c:otherwise>
					</c:choose>
					<c:if test="${status.errorMessage != ''}">
						<span class="error">${status.errorMessage}</span>
					</c:if>
				</spring:bind></td>
		</tr>
		<tr>
			<td valign="top"><spring:message code="general.instructions" />
			</td>
			<td valign="top"><spring:bind path="order.instructions">
					<textarea name="${status.expression}"
						<c:if test="${!referring && !super }">readonly="readonly"</c:if>
						>${status.value}</textarea>
					<c:if test="${status.errorMessage != ''}">
						<span class="error">${status.errorMessage}</span>
					</c:if>
				</spring:bind></td>
		</tr>
		<tr>
			<td valign="top"><spring:message code="Order.encounter" /></td>
			<td valign="top"><spring:bind path="order.encounter">
					<c:choose>
						<c:when test="${!referring && !super}">
							<input type="hidden" name="${status.expression }"
								value="${status.editor.value.id }" />
							<input readonly="readonly" value="${order.encounter}" />
						</c:when>
						<c:otherwise>

							<openmrs:fieldGen type="org.openmrs.Encounter"
								formFieldName="${status.expression}"
								val="${status.editor.value}" />
							<c:if test="${status.errorMessage != ''}">
								<span class="error">${status.errorMessage}</span>
							</c:if>

						</c:otherwise>
					</c:choose>
				</spring:bind></td>
		</tr>
		<tr>
			<td valign="top"><spring:message code="Order.orderer" /></td>
			<td valign="top"><spring:bind path="order.orderer">
					<c:choose>
						<c:when test="${!referring && !super}">
							<input type="hidden" name="${status.expression }"
								value="${status.editor.value.id }" />
							<input readonly="readonly" value="${order.orderer.personName}" />
						</c:when>
						<c:otherwise>
							<openmrs:fieldGen type="org.openmrs.User"
								formFieldName="${status.expression}"
								val="${status.editor.value}" />

						</c:otherwise>
					</c:choose>
					<c:if test="${status.errorMessage != ''}">
						<span class="error">${status.errorMessage}</span>
					</c:if>
				</spring:bind></td>
		</tr>
		<c:if test="${scheduler || super}">
			<tr>
				<td valign="top"><spring:message code="general.dateStart" /></td>
				<td valign="top"><spring:bind path="order.startDate">
						<openmrs:fieldGen type="java.util.Date"
							formFieldName="${status.expression}" val="${status.editor.value}" />
						<c:if test="${status.errorMessage != ''}">
							<span class="error">${status.errorMessage}</span>
						</c:if>
					</spring:bind></td>
			</tr>
			<tr>
				<td valign="top"><spring:message code="general.dateAutoExpire" />
				</td>
				<td valign="top"><spring:bind path="order.autoExpireDate">
						<openmrs:fieldGen type="java.util.Date"
							formFieldName="${status.expression}" val="${status.editor.value}" />
						<c:if test="${status.errorMessage != ''}">
							<span class="error">${status.errorMessage}</span>
						</c:if>
					</spring:bind></td>
			</tr>
		</c:if>
		<tr <c:if test="${!super}">style="display:none"</c:if>>
			<td valign="top"><spring:message
					code="radiology.scheduler" /></td>
			<td valign="top"><spring:bind path="study.scheduler">
					<c:choose>
						<c:when test="${!referring && !super}">
							<input type="hidden" name="${status.expression }"
								value="${status.editor.value.id }" />
							<input readonly="readonly"
								value="${study.scheduler.personName}" />
						</c:when>
						<c:otherwise>
							<openmrs:fieldGen type="org.openmrs.User"
								formFieldName="${status.expression}"
								val="${status.editor.value}" />
						</c:otherwise>
					</c:choose>
					<c:if test="${status.errorMessage != ''}">
						<span class="error">${status.errorMessage}</span>
					</c:if>
				</spring:bind></td>
		</tr>
		<tr <c:if test="${!super}">style="display:none"</c:if>>
			<td valign="top"><spring:message
					code="radiology.performingPhysician" /></td>
			<td valign="top"><spring:bind
					path="study.performingPhysician">
					<c:choose>
						<c:when test="${!referring && !super}">
							<input type="hidden" name="${status.expression }"
								value="${status.editor.value.id }" />
							<input readonly="readonly"
								value="${study.performingPhysician.personName}" />
						</c:when>
						<c:otherwise>
							<openmrs:fieldGen type="org.openmrs.User"
								formFieldName="${status.expression}"
								val="${status.editor.value}" />
						</c:otherwise>
					</c:choose>
					<c:if test="${status.errorMessage != ''}">
						<span class="error">${status.errorMessage}</span>
					</c:if>
				</spring:bind></td>
		</tr>
		<tr <c:if test="${referring}">style="display:none"</c:if>>
			<td valign="top"><spring:message
					code="radiology.readingPhysician" /></td>
			<td valign="top"><spring:bind
					path="study.readingPhysician">
					<c:choose>
						<c:when test="${!scheduler && !super}">
							<input type="hidden" name="${status.expression }"
								value="${status.editor.value.id }" />
							<input readonly="readonly"
								value="${study.readingPhysician.personName}" />
						</c:when>
						<c:otherwise>
							<openmrs:fieldGen type="org.openmrs.User"
								formFieldName="${status.expression}"
								val="${status.editor.value}" />
						</c:otherwise>
					</c:choose>
					<c:if test="${status.errorMessage != ''}">
						<span class="error">${status.errorMessage}</span>
					</c:if>
				</spring:bind></td>
		</tr>
		<c:if test="${order.discontinued}">
			<tr id="discontinuedBy">
				<td valign="top"><spring:message code="general.discontinuedBy" />
				</td>
				<td valign="top">${order.discontinuedBy.personName}</td>
			</tr>
			<tr id="dateDiscontinued">
				<td valign="top"><spring:message
						code="general.dateDiscontinued" /></td>
				<td valign="top"><openmrs:formatDate
						date="${order.discontinuedDate}" type="long" /></td>
			</tr>
			<tr id="discontinuedReason">
				<td valign="top"><spring:message
						code="general.discontinuedReason" /></td>
				<td valign="top">${order.discontinuedReason.name}</td>
			</tr>
		</c:if>
		<c:if test="${order.creator != null}">
			<tr>
				<td><spring:message code="general.createdBy" /></td>
				<td>${order.creator.personName} - <openmrs:formatDate
						date="${order.dateCreated}" type="long" />
				</td>
			</tr>
		</c:if>
	</table>
	<br /> <input type="submit" name="saveOrder"
		value="<spring:message code="Order.save"/>">
</form>


<c:if test="${order.discontinued}">
	<br />
	<form method="post" class="box">
            <input type="hidden" name="study_id" value="${study.id }" />                 
		<input type="submit"
			value='<spring:message code="Order.undiscontinueOrder"/>'
			name="undiscontinueOrder" />
	</form>
</c:if>

<c:if test="${not order.discontinued and not empty order.orderId}">
	<br />
	<form method="post" class="box">
            <input type="hidden" name="study_id" value="${study.id }" />             
		<table>
			<tr id="dateDiscontinued">
				<td valign="top"><spring:message
						code="general.dateDiscontinued" /></td>
				<td valign="top"><spring:bind path="order.discontinuedDate">
						<openmrs:fieldGen type="java.util.Date"
							formFieldName="${status.expression}" val="${status.editor.value}" />
						<c:if test="${status.errorMessage != ''}">
							<span class="error">${status.errorMessage}</span>
						</c:if>
					</spring:bind></td>
			</tr>
			<tr id="discontinuedReason">
				<td valign="top"><spring:message
						code="general.discontinuedReason" /></td>
				<td valign="top"><spring:bind path="order.discontinuedReason">
						<openmrs:fieldGen type="org.openmrs.Concept"
							formFieldName="${status.expression}" val="${status.editor.value}" />
						<c:if test="${status.errorMessage != ''}">
							<span class="error">${status.errorMessage}</span>
						</c:if>
					</spring:bind></td>
			</tr>
		</table>
		<input type="submit" name="discontinueOrder"
			value='<spring:message code="Order.discontinueOrder"/>' />
	</form>
</c:if>

<c:if test="${not order.voided and not empty order.orderId}">
	<br />
	<form method="post" class="box">
            <input type="hidden" name="study_id" value="${study.id }" />       
		<spring:message code="general.voidReason" />
                <spring:bind path="order.voidReason">
		<input type="text" value="${status.value}" size="40" name="${status.expression }" />
		<spring:hasBindErrors name="order">
			<c:forEach items="${errors.allErrors}" var="error">
				<c:if test="${error.code == 'voidReason'}">
					<span class="error"><spring:message
							code="${error.defaultMessage}" text="${error.defaultMessage}" />
					</span>
				</c:if>
			</c:forEach>
		</spring:hasBindErrors>
                </spring:bind>
		<input type="submit" name="voidOrder"
			value='<spring:message code="Order.voidOrder"/>' />
	</form>
</c:if>
        
<div id="moreInfoPopup"></div>
<%@ include file="/WEB-INF/template/footer.jsp"%>
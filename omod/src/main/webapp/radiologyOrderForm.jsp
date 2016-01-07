<%@ include file="/WEB-INF/template/include.jsp" %>

<openmrs:require privilege="Edit Orders" otherwise="/login.htm"
                 redirect="/module/radiology/radiologyOrder.form"/>

<%@ include file="/WEB-INF/template/header.jsp" %>
<%@ include file="localHeader.jsp" %>
<openmrs:htmlInclude file="/scripts/calendar/calendar.js"/>
<%@ include
        file="/WEB-INF/view/module/radiology/resources/js/moreInfo.js" %>
<script type="text/javascript">
    // on concept select:
    function onQuestionSelect(concept) {
        $j("#conceptDescription").show();
        $j("#conceptDescription").html(concept.description);
        updateObsValues(concept);
    }

    // on answer select:
    function onAnswerSelect(concept) {
        $j("#codedDescription").show();
        $j("#codedDescription").html(concept.description);
    }

    function showProposeConceptForm() {
        var qs = "?";
        var encounterId = "${obs.encounter.encounterId}"
                || $j("#encounterId").val();
        if (encounterId != "")
            qs += "&encounterId=" + encounterId;
        var obsConceptId = "${obs.concept.conceptId}" || $j("#conceptId").val();
        if (obsConceptId != "")
            qs += "&obsConceptId=" + obsConceptId;
        document.location = "${pageContext.request.contextPath}/admin/concepts/proposeConcept.form"
                + qs;
    }

    function updateObsValues(tmpConcept) {
        var values = [ 'valueBooleanRow', 'valueCodedRow', 'valueDatetimeRow',
            'valueDateRow', 'valueTimeRow', 'valueModifierRow',
            'valueTextRow', 'valueNumericRow', 'valueInvalidRow',
            'valueComplex' ];
        $j.each(values, function(x, val) {
            $j("#" + val).hide()
        });

        if (tmpConcept != null) {
            var datatype = tmpConcept.hl7Abbreviation;
            if (typeof datatype != 'string')
                datatype = tmpConcept.datatype.hl7Abbreviation;

            if (datatype == 'BIT') {
                $j('#valueBooleanRow').show();
            } else if (datatype == 'NM' || datatype == 'SN') {
                $j('#valueNumericRow').show();
                DWRConceptService.getConceptNumericUnits(tmpConcept.conceptId,
                        fillNumericUnits);
            } else if (datatype == 'CWE') {
                $j('#valueCodedRow').show();

                // clear any old values:
                $j("#valueCoded").val("");
                $j("#valueCoded_selection").val("");
                $j("#codedDescription").html("");

                // set up the autocomplete for the answers
                var conceptId = $j("#conceptId").val();
                new AutoComplete("valueCoded_selection", new CreateCallback({
                    showAnswersFor : conceptId
                }).conceptAnswersCallback(), {
                    'minLength' : '0'
                });
                $j("#valueCoded_selection").autocomplete().focus(
                        function(event, ui) {
                            if (event.target.value == "")
                                $j("#valueCoded_selection").trigger(
                                        'keydown.autocomplete');
                        }); // trigger the drop down on focus

                // something in the autocomplete is setting the focus to the conceptId box after
                // this method is done.  get around this and focus on our answer box by putting
                // a very small delay on the call using setTimeout
                setTimeout("$j('#valueCoded_selection').focus();", 0);
            } else if (datatype == 'ST') {
                $j('#valueTextRow').show();
            } else if (datatype == 'DT') {
                $j('#valueDateRow').show();
            } else if (datatype == 'TS') {
                $j('#valueDatetimeRow').show();
            } else if (datatype == 'TM') {
                $j('#valueTimeRow').show();
            }
            // TODO move datatype 'TM' to own time box.  How to have them select?
            else if (datatype == 'ED') {
                $j('#valueComplex').show();
            } else {
                $j('#valueInvalidRow').show();
                DWRConceptService.getQuestionsForAnswer(tmpConcept.conceptId,
                        fillValueInvalidPossible(tmpConcept));
            }
        }
    }

    function fillNumericUnits(units) {
        $j('#numericUnits').html(units);
    }

    function validateNumericRange(value) {
        if (!isNaN(value) && value != '') {
            var conceptId = $j("#conceptId").val();
            var numericErrorMessage = function(validValue) {
                var errorTag = document.getElementById('numericRangeError');
                errorTag.className = "error";
                if (validValue == false)
                    errorTag.innerHTML = '<openmrs:message code="ConceptNumeric.invalid.msg"/>';
                else
                    errorTag.innerHTML = errorTag.className = "";
            }
            DWRConceptService.isValidNumericValue(value, conceptId,
                    numericErrorMessage);
        }
    }

    function removeHiddenRows() {
        var rows = document.getElementsByTagName("TR");
        var i = 0;
        while (i < rows.length) {
            if (rows[i].style.display == "none")
                rows[i].parentNode.removeChild(rows[i]);
            else
                i = i + 1;
        }
    }

    var fillValueInvalidPossible = function(invalidConcept) {
        return function(questions) {
            var div = document.getElementById('valueInvalidPossibleConcepts');
            div.innerHTML = "";
            var txt = document
                    .createTextNode('<openmrs:message code="Obs.valueInvalid.didYouMean"/> ');
            for (var i = 0; i < questions.length && i < 10; i++) {
                if (i == 0)
                    div.appendChild(txt);
                var concept = questions[i];
                var link = document.createElement("a");
                link.href = "#selectAsQuestion";
                link.onclick = selectNewQuestion(concept, invalidConcept);
                link.title = concept.description;
                link.innerHTML = concept.name;
                if (i == (questions.length - 1) || i == 9)
                    link.innerHTML += "?";
                else
                    link.innerHTML += ", ";
                div.appendChild(link);
            }
        }
    }

    var selectNewQuestion = function(question, answer) {
        return function() {
            var msg = new Object();
            msg.objs = [ question ];
            dojo.event.topic.publish(conceptSearch.eventNames.select, msg);
            msg.objs = [ answer ];
            dojo.event.topic.publish(codedSearch.eventNames.select, msg);
            return false;
        };
    }
</script>

<h2>
    <spring:message code="Order.title"/>
</h2>


<spring:hasBindErrors name="radiologyOrder">
    <spring:message code="fix.error"/>
    <br/>
</spring:hasBindErrors>
<spring:hasBindErrors name="study">
    <spring:message code="fix.error"/>
    <br/>
</spring:hasBindErrors>

<c:choose>
    <c:when
            test="${not empty radiologyOrder && empty radiologyOrder.orderId}">
        <!-- Create a new RadiologyOrder -->
        <form:form method="post" modelAttribute="radiologyOrder"
                   cssClass="box">
            <table>
                <tr>
                    <td><spring:message code="Order.patient"/></td>
                    <td><spring:bind path="patient">
                        <openmrs:fieldGen type="org.openmrs.Patient"
                                          formFieldName="${status.expression}"
                                          val="${status.editor.value}"/>
                        <a style="cursor: pointer;" id="moreInfo"><spring:message
                                code="radiology.moreInfo"/></a>
                        <c:if test="${status.errorMessage != ''}">
                            <span class="error">${status.errorMessage}</span>
                        </c:if>
                    </spring:bind></td>

                </tr>

                <tr>
                    <openmrs:globalProperty key="radiology.radiologyConcepts" var="allowedConcepts"/>
                	<td><spring:message code="Order.concept" /></td>
                    <td><spring:bind path="concept">
                        <openmrs_tag:conceptField formFieldName="concept"
                                                  formFieldId="conceptId"
                                                  excludeDatatypes="N/A"
                                                  initialValue="${status.editor.value.conceptId}"
                                                  onSelectFunction="onQuestionSelect"
                                                  includeClasses="${allowedConcepts}" />
                        <div class="description" id="conceptDescription"></div>
                        <c:if test="${status.errorMessage != ''}">
                            <span class="error">${status.errorMessage}</span>
                        </c:if>
                    </spring:bind></td>
            	</tr>
                <tr>
                    <td><spring:message code="radiology.urgency"/></td>
                    <td><spring:bind path="urgency">
                        <select name="${status.expression}" id="urgencySelect">
                            <c:forEach var="urgency" items="${urgencies}">
                                <option value="${urgency}"
                                    ${status.value == urgency ? 'selected="selected"' : ''}>${urgency}</option>
                            </c:forEach>
                        </select>
                        <c:if test="${status.errorMessage != ''}">
                            <span class="error">${status.errorMessage}</span>
                        </c:if>
                    </spring:bind></td>
                </tr>
                <tr>
                    <td><spring:message code="radiology.scheduledStatus"/></td>
                    <td><spring:bind path="study.scheduledStatus">
                        <select name="${status.expression}"
                                id="scheduledProcedureStepStatusSelect">
                            <c:forEach var="scheduledProcedureStepStatus"
                                       items="${scheduledProcedureStepStatuses}">
                                <option value="${scheduledProcedureStepStatus.key}"
                                    ${status.value == scheduledProcedureStepStatus.key ? 'selected="selected"' : ''}>${scheduledProcedureStepStatus.value}</option>
                            </c:forEach>
                        </select>
                        <c:if test="${status.errorMessage != ''}">
                            <span class="error">${status.errorMessage}</span>
                        </c:if>
                    </spring:bind></td>
                </tr>
                <tr>
                    <td><spring:message code="radiology.performedStatus"/></td>
                    <td><spring:bind path="study.performedStatus">
                        <select name="${status.expression}" id="performedStatusSelect">
                            <c:forEach var="performedStatus" items="${performedStatuses}">
                                <option value="${performedStatus.key}"
                                    ${status.value == performedStatus.key ? 'selected="selected"' : ''}><spring:message
                                        code="radiology.${performedStatus.key}"
                                        text="${performedStatus.value}"/></option>
                            </c:forEach>
                        </select>
                        <c:if test="${status.errorMessage != ''}">
                            <span class="error">${status.errorMessage}</span>
                        </c:if>
                    </spring:bind></td>
                </tr>
                <tr>
                    <td><spring:message code="radiology.modality"/></td>
                    <td><spring:bind path="study.modality">
                        <select name="${status.expression}" id="modalitySelect">
                            <c:forEach var="modality" items="${modalities}">
                                <option value="${modality.key}"
                                    ${status.value == modality.key ? 'selected="selected"' : ''}>${modality.value}</option>
                            </c:forEach>
                        </select>
                        <c:if test="${status.errorMessage != ''}">
                            <span class="error">${status.errorMessage}</span>
                        </c:if>
                    </spring:bind></td>
                </tr>
                <tr>
                    <td><spring:message code="general.instructions"/></td>
                    <td><spring:bind path="instructions">
                        <textarea name="${status.expression}">${status.value}</textarea>
                        <c:if test="${status.errorMessage != ''}">
                            <span class="error">${status.errorMessage}</span>
                        </c:if>
                    </spring:bind></td>
                </tr>
                <tr>
                    <td><spring:message code="Order.orderer"/></td>
                    <td><spring:bind path="orderer">
                        <openmrs:fieldGen type="org.openmrs.Provider"
                                          formFieldName="${status.expression}"
                                          val="${status.editor.value}"/>
                        <c:if test="${status.errorMessage != ''}">
                            <span class="error">${status.errorMessage}</span>
                        </c:if>
                    </spring:bind></td>
                </tr>
                <tr>
                    <td><spring:message code="radiology.scheduledDate"/></td>
                    <td><spring:bind path="scheduledDate">
                        <openmrs:fieldGen type="java.util.Date"
                                          formFieldName="${status.expression}"
                                          val="${status.editor.value}"/>
                        <c:if test="${status.errorMessage != ''}">
                            <span class="error">${status.errorMessage}</span>
                        </c:if>
                    </spring:bind></td>
                </tr>
                <tr>
                    <td><spring:message code="general.dateAutoExpire"/></td>
                    <td><spring:bind path="autoExpireDate">
                        <openmrs:fieldGen type="java.util.Date"
                                          formFieldName="${status.expression}"
                                          val="${status.editor.value}"/>
                        <c:if test="${status.errorMessage != ''}">
                            <span class="error">${status.errorMessage}</span>
                        </c:if>
                    </spring:bind></td>
                </tr>
            </table>
            <br/>
            <input type="submit" name="saveRadiologyOrder"
                   value="<spring:message code="Order.save"/>">
        </form:form>
    </c:when>

    <c:otherwise>
        <c:if test="${empty radiologyOrder}">
            <!-- Show existing RadiologyOrder's and discontinuation Order's -->
            <form:form method="post" modelAttribute="order" cssClass="box">
                <table>
                    <tr>
                        <td><spring:message code="Order.title"/> <spring:message
                                code="general.id"/></td>
                        <td><spring:bind path="orderId">${status.value}</spring:bind></td>
                    </tr>
                    <tr>
                        <td><spring:message code="Order.patient"/></td>
                        <td><a
                                href="/openmrs/patientDashboard.form?patientId=<spring:bind path="patient.id">
								${status.value}
							</spring:bind>">
                            <spring:bind path="patient.personName.fullName">
                                ${status.value}
                            </spring:bind>
                        </a></td>
                    </tr>
                    <tr>
                        <td><spring:message code="Order.concept"/></td>
                        <td><spring:bind path="concept.name.name">
                            ${status.value}
                        </spring:bind></td>
                    </tr>
                    <tr>
                        <td><spring:message code="Order.orderer"/></td>
                        <td><spring:bind path="orderer.name">
                            ${status.value}
                        </spring:bind></td>
                    </tr>
                    <tr>
                        <td><spring:message code="general.dateDiscontinued"/></td>
                        <td><spring:bind path="dateActivated">
                            ${status.value}
                        </spring:bind></td>
                    </tr>
                    <tr>
                        <td><spring:message code="general.discontinuedReason"/></td>
                        <td><spring:bind path="orderReasonNonCoded">
                            ${status.value}
                        </spring:bind></td>
                    </tr>
                    <tr>
                        <td><spring:message code="radiology.discontinuedOrder"/></td>
                        <td><spring:bind path="previousOrder">
                            <a href="radiologyOrder.form?orderId=${status.value}">${status.value}</a>
                        </spring:bind></td>
                    </tr>
                    <tr>
                        <td><spring:message code="general.createdBy"/></td>
                        <td><spring:bind path="creator.personName">
                            ${status.value}
                        </spring:bind> - <spring:bind path="dateCreated">
                            ${status.value}
                        </spring:bind></td>
                    </tr>
                </table>
                <br/>
            </form:form>
        </c:if>
        <c:if test="${not empty radiologyOrder}">
            <form:form method="post" modelAttribute="radiologyOrder"
                       cssClass="box">
                <table>
                    <tr>
                        <td><spring:message code="Order.title"/> <spring:message
                                code="general.id"/></td>
                        <td><spring:bind path="orderId">${status.value}</spring:bind></td>
                    </tr>
                    <tr>
                        <td><spring:message code="Order.patient"/></td>
                        <td><a
                                href="/openmrs/patientDashboard.form?patientId=<spring:bind path="patient.id">
								${status.value}
							</spring:bind>">
                            <spring:bind path="patient.personName.fullName">
                                ${status.value}
                            </spring:bind>
                        </a></td>
                    </tr>
                    <tr>
                        <td><spring:message code="Order.concept"/></td>
                        <td><spring:bind path="concept.name.name">
                            ${status.value}
                        </spring:bind></td>
                    </tr>
                    <tr>
                        <td><spring:message code="radiology.urgency"/></td>
                        <td><spring:bind path="urgency">
                            ${status.value}
                        </spring:bind></td>
                    </tr>
                    <tr>
                        <td><spring:message code="radiology.scheduledStatus"/></td>
                        <td><spring:bind path="study.scheduledStatus">${status.value}</spring:bind></td>
                    </tr>
                    <tr>
                        <td><spring:message code="radiology.performedStatus"/></td>
                        <td><spring:bind path="study.performedStatus">${status.value}
                        </spring:bind></td>
                    </tr>
                    <tr>
                        <td><spring:message code="radiology.modality"/></td>
                        <td><spring:bind path="study.modality">
                            ${status.value}
                        </spring:bind></td>
                    </tr>
                    <tr>
                        <td><spring:message code="general.instructions"/></td>
                        <td><spring:bind path="instructions">
                            ${status.value}
                        </spring:bind></td>
                    </tr>
                    <tr>
                        <td><spring:message code="Order.orderer"/></td>
                        <td><spring:bind path="orderer.name">
                            ${status.value}
                        </spring:bind></td>
                    </tr>
                    <tr>
                        <td><spring:message code="radiology.scheduledDate"/></td>
                        <td><spring:bind path="effectiveStartDate">
                            ${status.value}
                        </spring:bind></td>
                    </tr>
                    <tr>
                        <td><spring:message code="radiology.stopDate"/></td>
                        <td><spring:bind path="effectiveStopDate">
                            ${status.value}
                        </spring:bind></td>
                    </tr>
                    <tr>
                        <td><spring:message code="general.createdBy"/></td>
                        <td><spring:bind path="creator.personName">
                            ${status.value}
                        </spring:bind> - <spring:bind path="dateCreated">
                            ${status.value}
                        </spring:bind></td>
                    </tr>
                </table>
            </form:form>
            <c:if test="${isOrderActive}">
                <br/>
                <form:form method="post" modelAttribute="discontinuationOrder"
                           cssClass="box">
                    <table>
                        <tr>
                            <td><spring:message code="Order.orderer"/></td>
                            <td><spring:bind path="orderer">
                                <openmrs:fieldGen type="org.openmrs.Provider"
                                                  formFieldName="${status.expression}"
                                                  val="${status.editor.value}"/>
                                <c:if test="${status.errorMessage != ''}">
                                    <span class="error">${status.errorMessage}</span>
                                </c:if>
                            </spring:bind></td>
                        </tr>
                        <tr>
                            <td><spring:message code="general.dateDiscontinued"/></td>
                            <td><spring:bind path="dateActivated">
                                <openmrs:fieldGen type="java.util.Date"
                                                  formFieldName="${status.expression}"
                                                  val="${status.editor.value}"/>
                                <c:if test="${status.errorMessage != ''}">
                                    <span class="error">${status.errorMessage}</span>
                                </c:if>
                            </spring:bind></td>
                        </tr>
                        <tr>
                            <td><spring:message code="general.discontinuedReason"/></td>
                            <td><spring:bind path="orderReasonNonCoded">
                                <textarea name="${status.expression}">${status.value}</textarea>
                                <c:if test="${status.errorMessage != ''}">
                                    <span class="error">${status.errorMessage}</span>
                                </c:if>
                            </spring:bind></td>
                        </tr>
                    </table>
                    <input type="submit" name="discontinueOrder"
                           value='<spring:message code="Order.discontinueOrder"/>'/>
                </form:form>
            </c:if>
        </c:if>
    </c:otherwise>
</c:choose>

<div id="moreInfoPopup"></div>
<%@ include file="/WEB-INF/template/footer.jsp" %>
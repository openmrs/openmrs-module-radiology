<%@ include file="/WEB-INF/template/include.jsp" %>

<div>
    <form:form modelAttribute="radiologyOrder" cssClass="box">
        <table>
            <tr>
                <td><spring:message code="Order.title"/> <spring:message
                        code="general.id"/></td>
                <td><spring:bind path="orderId">${status.value}</spring:bind></td>
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
</div>
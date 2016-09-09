<%@ include file="/WEB-INF/template/include.jsp"%>
<c:set var="DO_NOT_INCLUDE_JQUERY" value="true" />
<%@ include file="/WEB-INF/template/header.jsp"%>
<c:set var="INCLUDE_TIME_ADJUSTMENT" value="true" />
<%@ include file="/WEB-INF/view/module/radiology/template/includeScripts.jsp"%>

<openmrs:require
  allPrivileges="Get Radiology Modalities,Manage Radiology Modalities"
  otherwise="/login.htm" redirect="/module/radiology/radiologyModality.form" />

<spring:hasBindErrors name="radiologyModality">
  <div class="error">
    <spring:message code="fix.error" />
  </div>
  <br />
</spring:hasBindErrors>

<c:if test="${radiologyModality.retired}">
    <div class="retiredMessage">
        <div>
            <openmrs:message code="general.retiredBy"/>
            <c:out value="${radiologyModality.retiredBy.personName}" />
            <span class="datetime">"${radiologyModality.dateRetired}"</span>
            -
            <c:out value="${radiologyModality.retireReason}"/>
        </div>
    </div>
</c:if>

<div>
  <c:choose>
    <c:when test="${empty radiologyModality.modalityId}">
      <span class="boxHeader"> <b><spring:message code="radiology.radiologyModality.form.boxheader.add" /></b>
      </span>
    </c:when>
    <c:otherwise>
      <span class="boxHeader"> <b><spring:message code="radiology.radiologyModality.form.boxheader.edit" /></b>
      </span>
    </c:otherwise>
  </c:choose>
  <form:form method="post" modelAttribute="radiologyModality" cssClass="box">
    <%-- following properties are bound to the form as hidden since they should be or since we show them only in a readonly manner. --%>
    <%-- if you delete for example the dateCreated it will change on every update --%>
    <form:hidden path="uuid" />
    <form:hidden path="creator" />
    <form:hidden path="dateCreated" />
    <form:hidden path="retired" />
    <form:hidden path="retireReason" />
    <form:hidden path="retiredBy" />
    <form:hidden path="dateRetired" />
    <table>
      <tr>
        <td><spring:message code="radiology.RadiologyModality.aeTitle" /><span class="required">*</span></td>
        <td><form:input path="aeTitle" />
            <form:errors path="aeTitle" cssClass="error" />
        </td>
      </tr>
      <tr>
        <td><spring:message code="general.name" /><span class="required">*</span></td>
        <td><form:input path="name" />
            <form:errors path="name" cssClass="error" />
        </td>
      </tr>
      <tr>
        <td><spring:message code="general.description" /></td>
        <td><form:input path="description" />
            <form:errors path="description" cssClass="error" />
        </td>
      </tr>
      <c:if test="${not empty radiologyModality.creator}">
          <tr>
            <td><spring:message code="general.createdBy" /></td>
            <td>${radiologyModality.creator.personName} - <span class="datetime"> ${radiologyModality.dateCreated} </span></td>
          </tr>
      </c:if>
      <c:if test="${not empty radiologyModality.changedBy}">
          <tr>
            <td><spring:message code="general.changedBy" /></td>
            <td>${radiologyModality.changedBy.personName} - <span class="datetime"> ${radiologyModality.dateChanged} </span></td>
          </tr>
      </c:if>
      <c:if test="${not empty radiologyModality.modalityId}">
          <tr>
            <td><font color="#D0D0D0"><sub><openmrs:message code="general.uuid" /></sub></font></td>
            <td><font color="#D0D0D0"><sub>${radiologyModality.uuid}</sub></font></td>
          </tr>
      </c:if>
      <tr>
        <td/>
        <td><input type="submit" name="saveRadiologyModality" value="<spring:message code="general.save"/>"></td>
      </tr>
    </table>
  </form:form>
  <c:if test="${not radiologyModality.retired && not empty radiologyModality.modalityId}">
      </br>
      <form:form method="post" modelAttribute="radiologyModality" cssClass="box">
        <form:hidden path="id" />
        <form:hidden path="uuid" />
        <form:hidden path="aeTitle" />
        <form:hidden path="name" />
        <form:hidden path="description" />
        <input type="hidden" name="retired" value="true" />
          <table>
            <tr>
              <td><spring:message code="general.reason" /><span class="required">*</span></td>
              <td>
                    <form:input path="retireReason" />
                    <form:errors path="retireReason" cssClass="error" />
              </td>
            </tr>
            <tr>
              <td/>
              <td>
                <input type="submit" value='<openmrs:message code="general.retire"/>' name="retireRadiologyModality"/>
              </td>
            </tr>
          </table>
      </form:form>
  </c:if>
</div>
<%@ include file="/WEB-INF/template/footer.jsp"%>

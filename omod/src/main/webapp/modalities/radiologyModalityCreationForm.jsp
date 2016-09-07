<%@ include file="/WEB-INF/template/include.jsp"%>
<c:set var="DO_NOT_INCLUDE_JQUERY" value="true" />
<%@ include file="/WEB-INF/template/header.jsp"%>
<c:set var="INCLUDE_TIME_ADJUSTMENT" value="true" />
<%@ include file="/WEB-INF/view/module/radiology/template/includeScripts.jsp"%>

<openmrs:require
  allPrivileges="Add Radiology Modalities,Get Radiology Modalities"
  otherwise="/login.htm" redirect="/module/radiology/radiologyModality.form" />

<spring:hasBindErrors name="radiologyModality">
  <div class="error">
    <spring:message code="fix.error" />
  </div>
  <br />
</spring:hasBindErrors>

<div>
  <span class="boxHeader"> <b><spring:message code="radiology.radiologyModality.form.boxheader.add" /></b>
  </span>
  <form:form method="post" modelAttribute="radiologyModality" cssClass="box">
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
      <tr>
        <td><input type="submit" name="saveRadiologyModality" value="<spring:message code="general.save"/>"></td>
      </tr>
    </table>
  </form:form>
</div>
<%@ include file="/WEB-INF/template/footer.jsp"%>

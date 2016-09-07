<%@ include file="/WEB-INF/template/include.jsp"%>
<c:set var="DO_NOT_INCLUDE_JQUERY" value="true" />
<%@ include file="/WEB-INF/template/header.jsp"%>
<c:set var="INCLUDE_TIME_ADJUSTMENT" value="true" />
<%@ include file="/WEB-INF/view/module/radiology/template/includeScripts.jsp"%>

<openmrs:require
  allPrivileges="Get Radiology Modalities"
  otherwise="/login.htm" redirect="/module/radiology/radiologyModality.form" />

<!--  This form shows existing RadiologyModality s -->

<div>
  <span class="boxHeader"> <b><spring:message code="radiology.radiologyModality.form.boxheader" /></b>
  </span>
  <div class="box">
    <table>
      <tr>
        <td><spring:message code="general.id" /></td>
        <td>${radiologyModality.modalityId}</td>
      </tr>
      <tr>
        <td><spring:message code="radiology.RadiologyModality.aeTitle" /></td>
        <td>${radiologyModality.aeTitle}</td>
      </tr>
      <tr>
        <td><spring:message code="general.name" /></td>
        <td>${radiologyModality.name}</td>
      </tr>
      <tr>
        <td><spring:message code="general.description" /></td>
        <td>${radiologyModality.description}</td>
      </tr>
      <tr>
        <td><spring:message code="general.createdBy" /></td>
        <td>${radiologyModality.creator.personName} - <span class="datetime"> ${radiologyModality.dateCreated} </span></td>
      </tr>
    </table>
  </div>
</div>
<%@ include file="/WEB-INF/template/footer.jsp"%>

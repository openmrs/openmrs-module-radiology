<%@ include file="/WEB-INF/template/include.jsp"%>
<c:set var="DO_NOT_INCLUDE_JQUERY" value="true" />
<%@ include file="/WEB-INF/template/header.jsp"%>
<%@ include file="/WEB-INF/view/module/radiology/template/includeScripts.jsp"%>

<br>
<div id="mrrtReportTemplateProperties">
  <b class="boxHeader"><openmrs:message code="radiology.report.template.view.templateMetadata.boxheader" /></b>
  <div class="box">
    <table>
      <tr>
        <td><b><openmrs:message code="radiology.report.template.view.templateMetadata.title" /></b></td>
        <td>${template.dcTermsTitle}</td>
      </tr>
      <tr>
        <td><b><b><openmrs:message code="radiology.report.template.view.templateMetadata.identifier" /></b></b></td>
        <td>${template.dcTermsIdentifier}</td>
      </tr>
      <tr>
        <td><b><openmrs:message code="radiology.report.template.view.templateMetadata.creator" /></b></td>
        <td>${template.dcTermsCreator}</td>
      </tr>
      <tr>
        <td><b><openmrs:message code="radiology.report.template.view.templateMetadata.publisher" /></b></td>
        <td>${template.dcTermsPublisher}</td>
      </tr>
      <tr>
        <td><b><openmrs:message code="radiology.report.template.view.templateMetadata.rights" /></b></td>
        <td>${template.dcTermsRights}</td>
      </tr>
      <tr>
        <td><b><openmrs:message code="radiology.report.template.view.templateMetadata.description" /></b></td>
        <td>${template.dcTermsDescription}</td>
      </tr>
      <tr>
      	<td><b><openmrs:message code="radiology.report.template.view.templateMetadata.referenceTerms"/></b></td>
      	<td>
      	<table>
      		<tr>
      			<th><openmrs:message code="radiology.report.template.view.templateMetadata.referenceTerms.scheme"/></th>
      			<th><openmrs:message code="radiology.report.template.view.templateMetadata.referenceTerms.code"/></th>
      			<th><openmrs:message code="general.name"/></th>
      		</tr>	
      		<c:forEach items="${template.terms}" var="term">
      			<tr>
      				<td>${term.conceptSource.name}</td>
      				<td>${term.code}</td>
      				<td>${term.name}</td>
      			</tr>
      		</c:forEach>
      	</table>
      	</td>
      </tr>
    </table>  
  </div>
</div>
<br>
<div id="mrrtReportTemplateBody">
  <b class="boxHeader"><openmrs:message code="radiology.report.template.view.templateBody.boxheader" /></b>
  <div class="box">
    <div id="templateContent">${templateBody}</div>
  </div>
</div>

<%@ include file="/WEB-INF/template/footer.jsp"%>

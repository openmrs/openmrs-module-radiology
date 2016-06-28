<%@ include file="/WEB-INF/template/include.jsp"%>
<%@ include file="/WEB-INF/template/header.jsp"%>

<%@ include file="/WEB-INF/view/module/radiology/localHeader.jsp"%>

<openmrs:htmlInclude file="/moduleResources/radiology/scripts/tinymce/tinymce.min.js" />
<script type="text/javascript">
  var $j = jQuery.noConflict();
  $j(document).ready(function() {
    var reportBody = $j("#reportBodyId");

    tinymce.init({
      selector: '#reportBodyId',
      setup: function(editor) {
        if (reportBody.attr("disabled") != null) {
          editor.settings.readonly = true;
          editor.settings.toolbar = false;
          editor.settings.menubar = false;
        }
      },
      menubar: "edit,format",
      elementpath: false,
    });
  });
</script>

<openmrs:portlet url="patientHeader" id="patientDashboardHeader" patientId="${order.patient.patientId}" />
<br>
<div>
  <span class="boxHeader"> <b><spring:message code="radiology.radiologyOrder" /></b>
  </span>
  <%@ include file="/WEB-INF/view/module/radiology/orders/radiologyOrderDetailsSegment.jsp"%>
</div>
<br>
<spring:hasBindErrors name="radiologyReport">
  <div class="error">
    <spring:message code="fix.error" />
  </div>
  <br>
</spring:hasBindErrors>
<span class="boxHeader"> <b><spring:message code="radiology.radiologyReportTitle" /></b>
</span>
<form:form modelAttribute="radiologyReport" method="post">
  <div class="box">
    <table>
      <tr>
        <td><spring:message code="radiology.radiologyReportId" /></td>
        <td>${radiologyReport.id}</td>
        <form:hidden path="id" />
      </tr>
      <tr>
        <form:hidden path="radiologyOrder" />
      </tr>
      <tr>
        <td><spring:message code="radiology.reportStatus" /></td>
        <td>${radiologyReport.reportStatus}</td>
        <form:hidden path="reportStatus" />
      </tr>
      <c:if test="${radiologyReport.reportStatus == 'COMPLETED'}">
        <tr>
          <td><spring:message code="radiology.radiologyReportDate" /></td>
          <td>${radiologyReport.reportDate}</td>
          <form:hidden path="reportDate" />
        </tr>
      </c:if>
      <tr>
        <td><spring:message code="radiology.radiologyReportDiagnosis" /></td>
        <td><c:choose>
            <c:when test="${radiologyReport.reportStatus == 'COMPLETED'}">
              <spring:bind path="reportBody">
                <textarea id="${status.expression}Id" name="${status.expression}" disabled="true">${status.value}</textarea>
              </spring:bind>
            </c:when>
            <c:otherwise>
              <spring:bind path="reportBody">
                <textarea id="${status.expression}Id" name="${status.expression}">${status.value}</textarea>
                <c:if test="${status.errorMessage != ''}">
                  <span class="error">${status.errorMessage}</span>
                </c:if>
              </spring:bind>
            </c:otherwise>
          </c:choose></td>
      </tr>
      <tr>
        <td><spring:message code="radiology.radiologyReportProvider" /></td>
        <td><c:choose>
            <c:when test="${not empty radiologyReport.principalResultsInterpreter.id}">
                            ${radiologyReport.principalResultsInterpreter.name}
                            <form:hidden path="principalResultsInterpreter" />
            </c:when>
            <c:otherwise>
              <spring:bind path="principalResultsInterpreter">
                <openmrs:fieldGen type="org.openmrs.Provider" formFieldName="${status.expression}"
                  val="${status.editor.value}" />
                <c:if test="${status.errorMessage != ''}">
                  <span class="error">${status.errorMessage}</span>
                </c:if>
              </spring:bind>
            </c:otherwise>
          </c:choose></td>
      </tr>
      <tr>
        <td><spring:message code="general.createdBy" /></td>
        <td><spring:bind path="creator.personName">
					${status.value}
					<form:hidden path="creator" />
          </spring:bind> - <spring:bind path="dateCreated">
					${status.value}
					<form:hidden path="dateCreated" />
          </spring:bind></td>
      </tr>
    </table>
    <br>
    <c:if test="${radiologyReport.reportStatus != 'COMPLETED'}">
      <c:if test="${radiologyReport.reportStatus != 'DISCONTINUED'}">
        <input type="submit" value="<spring:message code="radiology.radiologyReportUnclaim"/>" name="unclaimRadiologyReport" />
        <input type="submit" value="<spring:message code="radiology.radiologyReportSave"/>" name="saveRadiologyReport" />
        <input type="submit" value="<spring:message code="radiology.radiologyReportComplete"/>"
          name="completeRadiologyReport" />
      </c:if>
    </c:if>
  </div>
</form:form>


<%@ include file="/WEB-INF/template/include.jsp"%>
<c:set var="DO_NOT_INCLUDE_JQUERY" value="true" />
<%@ include file="/WEB-INF/template/header.jsp"%>
<c:set var="INCLUDE_TIME_ADJUSTMENT" value="true" />
<%@ include file="/WEB-INF/view/module/radiology/template/includeScripts.jsp"%>
<openmrs:htmlInclude file="/moduleResources/radiology/scripts/tinymce/tinymce.min.js" />

<%@ include file="/WEB-INF/view/module/radiology/localHeader.jsp"%>

<script type="text/javascript">
  var $j = jQuery.noConflict();
  
  function showUnclaimRadiologyReportDialog() {
    var dialogDiv = $j("<div></div>")
            .html(
                    '<spring:message code="radiology.report.form.unclaim.dialog.message"/>');
    dialogDiv
            .dialog({
              resizable: false,
              width: 'auto',
              height: 'auto',
              title: '<spring:message code="radiology.report.form.unclaim.dialog.title"/>',
              modal: true,
              buttons: {
                '<spring:message code="radiology.report.form.unclaim.dialog.button.ok"/>': function() {
                  $j(this).dialog("close");
                  submitUnclaimRadiologyReport();
                },
                '<spring:message code="radiology.report.form.unclaim.dialog.button.cancel"/>': function() {
                  $j(this).dialog("close");
                }
              }
            });
  }

  function submitUnclaimRadiologyReport() {
    var unclaimRadiologyReport = $j("<input>").attr("type", "hidden").attr(
            "name", "unclaimRadiologyReport").val("Unclaim");
    $j("#radiologyReportFormId").append(unclaimRadiologyReport);
    $j("#radiologyReportFormId").submit()
  }

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

    $j("#unclaimRadiologyReportButtonId").click(function() {
      if (tinymce.activeEditor.getContent() != "") {
        showUnclaimRadiologyReportDialog();
      } else {
        submitUnclaimRadiologyReport();
      }
    });

  });
</script>

<openmrs:hasPrivilege privilege="View Patients">
  <openmrs:portlet url="patientHeader" id="patientDashboardHeader" patientId="${order.patient.patientId}" />
  <br>
</openmrs:hasPrivilege>
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
<form:form id="radiologyReportFormId" modelAttribute="radiologyReport" method="post">
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
          </spring:bind> - <span class="datetime"><spring:bind path="dateCreated">
					${status.value}
					<form:hidden path="dateCreated" />
            </spring:bind></span></td>
      </tr>
    </table>
    <br>
    <c:if test="${radiologyReport.reportStatus != 'COMPLETED'}">
      <c:if test="${radiologyReport.reportStatus != 'DISCONTINUED'}">
        <input type="button" value="<spring:message code="radiology.radiologyReportUnclaim"/>"
          id="unclaimRadiologyReportButtonId" />
        <input type="submit" value="<spring:message code="radiology.radiologyReportSave"/>" name="saveRadiologyReport" />
        <input type="submit" value="<spring:message code="radiology.radiologyReportComplete"/>"
          name="completeRadiologyReport" />
      </c:if>
    </c:if>
  </div>
</form:form>
<%@ include file="/WEB-INF/template/footer.jsp"%>

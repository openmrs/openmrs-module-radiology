<%@ include file="/WEB-INF/template/include.jsp"%>
<c:set var="DO_NOT_INCLUDE_JQUERY" value="true" />
<%@ include file="/WEB-INF/template/header.jsp"%>
<c:set var="INCLUDE_TIME_ADJUSTMENT" value="true" />
<%@ include file="/WEB-INF/view/module/radiology/template/includeScripts.jsp"%>
<openmrs:htmlInclude file="/moduleResources/radiology/scripts/tinymce/tinymce.min.js" />
<openmrs:htmlInclude file="/moduleResources/radiology/fonts/font-awesome/css/font-awesome.min.css" />

<%@ include file="/WEB-INF/view/module/radiology/localHeader.jsp"%>

<script type="text/javascript">
  var $j = jQuery.noConflict();
 
  function showVoidRadiologyReportDialog() {
    var dialogDiv = $j("<div></div>")
            .html(
                    '<spring:message code="radiology.report.form.void.dialog.message"/>');
    dialogDiv
            .dialog({
              resizable: false,
              width: 'auto',
              height: 'auto',
              title: '<spring:message code="radiology.report.form.void.dialog.title"/>',
              modal: true,
              buttons: {
                '<spring:message code="radiology.report.form.void.dialog.button.ok"/>': function() {
                  $j(this).dialog("close");
                  submitVoidRadiologyReport();
                },
                '<spring:message code="radiology.report.form.void.dialog.button.cancel"/>': function() {
                  $j(this).dialog("close");
                }
              }
            });
  }

  function submitVoidRadiologyReport() {
    var voidRadiologyReport = $j("<input>").attr("type", "hidden").attr(
            "name", "voidRadiologyReport").val('<spring:message code="general.void"/>');
    $j("#voidRadiologyReportForm").append(voidRadiologyReport);
    $j("#voidRadiologyReportForm").submit()
  }

  $j(document).ready(function() {
    var reportBody = $j("#bodyId");

    tinymce.init({
      selector: '#bodyId',
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

    $j("#voidRadiologyReportButtonId").click(function() {
      if (tinymce.activeEditor.getContent() != "") {
        showVoidRadiologyReportDialog();
      } else {
        submitVoidRadiologyReport();
      }
    });

    $j('#radiologyOrderDetailsId').hide();
    $j('#radiologyOrderDetailsAccordion > .boxHeader').click(function() {
      $j('#radiologyOrderDetailsId').slideToggle();
      $j('#expandIconId').toggleClass('fa-chevron-down fa-chevron-up');
    });
  });
</script>

<openmrs:hasPrivilege privilege="View Patients">
  <openmrs:portlet url="patientHeader" id="patientDashboardHeader" patientId="${order.patient.patientId}" />
  <br>
</openmrs:hasPrivilege>
<div id="radiologyOrderDetailsAccordion">
  <span class="boxHeader"><i id="expandIconId" class="fa fa-chevron-down"></i><b> <spring:message
        code="radiology.radiologyOrder" /></b> </span>
  <div id="radiologyOrderDetailsId">
    <%@ include file="/WEB-INF/view/module/radiology/orders/radiologyOrderDetailsSegment.jsp"%>
  </div>
</div>
<br>
<spring:hasBindErrors name="radiologyReport">
  <div class="error">
    <spring:message code="fix.error" />
  </div>
  <br>
</spring:hasBindErrors>
<c:if test="${radiologyReport.voided}">
  <div class="retiredMessage">
    <div>
      <spring:message code="general.voided"/>
    </div>
  </div>
</c:if>
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
        <form:hidden path="uuid" />
      </tr>
      <tr>
        <form:hidden path="radiologyOrder" />
      </tr>
      <tr>
        <td><spring:message code="radiology.reportStatus" /></td>
        <td><spring:message code="radiology.report.status.${radiologyReport.status}" text="${radiologyReport.status}" /></td>
        <form:hidden path="status" />
      </tr>
      <c:if test="${radiologyReport.status == 'COMPLETED'}">
        <tr>
          <td><spring:message code="radiology.radiologyReportDate" /></td>
          <td id="reportDateId"><spring:bind path="date">${status.value}</spring:bind></td>
          <form:hidden path="date" />
        </tr>
      </c:if>
      <tr>
        <td><spring:message code="radiology.radiologyReportDiagnosis" /></td>
        <td><c:choose>
            <c:when test="${radiologyReport.status == 'COMPLETED'}">
              <spring:bind path="body">
                <textarea id="${status.expression}Id" name="${status.expression}" disabled="true">${status.value}</textarea>
              </spring:bind>
            </c:when>
            <c:otherwise>
              <spring:bind path="body">
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
      <c:if test="${radiologyReport.voided}">
          <form:hidden path="voided" />
          <tr>
            <td><spring:message code="general.voidedBy" /></td>
            <td><spring:bind path="voidedBy.personName">
                        ${status.value}
                        <form:hidden path="voidedBy" />
              </spring:bind> - <span class="datetime"><spring:bind path="dateVoided">
                        ${status.value}
                        <form:hidden path="dateVoided" />
                </spring:bind></span></td>
          </tr>
          <tr>
            <td><spring:message code="general.voidReason" /></td>
            <td><spring:bind path="voidReason">${status.value}</spring:bind></td>
          </tr>
      </c:if>
    </table>
    <br>
    <c:if test="${(radiologyReport.status == 'DRAFT') && (not radiologyReport.voided)}">
        <input type="submit" value="<spring:message code="radiology.radiologyReportSave"/>" name="saveRadiologyReportDraft" />
        <input type="submit" value="<spring:message code="radiology.radiologyReportComplete"/>"
          name="completeRadiologyReport" />
    </c:if>
  </div>
</form:form>
<c:if test="${(radiologyReport.status == 'DRAFT') && (not radiologyReport.voided)}">
  </br>
  <form:form method="post" id="voidRadiologyReportForm" modelAttribute="voidRadiologyReportRequest" cssClass="box">
    <table>
        <td><spring:message code="general.voidReason" /></td>
        <td><spring:bind path="voidReason">
            <textarea name="${status.expression}">${status.value}</textarea>
            <c:if test="${not empty status.errorMessage}">
              <span class="error">${status.errorMessage}</span>
            </c:if>
          </spring:bind></td>
      </tr>
    </table>
    <input type="button" value="<spring:message code="general.void"/>" id="voidRadiologyReportButtonId" />
  </form:form>
</c:if>
<%@ include file="/WEB-INF/template/footer.jsp"%>

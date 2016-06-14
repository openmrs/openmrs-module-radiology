<%@ include file="/WEB-INF/view/module/radiology/template/include.jsp"%>
<%@ include file="/WEB-INF/view/module/radiology/template/includeDatatables.jsp"%>

<script type="text/javascript">
  var $j = jQuery.noConflict();
  $j(document)
          .ready(
                  function() {
                    var patientUuid = $j('#patientUuid');
                    var find = $j('#findButton');
                    var clearResults = $j('a#clearResults');

                    $j('#addTemplatePopup').dialog({
                      autoOpen: false,
                      modal: true,
                      title: '<openmrs:message code="radiology.report.templates.AddReportTemplates" javaScriptEscape="true"/>',
                      width: '90%'
                    });

                    $j('#addTemplateButton').click(function() {
                      $j('#addTemplatePopup').dialog('open');
                    });

                    var radiologyOrdersTable = $j('#radiologyOrdersTable')
                            .DataTable(
                                    {
                                      "processing": true,
                                      "serverSide": true,
                                      "ajax": {
                                        headers: {
                                          Accept: "application/json; charset=utf-8",
                                          "Content-Type": "text/plain; charset=utf-8",
                                        },
                                        cache: true,
                                        dataType: "json",
                                        url: "http://localhost:8080/openmrs/ws/rest/v1/radiologyorder/",
                                        data: function(data) {
                                          return {
                                            startIndex: data.start,
                                            limit: data.length,
                                            v: "full",
                                            patient: patientUuid.val(),
                                          };
                                        },
                                        "dataSrc": function(json) {
                                          var result = [];
                                          for (var i = 0, ien = json.results.length; i < ien; i++) {
                                            result[i] = [
                                                '<a href="http://localhost:8080/openmrs/module/radiology/radiologyOrder.form?orderId='
                                                        + json.results[i].uuid
                                                        + '">'
                                                        + json.results[i].orderNumber
                                                        + '</a>',
                                                json.results[i].patient.display,
                                                json.results[i].urgency,
                                                json.results[i].concept.display,
                                                json.results[i].orderer.display,
                                                json.results[i].scheduledDate,
                                                json.results[i].dateActivated, ]
                                          }
                                          return result;
                                        }
                                      },
                                      "searching": false,
                                      "ordering": false,
                                      "columns": [{
                                        "name": "orderNumber",
                                      }, {
                                        "name": "patient",
                                      }, {
                                        "name": "urgency",
                                      }, {
                                        "name": "concept",
                                      }, {
                                        "name": "orderer",
                                      }, {
                                        "name": "scheduledDate",
                                      }, {
                                        "name": "dateActivated",
                                      }, ],
                                    });

                    // prevent form submit when user hits enter
                    $j(window).keydown(function(event) {
                      if (event.keyCode == 13) {
                        event.preventDefault();
                        return false;
                      }
                    });

                    find.on('mouseup keyup', function(event) {
                      if (event.type == 'keyup' && event.keyCode != 13) return;
                      radiologyOrdersTable.ajax.reload();
                    });

                    clearResults.on('mouseup keyup', function() {
                      $j('table#searchForm input:text').val('');
                      patientUuid.val('');
                      radiologyOrdersTable.ajax.reload();
                    });
                  });
</script>

<br/>
<div id="buttonPanel">
  <div style="float:left">
    <input type="button" id="addTemplateButton" value="<openmrs:message code="radiology.report.templates.AddReportTemplates" javaScriptEscape="true"/>"/>
    <div id="addTemplatePopup">
      <b class="boxHeader"><openmrs:message code="radiology.report.templates.importTemplate"/></b>
      <div class="box">
        <form id="templateAddForm" action="radiologyDashboard.form" method="post" enctype="multipart/form-data">
          <input type="file" name="templateFile" size="40"/>
          <input type="hidden" name="action" value="upload"/>
          <input type="submit" value='<openmrs:message code="radiology.report.templates.Upload"/>'/>
        </form>
      </div>
      <br/>
    </div>
  </div>
  <div style="clear:both">&nbsp;</div>
</div>

<br>
<span class="boxHeader"> <b><spring:message code="radiology.radiologyReportTemplates" /></b> <a id="clearResults" href="#"
  style="float: right"> <spring:message code="radiology.clearResults" />
</a>
</span>
<div class="box">
  <table id="searchForm" cellspacing="10">
    <tr>
      <form id="reportTemplateListForm">
        <td><label><spring:message code="radiology.report.template.title" />:</label> <input name="titleQuery" type="text"
                                                                                                 style="width: 20em" title="<spring:message
						code="radiology.minChars" />" /></td>
        <td><input id="findButton" type="button" value="<spring:message code="radiology.find"/>" /></td>
        <td id="errorSpan"></td>
      </form>
    </tr>
  </table>
  <br>
  <div id="results">
    <table id="radiologyTemplatesTable" cellspacing="0" width="100%" class="display nowrap">
      <thead>
        <tr>
          <th><spring:message code="radiology.report.template.templateId" /></th>
          <th><spring:message code="radiology.report.template.title" /></th>
          <th><spring:message code="radiology.report.template.type" /></th>
          <th><spring:message code="radiology.report.template.creator" /></th>
          <th><spring:message code="radiology.report.template.publisher" /></th>
          <th><spring:message code="radiology.report.template.rights" /></th>
          <th><spring:message code="radiology.report.template.description" /></th>
        </tr>
      </thead>
    </table>
  </div>
</div>
<br />

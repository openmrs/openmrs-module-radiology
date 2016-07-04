<%@ include file="/WEB-INF/view/module/radiology/template/include.jsp"%>
<%@ include file="/WEB-INF/view/module/radiology/template/includeDatatablesWithDefaults.jsp"%>

<script type="text/javascript">
  var $j = jQuery.noConflict();
  $j(document)
          .ready(
                  function() {
                    var templateTitle = $j('#templateTitle');
                    var find = $j('#findReportTemplates');
                    var clearResults = $j('a#clearResults');

                    var radiologyTemplatesTable = $j('#radiologyTemplatesTable')
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
                                        url: "${pageContext.request.contextPath}/ws/rest/v1/mrrtreporttemplate/",
                                        data: function(data) {
                                          return {
                                            startIndex: data.start,
                                            limit: data.length,
                                            v: "full",
                                            title: templateTitle.val(),
                                            totalCount: true,
                                          };
                                        },
                                        "dataFilter": function(data) {
                                          var json = $j.parseJSON(data);
                                          json.recordsTotal = json.totalCount || 0;
                                          json.recordsFiltered = json.totalCount || 0;
                                          json.data = json.results;
                                          return JSON.stringify(json);
                                        }
                                      },
                                      "columns": [
                                          {
                                            "name": "templateId",
                                            "render": function(data, type,
                                                    full, meta) {
                                              return full.templateId;
                                            }
                                          },
                                          {
                                            "name": "dcTermsTitle",
                                            "render": function(data, type,
                                                    full, meta) {
                                              return full.dcTermsTitle;
                                            }
                                          },
                                          {
                                            "name": "dcTermsType",
                                            "render": function(data, type,
                                                    full, meta) {
                                              return full.dcTermsType;
                                            }
                                          },
                                          {
                                            "name": "dcTermsCreator",
                                            "render": function(data, type,
                                                    full, meta) {
                                              return full.dcTermsCreator;
                                            }
                                          },
                                          {
                                            "name": "dcTermsPublisher",
                                            "render": function(data, type,
                                                    full, meta) {
                                              return full.dcTermsPublisher;
                                            }
                                          },
                                          {
                                            "name": "dcTermsRights",
                                            "render": function(data, type,
                                                    full, meta) {
                                              return full.dcTermsRights;
                                            }
                                          },
                                          {
                                            "name": "dcTermsDescription",
                                            "render": function(data, type,
                                                    full, meta) {
                                              return full.dcTermsDescription;
                                            }
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
                      radiologyTemplatesTable.ajax.reload();
                    });

                    clearResults.on('mouseup keyup', function() {
                      $j('table#searchForm input:text').val('');
                      templateTitle.val('');
                      radiologyTemplatesTable.ajax.reload();
                    });

                    $j('#addTemplatePopup')
                            .dialog(
                                    {
                                      autoOpen: false,
                                      modal: true,
                                      title: '<openmrs:message code="radiology.report.template.AddReportTemplate" javaScriptEscape="true"/>',
                                      width: '90%'
                                    });

                    $j('#addTemplateButton').click(function() {
                      $j('#addTemplatePopup').dialog('open');
                    });
                  });
</script>

<br />
<div id="buttonPanel">
  <div style="float: left">
    <input type="button" id="addTemplateButton"
      value="<openmrs:message code="radiology.report.template.AddReportTemplate" javaScriptEscape="true"/>" />
    <div id="addTemplatePopup">
      <b class="boxHeader"><openmrs:message code="radiology.report.template.importTemplate" /></b>
      <div class="box">
        <form id="templateAddForm" action="radiologyDashboard.form" method="post" enctype="multipart/form-data">
          <input type="file" name="templateFile" size="40" /> <input type="hidden" name="action" value="upload" /> <input
            type="submit" value='<openmrs:message code="radiology.report.template.Upload"/>' />
        </form>
      </div>
      <br />
    </div>
  </div>
  <div style="clear: both">&nbsp;</div>
</div>

<br>
<span class="boxHeader"> <b><spring:message code="radiology.radiologyReportTemplates" /></b> <a id="clearResults"
  href="#" style="float: right"> <spring:message code="radiology.clearResults" />
</a>
</span>
<div class="box">
  <table id="searchForm" cellspacing="10">
    <tr>
      <form id="reportTemplateListForm">
        <td><label><spring:message code="radiology.report.template.title" />:</label> <input id="templateTitle"
          name="titleQuery" type="text" style="width: 20em" title="<spring:message
						code="radiology.minChars" />" /></td>
        <td><input id="findReportTemplates" type="button" value="<spring:message code="radiology.find"/>" /></td>
        <td id="errorSpan"></td>
      </form>
    </tr>
  </table>
  <br>
  <div id="results">
    <table id="radiologyTemplatesTable" cellspacing="0" width="100%" class="display nowrap">
      <thead>
        <tr>
          <th><spring:message code="radiology.datatables.column.templateId" /></th>
          <th><spring:message code="radiology.datatables.column.title" /></th>
          <th><spring:message code="radiology.datatables.column.type" /></th>
          <th><spring:message code="radiology.datatables.column.creator" /></th>
          <th><spring:message code="radiology.datatables.column.publisher" /></th>
          <th><spring:message code="radiology.datatables.column.rights" /></th>
          <th><spring:message code="radiology.datatables.column.description" /></th>
        </tr>
      </thead>
    </table>
  </div>
</div>
<br />
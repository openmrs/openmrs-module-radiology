<%@ include file="/WEB-INF/view/module/radiology/dashboardHeader.jsp"%>
<openmrs:htmlInclude file="/moduleResources/radiology/scripts/jquery/daterangepicker/css/daterangepicker.min.css" />
<openmrs:htmlInclude file="/moduleResources/radiology/scripts/jquery/daterangepicker/js/jquery.daterangepicker.min.js" />

<script type="text/javascript">
  var $j = jQuery.noConflict();
  $j(document)
          .ready(
                  function() {
                    var templateTitle = $j('#reportTemplatesTabTitleFilter');
                    var find = $j('#reportTemplatesTabFind');
                    var clearResults = $j('a#reportTemplatesTabClearFilters');

                    $j("#radiologyReportTemplatesTab").parent().addClass(
                            "ui-tabs-selected ui-state-active");

                    var radiologyTemplatesTable = $j('#reportTemplatesTable')
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
                                        url: Radiology.getRestRootEndpoint()
                                                + "/mrrtreporttemplate/",
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
                                            "className": "control",
                                            "orderable": false,
                                            "data": null,
                                            "defaultContent": "",
                                            "responsivePriority": 1
                                          },
                                          {
                                            "name": "templateId",
                                            "responsivePriority": 1,
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
                                            "className": "none",
                                            "render": function(data, type,
                                                    full, meta) {
                                              return Radiology.getProperty(
                                                      full, 'dcTermsRights');
                                            }
                                          },
                                          {
                                            "name": "dcTermsDescription",
                                            "className": "none",
                                            "render": function(data, type,
                                                    full, meta) {
                                              return Radiology.getProperty(
                                                      full,
                                                      'dcTermsDescription');
                                            }
                                          },
                                          {
                                            "name": "action",
                                            "className": "dt-center",
                                            "responsivePriority": 1,
                                            "render": function(data, type,
                                                    full, meta) {
                                              return '<a href="${pageContext.request.contextPath}/module/radiology/mrrtReportTemplate.form?templateId='
                                                      + full.uuid
                                                      + '"><i class="fa fa-eye fa-lg"></i></a>';
                                            }
                                          }],
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
                      $j('table#reportTemplatesTableFilters input:text')
                              .val('');
                      radiologyTemplatesTable.ajax.reload();
                    });
                    $j('#reportTemplatesTabImportPopup')
                            .dialog(
                                    {
                                      autoOpen: false,
                                      modal: true,
                                      title: '<openmrs:message code="radiology.reportTemplates.import.popup.boxheader" javaScriptEscape="true"/>',
                                      width: '90%'
                                    });
                    $j('#reportTemplatesTabImportTemplates').click(function() {
                      $j('#reportTemplatesTabImportPopup').dialog('open');
                    });

                  });
</script>

<openmrs:hasPrivilege privilege="View Radiology Report Templates">
  <div id="radiologyReportTemplates">
    <br />
    <div id="buttonPanel">
      <div style="float: left">
        <input type="button" id="reportTemplatesTabImportTemplates"
          value="<openmrs:message code="radiology.reportTemplates.import.popup.button" javaScriptEscape="true"/>" />
        <div id="reportTemplatesTabImportPopup">
          <b class="boxHeader"><openmrs:message code="radiology.reportTemplates.import.popup.boxheader" /></b>
          <div class="box">
            <form id="templateAddForm" action="radiologyDashboardReportTemplatesTab.htm" method="post" enctype="multipart/form-data">
              <input type="file" name="templateFile" size="40" /> <input type="submit" name="uploadReportTemplate"
                value='<openmrs:message code="radiology.reportTemplates.import.popup.upload"/>' />
            </form>
          </div>
          <br />
        </div>
      </div>
      <div style="clear: both">&nbsp;</div>
    </div>

    <span class="boxHeader"> <b><spring:message code="radiology.dashboard.tabs.reportTemplates.boxheader" /></b> <a
      id="reportTemplatesTabClearFilters" href="#" style="float: right"> <spring:message
          code="radiology.dashboard.tabs.filters.clearFilters" />
    </a>
    </span>
    <div class="box">
      <table id="reportTemplatesTableFilters" cellspacing="10">
        <tr>
          <form>
            <td><label><spring:message code="radiology.dashboard.tabs.filters.filterby" /></label> <input
              id="reportTemplatesTabTitleFilter" name="titleQuery" type="text" style="width: 20em"
              title="<spring:message
            code="radiology.minChars" />"
              placeholder='<spring:message code="radiology.dashboard.tabs.reportTemplates.filters.title" />' /></td>
            <td><input id="reportTemplatesTabFind" type="button"
              value="<spring:message code="radiology.dashboard.tabs.filters.filter"/>" /></td>
          </form>
        </tr>
      </table>
      <br>
      <div>
        <table id="reportTemplatesTable" cellspacing="0" width="100%" class="display responsive compact">
          <thead>
            <tr>
              <th></th>
              <th><spring:message code="radiology.datatables.column.report.template.id" /></th>
              <th><spring:message code="radiology.datatables.column.report.template.title" /></th>
              <th><spring:message code="radiology.datatables.column.report.template.creator" /></th>
              <th><spring:message code="radiology.datatables.column.report.template.publisher" /></th>
              <th><spring:message code="radiology.datatables.column.report.template.rights" /></th>
              <th><spring:message code="radiology.datatables.column.report.template.description" /></th>
              <th><spring:message code="radiology.datatables.column.action" /></th>
            </tr>
          </thead>
        </table>
      </div>
    </div>
  </div>
</openmrs:hasPrivilege>
</div>
<%@ include file="/WEB-INF/template/footer.jsp"%>
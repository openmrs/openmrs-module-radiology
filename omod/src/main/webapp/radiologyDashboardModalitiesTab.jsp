<%@ include file="/WEB-INF/view/module/radiology/dashboardHeader.jsp"%>

<script type="text/javascript">
  var $j = jQuery.noConflict();
  $j(document)
          .ready(
                  function() {
                    var includeAll = $j('#includeAllFilter');
                    var find = $j('#find');
                    var clearResults = $j('a#clearFilters');

                    $j("#radiologyModalitiesTab").parent().addClass(
                            "ui-tabs-selected ui-state-active");

                    var radiologyModalitiesTable = $j('#modalitiesTable')
                            .DataTable(
                                    {
                                      searching: true,
                                      "processing": false,
                                      "serverSide": false,
                                      "ajax": {
                                        headers: {
                                          Accept: "application/json; charset=utf-8",
                                          "Content-Type": "text/plain; charset=utf-8",
                                        },
                                        cache: true,
                                        dataType: "json",
                                        url: Radiology.getRestRootEndpoint()
                                                + "/radiologymodality/",
                                        data: function(data) {
                                          return {
                                            startIndex: data.start,
                                            limit: data.length,
                                            v: "full",
                                            includeAll: includeAll
                                                    .is(':checked'),
                                          };
                                        },
                                        "dataFilter": function(data) {
                                          var json = $j.parseJSON(data);
                                          json.recordsTotal = json.totalCount || 0;
                                          json.recordsFiltered = json.totalCount || 0;
                                          json.data = json.results;
                                          return JSON.stringify(json);
                                        },
                                        error: function(jqXHR, textStatus,
                                                errorThrown) {
                                          Radiology
                                                  .showAlertDialog(
                                                          '<spring:message code="radiology.rest.error.dialog.title"/>',
                                                          '<spring:message code="radiology.rest.error.dialog.message.line1"/><br />'
                                                                  + '<spring:message code="radiology.rest.error.dialog.message.line2"/>',
                                                          '<spring:message code="radiology.rest.error.dialog.button.ok"/>');
                                          $j("#modalitiesTable_processing")
                                                  .hide();
                                          console
                                                  .error("A rest error occured - "
                                                          + textStatus
                                                          + ":\n"
                                                          + errorThrown);
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
                                            "name": "aeTitle",
                                            "responsivePriority": 1,
                                            "render": function(data, type,
                                                    full, meta) {
                                              return full.aeTitle;
                                            }
                                          },
                                          {
                                            "name": "name",
                                            "render": function(data, type,
                                                    full, meta) {
                                              return full.name;
                                            }
                                          },
                                          {
                                            "name": "description",
                                            "render": function(data, type,
                                                    full, meta) {
                                              return full.description;
                                            }
                                          },
                                          {
                                            "name": "status",
                                            "className": "dt-center",
                                            "render": function(data, type,
                                                    full, meta) {
                                              if (full.retired) {
                                                  return '<i title="<spring:message code="general.retired"/>" class="fa fa-times-circle fa-lg"></i>';
                                              } else {
                                                  return '<i title="<spring:message code="radiology.active"/>" class="fa fa-check-circle fa-lg"></i>';
                                              }
                                            }
                                          },
                                          {
                                            "name": "action",
                                            "className": "dt-center",
                                            "responsivePriority": 1,
                                            "render": function(data, type,
                                                    full, meta) {
                                              return '<a href="${pageContext.request.contextPath}/module/radiology/radiologyModality.form?modalityId='
                                                      + full.uuid
                                                      + '"><i class="fa fa-eye fa-lg"></i></a>';
                                            }
                                          }],
                                    });

                    find.click(function() {
                      radiologyModalitiesTable.ajax.reload();
                    });
                    clearResults
                            .on(
                                    'mouseup keyup',
                                    function() {
                                      includeAll.prop("checked", false);
                                      radiologyModalitiesTable.search('');
                                      radiologyModalitiesTable.ajax.reload();
                                    });
                  });
</script>

<openmrs:hasPrivilege privilege="Get Radiology Modalities">
  <div id="radiologyModalities">
    <openmrs:hasPrivilege privilege="Manage Radiology Modalities">
      <br>
      <a href="radiologyModality.form"><spring:message code="radiology.addModality" /></a>
      <br>
    </openmrs:hasPrivilege>
    <br> <span class="boxHeader"> <b><spring:message code="radiology.dashboard.tabs.radiologyModalities.boxheader" /></b> <a
      id="clearFilters" href="#" style="float: right"> <spring:message
          code="radiology.dashboard.tabs.filters.clearFilters" />
    </a>
    </span>
    <div class="box">
      <table cellspacing="10">
        <tr>
          <form>
            <td id="tableFilters"><label><spring:message
                  code="radiology.dashboard.tabs.filters.filterby" /></label>
            <input type="checkbox" id="includeAllFilter" name="includeAllFilter"> <label><spring:message
                  code="radiology.dashboard.tabs.reports.filters.includeAll.description" /></label></td>
            <td><input id="find" type="button"
              value="<spring:message code="radiology.dashboard.tabs.filters.filter"/>" /></td>
          </form>
        </tr>
      </table>
      <br>
      <div>
        <table id="modalitiesTable" cellspacing="0" width="100%" class="display responsive compact">
          <thead>
            <tr>
              <th></th>
              <th><spring:message code="radiology.datatables.column.modality.aeTitle" /></th>
              <th><spring:message code="radiology.datatables.column.modality.name" /></th>
              <th><spring:message code="radiology.datatables.column.modality.description" /></th>
              <th><spring:message code="radiology.datatables.column.status" /></th>
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

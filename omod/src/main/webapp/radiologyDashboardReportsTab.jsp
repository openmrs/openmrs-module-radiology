<%@ include file="/WEB-INF/view/module/radiology/dashboardHeader.jsp"%>
<openmrs:htmlInclude file="/moduleResources/radiology/vendor/jquery-date-range-picker/daterangepicker.min.css" />
<openmrs:htmlInclude file="/moduleResources/radiology/vendor/jquery-date-range-picker/jquery.daterangepicker.min.js" />
<script type="text/javascript">
  var $j = jQuery.noConflict();
  $j(document)
          .ready(
                  function() {
                    var fromDate = $j('#reportsTabFromDateFilter');
                    var toDate = $j('#reportsTabToDateFilter');
                    var principalResultsInterpreterUuid = $j('#reportsTabProviderFilter');
                    var status = $j('#reportsTabStatusSelect');
                    var includeAll = $j('#reportsTabIncludeAllFilter');
                    var find = $j('#reportsTabFind');
                    var clearResults = $j('a#reportsTabClearFilters');

                    $j("#radiologyReportsTab").parent().addClass(
                            "ui-tabs-selected ui-state-active");

                    $j('#reportsTabDateRangePicker')
                            .dateRangePicker(
                                    {
                                      startOfWeek: "monday",
                                      customTopBar: '<b class="start-day">...</b> - <b class="end-day">...</b><i class="selected-days"> (<span class="selected-days-num">3</span>)</i>',
                                      showShortcuts: true,
                                      shortcuts: {
                                        'prev-days': [3, 5, 7],
                                        'prev': ['week', 'month'],
                                        'next-days': null,
                                        'next': null
                                      },
                                      separator: '-',
                                      format: 'L',
                                      getValue: function() {
                                        if (fromDate.val() && toDate.val())
                                          return fromDate.val() + '-'
                                                  + toDate.val();
                                        else
                                          return '';
                                      },
                                      setValue: function(s, s1, s2) {
                                        fromDate.val(s1);
                                        toDate.val(s2);
                                      }
                                    });
                    
                    status.change(function() {
                      if (status.val() === "COMPLETED") {
                        $j("#reportsTabIncludeAllFilter")
                                .prop("disabled", true).prop("checked", false)
                                .next().css("color", "lightgrey");
                      } else {
                        $j("#reportsTabIncludeAllFilter").prop("disabled",
                                false).next().css("color", "black");
                      }
                    });

                    $j('#reportsTabDateRangePicker').data('dateRangePicker')
                            .setDateRange(
                                    moment().subtract(1, 'weeks').startOf(
                                            'week').format('L'),
                                    moment().subtract(1, 'weeks').endOf('week')
                                            .format('L'));
                    
                    if (typeof (Storage) !== "undefined") {
                      if ((sessionStorage.getItem("fromDate") !== null)
                              || (sessionStorage.getItem("toDate") !== null)) {
                        fromDate.val(sessionStorage.getItem("fromDate"));
                        toDate.val(sessionStorage.getItem("toDate"));
                      }
                      $j("#reportsTabProviderFilter_selection")
                              .val(
                                      sessionStorage
                                              .getItem("principalResultsInterpreterName"));
                      principalResultsInterpreterUuid.val(sessionStorage
                              .getItem("principalResultsInterpreterUuid"));
                      status.val(sessionStorage.getItem("status"));
                      if(sessionStorage.getItem("includeAll") === "true") {
                        includeAll.prop("checked", true);
                      }
                      status.change();
                    }

                    var radiologyReportsTable = $j('#reportsTabTable')
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
                                                + "/radiologyreport/",
                                        data: function(data) {
                                          return {
                                            startIndex: data.start,
                                            limit: data.length,
                                            v: "full",
                                            fromdate: fromDate.val() === ""
                                                    ? ""
                                                    : moment(fromDate.val(),
                                                            "L").format(
                                                            "YYYY-MM-DD"),
                                            todate: toDate.val() === ""
                                                    ? ""
                                                    : moment(toDate.val(), "L")
                                                            .format(
                                                                    "YYYY-MM-DD"),
                                            principalResultsInterpreter: principalResultsInterpreterUuid
                                                    .val(),
                                            status: status.val(),
                                            includeAll: includeAll
                                                    .is(':checked'),
                                            totalCount: true,
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
                                          $j("#reportsTabTable_processing")
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
                                            "name": "radiologyOrder",
                                            "responsivePriority": 1,
                                            "render": function(data, type,
                                                    full, meta) {
                                              return full.radiologyOrder.display;
                                            }
                                          },
                                          {
                                            "name": "principalResultsInterpreter",
                                            "render": function(data, type,
                                                    full, meta) {

                                              return Radiology
                                                      .getProperty(full,
                                                              "principalResultsInterpreter.display");
                                            }
                                          },
                                          {
                                            "name": "date",
                                            "render": function(data, type,
                                                    full, meta) {
                                              var result = "";
                                              if (full.date) {
                                                result = moment(full.date)
                                                        .format("LL");
                                              }
                                              return result;
                                            }
                                          },
                                          {
                                            "name": "dateCreated",
                                            "responsivePriority": 11000,
                                            "render": function(data, type,
                                                    full, meta) {
                                              var result = "";
                                              if (full.auditInfo.dateCreated) {
                                                result = moment(
                                                        full.auditInfo.dateCreated)
                                                        .format("LLL");
                                              }
                                              return result;
                                            }
                                          },
                                          {
                                            "name": "creatorBy",
                                            "responsivePriority": 11000,
                                            "render": function(data, type,
                                                    full, meta) {
                                              return full.auditInfo.creator.display;
                                            }
                                          },
                                          {
                                            "name": "status",
                                            "className": "dt-center",
                                            "render": function(data, type,
                                                    full, meta) {
                                              switch (full.status) {
                                              case "COMPLETED":
                                                return '<i title="<spring:message code="radiology.report.status.COMPLETED"/>" class="fa fa-check-circle fa-lg"></i>';
                                              case "DRAFT":
                                                if (full.voided) {
                                                  return '<i title="<spring:message code="general.voided"/>" class="fa fa-times-circle fa-lg"></i>';
                                                } else {
                                                  return '<i title="<spring:message code="radiology.report.status.DRAFT"/>" class="fa fa-circle fa-lg"></i>';
                                                }
                                              }
                                            }
                                          },
                                          {
                                            "name": "action",
                                            "className": "dt-center",
                                            "responsivePriority": 1,
                                            "render": function(data, type,
                                                    full, meta) {
                                              return '<a href="${pageContext.request.contextPath}/module/radiology/radiologyReport.form?reportId='
                                                      + full.uuid
                                                      + '"><i class="fa fa-eye fa-lg"></i></a>';
                                            }
                                          }],
                                    });

                    function storeFilters() {
                      if (typeof (Storage) !== "undefined") {
                        sessionStorage.setItem("fromDate", fromDate.val());
                        sessionStorage.setItem("toDate", toDate.val());
                        sessionStorage.setItem(
                                "principalResultsInterpreterName", $j(
                                        "#reportsTabProviderFilter_selection")
                                        .val());
                        sessionStorage.setItem(
                                "principalResultsInterpreterUuid",
                                principalResultsInterpreterUuid.val());
                        sessionStorage.setItem("status", status.val());
                        sessionStorage.setItem("includeAll", includeAll.is(":checked"));
                      }
                    }

                    $j("#reportsTabTableFilters input:visible:enabled:first")
                            .focus();
                    find.add(fromDate).add(toDate).add(
                            "#reportsTabProviderFilter_selection").add(status)
                            .add(includeAll).keypress(function(event) {
                              if (event.which == 13) {
                                event.preventDefault();
                                radiologyReportsTable.ajax.reload();
                                storeFilters();
                              }
                            });
                    find.click(function() {
                      radiologyReportsTable.ajax.reload();
                      storeFilters();
                    });
                    clearResults
                            .on(
                                    'mouseup keyup',
                                    function() {
                                      $j(
                                              '#reportsTabTableFilters input, #reportsTabTableFilters select')
                                              .val('');
                                      $j("#reportsTabIncludeAllFilter").prop(
                                              "checked", false).prop(
                                              "disabled", false).next().css(
                                              "color", "black");
                                      $j('#reportsTabDateRangePicker').data(
                                              'dateRangePicker').clear();
                                      $j(
                                              "#reportsTabTableFilters input:visible:enabled:first")
                                              .focus();
                                      radiologyReportsTable.ajax.reload();
                                      storeFilters();
                                    });
                  });
</script>

<openmrs:hasPrivilege privilege="Get Radiology Reports">
  <div id="radiologyReports">
    <br /> <span class="boxHeader"> <b><spring:message code="radiology.report.boxheader" /></b> <a
      id="reportsTabClearFilters" href="#" style="float: right"> <spring:message
          code="radiology.dashboard.tabs.filters.clearFilters" />
    </a>
    </span>
    <div class="box">
      <table cellspacing="10">
        <tr>
          <form>
            <td id="reportsTabTableFilters"><label><spring:message
                  code="radiology.dashboard.tabs.filters.filterby" /></label> <span id="reportsTabDateRangePicker"> <input
                type="text" id="reportsTabFromDateFilter"
                placeholder='<spring:message code="radiology.dashboard.tabs.reports.filters.date.from" />' /> <span>-</span>
                <input type="text" id="reportsTabToDateFilter"
                placeholder='<spring:message code="radiology.dashboard.tabs.reports.filters.date.to" />' />
            </span> <radiology:providerField formFieldName="principalResultsInterpreter" formFieldId="reportsTabProviderFilter" />
              <select id="reportsTabStatusSelect">
                <c:forEach var="reportStatus" items="${reportStatuses}">
                  <option value="${reportStatus.key}"><spring:message
                      code="radiology.report.status.${reportStatus.value}" text="${reportStatus.value}" /></option>
                </c:forEach>
            </select> <input type="checkbox" id="reportsTabIncludeAllFilter" name="includeAllFilter"
              title="<spring:message code="radiology.dashboard.tabs.reports.filters.includeAll.title"/>"> <label><spring:message
                  code="radiology.dashboard.tabs.reports.filters.includeAll.description" /></label></td>
            <td><input id="reportsTabFind" type="button"
              value="<spring:message code="radiology.dashboard.tabs.filters.filter"/>" /></td>
          </form>
        </tr>
      </table>
      <br>
      <div>
        <table id="reportsTabTable" cellspacing="0" width="100%" class="display responsive compact">
          <thead>
            <tr>
              <th></th>
              <th><spring:message code="radiology.datatables.column.report.order" /></th>
              <th><spring:message code="radiology.datatables.column.report.principalResultsInterpreter" /></th>
              <th><spring:message code="radiology.datatables.column.report.date" /></th>
              <th><spring:message code="radiology.datatables.column.report.dateCreated" /></th>
              <th><spring:message code="radiology.datatables.column.report.createdBy" /></th>
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

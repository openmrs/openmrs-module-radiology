<%@ include file="/WEB-INF/view/module/radiology/template/include.jsp"%>
<%@ include file="/WEB-INF/view/module/radiology/template/includeScripts.jsp"%>
<%@ include file="/WEB-INF/view/module/radiology/template/includeDatatablesWithDefaults.jsp"%>
<openmrs:htmlInclude file="/moduleResources/radiology/scripts/moment/moment-with-locales.min.js" />
<openmrs:htmlInclude file="/moduleResources/radiology/scripts/jquery/daterangepicker/css/daterangepicker.min.css" />
<openmrs:htmlInclude file="/moduleResources/radiology/scripts/jquery/daterangepicker/js/jquery.daterangepicker.min.js" />

<script type="text/javascript">
  // configure current locale as momentjs default, fall back to "en" if locale not found
  moment.locale([jsLocale, 'en']);

  var $j = jQuery.noConflict();
  $j(document)
          .ready(
                  function() {
                    var fromDate = $j('#reportsTabFromDateFilter');
                    var toDate = $j('#reportsTabToDateFilter');
                    var principalResultsInterpreterUuid = $j('#reportsTabProviderFilter');
                    var status = $j('#reportsTabStatusSelect');
                    var find = $j('#reportsTabFind');
                    var clearResults = $j('a#reportsTabClearFilters');

                    fromDate.val(moment().subtract(1, 'weeks').startOf('week')
                            .format('YYYY-MM-DD'));
                    toDate.val(moment().subtract(1, 'weeks').endOf('week')
                            .format('YYYY-MM-DD'));

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
                                            fromdate: fromDate.val(),
                                            todate: toDate.val(),
                                            principalResultsInterpreter: principalResultsInterpreterUuid
                                                    .val(),
                                            status: status.val(),
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
                                            "name": "radiologyOrder",
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
                                              if (full.reportDate) {
                                                result = moment(full.reportDate)
                                                        .format("LL");
                                              }
                                              return result;
                                            }
                                          },
                                          {
                                            "name": "dateCreated",
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
                                            "render": function(data, type,
                                                    full, meta) {
                                              return full.auditInfo.creator.display;
                                            }
                                          },
                                          {
                                            "name": "reportStatus",
                                            "className": "dt-center",
                                            "render": function(data, type,
                                                    full, meta) {
                                              switch (full.reportStatus) {
                                              case "COMPLETED":
                                                return '<i title="<spring:message code="radiology.report.status.COMPLETED"/>" class="fa fa-check-circle fa-lg"></i>';
                                              case "CLAIMED":
                                                return '<i title="<spring:message code="radiology.report.status.CLAIMED"/>" class="fa fa-circle fa-lg"></i>';
                                              case "DISCONTINUED":
                                                return '<i title="<spring:message code="radiology.report.status.DISCONTINUED"/>" class="fa fa-times-circle fa-lg"></i>';
                                              }
                                            }
                                          },
                                          {
                                            "name": "action",
                                            "className": "dt-center",
                                            "render": function(data, type,
                                                    full, meta) {
                                              return '<a href="${pageContext.request.contextPath}/module/radiology/radiologyReport.form?radiologyReportId='
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
                      radiologyReportsTable.ajax.reload();
                    });

                    clearResults
                            .on(
                                    'mouseup keyup',
                                    function() {
                                      $j(
                                              '#reportsTabTableFilterFields input, #reportsTabTableFilterFields select')
                                              .val('');
                                      $j('#reportsTabDateRangePicker').data(
                                              'dateRangePicker').clear();
                                      radiologyReportsTable.ajax.reload();
                                    });

                    $j('#reportsTabDateRangePicker')
                            .dateRangePicker(
                                    {
                                      showShortcuts: true,
                                      shortcuts: {
                                        'prev-days': [3, 5, 7],
                                        'prev': ['week', 'month'],
                                        'next-days': null,
                                        'next': null
                                      },
                                      separator: ' <spring:message code="radiology.dashboard.tabs.reports.filters.date.to"/> ',
                                      getValue: function() {
                                        if (fromDate.val() && toDate.val())
                                          return fromDate.val()
                                                  + ' <spring:message code="radiology.dashboard.tabs.reports.filters.date.to"/> '
                                                  + toDate.val();
                                        else
                                          return '';
                                      },
                                      setValue: function(s, s1, s2) {
                                        fromDate.val(s1);
                                        toDate.val(s2);
                                      }
                                    });
                  });
</script>

<br>
<span class="boxHeader"> <b><spring:message code="radiology.report.boxheader" /></b> <a id="reportsTabClearFilters"
  href="#" style="float: right"> <spring:message code="radiology.dashboard.tabs.filters.clearFilters" />
</a>
</span>
<div class="box">
  <table cellspacing="10">
    <tr>
      <form>
        <td id="reportsTabTableFilterFields"><span id="reportsTabDateRangePicker"> <label
            for="reportsTabFromDateFilter"> <spring:message code="radiology.dashboard.tabs.reports.filters.date" />
              <spring:message code="radiology.dashboard.tabs.reports.filters.date.from" />
          </label> <input type="text" id="reportsTabFromDateFilter" /> <label for="reportsTabToDateFilter"> <spring:message
                code="radiology.dashboard.tabs.reports.filters.date.to" />
          </label> <input type="text" id="reportsTabToDateFilter" />
        </span> <label for="reportsTabProviderFilter"> <spring:message
              code="radiology.dashboard.tabs.reports.filters.principalResultsInterpreter" />
        </label> <radiology:providerField formFieldName="principalResultsInterpreter" formFieldId="reportsTabProviderFilter" /> <label
          for="reportsTabStatusSelect"> <spring:message code="radiology.dashboard.tabs.reports.filters.status" />
        </label> <select id="reportsTabStatusSelect">
            <c:forEach var="radiologyReportStatus" items="${model.radiologyReportStatuses}">
              <option value="${radiologyReportStatus}">
                <spring:message code="radiology.report.status.${radiologyReportStatus}" text="${radiologyReportStatus}" />
              </option>
            </c:forEach>
        </select></td>
        <td><input id="reportsTabFind" type="button"
          value="<spring:message code="radiology.dashboard.tabs.filters.filter"/>" /></td>
      </form>
    </tr>
  </table>
  <br>
  <div>
    <table id="reportsTabTable" cellspacing="0" width="100%" class="display nowrap">
      <thead>
        <tr>
          <th><spring:message code="radiology.datatables.column.report.order" /></th>
          <th><spring:message code="radiology.datatables.column.report.principalResultsInterpreter" /></th>
          <th><spring:message code="radiology.datatables.column.report.date" /></th>
          <th><spring:message code="radiology.datatables.column.report.dateCreated" /></th>
          <th><spring:message code="radiology.datatables.column.report.createdBy" /></th>
          <th><spring:message code="radiology.datatables.column.report.status" /></th>
          <th><spring:message code="radiology.datatables.column.action" /></th>
        </tr>
      </thead>
    </table>
  </div>
</div>
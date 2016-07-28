<%@ include file="/WEB-INF/view/module/radiology/template/include.jsp"%>
<%@ include file="/WEB-INF/view/module/radiology/template/includeScripts.jsp"%>
<%@ include file="/WEB-INF/view/module/radiology/template/includeDatatablesWithDefaults.jsp"%>
<openmrs:htmlInclude file="/moduleResources/radiology/scripts/moment/moment-with-locales.min.js" />

<script type="text/javascript">
  // configure current locale as momentjs default, fall back to "en" if locale not found
  moment.locale([jsLocale, 'en']);

  var $j = jQuery.noConflict();
  $j(document)
          .ready(
                  function() {
                    var patientUuid = $j('#ordersTabPatientFilter');
                    var urgency = $j('#ordersTabUrgencySelect');
                    var find = $j('#ordersTabFind');
                    var clearResults = $j('a#ordersTabClearFilters');

                    var radiologyOrdersTable = $j('#ordersTabTable')
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
                                                + "/radiologyorder/",
                                        data: function(data) {
                                          return {
                                            startIndex: data.start,
                                            limit: data.length,
                                            v: "full",
                                            patient: patientUuid.val(),
                                            urgency: urgency.val(),
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
                                            "className": "expand",
                                            "orderable": false,
                                            "data": null,
                                            "defaultContent": "",
                                            "render": function() {
                                              return '<i class="fa fa-chevron-circle-down fa-lg"></i>';
                                            }
                                          },
                                          {
                                            "name": "orderNumber",
                                            "render": function(data, type,
                                                    full, meta) {
                                              return full.orderNumber;
                                            }
                                          },
                                          {
                                            "name": "patient",
                                            "render": function(data, type,
                                                    full, meta) {
                                              return full.patient.display;
                                            }
                                          },
                                          {
                                            "name": "urgency",
                                            "render": function(data, type,
                                                    full, meta) {
                                              switch (full.urgency) {
                                              case "ROUTINE":
                                                return '<spring:message code="radiology.order.urgency.ROUTINE"/>';
                                              case "STAT":
                                                return '<spring:message code="radiology.order.urgency.STAT"/>';
                                              case "ON_SCHEDULED_DATE":
                                                return '<spring:message code="radiology.order.urgency.ON_SCHEDULED_DATE"/>';
                                              }
                                            }
                                          },
                                          {
                                            "name": "concept",
                                            "render": function(data, type,
                                                    full, meta) {
                                              return full.concept.display;
                                            }
                                          },
                                          {
                                            "name": "orderer",
                                            "render": function(data, type,
                                                    full, meta) {
                                              return full.orderer.display;
                                            }
                                          },
                                          {
                                            "name": "scheduledDate",
                                            "render": function(data, type,
                                                    full, meta) {
                                              var result = "";
                                              if (full.scheduledDate) {

                                                result = moment(
                                                        full.scheduledDate)
                                                        .format("LLL");
                                              }
                                              return result;
                                            }
                                          },
                                          {
                                            "name": "dateActivated",
                                            "render": function(data, type,
                                                    full, meta) {
                                              var result = "";
                                              if (full.dateActivated) {

                                                result = moment(
                                                        full.dateActivated)
                                                        .format("LLL");
                                              }
                                              return result;
                                            }
                                          },
                                          {
                                            "name": "action",
                                            "className": "dt-center",
                                            "render": function(data, type,
                                                    full, meta) {
                                              return '<a href="${pageContext.request.contextPath}/module/radiology/radiologyOrder.form?orderId='
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
                      radiologyOrdersTable.ajax.reload();
                    });

                    clearResults.on('mouseup keyup', function() {
                      $j('table#ordersTabTableFilters input:text').val('');
                      patientUuid.val('');
                      radiologyOrdersTable.ajax.reload();
                    });

                    function formatChildRow(data) {
                      var orderReason = Radiology.getProperty(data,
                              "orderReason.display");
                      var orderReasonNonCoded = Radiology.getProperty(data,
                              "orderReasonNonCoded");
                      var instructions = Radiology.getProperty(data,
                              "instructions");
                      return '<table cellpadding="5" cellspacing="0" border="0" style="padding-left:50px;">'
                              + '<tr>'
                              + '<td><spring:message code="radiology.datatables.column.order.reason"/>:</td>'
                              + '<td>'
                              + orderReason
                              + '</td>'
                              + '</tr>'
                              + '<tr>'
                              + '<td><spring:message code="radiology.datatables.column.order.reasonNonCoded"/>:</td>'
                              + '<td>'
                              + orderReasonNonCoded
                              + '</td>'
                              + '</tr>'
                              + '<tr>'
                              + '<td><spring:message code="radiology.datatables.column.order.instructions"/>:</td>'
                              + '<td>'
                              + instructions
                              + '</td>'
                              + '</tr>'
                              + '</table>';
                    }

                    $j('#ordersTabTable tbody')
                            .on(
                                    'click',
                                    'td',
                                    function(e) {
                                      if ($j(e.target).is(':not(td)')) { return; }

                                      var tr = $j(this).closest('tr');
                                      var row = radiologyOrdersTable.row(tr);
                                      var expandIconField = tr.find('.expand');

                                      if (row.child.isShown()) {
                                        row.child.hide();
                                        expandIconField
                                                .html("<i class='fa fa-chevron-circle-down fa-lg'></i>");
                                        tr.removeClass('shown');
                                      } else {
                                        row.child(formatChildRow(row.data()))
                                                .show();
                                        expandIconField
                                                .html("<i class='fa fa-chevron-circle-up fa-lg'></i>");
                                        tr.addClass('shown');
                                      }
                                    });

                  });
</script>

<openmrs:hasPrivilege privilege="Add Radiology Orders">
  <br>
  <a href="radiologyOrder.form"><spring:message code="radiology.addOrder" /></a>
  <br>
</openmrs:hasPrivilege>
<br>
<span class="boxHeader"> <b><spring:message code="radiology.radiologyOrders" /></b> <a id="ordersTabClearFilters"
  href="#" style="float: right"> <spring:message code="radiology.dashboard.tabs.filters.clearFilters" />
</a>
</span>
<div class="box">
  <table id="ordersTabTableFilters" cellspacing="10">
    <tr>
      <form>
        <td><label><spring:message code="radiology.dashboard.tabs.filters.filterby" /></label> <radiology:patientField
            formFieldName="patient" formFieldId="ordersTabPatientFilter" /> <select id="ordersTabUrgencySelect">
            <c:forEach var="urgency" items="${model.urgencies}">
              <option value='${urgency}'>
                <c:choose>
                  <c:when test="${not empty urgency}">
                    <spring:message code="radiology.order.urgency.${urgency}" text="${urgency}" />
                  </c:when>
                  <c:otherwise>
                    <spring:message code="radiology.order.urgency.allurgencies" />
                  </c:otherwise>
                </c:choose>
              </option>
            </c:forEach>
        </select></td>
        <td><input id="ordersTabFind" type="button"
          value="<spring:message code="radiology.dashboard.tabs.filters.filter"/>" /></td>
      </form>
    </tr>
  </table>
  <br>
  <div>
    <table id="ordersTabTable" cellspacing="0" width="100%" class="display nowrap">
      <thead>
        <tr>
          <th></th>
          <th><spring:message code="radiology.datatables.column.order.orderNumber" /></th>
          <th><spring:message code="radiology.datatables.column.order.patient" /></th>
          <th><spring:message code="radiology.datatables.column.order.urgency" /></th>
          <th><spring:message code="radiology.datatables.column.order.imagingProcedure" /></th>
          <th><spring:message code="radiology.datatables.column.order.referringPhysician" /></th>
          <th><spring:message code="radiology.datatables.column.order.scheduledDate" /></th>
          <th><spring:message code="radiology.datatables.column.order.dateActivated" /></th>
          <th><spring:message code="radiology.datatables.column.action" /></th>
        </tr>
      </thead>
    </table>
  </div>
</div>
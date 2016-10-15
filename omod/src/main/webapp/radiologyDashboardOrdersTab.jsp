<%@ include file="/WEB-INF/view/module/radiology/dashboardHeader.jsp"%>
<openmrs:htmlInclude file="/moduleResources/radiology/vendor/jquery-date-range-picker/daterangepicker.min.css" />
<openmrs:htmlInclude file="/moduleResources/radiology/vendor/jquery-date-range-picker/jquery.daterangepicker.min.js" />
<script type="text/javascript">
  var $j = jQuery.noConflict();

  $j(document)
          .ready(
                  function() {
                    var accessionNumber = $j('#ordersTabAccessionNumberFilter');
                    var patientUuid = $j('#ordersTabPatientFilter');
                    var fromEffectiveStartDate = $j('#ordersTabFromEffectiveStartDateFilter');
                    var toEffectiveStartDate = $j('#ordersTabToEffectiveStartDateFilter');
                    var urgency = $j('#ordersTabUrgencySelect');
                    var find = $j('#ordersTabFind');
                    var clearResults = $j('a#ordersTabClearFilters');

                    if (typeof (Storage) !== "undefined") {
                      accessionNumber.val(sessionStorage
                              .getItem("accessionNumber"));
                      $j("#ordersTabPatientFilter_selection").val(
                              sessionStorage.getItem("patientName"));
                      patientUuid.val(sessionStorage.getItem("patientUuid"));
                      fromEffectiveStartDate.val(sessionStorage
                              .getItem("fromEffectiveStartDate"));
                      toEffectiveStartDate.val(sessionStorage
                              .getItem("toEffectiveStartDate"));
                      urgency.val(sessionStorage.getItem("urgency"));
                    }

                    $j("#radiologyOrdersTab").parent().addClass(
                            "ui-tabs-selected ui-state-active");

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
                                            accessionNumber: accessionNumber
                                                    .val(),
                                            patient: patientUuid.val(),
                                            fromEffectiveStartDate: fromEffectiveStartDate
                                                    .val() === ""
                                                    ? ""
                                                    : moment(
                                                            fromEffectiveStartDate
                                                                    .val(),
                                                            "L LT")
                                                            .format(
                                                                    "YYYY-MM-DDTHH:mm:ss.SSSZ"),
                                            toEffectiveStartDate: toEffectiveStartDate
                                                    .val() === ""
                                                    ? ""
                                                    : moment(
                                                            toEffectiveStartDate
                                                                    .val(),
                                                            "L LT")
                                                            .format(
                                                                    "YYYY-MM-DDTHH:mm:ss.SSSZ"),
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
                                        },
                                        error: function(jqXHR, textStatus,
                                                errorThrown) {
                                          Radiology
                                                  .showAlertDialog(
                                                          '<spring:message code="radiology.rest.error.dialog.title"/>',
                                                          '<spring:message code="radiology.rest.error.dialog.message.line1"/><br />'
                                                                  + '<spring:message code="radiology.rest.error.dialog.message.line2"/>',
                                                          '<spring:message code="radiology.rest.error.dialog.button.ok"/>');
                                          $j("#ordersTabTable_processing")
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
                                            "name": "accessionNumber",
                                            "responsivePriority": 1,
                                            "render": function(data, type,
                                                    full, meta) {
                                              return full.accessionNumber;
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
                                            "responsivePriority": 11000,
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
                                            "name": "dateStopped",
                                            "render": function(data, type,
                                                    full, meta) {
                                              var result = "";
                                              if (full.dateStopped) {

                                                result = moment(
                                                        full.dateStopped)
                                                        .format("LLL");
                                              }
                                              return result;
                                            }
                                          },
                                          {
                                            "name": "orderReason",
                                            "className": "none",
                                            "render": function(data, type,
                                                    full, meta) {
                                              return Radiology.getProperty(
                                                      full,
                                                      "orderReason.display");
                                            }
                                          },
                                          {
                                            "name": "orderReasonNonCoded",
                                            "className": "none",
                                            "render": function(data, type,
                                                    full, meta) {
                                              return Radiology.getProperty(
                                                      full,
                                                      "orderReasonNonCoded");
                                            }
                                          },
                                          {
                                            "name": "instructions",
                                            "className": "none",
                                            "render": function(data, type,
                                                    full, meta) {
                                              return Radiology.getProperty(
                                                      full, "instructions");
                                            }
                                          },
                                          {
                                            "name": "action",
                                            "className": "dt-center",
                                            "responsivePriority": 1,
                                            "render": function(data, type,
                                                    full, meta) {
                                              return '<a href="${pageContext.request.contextPath}/module/radiology/radiologyOrder.form?orderId='
                                                      + full.uuid
                                                      + '"><i class="fa fa-eye fa-lg"></i></a>';
                                            }
                                          }],
                                    });

                    function storeFilters() {
                      if (typeof (Storage) !== "undefined") {
                        sessionStorage.setItem("accessionNumber",
                                accessionNumber.val());
                        sessionStorage.setItem("patientName", $j(
                                "#ordersTabPatientFilter_selection").val());
                        sessionStorage
                                .setItem("patientUuid", patientUuid.val());
                        sessionStorage.setItem("fromEffectiveStartDate",
                                fromEffectiveStartDate.val());
                        sessionStorage.setItem("toEffectiveStartDate",
                                toEffectiveStartDate.val());
                        sessionStorage.setItem("urgency", urgency.val());
                      }
                    }

                    $j("#ordersTabTableFilters input:visible:enabled:first")
                            .focus();
                    find.add(accessionNumber).add(
                            "#ordersTabPatientFilter_selection").add(
                            fromEffectiveStartDate).add(toEffectiveStartDate)
                            .add(urgency).keypress(function(event) {
                              if (event.which == 13) {
                                event.preventDefault();
                                radiologyOrdersTable.ajax.reload();
                                storeFilters();
                              }
                            });
                    find.click(function() {
                      radiologyOrdersTable.ajax.reload();
                      storeFilters();
                    });
                    clearResults
                            .on(
                                    'mouseup keyup',
                                    function() {
                                      $j(
                                              '#ordersTabTableFilters input, #ordersTabTableFilters select')
                                              .val('');
                                      $j(
                                              '#ordersTabEffectiveStartDateRangePicker')
                                              .data('dateRangePicker').clear();
                                      $j(
                                              "#ordersTabTableFilters input:visible:enabled:first")
                                              .focus();
                                      radiologyOrdersTable.ajax.reload();
                                      storeFilters();
                                    });

                    $j('#ordersTabEffectiveStartDateRangePicker')
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
                                      format: 'L LT',
                                      time: {
                                        enabled: true
                                      },
                                      defaultTime: moment().startOf('day')
                                              .toDate(),
                                      defaultEndTime: moment().endOf('day')
                                              .toDate(),
                                      getValue: function() {
                                        if (fromEffectiveStartDate.val()
                                                && toEffectiveStartDate.val())
                                          return fromEffectiveStartDate.val()
                                                  + '-'
                                                  + toEffectiveStartDate.val();
                                        else
                                          return '';
                                      },
                                      setValue: function(s, s1, s2) {
                                        fromEffectiveStartDate.val(s1);
                                        toEffectiveStartDate.val(s2);
                                      }
                                    });
                  });
</script>

<openmrs:hasPrivilege privilege="View Orders">
  <div id="radiologyOrders">
    <openmrs:hasPrivilege privilege="Add Radiology Orders">
      <br>
      <a href="radiologyOrder.form"><spring:message code="radiology.addOrder" /></a>
      <br>
    </openmrs:hasPrivilege>
    <br> <span class="boxHeader"> <b><spring:message code="radiology.radiologyOrders" /></b> <a
      id="ordersTabClearFilters" href="#" style="float: right"> <spring:message
          code="radiology.dashboard.tabs.filters.clearFilters" />
    </a>
    </span>
    <div class="box">
      <table cellspacing="10">
        <tr>
          <form>
            <td id="ordersTabTableFilters"><label><spring:message
                  code="radiology.dashboard.tabs.filters.filterby" /></label> <input type="text" id="ordersTabAccessionNumberFilter"
              placeholder='<spring:message code="radiology.dashboard.tabs.orders.filters.accessionNumber"/>' /> <radiology:patientField
                formFieldName="patient" formFieldId="ordersTabPatientFilter" /> <span
              id="ordersTabEffectiveStartDateRangePicker"> <input type="text"
                id="ordersTabFromEffectiveStartDateFilter"
                placeholder='<spring:message code="radiology.dashboard.tabs.orders.filters.effectiveStartDate.from" />' /> <span>-</span>
                <input type="text" id="ordersTabToEffectiveStartDateFilter"
                placeholder='<spring:message code="radiology.dashboard.tabs.orders.filters.effectiveStartDate.to" />' />
            </span><select id="ordersTabUrgencySelect">
                <c:forEach var="urgency" items="${urgencies}">
                  <option value='${urgency.key}'><spring:message code="radiology.order.urgency.${urgency.value}"
                      text="${urgency.value}" /></option>
                </c:forEach>
            </select></td>
            <td><input id="ordersTabFind" type="button"
              value="<spring:message code="radiology.dashboard.tabs.filters.filter"/>" /></td>
          </form>
        </tr>
      </table>
      <br>
      <div>
        <table id="ordersTabTable" cellspacing="0" width="100%" class="display responsive compact">
          <thead>
            <tr>
              <th></th>
              <th><spring:message code="radiology.datatables.column.order.accessionNumber" /></th>
              <th><spring:message code="radiology.datatables.column.order.patient" /></th>
              <th><spring:message code="radiology.datatables.column.order.urgency" /></th>
              <th><spring:message code="radiology.datatables.column.order.imagingProcedure" /></th>
              <th><spring:message code="radiology.datatables.column.order.referringPhysician" /></th>
              <th><spring:message code="radiology.datatables.column.order.scheduledDate" /></th>
              <th><spring:message code="radiology.datatables.column.order.dateActivated" /></th>
              <th><spring:message code="radiology.datatables.column.order.dateStopped" /></th>
              <th><spring:message code="radiology.datatables.column.order.reason" /></th>
              <th><spring:message code="radiology.datatables.column.order.reasonNonCoded" /></th>
              <th><spring:message code="radiology.datatables.column.order.instructions" /></th>
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

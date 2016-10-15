<%@ include file="/WEB-INF/template/include.jsp"%>
<%@ include file="/WEB-INF/view/module/radiology/template/includeDatatables.jsp"%>
<openmrs:htmlInclude file="/moduleResources/radiology/vendor/moment/min/moment-with-locales.min.js" />
<openmrs:htmlInclude file="/moduleResources/radiology/css/radiology.css" />
<openmrs:htmlInclude file="/moduleResources/radiology/js/radiology.js" />

<script type="text/javascript">
  // configure current locale as momentjs default, fall back to "en" if locale not found
  moment.locale([jsLocale, 'en']);

  var $j = jQuery.noConflict();
  $j(document)
          .ready(
                  function() {
                    var radiologyOrdersTable = $j('#radiologyOrdersTable')
                            .DataTable(
                                    {
                                      "info": true,
                                      "searching": false,
                                      "ordering": false,
                                      "language": {
                                        "zeroRecords": '<spring:message code="radiology.datatables.noresult"/>',
                                        "processing": '<spring:message code="radiology.datatables.loading"/>',
                                        "info": '<spring:message code="radiology.datatables.viewing"/> _START_ - _END_ <spring:message code="radiology.datatables.of"/> _TOTAL_',
                                        "infoEmpty": '<spring:message code="radiology.datatables.viewing"/> 0 <spring:message code="radiology.datatables.of"/> 0',
                                        "lengthMenu": '<spring:message code="radiology.datatables.show"/> _MENU_ <spring:message code="radiology.datatables.entries"/>',
                                        "paginate": {
                                          "first": '<spring:message code="radiology.datatables.first"/>',
                                          "previous": '<spring:message code="radiology.datatables.previous"/>',
                                          "next": '<spring:message code="radiology.datatables.next"/>',
                                          "last": '<spring:message code="radiology.datatables.last"/>',
                                        },
                                      },
                                      responsive: {
                                        "autoWidth": false,
                                        details: {
                                          type: "column",
                                          target: "tr > td:not(:has(a))",
                                          renderer: function(api, rowIdx,
                                                  columns) {
                                            var data = $j
                                                    .map(
                                                            columns,
                                                            function(col, i) {
                                                              return col.hidden
                                                                      ? '<tr data-dt-row="'+col.rowIndex+'" data-dt-column="'+col.columnIndex+'">'
                                                                              + '<td style="font-weight: bold;">'
                                                                              + col.title
                                                                              + ':'
                                                                              + '</td> '
                                                                              + '<td>'
                                                                              + col.data
                                                                              + '</td>'
                                                                              + '</tr>'
                                                                      : '';
                                                            }).join('');

                                            return data
                                                    ? $j(
                                                            '<table style="padding-left:50px;"/>')
                                                            .append(data)
                                                    : false;
                                          }
                                        }
                                      },
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
                                            patient: $j("#patientUuid").val(),
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
                                            "name": "accessionNumber",
                                            "responsivePriority": 1,
                                            "render": function(data, type,
                                                    full, meta) {
                                              return full.accessionNumber;
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
                                            "responsivePriority": 11000,
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
                  });
</script>

<openmrs:hasPrivilege privilege="Add Orders">
  <p>
    <a href="module/radiology/radiologyOrder.form?patientId=${patient.patientId}"><spring:message
        code="radiology.addOrder" /></a> <br />
  </p>
</openmrs:hasPrivilege>
<span class="boxHeader"> <b><spring:message code="radiology.radiologyOrders" /></b>
</span>
<div class="box">
  <br>
  <div id="results">
    <table id="radiologyOrdersTable" cellspacing="0" width="100%" class="display responsive compact">
      <thead>
        <tr>
          <th></th>
          <th><spring:message code="radiology.datatables.column.order.accessionNumber" /></th>
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
<input type="hidden" id="patientUuid" value="${patient.uuid}" />

var $j = jQuery.noConflict();
$j(document)
        .ready(
                function() {
                  pQuery = $j('input[name="patientQuery"]');
                  loading = $j('div#openmrs_msg[name="loading"]');
                  startDate = $j('input[name="startDate"]');
                  endDate = $j('input[name="endDate"]');
                  find = $j('#findButton');
                  results = $j('#results');
                  clearResults = $j('a#clearResults');

                  firstTime();
                  function firstTime() {
                    sendRequest();
                  }

                  function sendRequest() {

                    $j('#errorSpan').html('');
                    var oTable = $j('#radiologyOrdersTable')
                            .DataTable(
                                    {
                                      "processing": true,
                                      "serverSide": true,
                                      "ajax": {
                                        headers: {
                                          Accept: "application/json; charset=utf-8",
                                          "Content-Type": "text/plain; charset=utf-8"
                                        },
                                        dataType: "json",
                                        url: "http://localhost:8080/openmrs/ws/rest/v1/radiologyorder/63359e3b-2e40-411a-9449-139b26de839a",
                                        "dataSrc": function(json) {
                                          //for ( var i=0, ien=json.length ; i<ien ; i++ ) {
                                          console.log(json);
                                          //}
                                          return [[
                                              "OpenChildRow",
                                              '<a href="http://localhost:8080/openmrs/module/radiology/radiologyOrder.form?orderId='
                                                      + json.uuid
                                                      + '">'
                                                      + json.orderNumber
                                                      + '</a>',
                                              json.patient.display,
                                              json.urgency, json.dateActivated,
                                              "modality", "pstatus",
                                              json.orderer.display, "sstatus",
                                              json.instructions]];
                                        }
                                      },
                                      "order": [[1, 'asc']],
                                      "oLanguage": {
                                        "sLengthMenu": '<spring:message code="radiology.show"/>'
                                                + ' _MENU_ <spring:message code="radiology.entries"/>',
                                        "sSearch": '<spring:message code="general.search"/>:',
                                        "sInfo": '<spring:message code="radiology.viewing"/> _START_ '
                                                + '- _END_ '
                                                + '<spring:message code="radiology.of"/> _TOTAL_',
                                        "oPaginate": {
                                          "sFirst": '<spring:message code="radiology.first"/>',
                                          "sPrevious": '<spring:message code="general.previous"/>',
                                          "sNext": '<spring:message code="general.next"/>',
                                          "sLast": '<spring:message code="radiology.last"/>',
                                        },
                                        "sProcessing": '<spring:message code="general.loading"/>'
                                      },
                                    });
                  }

                  // ***********Events*************
                  find.click(function() {
                    page = 0;
                    sendRequest();
                  });

                  pQuery.keypress(function(event) {
                    if (event.which == '13') {
                      sendRequest();
                    }
                  });

                  clearResults.click(function() {
                    $j('table#searchForm input:text').val('');
                    $j('tbody#radiologyOrdersTableBody').html('');
                  });
                });

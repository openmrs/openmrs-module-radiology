var $j = jQuery.noConflict();
$j(document)
        .ready(
                function() {
                  patientQuery = $j('input[name="patientQuery"]');
                  find = $j('#findButton');
                  clearResults = $j('a#clearResults');

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
                                        console.log(data);
                                        return {
                                          startIndex: data.start,
                                          limit: data.length,
                                          v: "full",
                                          patient: patientQuery.val(),
                                        };
                                      },
                                      "dataSrc": function(json) {
                                        console.log(json);
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
                                    "oLanguage": {
                                      "sLengthMenu": '<spring:message code="radiology.show"/>'
                                              + ' _MENU_ <spring:message code="radiology.entries"/>',
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
                    radiologyOrdersTable.ajax.reload();
                  });
                });

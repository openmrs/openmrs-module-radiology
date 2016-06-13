var $j = jQuery.noConflict();
$j(document)
        .ready(
                function() {
                  patientUuid = $j('#patientUuid');
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
                                          patient: patientUuid.val(),
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
                    patientUuid.val('');
                    radiologyOrdersTable.ajax.reload();
                  });
                });

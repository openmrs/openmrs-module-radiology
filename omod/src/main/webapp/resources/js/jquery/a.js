$j("#radiologyOrdersTable")
        .radiologyDataTable(
                {
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
                                  + json.results[i].orderNumber + '</a>',
                          json.results[i].patient.display,
                          json.results[i].urgency,
                          json.results[i].concept.display,
                          json.results[i].orderer.display,
                          json.results[i].scheduledDate,
                          json.results[i].dateActivated, ]
                    }
                    return result;
                  },
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
                  }, ]
                });

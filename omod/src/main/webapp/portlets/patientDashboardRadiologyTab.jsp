<%@ include file="/WEB-INF/view/module/radiology/template/include.jsp"%>
<%@ include file="/WEB-INF/view/module/radiology/template/includeDatatables.jsp"%>

<script type="text/javascript">
  var $j = jQuery.noConflict();
  $j(document)
          .ready(
                  function() {
                    $j('#radiologyOrdersTable')
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
                                        url: "${pageContext.request.contextPath}/ws/rest/v1/radiologyorder/",
                                        data: function(data) {
                                          return {
                                            startIndex: data.start,
                                            limit: data.length,
                                            v: "full",
                                            patient: $j("#patientUuid").val(),
                                          };
                                        },
                                        "dataSrc": function(json) {
                                          var result = [];
                                          for (var i = 0, ien = json.results.length; i < ien; i++) {
                                            result[i] = [
                                                '<a href="${pageContext.request.contextPath}/radiology/radiologyOrder.form?orderId='
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
                  });
</script>

<openmrs:hasPrivilege privilege="Add Orders">
  <p>
    <a href="module/radiology/radiologyOrder.form?patientId=${patient.patientId}"><spring:message
        code="radiology.addOrder" /></a> <br />
  </p>
</openmrs:hasPrivilege>

<div id="results">
  <table id="radiologyOrdersTable" cellspacing="0" width="100%" class="display nowrap">
    <thead>
      <tr>
        <th><spring:message code="radiology.datatables.column.orderNumber" /></th>
        <th><spring:message code="radiology.datatables.column.patient" /></th>
        <th><spring:message code="radiology.datatables.column.priority" /></th>
        <th><spring:message code="radiology.datatables.column.imagingProcedure" /></th>
        <th><spring:message code="radiology.datatables.column.referringPhysician" /></th>
        <th><spring:message code="radiology.datatables.column.scheduledDate" /></th>
        <th><spring:message code="radiology.datatables.column.dateActivated" /></th>
      </tr>
    </thead>
  </table>
</div>
<input type="hidden" id="patientUuid" value="${patient.uuid}" />
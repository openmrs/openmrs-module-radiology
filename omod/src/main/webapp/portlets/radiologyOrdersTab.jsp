<%@ include file="/WEB-INF/view/module/radiology/template/include.jsp"%>
<%@ include file="/WEB-INF/view/module/radiology/template/includeDatatables.jsp"%>

<script type="text/javascript">
  var $j = jQuery.noConflict();
  $j(document)
          .ready(
                  function() {
                    var patientUuid = $j('#patientUuid');
                    var find = $j('#findButton');
                    var clearResults = $j('a#clearResults');

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
                                        url: "${pageContext.request.contextPath}/ws/rest/v1/radiologyorder/",
                                        data: function(data) {
                                          return {
                                            startIndex: data.start,
                                            limit: data.length,
                                            v: "full",
                                            patient: patientUuid.val(),
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
                      patientUuid.val('');
                      radiologyOrdersTable.ajax.reload();
                    });
                  });
</script>

<openmrs:hasPrivilege privilege="Add Radiology Orders">
  <br>
  <a href="radiologyOrder.form"><spring:message code="radiology.addOrder" /></a>
  <br>
</openmrs:hasPrivilege>
<br>
<span class="boxHeader"> <b><spring:message code="radiology.radiologyOrders" /></b> <a id="clearResults" href="#"
  style="float: right"> <spring:message code="radiology.clearResults" />
</a>
</span>
<div class="box">
  <table id="searchForm" cellspacing="10">
    <tr>
      <form id="radiologyOrderListForm">
        <td><label><spring:message code="radiology.patient" /></label> <radiology:patientField formFieldName="patient"
            formFieldId="patientUuid" /></td>
        <td><input id="findButton" type="button" value="<spring:message code="radiology.find"/>" /></td>
      </form>
    </tr>
  </table>
  <br>
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
</div>
<br />

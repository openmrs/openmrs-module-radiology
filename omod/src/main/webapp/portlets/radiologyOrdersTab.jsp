<%@ include file="/WEB-INF/view/module/radiology/include.jsp"%>

<openmrs:htmlInclude file="/scripts/jquery-ui/js/jquery-ui-1.7.2.custom.min.js" />
<openmrs:htmlInclude file="/moduleResources/radiology/js/datatables/jquery.dataTables.min.js" />
<script type="text/javascript">
  jQuery.extend(true, jQuery.fn.dataTable.defaults, {
    "language": {
      "paginate": {
        "first": '<spring:message code="radiology.first"/>',
        "previous": '<spring:message code="general.previous"/>',
        "next": '<spring:message code="general.next"/>',
        "last": '<spring:message code="radiology.last"/>',
      },
      "processing": '<spring:message code="general.loading"/>'
    },
  });
</script>

<openmrs:htmlInclude file="/moduleResources/radiology/js/radiologyOrderList.js" />

<openmrs:htmlInclude file="/moduleResources/radiology/css/jquery.dataTables.min.css" />
<openmrs:htmlInclude file="/moduleResources/radiology/css/details-control.dataTables.css" />

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
        <td><label><spring:message code="radiology.patient" /></label> <radiology:patientField
            formFieldName="patientQuery" /></td>
        <td><input id="findButton" type="button" value="<spring:message code="radiology.find"/>" /></td>
      </form>
    </tr>
  </table>
  <br>
  <div id="results">
    <table id="radiologyOrdersTable" cellspacing="0" width="100%" class="display nowrap">
      <thead>
        <tr>
          <th><spring:message code="radiology.orderNumber" /></th>
          <th><spring:message code="Order.patient" /></th>
          <th><spring:message code="radiology.priority" /></th>
          <th><spring:message code="radiology.imagingProcedure" /></th>
          <th><spring:message code="radiology.referringPhysician" /></th>
          <th><spring:message code="radiology.scheduledDate" /></th>
          <th><spring:message code="radiology.dateActivated" /></th>
        </tr>
      </thead>
    </table>
  </div>

</div>
<br />

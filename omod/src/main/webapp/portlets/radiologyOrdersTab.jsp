<%@ include file="/WEB-INF/view/module/radiology/template/include.jsp"%>
<%@ include file="/WEB-INF/view/module/radiology/template/includeDatatables.jsp"%>

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

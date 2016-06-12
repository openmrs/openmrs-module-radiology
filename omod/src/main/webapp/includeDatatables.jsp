<openmrs:htmlInclude file="/scripts/jquery-ui/js/jquery-ui-1.7.2.custom.min.js" />
<openmrs:htmlInclude file="/moduleResources/radiology/js/datatables/jquery.dataTables.min.js" />
<script>
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
<openmrs:htmlInclude file="/moduleResources/radiology/css/jquery.dataTables.min.css" />
<openmrs:htmlInclude file="/moduleResources/radiology/css/details-control.dataTables.css" />
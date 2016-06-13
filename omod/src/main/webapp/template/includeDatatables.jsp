<openmrs:htmlInclude file="/scripts/jquery-ui/js/jquery-ui-1.7.2.custom.min.js" />
<openmrs:htmlInclude file="/moduleResources/radiology/js/datatables/jquery.dataTables.min.js" />
<script type="text/javascript">
  jQuery
          .extend(
                  true,
                  jQuery.fn.dataTable.defaults,
                  {
                    "language": {
                      "zeroRecords": '<spring:message code="general.noresult"/>',
                      "processing": '<spring:message code="general.loading"/>',
                      "info": '<spring:message code="radiology.viewing"/> _START_ - _END_ <spring:message code="radiology.of"/> _TOTAL_',
                      "infoEmpty": '<spring:message code="radiology.viewing"/> 0 <spring:message code="radiology.of"/> 0',
                      "lengthMenu": '<spring:message code="radiology.show"/> _MENU_ <spring:message code="radiology.entries"/>',
                      "paginate": {
                        "first": '<spring:message code="radiology.first"/>',
                        "previous": '<spring:message code="general.previous"/>',
                        "next": '<spring:message code="general.next"/>',
                        "last": '<spring:message code="general.last"/>',
                      },
                    },
                  });
</script>
<openmrs:htmlInclude file="/moduleResources/radiology/css/jquery.dataTables.min.css" />
<openmrs:htmlInclude file="/moduleResources/radiology/css/details-control.dataTables.css" />
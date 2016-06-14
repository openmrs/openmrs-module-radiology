<openmrs:htmlInclude file="/scripts/jquery-ui/js/jquery-ui-1.7.2.custom.min.js" />
<openmrs:htmlInclude file="/moduleResources/radiology/js/datatables/jquery.dataTables.min.js" />
<openmrs:htmlInclude file="/moduleResources/radiology/js/moment.min.js" />
<script type="text/javascript">
  jQuery.fn.dataTable.moment('HH:mm MMM D, YY');
  jQuery
          .extend(
                  true,
                  jQuery.fn.dataTable.defaults,
                  {
                    "pagingType": "simple",
                    "info": false,
                    "language": {
                      "zeroRecords": '<spring:message code="radiology.datatables.noresult"/>',
                      "processing": '<spring:message code="radiology.datatables.loading"/>',
                      "info": '<spring:message code="radiology.datatables.viewing"/> _START_ - _END_ <spring:message code="radiology.datatables.of"/> _TOTAL_',
                      "infoEmpty": '<spring:message code="radiology.datatables.viewing"/> 0 <spring:message code="radiology.datatables.of"/> 0',
                      "lengthMenu": '<spring:message code="radiology.datatables.show"/> _MENU_ <spring:message code="radiology.datatables.entries"/>',
                      "paginate": {
                        "first": '<spring:message code="radiology.datatables.first"/>',
                        "previous": '<spring:message code="radiology.datatables.previous"/>',
                        "next": '<spring:message code="radiology.datatables.next"/>',
                        "last": '<spring:message code="radiology.datatables.last"/>',
                      },
                    },
                  });
</script>
<openmrs:htmlInclude file="/moduleResources/radiology/css/jquery.dataTables.min.css" />
<openmrs:htmlInclude file="/moduleResources/radiology/css/details-control.dataTables.css" />
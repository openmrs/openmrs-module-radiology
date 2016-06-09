<openmrs:htmlInclude file="/scripts/jquery-ui/js/jquery-ui-1.7.2.custom.min.js" />
<openmrs:htmlInclude file="/moduleResources/radiology/scripts/jquery/datatables/js/jquery.dataTables.min.js" />
<openmrs:htmlInclude file="/moduleResources/radiology/scripts/jquery/datatables/css/jquery.dataTables.min.css" />
<openmrs:htmlInclude file="/moduleResources/radiology/scripts/jquery/datatables/css/jquery.details-control.dataTables.css" />
<script type="text/javascript">
  jQuery
          .extend(
                  true,
                  jQuery.fn.dataTable.defaults,
                  {
                    "pagingType": "simple",
                    fnDrawCallback: function() {
                      // WORKAROUND needed since paging is not fully implemented yet.
                      // datatables "paging" cannot be set to false, because otherwise the AJAX requests are given a limit of -1.
                      // therefore "pagingType" is set to "simple" showing only next & previous buttons which we simply hide.
                      // delete this hack as soon as paging is well implemented
                      jQuery('.previous, .next').hide();
                    },
                    "info": false,
                    "searching": false,
                    "ordering": false,
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
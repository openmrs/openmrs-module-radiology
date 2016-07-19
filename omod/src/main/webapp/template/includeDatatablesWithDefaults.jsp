<%@ include file="/WEB-INF/view/module/radiology/template/includeDatatables.jsp"%>
<script type="text/javascript">
  jQuery
          .extend(
                  true,
                  jQuery.fn.dataTable.defaults,
                  {
                    "info": true,
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
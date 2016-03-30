<script type="text/javascript">
var $j=jQuery.noConflict();
	$j(document).ready(function(){
		pQuery=$j('input[name="patientQuery"]');
		loading=$j('div#openmrs_msg[name="loading"]');
		startDate=$j('input[name="startDate"]');
		endDate=$j('input[name="endDate"]');
		find=$j('#findButton');
		results=$j('#results');
		clearResults=$j('a#clearResults');

		function format(order_id, physician, status, instructions, mwl) {
			// `d` is the original data object for the row
			return '<table cellpadding="5" cellspacing="0" border="0" style="padding-left:50px;">' +
				'<tr>' +
				'<td><spring:message code="radiology.referringPhysician"/></td>' +
				'<td>' + physician + '</td>' +
				'</tr>' +
				'<tr>' +
				'<td><spring:message code="radiology.scheduledStatus"/></td>' +
				'<td>' + status + '</td>' +
				'</tr>' +
				'<tr>' +
				'<td><spring:message code="general.instructions"/></td>' +
				'<td>' + instructions + '</td>' +
				'</tr>' +
				'<tr>' +
				'<td><spring:message code="radiology.mwlStatus"/></td>' +
				'<td>' + mwl + '</td>' +
				'</tr>' +
				'</table>';
		}

		firstTime();
		function firstTime(){
			sendRequest();
		}

		function sendRequest(){

			loading.show();
			$j('#errorSpan').html('');
			$j.get('portlets/orderSearch.portlet',
			{patientQuery:pQuery.val(),startDate:startDate.val(),
			endDate:endDate.val()},
			function(data){
				loading.hide();
				// crossDate error span rendered/sended from portlet
				if(data.match(/crossDate/ig)!=null){
					$j('#errorSpan').html($j(data));
				}
				// no errors
				else{
					// load data
					results.html(data);
					var oTable = $j('table#radiologyOrdersTable').DataTable({
						"order": [[1, 'asc']],
						"oLanguage": {
							"sLengthMenu": '<spring:message code="radiology.show"/>' +
							' _MENU_ <spring:message code="radiology.entries"/>',
							"sSearch": '<spring:message code="general.search"/>:',
							"sInfo": '<spring:message code="radiology.viewing"/> _START_ ' +
							'- _END_ ' +
							'<spring:message code="radiology.of"/> _TOTAL_',
							"oPaginate": {
								"sFirst": '<spring:message code="radiology.first"/>',
								"sPrevious": '<spring:message code="general.previous"/>',
								"sNext": '<spring:message code="general.next"/>',
								"sLast": '<spring:message code="radiology.last"/>',
							},
							"sProcessing": '<spring:message code="general.loading"/>'
						},
						"columnDefs": [
							{
								"sType": "num-html",
								"bSortable": true,
								"aTargets": [0]
							},
							{
								"targets": [8],
								"visible": false,
								"searchable": false
							},
							{
								"targets": [9],
								"visible": false,
								"searchable": false
							},
							{
								"targets": [10],
								"visible": false,
								"searchable": false
							},
							{
								"targets": [11],
								"visible": false,
								"searchable": false
							}
						]
					});
					$j('#radiologyOrdersTableBody').on('click', 'tr', function () {
						var tr = $j(this);
						var row = oTable.row(this);

						if (row.child.isShown()) {
							row.child.hide();
							tr.removeClass('shown');
						}
						else {
							row.child(format(tr.data('child-order_id'),tr.data('child-physician'), tr.data('child-status'), tr.data('child-instructions'),tr.data('child-mwl')), 'no-padding').show();
							tr.addClass('shown');
						}
					});
				}
			});
		}

		// ***********Events*************
		find.click(function(){
			page=0;
			sendRequest();
		});
		
		pQuery.keypress(function(event) {
		if (event.which == '13'){
			sendRequest();
		}
		});
		
		clearResults.click(function(){
			$j('table#searchForm input:text').val('');
			$j('tbody#radiologyOrdersTableBody').html('');
		});	
	});
</script>

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
		sortType = $j('#selectSortType');

		function format(order_id,report_id, physician, status, instructions, mwl) {
			var link ="";
			if(report_id!=0){ link = '<a href="/openmrs/module/radiology/radiologyReport.form?orderId='+order_id+'&radiologyReportId='+report_id+'">'+report_id+'</a>'}
			// `d` is the original data object for the row
			return '<table cellpadding="5" cellspacing="0" border="0" style="padding-left:50px;">' +
				'<tr>' +
				'<td><spring:message code="radiology.radiologyReportId"/></td>' +
				'<td>' + link + '</td>' +
				'</tr>' +
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
			mySortType = $j('#selectSortType option:selected').val();
			$j.get('portlets/orderSearch.portlet',
			{patientQuery:pQuery.val(),startDate:startDate.val(),
			endDate:endDate.val(), selectSortType: mySortType},

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
					var oTable = $j('table#matchedOrders').DataTable({
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
							},
							{
								"targets": [12],
								"visible": false,
								"searchable": false
							}
						]
					});
					$j('#matchedOrders tbody').on('click', 'td.details-control', function () {
						var tr = $j(this).closest('tr');
						var row = oTable.row(tr);

						if (row.child.isShown()) {
							// This row is already open - close it
							row.child.hide();
							tr.removeClass('shown');
						}
						else {
							// Open this row
							row.child(format(tr.data('child-order_id'),tr.data('child-report_id'),tr.data('child-physician'), tr.data('child-status'), tr.data('child-instructions'),tr.data('child-mwl')), 'no-padding').show();
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

		sortType.change(function () {
			sendRequest();
		});

		pQuery.keypress(function(event) {
		if (event.which == '13'){
			sendRequest();
		}
		});

		// ************Popups***************
		$j('#voidReasonPopup').dialog({
			autoOpen: false,
			modal: true,
			position: top,
			title: '<spring:message code="radiology.voidReason" javaScriptEscape="true"/>',
			width: '40%'
		});
	});
	</script>

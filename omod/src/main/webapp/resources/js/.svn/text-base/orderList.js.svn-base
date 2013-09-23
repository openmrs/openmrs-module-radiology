<script type="text/javascript">
var $j=jQuery.noConflict();
	$j(document).ready(function(){
		pQuery=$j('input[name="patientQuery"]');
		loading=$j('div#openmrs_msg[name="loading"]');
		startDate=$j('input[name="startDate"]');
		finalDate=$j('input[name="finalDate"]');
		completed=$j('input[name="completed"]');
		pending=$j('input[name="pending"]');
		find=$j('#findButton');
		results=$j('#results');
		clearResults=$j('a#clearResults');
		
		firstTime();
		function firstTime(){
			sendRequest();
		}
		
		function sendRequest(){
		
			loading.show();
			$j('#errorSpan').html('');
			$j.get('portlets/orderSearch.portlet',
			{patientQuery:pQuery.val(),startDate:startDate.val(),
			finalDate:finalDate.val(),completed:completed.attr('checked'),
			pending:pending.attr('checked')},
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
					$j('table#matchedOrders').dataTable({
						"iDisplayLength": 20,
						"aLengthMenu": [20,50],
						"sPaginationType": 'full_numbers',
						"bJQueryUI": true,
						"bAutoWidth": false,
						"sDom": '<"H"<"tableHeader verticalCentered"<"left"l><"center"fr><"right colVisDiv"C>>>'+
								't<"F" <"verticalCentered"<"left"i><"right"p>>>',
						"oColVis": {
							"buttonText": '<spring:message code="radiology.showHideColumns"/>',
							"aiExclude": [0]
						},
						"aoColumnDefs":[
							{
							"sType":"num-html",
							"bSortable":true,
							"aTargets":[0]
							}
						],
						"fnDrawCallback": function(){
							
							$j('#actionSelect > option[value="-1"]').hide();
							$j('#actionSelect').change(function(){
								action=$j('#actionSelect').attr('selectedIndex');
								if(action==1){
									$j('#voidReasonPopup').dialog('open');
								}
								$j('#actionSelect').attr('selectedIndex','0');
							});
							$j('div.right.colVisDiv').click(function(){
								style=$j('div#footer + div + div').attr('style');
								$j('div#footer + div + div').attr('style',style+' width:103%;');
							});
							
							//********************** DT Events *******************							
							
							$j('#markAll').click(function(){
								$j('td#actionCheckboxes [type="checkbox"]').attr('checked',true);
							});
							
							$j('#markNone').click(function(){
								$j('td#actionCheckboxes [type="checkbox"]').attr('checked',false);
							});
						},
						"oLanguage": {
							"sLengthMenu": '<spring:message code="radiology.show"/>'+
										   ' _MENU_ <spring:message code="radiology.entries"/>',
							"sSearch": '<spring:message code="general.search"/>:',
							"sInfo": '<spring:message code="radiology.viewing"/> _START_ '+
									 '- _END_ '+
									 '<spring:message code="radiology.of"/> _TOTAL_',
							"oPaginate": {
								"sFirst": '<spring:message code="radiology.first"/>',
								"sPrevious": '<spring:message code="general.previous"/>',
								"sNext": '<spring:message code="general.next"/>',
								"sLast": '<spring:message code="radiology.last"/>',
							},
							"sProcessing": '<spring:message code="general.loading"/>'
						}
					});
				}
			});
		}
		
		// ***********Events*************
		completed.click(function(){
			
		});
		
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
			$j('table#searchForm input[type="checkbox"]').attr('checked',false);
			$j('tbody#matchedOrdersBody').html('');
		});	
		
		$j('input#voidOrderButton').click(function(){
			//TODO
			
		});
		
		//************Popups***************
		$j('#voidReasonPopup').dialog({
			autoOpen: false,
			modal: true,
			position: top,
			title: '<spring:message code="radiology.voidReason" javaScriptEscape="true"/>',
			width: '40%'
		});
	});
	</script>
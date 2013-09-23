<script>
var $j=jQuery.noConflict();
	$j(document).ready(function() {
		$j('#moreInfoPopup').dialog({
			autoOpen: false,
			modal: true,
			position: top,
			title: '<spring:message code="radiology.patientOverview" javaScriptEscape="true"/>',
			width: '90%'
		});
				
		$j('#moreInfo').click(function() {
			var patientIdReq=$j('#moreInfo').closest('td').find('input:hidden').val();
			$j('#moreInfoPopup').load('portlets/patientOverview.portlet',{patientIdReq:patientIdReq});
			$j('#moreInfoPopup').dialog('open');
		});
		
	});
</script>
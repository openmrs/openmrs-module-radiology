<%@ include file="/WEB-INF/view/module/radiology/template/include.jsp"%>
<%@ include file="/WEB-INF/view/module/radiology/template/includeScripts.jsp"%>
<%@ include file="/WEB-INF/view/module/radiology/template/includeDatatablesWithDefaults.jsp"%>

<script type="text/javascript">
	var $j = jQuery.noConflict();
	$j(document)
			.ready(
					function() {
						var templateTitle = $j('#reportTemplatesTabTitleFilter');
						var find = $j('#reportTemplatesTabFind');
						var clearResults = $j('a#reportTemplatesTabClearFilters');

						var reportTemplatesTable = $j('#reportTemplatesTable')
								.DataTable(
										{
											"processing" : true,
											"serverSide" : true,
											"ajax" : {
												headers : {
													Accept : "application/json; charset=utf-8",
													"Content-Type" : "text/plain; charset=utf-8",
												},
												cache : true,
												dataType : "json",
												url : "${pageContext.request.contextPath}/ws/rest/v1/mrrtreporttemplate/",
												data : function(data) {
													return {
														startIndex : data.start,
														limit : data.length,
														v : "full",
														title : templateTitle
																.val(),
														totalCount : true,
													};
												},
												"dataFilter" : function(data) {
													var json = $j
															.parseJSON(data);
													json.recordsTotal = json.totalCount || 0;
													json.recordsFiltered = json.totalCount || 0;
													json.data = json.results;
													return JSON.stringify(json);
												}
											},
											"columns" : [
													{
														"name" : "templateId",
														"render" : function(
																data, type,
																full, meta) {
															return full.templateId;
														}
													},
													{
														"name" : "dcTermsTitle",
														"render" : function(
																data, type,
																full, meta) {
															return full.dcTermsTitle;
														}
													},
													{
														"name" : "dcTermsCreator",
														"render" : function(
																data, type,
																full, meta) {
															return full.dcTermsCreator;
														}
													},
													{
														"name" : "dcTermsPublisher",
														"render" : function(
																data, type,
																full, meta) {
															return full.dcTermsPublisher;
														}
													},
													{
														"name" : "dcTermsRights",
														"render" : function(
																data, type,
																full, meta) {
															return full.dcTermsRights;
														}
													},
													{
														"name" : "dcTermsDescription",
														"render" : function(
																data, type,
																full, meta) {
															return full.dcTermsDescription;
														}
													}, ],
										});

						// prevent form submit when user hits enter
						$j(window).keydown(function(event) {
							if (event.keyCode == 13) {
								event.preventDefault();
								return false;
							}
						});

						find.on('mouseup keyup', function(event) {
							if (event.type == 'keyup' && event.keyCode != 13)
								return;
							reportTemplatesTable.ajax.reload();
						});

						clearResults.on('mouseup keyup', function() {
							$j('table#reportTemplatesTableFilters input:text')
									.val('');
							templateTitle.val('');
							reportTemplatesTable.ajax.reload();
						});

						$j('#reportTemplatesTabImportPopup')
								.dialog(
										{
											autoOpen : false,
											modal : true,
											title : '<openmrs:message code="radiology.reportTemplates.import.popup.boxheader" javaScriptEscape="true"/>',
											width : '90%'
										});

						$j('#reportTemplatesTabImportTemplates').click(
								function() {
									$j('#reportTemplatesTabImportPopup')
											.dialog('open');

								});
					});
</script>

<br />
<div id="buttonPanel">
  <div style="float: left">
    <input type="button" id="reportTemplatesTabImportTemplates"
      value="<openmrs:message code="radiology.reportTemplates.import.popup.button" javaScriptEscape="true"/>" />
    <div id="reportTemplatesTabImportPopup">
      <b class="boxHeader"><openmrs:message code="radiology.reportTemplates.import.popup.boxheader" /></b>
      <div class="box">
        <form id="templateAddForm" action="radiologyDashboard.form" method="post" enctype="multipart/form-data">
          <input type="file" name="templateFile" size="40" /> <input type="submit" name="uploadReportTemplate"
            value='<openmrs:message code="radiology.reportTemplates.import.popup.upload"/>' />
        </form>
      </div>
      <br />
    </div>
  </div>
  <div style="clear: both">&nbsp;</div>
</div>

<br>
<span class="boxHeader"> <b><spring:message code="radiology.dashboard.tabs.reportTemplates.boxheader" /></b> <a
  id="reportTemplatesTabClearFilters" href="#" style="float: right"> <spring:message
      code="radiology.dashboard.tabs.filters.clearFilters" />
</a>
</span>
<div class="box">
  <table id="reportTemplatesTableFilters" cellspacing="10">
    <tr>
      <form>
        <td><label><spring:message code="radiology.dashboard.tabs.reportTemplates.filters.title" /></label> <input
          id="reportTemplatesTabTitleFilter" name="titleQuery" type="text" style="width: 20em"
          title="<spring:message
						code="radiology.minChars" />" /></td>
        <td><input id="reportTemplatesTabFind" type="button"
          value="<spring:message code="radiology.dashboard.tabs.filters.find"/>" /></td>
      </form>
    </tr>
  </table>
  <br>
  <div>
    <table id="reportTemplatesTable" cellspacing="0" width="100%" class="display nowrap">
      <thead>
        <tr>
          <th><spring:message code="radiology.datatables.column.report.template.id" /></th>
          <th><spring:message code="radiology.datatables.column.report.template.title" /></th>
          <th><spring:message code="radiology.datatables.column.report.template.creator" /></th>
          <th><spring:message code="radiology.datatables.column.report.template.publisher" /></th>
          <th><spring:message code="radiology.datatables.column.report.template.rights" /></th>
          <th><spring:message code="radiology.datatables.column.report.template.description" /></th>
        </tr>
      </thead>
    </table>
  </div>
</div>
<br />
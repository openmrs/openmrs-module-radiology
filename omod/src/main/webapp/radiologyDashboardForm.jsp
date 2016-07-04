<%@ include file="/WEB-INF/template/include.jsp"%>

<openmrs:require privilege="View Orders" otherwise="/login.htm" redirect="/module/radiology/radiologyDashboard.form" />

<%@ include file="/WEB-INF/template/header.jsp"%>

<openmrs:htmlInclude file="/scripts/jquery-ui/js/jquery-ui-1.7.2.custom.min.js" />
<openmrs:htmlInclude file="/moduleResources/radiology/css/radiology.css" />

<h2>
  <spring:message code="radiology.dashboard.title" />
</h2>

<script type="text/javascript">
  var timeOut = null;

  <openmrs:authentication>var
  userId = "${authenticatedUser.userId}";
  </openmrs:authentication>

  //initTabs
  $j(document).ready(
          function() {
            var c = getTabCookie();
            if (c == null || (!document.getElementById(c))) {
              var tabs = document.getElementById("radiologyTabs")
                      .getElementsByTagName("a");
              if (tabs.length && tabs[0].id) c = tabs[0].id;
            }
            changeTab(c);
          });

  function setTabCookie(tabType) {
    document.cookie = "dashboardTab-" + userId + "=" + escape(tabType);
  }

  function getTabCookie() {
    var cookies = document.cookie.match('dashboardTab-' + userId
            + '=(.*?)(;|$)');
    if (cookies) { return unescape(cookies[1]); }
    return null;
  }

  function changeTab(tabObj) {
    if (!document.getElementById || !document.createTextNode) { return; }
    if (typeof tabObj == "string") tabObj = document.getElementById(tabObj);

    if (tabObj) {
      console.log("ChangeTab is called on every click, and id=" + tabObj);
      var tabs = tabObj.parentNode.parentNode.getElementsByTagName('a');
      for (var i = 0; i < tabs.length; i++) {
        if (tabs[i].className.indexOf('current') != -1) {
          manipulateClass('remove', tabs[i], 'current');
        }
        var divId = tabs[i].id.substring(0, tabs[i].id.lastIndexOf("Tab"));
        var divObj = document.getElementById(divId);
        if (divObj) {
          if (tabs[i].id == tabObj.id)
            divObj.style.display = "";
          else
            divObj.style.display = "none";
        }
      }
      addClass(tabObj, 'current');

      setTabCookie(tabObj.id);
    }
    return false;
  }
</script>

<div id="radiologyTabs">
  <ul>
    <openmrs:hasPrivilege privilege="View Orders">
      <li><a id="radiologyOrdersTab" href="#" onclick="return changeTab(this);" hidefocus="hidefocus"><openmrs:message
            code="radiology.dashboard.tabs.orders" /></a></li>
    </openmrs:hasPrivilege>
    <openmrs:hasPrivilege privilege="View Radiology Report Templates">
      <li><a id="radiologyReportTemplatesTab" href="#" onclick="return changeTab(this);" hidefocus="hidefocus"><openmrs:message
              code="radiology.dashboard.tabs.reportTemplates" /></a></li>
    </openmrs:hasPrivilege>
  </ul>
</div>

<div id="radiologySections">
  <openmrs:hasPrivilege privilege="View Orders">
    <div id="radiologyOrders" style="display: none;">

      <openmrs:portlet url="radiologyOrdersTab" id="ordersTab" moduleId="radiology" />

    </div>
  </openmrs:hasPrivilege>
  <openmrs:hasPrivilege privilege="View Radiology Report Templates">
    <div id="radiologyReportTemplates" style="display: none;">

      <openmrs:portlet url="radiologyReportTemplatesTab" id="reportTemplatesTab" moduleId="radiology" />

    </div>
  </openmrs:hasPrivilege>
</div>

<%@ include file="/WEB-INF/template/footer.jsp"%>
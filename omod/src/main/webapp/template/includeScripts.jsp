<openmrs:htmlInclude file="/moduleResources/radiology/scripts/jquery/jquery-1.8.1.min.js" />
<openmrs:htmlInclude file="/moduleResources/radiology/css/radiology.css" />
<openmrs:htmlInclude file="/moduleResources/radiology/scripts/radiology.js" />
<openmrs:htmlInclude file="/scripts/jquery-ui/js/jquery-ui.custom.min.js" />
<link href="<openmrs:contextPath/>/scripts/jquery-ui/css/<spring:theme code='jqueryui.theme.name' />/jquery-ui.custom.css"
  type="text/css" rel="stylesheet" />
<openmrs:htmlInclude file="/moduleResources/radiology/scripts/moment/moment-with-locales.min.js" />
<c:if test="${not empty INCLUDE_TIME_ADJUSTMENT}">
  <script type="text/javascript">
      // configure current locale as momentjs default, fall back to "en" if locale not found
      moment.locale([jsLocale, 'en']);
      var $j = jQuery.noConflict();
      $j(document).ready(
              function() {
                $j('.datetime').each(
                        function() {
                          if ($j.trim($j(this).html())) {
                            $j(this).html(
                                    moment.utc($j(this).html(), ["L LT","YYYY-MM-DD hh:mm:ss.S"]).local()
                                            .format("L LT"));
                          }
                        });

              });
    </script>
</c:if>
<html>
<head>
<link rel="stylesheet" type="text/css" href="css/style.css" />
<script type="text/javascript" src="js/jquery-1.4.2.min.js"></script>
<script src="js/jquery.autocomplete.js"></script>
<script>
jQuery(function(){
$("#name").autocomplete("List.jsp");
});
</script>
 
</head>
<body>
<br><br><center>
<font face="verdana" size="2">

Select Country   :
<input type="text" id="name" name="name"/>
 
</font>
</body>
</html>
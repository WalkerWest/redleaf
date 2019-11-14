<?php require_once("java/Java.inc");
	$memoryStream = new java("redleaf.hello");
	$memoryStream->sayHi();
	echo java_values($memoryStream->sayHiString());
  phpinfo();
?>

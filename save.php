<?php
	$myfile = fopen("data.txt", "a") or die("Unable to open file!");
	fwrite($myfile, json_encode($_POST));
	fclose($myfile);
?>


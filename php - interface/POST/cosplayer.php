<?php

$response = array();
$cosplayers = json_decode(file_get_contents('php://input', true));
if (isset($cosplayers -> cosplayer) || isset($cosplayers -> house) || isset($cosplayers -> lat) || isset($cosplayers -> lon) || isset($cosplayers -> time) || isset($cosplayers -> last_seen)) {

	require_once '../DB/db_connect.php';
	$db = new DB_CONNECT();

	$result = mysql_query("insert into cosplayers(cosplayer_name,cosplayer_house,lat,lon,time,last_seen) values ('" . $cosplayers -> cosplayer . "','" . $cosplayers -> house . "'," . $cosplayers -> lat . "," . $cosplayers -> lon . "," . $cosplayers -> time . ",'" . $cosplayers -> last_seen . "')");

	if (!$result) {
		$response['message'] = 'failure';
		$response['rcode'] = 0;
	} else {
		$response['message'] = 'success';
		$response['rcode'] = 1;
	}

	echo json_encode($response);

} else {

	$response['rcode'] = 0;
	$response['message'] = "Solicitud incompleta";

	echo json_encode($response);
}
?>

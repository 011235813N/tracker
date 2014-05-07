<?php
require_once '../DB/db_connect.php';
$db = new DB_CONNECT();

$result = mysql_query("SELECT * FROM cosplayers WHERE id IN (SELECT MAX(id) FROM cosplayers GROUP BY cosplayer_name)");

$json = array();
while($r = mysql_fetch_assoc($result)) {
    //$rows[] = $r;
	$json['cosplayers'][]=$r;
}


mysql_free_result($result);//liberar recursos
echo json_encode($json);


?>
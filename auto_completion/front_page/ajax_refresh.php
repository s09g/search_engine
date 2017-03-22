<?php
// PDO connect *********
define ('DBUSER', 'root');
define ('DBPASS','');
define ('DBNAME','test');

function connect() {
    return new PDO('mysql:host=localhost;dbname=test', 'root', '', array(PDO::ATTR_ERRMODE => PDO::ERRMODE_EXCEPTION, PDO::MYSQL_ATTR_INIT_COMMAND => "SET NAMES utf8"));
}

$pdo = connect();
$keyword = $_POST['keyword'].'%';
$sql = "SELECT * FROM output WHERE starting_phrase LIKE (:keyword) ORDER BY count DESC LIMIT 0, 10";
$query = $pdo->prepare($sql);
$query->bindParam(':keyword', $keyword, PDO::PARAM_STR);
$query->execute();
$list = $query->fetchAll();
foreach ($list as $rs) {
	// put in bold the written text
	//$country_name = str_replace($_POST['keyword'], '<b>'.$_POST['keyword'].'</b>', $rs['following_word']);
	$predictor = $rs['starting_phrase'] . ' ' . $rs['following_word'];
	// add new option
    echo '<li onclick="set_item(\''.str_replace("'", "\'", $rs['following_word']).'\')">'.$predictor.'</li>';
}
?>
<?php
// Fichier de configuration pour la connexion à la base de données
$host="localhost";
$user="root";
$mdp="";
$db="gestionnaire_taches";

/**************************************************************
*  fonction permettant de se connecter à la base de données   *
***************************************************************/
function connexion($host,$user,$mdp,$db){

	$link = mysql_connect($host,$user,$mdp);
	mysql_select_db($db,$link);	
	return $link;
	
}

$link = connexion($host,$user,$mdp,$db);


?>
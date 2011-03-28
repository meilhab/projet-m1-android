<?php

include_once("config.inc.php");

//test si on a le droit de demander des informations sur le serveur (login et mot de passe correct)
function connexionAutorisee() {

	$autorisee = false;

	if ((isset($_POST['identifiant']) && !empty($_POST['identifiant'])) && 
		(isset($_POST['mdPasse']) && !empty($_POST['mdPasse']))) {

		// on teste si une entrée de la base contient ce couple login / pass 
		$sql = 'SELECT * FROM utilisateur WHERE identifiant="'.addslashes($_POST['identifiant']).'"'; 
		$req = mysql_query($sql) or die('Erreur SQL !<br />'.$sql.'<br />'.mysql_error()); 
		
		// On vérifie que l'utilisateur existe bien
		if (mysql_num_rows($req) > 0) {
			$data = mysql_fetch_assoc($req);
			// On vérifie que son mot de passe est correct
			if ($_POST['mdPasse'] == $data['mdPasse'])
				$autorisee = true;
		}
	}

	return $autorisee;

}



function importerJson($json, $id) {

	$reussi=true;

	if(!mysql_query("TRUNCATE apourfils") or !mysql_query("TRUNCATE apourtag") or!mysql_query("TRUNCATE tache") or !mysql_query("TRUNCATE tag"))
		$reussi=false;

	if($reussi) {
	
		$tab = json_decode($json, true);
		
		$tabTags = $tab['tags'];
		$tabTaches = $tab['taches'];
		$tabApourTag = $tab['apourtag'];
		$tabApourFils = $tab['apourfils'];

		foreach($tabTags as $tag) {
			//echo $tag['idTag'].', '.$tag['libelleTag'].', '.$id.'</br>';
			if(!mysql_query("INSERT INTO tag VALUES('".$tag['idTag']."','".$tag['libelleTag']."','".$id."')"))
				$reussi=false;
		}
		
		foreach($tabTaches as $tache) {
			//echo $tache['idTache'].', '.$tache['nomTache'].', '.$tache['descriptionTache'].', '.$tache['dateLimite'].', '.$tache['idEtat'].', '.$tache['idPriorite'].', '.$id.'</br>';
			if(!mysql_query("INSERT INTO tache VALUES('".$tache['idTache']."','".$tache['nomTache']."','".$tache['descriptionTache']."','".$tache['dateLimite']."','".$tache['idEtat']."','".$tache['idPriorite']."','".$id."')"))
				$reussi=false;
		}
		
		foreach($tabApourTag as $apourtag) {
			//echo $apourtag['idTache'].', '.$apourtag['idTag'].', '.$id.'</br>';
			if(!mysql_query("INSERT INTO apourtag VALUES('".$apourtag['idTache']."','".$apourtag['idTag']."','".$id."')"))
				$reussi=false;
		}
		
		foreach($tabApourFils as $apourfils) {
			//echo $apourfils['idPere'].', '.$apourfils['idFils'].', '.$id.'</br>';
			if(!mysql_query("INSERT INTO apourfils VALUES('".$apourfils['idPere']."','".$apourfils['idFils']."','".$id."')"))
				$reussi=false;
		}
	
	}
	
	return $reussi;

}


function exporterJson($id) {

	$codeJson = array();
	$codeJson['tags'] = array();
	$codeJson['taches'] = array();
	$codeJson['apourtag'] = array();
	$codeJson['apourfils'] = array();
	

	$req="SELECT * FROM tag WHERE identifiant='$id'";
	$resTag = mysql_query($req);
	$compt = 0;
	while($tag=mysql_fetch_array($resTag)) {
		$codeJson['tags'][$compt]['idTag'] = $tag['idTag'];
		$codeJson['tags'][$compt]['libelleTag'] = $tag['libelleTag'];
		$compt++;
	}
	
	$codeJson['nbTags'] = $compt;
	
	$req="SELECT * FROM tache WHERE identifiant='$id'";
	$resTache = mysql_query($req);
	$compt = 0;
	while($tache=mysql_fetch_array($resTache)) {
		$codeJson['taches'][$compt]['idTache'] = $tache['idTache'];
		$codeJson['taches'][$compt]['nomTache'] = $tache['nomTache'];
		$codeJson['taches'][$compt]['descriptionTache'] = $tache['descriptionTache'];
		$codeJson['taches'][$compt]['dateLimite'] = $tache['dateLimite'];
		$codeJson['taches'][$compt]['idEtat'] = $tache['idEtat'];
		$codeJson['taches'][$compt]['idPriorite'] = $tache['idPriorite'];
		
		$codeJson['apourtag'] = array();
		$req="SELECT * FROM apourtag WHERE idTache='".$tache['idTache']."' AND identifiant='$id'";
		$resApourtag = mysql_query($req);
		$comptApourtag = 0;
		while($apourtag=mysql_fetch_array($resApourtag)) {
			$codeJson['taches'][$compt]['apourtag'][$comptApourtag] = $apourtag['idTag'];
			$comptApourtag++;
		}
		
		$codeJson['apourfils'] = array();
		$req="SELECT * FROM apourfils WHERE idPere='".$tache['idTache']."' AND identifiant='$id'";
		$resApourfils = mysql_query($req);
		$comptApourfils = 0;
		while($apourfils=mysql_fetch_array($resApourfils)) {
			$codeJson['taches'][$compt]['apourfils'][$comptApourfils] = $apourfils['idFils'];
			$comptApourfils++;
		}
		
		$compt++;
	}
	
	$codeJson['nbTaches'] = $compt;
	
	
	$req="SELECT * FROM apourtag WHERE identifiant='$id'";
	$resApourtag = mysql_query($req);
	$compt = 0;
	while($apourtag=mysql_fetch_array($resApourtag)) {
		$codeJson['apourtag'][$compt]['idTache'] = $apourtag['idTache'];
		$codeJson['apourtag'][$compt]['idTag'] = $apourtag['idTag'];
		$compt++;
	}
	
	$req="SELECT * FROM apourfils WHERE identifiant='$id'";
	$resApourfils = mysql_query($req);
	$compt = 0;
	while($apourfils=mysql_fetch_array($resApourfils)) {
		$codeJson['apourfils'][$compt]['idPere'] = $apourfils['idPere'];
		$codeJson['apourfils'][$compt]['idFils'] = $apourfils['idFils'];
		$compt++;
	}
	
	return json_encode($codeJson);
			
}

?>
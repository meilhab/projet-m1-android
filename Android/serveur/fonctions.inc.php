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

	$reussi=false;

	$tab = json_decode($json, true);
	//echo $json;
	if(intval($tab['nbTags'])>0 or intval($tab['nbTaches'])>0) {
		
		$reussi = viderTablesUtilisateur($id);

		if($reussi) {

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

function viderTablesUtilisateur($id) {

	$reussi = true;

	if(!mysql_query("DELETE FROM apourfils WHERE identifiant='$id'") or !mysql_query("DELETE FROM apourtag WHERE identifiant='$id'") or !mysql_query("DELETE FROM tache WHERE identifiant='$id'") or !mysql_query("DELETE FROM tag WHERE identifiant='$id'"))
		$reussi = false;

	return $reussi;
	
}

function ajoutUtilisateur() {

	//captcha correct
	if(isset($_POST['nom']) and !empty($_POST['nom']) and isset($_POST['prenom']) and !empty($_POST['prenom']) and isset($_POST['mail']) and test_mail($_POST['mail']) and isset($_POST['mdPasse']) and strlen($_POST['mdPasse'])>=6 and isset($_POST['identifiant']) and !empty($_POST['identifiant'])) {

		$nom = strtoupper(mysql_real_escape_string($_POST['nom']));
		$prenom = ucfirst(strtolower(mysql_real_escape_string($_POST['prenom'])));
		$mail = $_POST['mail'];
		$mdPasse = md5($_POST['mdPasse']);
		$idProfil = 2;
		$identifiant = mysql_real_escape_string($_POST['identifiant']);
		
		
		if(mysql_query("INSERT INTO utilisateur VALUES('$identifiant','$mdPasse','$nom','$prenom','$mail','$idProfil')"))
			return true;
		else
			return false;
	
	}
	else
		return false;

}

function supprimerUtilisateur($id) {

	$profilUtilisateur = "";
	
	$req="SELECT libelleProfil FROM utilisateur u, profil p WHERE u.identifiant='$id' AND u.idProfil=p.idProfil";
	$res = mysql_query($req);
	$libelleProfil=mysql_fetch_array($res);
	if(isset($libelleProfil[0]) and !empty($libelleProfil[0]))
		$profilUtilisateur = $libelleProfil[0];
	
	if($profilUtilisateur == "administrateur") {
	
		$identifiant = mysql_real_escape_string($_POST['identifiant']);
		
		$rqt="DELETE FROM utilisateur WHERE identifiant='$identifiant'";
		
		if(viderTablesUtilisateur($identifiant) and mysql_query($rqt))
			return true;
		else
			return false;
			
	}
	else
		return false;

}

function ajoutTache($json, $id) {

	$reussi = true;

	$tache = json_decode($json, true);

	if(isset($tache['idTache']) and intval($tache['idTache'])>0 and isset($tache['nomTache']) and !empty($tache['nomTache']) and isset($tache['idEtat']) and !empty($tache['idEtat']) and isset($tache['idPriorite']) and !empty($tache['idPriorite'])) {
	
		$ajout = "INSERT INTO tache VALUES('".$tache['idTache']."','".$tache['nomTache']."','".$tache['descriptionTache']."','".$tache['dateLimite']."','".$tache['idEtat']."','".$tache['idPriorite']."','".$id."')";
		
		if(!mysql_query($ajout))
			$reussi = false;
		else {
			$tabTag = $tache['apourtag'];
			foreach($tabTag as $tag) {
				$ajoutTag = "INSERT INTO apourtag VALUES('".$tache['idTache']."','".$tag."','$id')";
				if(!mysql_query($ajoutTag))
					$reussi = false;
			}
		}
	
	}
	else
		$reussi = false;
		
	return $reussi;

}

function modifierTache($json, $id) {

	$reussi = true;

	$tache = json_decode($json, true);
	
	if(isset($tache['idTache']) and !empty($tache['idTache']) and isset($tache['nomTache']) and !empty($tache['nomTache']) and isset($tache['idEtat']) and !empty($tache['idEtat']) and isset($tache['idPriorite']) and !empty($tache['idPriorite'])) {
		
		$req="UPDATE tache SET libelleTag='".$_POST['libelleTag']."' WHERE idTache='".$_POST['idTache']."' AND identifiant='$id'";
		if(!mysql_query($req))
			$reussi = false;
		else {
			
			$req="DELETE * FROM apourtag WHERE idTache='".$tache['idTache']."' AND identifiant='$id'";
			if(!mysql_query($req))
				$reussi = false;
			else {
				$tabTag = $tache['apourtag'];
				foreach($tabTag as $tag) {
					$ajoutTag = "INSERT INTO apourtag VALUES('".$tache['idTache']."','".$tag."','$id')";
					if(!mysql_query($ajoutTag))
						$reussi = false;
				}
			}
		}
		
	}
	else
		$reussi = false;
		
	return $reussi;

}

function supprimerTache($id) {

/*
	$req="SELECT * FROM apourfils WHERE idPere='".intval($_POST['idTache'])."' AND identifiant='$id'";
	$resApourtag = mysql_query($req);
	$compt = 0;
	while($apourtag=mysql_fetch_array($resApourtag)) {
	}

	$rqt="DELETE * FROM tache WHERE idTache='".intval($_POST['idTache'])."' AND identifiant='$id'";
		
	if(mysql_query($rqt))
*/

	return false;
}

function ajoutTag($json, $id) {

	if(isset($_POST['idTag']) and intval($_POST['idTag'])>0 and isset($_POST['libelleTag']) and !empty($_POST['libelleTag'])) {
		
		$ajoutTag = "INSERT INTO tag VALUES('".intval($_POST['idTag'])."','".mysql_real_escape_string($_POST['libelleTag'])."','".$id."')";
		if(mysql_query($ajoutTag))
			return true;
		else
			return false;
			
	}
	else
		return false;

}

function modifierTag($json, $id) {

	if(isset($_POST['libelleTag']) and !empty($_POST['libelleTag'])) {
		$req="UPDATE tag SET libelleTag='".mysql_real_escape_string($_POST['libelleTag'])."' WHERE idTag='".intval($_POST['idTag'])."' AND identifiant='$id'";
		if(mysql_query($req))
			return true;
		else
			return false;
	}
	else
		return false;

}

function supprimerTag($id) {

	$delApourtag="DELETE FROM apourtag WHERE idTag='".intval($_POST['idTag'])."' AND identifiant='$id'";
	$delTag="DELETE FROM tag WHERE idTag='".intval($_POST['idTag'])."' AND identifiant='$id'";
		
	if(mysql_query($delApourtag) and mysql_query($delTag))
		return true;
	else
		return false;

}

/*****************************************************
*  fonction testant si une adresse mail est valide   *
******************************************************/
function test_mail($mail) {
		
	// On vérifie la variable $mail n'est pas vide
	if(empty($mail))
		return false;
		
	// On va tester la chaîne grâce aux expressions régulières.
	$masque  = '`^[[:alnum:]]([-_.]?[[:alnum:]])*';
	$masque .= '@[[:alnum:]]([-_.]?[[:alnum:]])*\.([a-z]{2,4})$`';
	if(preg_match($masque, $mail)) {

		// l'email est bien formé.
		return true;
		}

	return false;
}

?>
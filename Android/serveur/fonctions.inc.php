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
				if(!mysql_query("INSERT INTO tag VALUES('".$tag['idTag']."','".$tag['libelleTag']."','".$tag['versionTag']."','".$id."')"))
					$reussi=false;
			}
			
			foreach($tabTaches as $tache) {
				//echo $tache['idTache'].', '.$tache['nomTache'].', '.$tache['descriptionTache'].', '.$tache['dateLimite'].', '.$tache['idEtat'].', '.$tache['idPriorite'].', '.$id.'</br>';
				if(!mysql_query("INSERT INTO tache VALUES('".$tache['idTache']."','".$tache['nomTache']."','".$tache['descriptionTache']."','".$tache['dateLimite']."','".$tache['idEtat']."','".$tache['idPriorite']."','".$tache['versionTag']."','".$id."')"))
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

function exporterJson($id, $listeTagsIdentiques = array(), $listeTachesIdentiques = array(), $listeTagsAjoute = array(), $listeTachesAjoute = array()) {

	$codeJson = array();
	$codeJson['tags'] = array();
	$codeJson['taches'] = array();
	$codeJson['apourtag'] = array();
	$codeJson['apourfils'] = array();
	

	$req="SELECT * FROM tag WHERE identifiant='$id'";
	$resTag = mysql_query($req);
	$compt = 0;
	while($tag=mysql_fetch_array($resTag)) {
		if(!in_array(intval($tag['idTag']), $listeTagsAjoute) and !in_array(intval($tag['idTag']), $listeTagsIdentiques)) {
			$codeJson['tags'][$compt]['idTag'] = $tag['idTag'];
			$codeJson['tags'][$compt]['libelleTag'] = utf8_encode($tag['libelleTag']);
			$codeJson['tags'][$compt]['versionTag'] = $tag['versionTag'];
			$compt++;
		}
	}
	
	$codeJson['nbTags'] = $compt;
	
	$req="SELECT * FROM tache WHERE identifiant='$id'";
	$resTache = mysql_query($req);
	$compt = 0;
	while($tache=mysql_fetch_array($resTache)) {
	
		if(!in_array(intval($tache['idTache']), $listeTachesAjoute) and !in_array(intval($tache['idTache']), $listeTachesIdentiques)) {
			$codeJson['taches'][$compt]['idTache'] = $tache['idTache'];
			$codeJson['taches'][$compt]['nomTache'] = utf8_encode($tache['nomTache']);
			$codeJson['taches'][$compt]['descriptionTache'] = utf8_encode($tache['descriptionTache']);
			$codeJson['taches'][$compt]['dateLimite'] = $tache['dateLimite'];
			$codeJson['taches'][$compt]['idEtat'] = $tache['idEtat'];
			$codeJson['taches'][$compt]['idPriorite'] = $tache['idPriorite'];
			$codeJson['taches'][$compt]['versionTache'] = $tache['versionTache'];
			
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
	}
	
	$codeJson['nbTaches'] = $compt;
	
	
	$req="SELECT * FROM apourtag WHERE identifiant='$id'";
	$resApourtag = mysql_query($req);
	$compt = 0;
	while($apourtag=mysql_fetch_array($resApourtag)) {
		if(!in_array(intval($apourtag['idTache']), $listeTachesAjoute) and !in_array(intval($apourtag['idTache']), $listeTachesIdentiques)) {
			$codeJson['apourtag'][$compt]['idTache'] = $apourtag['idTache'];
			$codeJson['apourtag'][$compt]['idTag'] = $apourtag['idTag'];
			$compt++;
		}
	}
	
	$req="SELECT * FROM apourfils WHERE identifiant='$id'";
	$resApourfils = mysql_query($req);
	$compt = 0;
	while($apourfils=mysql_fetch_array($resApourfils)) {
		if(!in_array(intval($apourfils['idPere']), $listeTachesAjoute) and !in_array(intval($apourfils['idPere']), $listeTachesIdentiques)) {
			$codeJson['apourfils'][$compt]['idPere'] = $apourfils['idPere'];
			$codeJson['apourfils'][$compt]['idFils'] = $apourfils['idFils'];
			$compt++;
		}
	}

	return json_encode($codeJson);
			
}

function importerPuisExporterJson($json, $id) {

	$reussi=false;

	$tab = json_decode($json, true);
	//echo $json;
	
	$listeTagsIdentiques = array();
	$listeTachesIdentiques = array();
	$listeTagsAjoute = array();
	$listeTachesAjoute = array();
	
	if(intval($tab['nbTags'])>0 or intval($tab['nbTaches'])>0) {

		$tabTags = $tab['tags'];
		$tabTaches = $tab['taches'];
		$tabApourTag = $tab['apourtag'];
		$tabApourFils = $tab['apourfils'];
		
		foreach($tabTags as $tag) {
			//echo $tag['idTag'].', '.$tag['libelleTag'].', '.$id.'</br>';
			$listeTags[] = intval($tag['idTag']);
			$versionTag = getVersionTag($id, intval($tag['idTag']));
			if($versionTag == -1) {
				$listeTagsAjoute[] = intval($tag['idTag']);
				if(!mysql_query("INSERT INTO tag VALUES('".$tag['idTag']."','".$tag['libelleTag']."','".$tag['versionTag']."','".$id."')"))
					$reussi=false;
			}
			else if(intval($tag['versionTag']) > $versionTag) {
				$listeTagsAjoute[] = intval($tag['idTag']);
				if(!mysql_query("UPDATE tag SET libelleTag='".$tag['libelleTag']."', versionTag='".$tag['versionTag']."' WHERE idTag='".$tag['idTag']."' AND identifiant='$id'"))
					$reussi=false;
			}
			else if(intval($tag['versionTag']) == $versionTag)
				$listeTagsIdentiques[] = intval($tag['idTag']);
		}
		
		foreach($tabTaches as $tache) {
			//echo $tache['idTache'].', '.$tache['nomTache'].', '.$tache['descriptionTache'].', '.$tache['dateLimite'].', '.$tache['idEtat'].', '.$tache['idPriorite'].', '.$id.'</br>';
			$listeTaches[] = intval($tache['idTache']);
			$versionTache = getVersionTache($id, intval($tache['idTache']));
			if($versionTache == -1) {
				$listeTachesAjoute[] = intval($tache['idTache']);
				if(!mysql_query("INSERT INTO tache VALUES('".$tache['idTache']."','".$tache['nomTache']."','".$tache['descriptionTache']."','".$tache['dateLimite']."','".$tache['idEtat']."','".$tache['idPriorite']."','".$tache['versionTache']."','".$id."')"))
					$reussi=false;
			}
			else if(intval($tache['versionTache']) > $versionTache) {
				$listeTachesAjoute[] = intval($tache['idTache']);
				if(!mysql_query("UPDATE tache SET nomTache='".$tache['nomTache']."', descriptionTache='".$tache['descriptionTache']."', dateLimite='".$tache['dateLimite']."', idEtat='".$tache['idEtat']."', idPriorite='".$tache['idPriorite']."', versionTache='".$tache['versionTache']."' WHERE idTache='".$tache['idTache']."' AND identifiant='$id'"))
					$reussi=false;
			}
			else if(intval($tache['versionTache']) == $versionTache)
				$listeTachesIdentiques[] = intval($tache['idTache']);
		}
		
		foreach($tabApourTag as $apourtag) {
			//echo $apourtag['idTache'].', '.$apourtag['idTag'].', '.$id.'</br>';
			if(in_array(intval($apourtag['idTache']),$listeTachesAjoute))
				if(!mysql_query("DELETE FROM apourtag WHERE idTache='".$apourtag['idTache']."' AND identifiant='$id'") or !mysql_query("INSERT INTO apourtag VALUES('".$apourtag['idTache']."','".$apourtag['idTag']."','".$id."')"))
					$reussi=false;
		}
		
		foreach($tabApourFils as $apourfils) {
			//echo $apourfils['idPere'].', '.$apourfils['idFils'].', '.$id.'</br>';
			if(in_array(intval($apourfils['idPere']),$listeTachesAjoute))
				if(!mysql_query("DELETE FROM apourfils WHERE idPere='".$apourfils['idPere']."' AND identifiant='$id'") or !mysql_query("INSERT INTO apourfils VALUES('".$apourfils['idPere']."','".$apourfils['idFils']."','".$id."')"))
					$reussi=false;
		}
	
	}
	
	return exporterJson($id, $listeTagsIdentiques, $listeTachesIdentiques, $listeTagsAjoute, $listeTachesAjoute);

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
	
		$versionTache = 1;
		if(isset($tache['versionTache']) and intval($tache['versionTache'])>0)
			$versionTache = intval($tache['versionTache']);
		
		$ajout = "INSERT INTO tache VALUES('".$tache['idTache']."','".$tache['nomTache']."','".$tache['descriptionTache']."','".$tache['dateLimite']."','".$tache['idEtat']."','".$tache['idPriorite']."','$versionTache','$id')";
		
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
	
	if(isset($tache['idTache']) and intval($tache['idTache'])>0 and isset($tache['nomTache']) and !empty($tache['nomTache']) and isset($tache['idEtat']) and intval($tache['idEtat'])>0 and isset($tache['idPriorite']) and intval($tache['idPriorite'])>0 and isset($tache['versionTache']) and intval($tache['versionTache']) > getVersionTache($id, intval($tache['idTache']))) {
		
		$dateLimite = "";
		if(isset($tache['dateLimite']) and !empty($tache['dateLimite']))
			$dateLimite = mysql_real_escape_string($tache['dateLimite']);
		$descriptionTache = "";
		if(isset($tache['descriptionTache']) and !empty($tache['descriptionTache']))
			$descriptionTache = mysql_real_escape_string($tache['descriptionTache']);
		
		if(getVersionTache($id, intval($tache['idTache'])) == -1)
			$req="INSERT INTO tache VALUES('".intval($tache['idTache'])."','".mysql_real_escape_string($tache['nomTache'])."','$descriptionTache','$dateLimite','".intval($tache['idEtat'])."','".intval($tache['idPriorite'])."','".intval($tache['versionTache'])."','$id')";
		else
			$req="UPDATE tache SET nomTache='".mysql_real_escape_string($tache['nomTache'])."', descriptionTache='$descriptionTache', dateLimite='$dateLimite', idEtat='".intval($tache['idEtat'])."', idPriorite='".intval($tache['idPriorite'])."', versionTache='".intval($tache['versionTache'])."' WHERE idTache='".intval($tache['idTache'])."' AND identifiant='$id'";
		
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
		
		$versionTag = 1;
		if(isset($_POST['versionTag']) and intval($_POST['versionTag'])>0)
			$versionTag = intval($_POST['versionTag']);
		
		$ajoutTag = "INSERT INTO tag VALUES('".intval($_POST['idTag'])."','".mysql_real_escape_string($_POST['libelleTag'])."','$versionTag','$id')";
		if(mysql_query($ajoutTag))
			return true;
		else
			return false;
			
	}
	else
		return false;

}

function modifierTag($json, $id) {

	if(isset($_POST['idTag']) and intval($_POST['idTag'])>0 and isset($_POST['libelleTag']) and !empty($_POST['libelleTag']) and isset($_POST['versionTag']) and intval($_POST['versionTag']) > getVersionTag($id, intval($_POST['idTag']))) {
		if(getVersionTag($id, intval($_POST['idTag'])) == -1)
			$req="INSERT INTO tag VALUES('".intval($_POST['idTag'])."','".mysql_real_escape_string($_POST['libelleTag'])."','".intval($_POST['versionTag'])."','$id')";
		else
			$req="UPDATE tag SET libelleTag='".mysql_real_escape_string($_POST['libelleTag'])."', versionTag='".intval($_POST['versionTag'])."' WHERE idTag='".intval($_POST['idTag'])."' AND identifiant='$id'";
		
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

function getVersionTache($id, $idTache) {

	$versionTache = -1;

	$req="SELECT versionTache FROM tache WHERE idTache='$idTache' AND identifiant='$id'";
	$resTache = mysql_query($req);
	$tache=mysql_fetch_array($resTache);
	if(isset($tache[0]))
		$versionTache = intval($tache[0]);
	
	return $versionTache;

}

function getVersionTag($id, $idTag) {

	$versionTag = -1;

	$req="SELECT versionTag FROM tag WHERE idTag='$idTag' AND identifiant='$id'";
	$resTag = mysql_query($req);
	$tag=mysql_fetch_array($resTag);
	if(isset($tag[0]))
		$versionTag = intval($tag[0]);
	
	return $versionTag;

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
<?php

include_once("fonctions.inc.php");

if(connexionAutorisee()) {

	if(isset($_POST['objet'])) {
		
		if(isset($_POST['json']) and !empty($_POST['json']))
			$_POST['json'] = str_replace("\\\"","\"",$_POST['json']);
		
		switch($_POST['objet']) {
			
			case 'exporter' : 
				if(isset($_POST['json']) and !empty($_POST['json'])) {
					if(importerJson($_POST['json'], $_POST['identifiant']))
						echo 'reussi';
					else
						echo 'echec lors de l\'importation';
				}
				else
					echo 'pas de json';
				break;
			
			case 'importer' : 
				echo exporterJson($_POST['identifiant']);
				break;
			
			case 'exporter_puis_importer' : 
				if(isset($_POST['json']) and !empty($_POST['json'])) {
					echo importerPuisExporterJson($_POST['json'], $_POST['identifiant']);
				}
				else
					echo 'pas de json';
				break;

			case 'ajouter_tache' : 
				if(isset($_POST['json']) and !empty($_POST['json'])) {
					if(ajouterTache($_POST['identifiant']))
						echo 'reussi';
					else
						echo 'echec';
				}
				else
					echo 'echec';
				break;
				
			case 'modifier_tache' : 
				if(isset($_POST['json']) and !empty($_POST['json'])) {
					if(modifierTache($_POST['identifiant']))
						echo 'reussi';
					else
						echo 'echec';
				}
				else
					echo 'echec';
				break;
				
			case 'supprimer_tache' : 
				if(supprimerTache($_POST['identifiant'], intval($_POST['idTache'])))
					echo 'reussi';
				else
					echo 'echec';
				break;
				
			case 'ajouter_tag' : 
				if(isset($_POST['json']) and !empty($_POST['json'])) {
					if(ajouterTag($_POST['identifiant']))
						echo 'reussi';
					else
						echo 'echec';
				}
				else
					echo 'echec';
				break;
				
			case 'modifier_tag' :
				if(isset($_POST['json']) and !empty($_POST['json'])) {
					if(modifierTag($_POST['identifiant']))
						echo 'reussi';
					else
						echo 'echec';
				}
				else
					echo 'echec';
				break;
				
			case 'supprimer_tag' : 
				if(supprimerTag($_POST['identifiant']))
					echo 'reussi';
				else
					echo 'echec';
				break;
				
			case 'ajouter_user' : 
				if(ajoutUtilisateur())
					echo 'reussi';
				else
					echo 'echec';
				break;
				
			case 'supprimer_user' : 
				if(supprimerUtilisateur($id))
					echo 'reussi';
				else
					echo 'echec';
				break;
			
			default : 
				echo 'objet inexistant';
				break;
		}
	}
	else
		echo 'pas d\'objets';
}
else
	echo 'Erreur de connexion';
	
//mysql_close();

?>
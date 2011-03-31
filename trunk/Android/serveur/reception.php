<?php

include_once("fonctions.inc.php");

if(connexionAutorisee()) {

	$codeJson='
	{"tags":[
			{"idTag":1,"libelleTag":"Université"},
			{"idTag":2,"libelleTag":"personnel"},
			{"idTag":3,"libelleTag":"professionnel"},
			{"idTag":4,"libelleTag":"examen"}
		],"nbTags":4,
	"taches":[
			{"idTache":1,"nomTache":"test ahaaa","descriptionTache":"description test tache 1","dateLimite":"","idEtat":2,"idPriorite":1,"apourtag":[1,2,3],"apourfils":[2,3]},
			{"idTache":2,"nomTache":"svsv","descriptionTache":"svs","dateLimite":"","idEtat":1,"idPriorite":4,"apourtag":[1,3],"apourfils":[]},
			{"idTache":3,"nomTache":"ahaaa","descriptionTache":"description test tache 3","dateLimite":"","idEtat":2,"idPriorite":4,"apourtag":[1,2],"apourfils":[]},
			{"idTache":4,"nomTache":"test tache 4","descriptionTache":"description test tache 4","dateLimite":"","idEtat":3,"idPriorite":3,"apourtag":[1],"apourfils":[5,6,7]},
			{"idTache":5,"nomTache":"test tache 5","descriptionTache":"description test tache 5","dateLimite":"","idEtat":4,"idPriorite":2,"apourtag":[3],"apourfils":[]},
			{"idTache":6,"nomTache":"test tache 6","descriptionTache":"description test tache 6","dateLimite":"","idEtat":3,"idPriorite":2,"apourtag":[3],"apourfils":[]},
			{"idTache":7,"nomTache":"test tache 7","descriptionTache":"description test tache 7","dateLimite":"","idEtat":1,"idPriorite":3,"apourtag":[2],"apourfils":[]},
			{"idTache":8,"nomTache":"test tache 8","descriptionTache":"description test tache 8","dateLimite":"","idEtat":1,"idPriorite":1,"apourtag":[1,2,3],"apourfils":[]}
		],"nbTaches":8,
	"apourtag":[
			{"idTache":1,"idTag":1},
			{"idTache":1,"idTag":2},
			{"idTache":1,"idTag":3},
			{"idTache":2,"idTag":1},
			{"idTache":2,"idTag":3},
			{"idTache":3,"idTag":1},
			{"idTache":3,"idTag":2},
			{"idTache":4,"idTag":1},
			{"idTache":5,"idTag":3},
			{"idTache":6,"idTag":3},
			{"idTache":7,"idTag":2},
			{"idTache":8,"idTag":1},
			{"idTache":8,"idTag":2},
			{"idTache":8,"idTag":3}
		],
	"apourfils":[
			{"idPere":1,"idFils":2},
			{"idPere":1,"idFils":3},
			{"idPere":4,"idFils":5},
			{"idPere":4,"idFils":6},
			{"idPere":4,"idFils":7}
		]
	}
	';

	/*
	if(isset($_POST['json']) and !empty($_POST['json']))
		echo $_POST['json'];
	else
		echo 'echec</br>';

	$json = '{"a":[{"a":5,"b":20}],"b":2,"c":3,"d":4,"e":5}';  

	$tab=json_decode($json);
	$tabassoc=json_decode($json, true);

	echo $tabassoc['a'][0]['a'];
	*/
	if(isset($_POST['json']) and !empty($_POST['json'])) {
		if(importerJson($_POST['json'], $_POST['identifiant']))
			echo 'reussi';
		else
			echo 'echec';
	}
	else
		echo 'echec';

}
else
	echo 'Erreur de connexion';
	
//mysql_close();

?>
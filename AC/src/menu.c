/*
 ******************************************************************************
 *
 * Programme : menu.c
 *
 * ecrit par : Guillaume MONTAVON & Benoit MEILHAC
 *
 * resume :    permet l'affichage d'un menu donnant acces aux fonctionnalites
 *             demandees (creation, lecture, insertion, ...)
 *
 * date :      24/02/2011
 *
 ******************************************************************************
 */

#include "menu.h"

/*
 * Fonction :    clearScanf
 *
 * Parametres :  rien
 *
 * Retour :      rien
 *
 * Description : libere le buffer du scanf
 *
 */
void clearScanf(void){                                                      
	char c;
	while((c = getchar()) != EOF && c != '\n'); 
	return;
}

/*
 * Fonction :    menu
 *
 * Parametres :  TypGraphe**, graphe courant
 *
 * Retour :      rien
 *
 * Description : affichage d'un menu qui donne acces a l'utilisateur a de
 *               nombreuses fonctionnalites afin de modifier un graphe
 *
 */
void menu(TypGraphe** grapheCourant) { 
	while(1) {
		afficherMenu(grapheCourant);
		actionsMenu(grapheCourant);
	}
}

/*
 * Fonction :    afficherMenu
 *
 * Parametres :  TypGraphe**, graphe courant
 *
 * Retour :      rien
 *
 * Description : affichage simplement le menu avec les differentes options
 *               disponibles
 *
 */
void afficherMenu(TypGraphe** grapheCourant) {

	fprintf(stdout, "============== Menu =============\n\n");
	fprintf(stdout, "Que souhaitez-vous faire?\n");
	fprintf(stdout, "  1.Creation d'un graphe\n");
	fprintf(stdout, "  2.Lecture d'un graphe dans un fichier texte\n");
	if((*grapheCourant) != NULL && grapheCourant != NULL) {
		fprintf(stdout, "  3.Insertion d'un sommet dans le graphe\n");
		fprintf(stdout, "  4.Insertion d'une arete entre deux sommets");
		fprintf(stdout, " du graphe\n");
		fprintf(stdout, "  5.Suppression d'un sommet du graphe\n");
		fprintf(stdout, "  6.Suppression d'une arete entre deux sommets");
		fprintf(stdout, " du graphe\n");
		fprintf(stdout, "  7.ACM Kruskal\n");
		fprintf(stdout, "  8.ACM Prim\n");
		fprintf(stdout, "  9.Affichage du graphe\n");
		fprintf(stdout, "  10.Sauvegarde du graphe dans un fichier\n");
		fprintf(stdout, "  11.Quitter\n");
	}
	else
		fprintf(stdout, "  3.Quitter\n");
	fprintf(stdout, "=================================\n");

}

/*
 * Fonction :    actionsMenu
 *
 * Parametres :  TypGraphe**, graphe courant
 *
 * Retour :      rien
 *
 * Description : Gere les differentes actions qui peuvent etre demandees par
 *               l'utilisateur
 *
 */
void actionsMenu(TypGraphe** grapheCourant) {

	int reponse = 0;

	scanf("%d",&reponse);
	clearScanf();
	if((*grapheCourant) == NULL || grapheCourant == NULL) {
		if(reponse < CREATION || reponse > INSERT_SOMMET)
			reponse = -1;
		if(reponse == INSERT_SOMMET)
			reponse = QUITTER;
	}

	switch(reponse) {
			//Creation d'un graphe
		case CREATION :
			creationGraphe(grapheCourant);
			break;
			//Lecture d'un graphe dans un fichier texte
		case LECTURE_EXISTANT :
			lectureFichierExistant(grapheCourant);
			break;
			//Insertion d'un sommet dans le graphe
		case INSERT_SOMMET :
			insertionSommetGraphe(grapheCourant);	
			break;
			//Insertion d'une arete entre deux sommets du graphe
		case INSERT_ARETE :
			insertionAreteGraphe(grapheCourant);
			break;
			//Suppression d'un sommet du graphe
		case SUPPRIME_SOMMET :
			supprimeSommetGraphe(grapheCourant);
			break;
			//Suppression d'une arete entre deux sommets du graphe
		case SUPPRIME_ARETE :
			supprimeAreteGraphe(grapheCourant);
			break;
			//ACM Kruskal
		case KRUSKAL:
			Kruskal(*grapheCourant);
			break;
			//ACM Prim
		case PRIM:
			
			break;
			//Affichage du graphe
		case AFFICHAGE :
			affichageGraphe((*grapheCourant));
			break;
			//Sauvegarde du graphe dans un fichier
		case SAUVEGARDE :
			sauvegardeGraphe(grapheCourant);
			break;
			//Quitter
		case QUITTER :
			quitterMenuGraphe(grapheCourant);
			break;
			//mauvais choix
		default :
			fprintf(stderr, "Erreur lors de la saisie de votre choix, ");
			fprintf(stderr, "Veuillez recommencer.\n");
	}

}

/*
 * Fonction :    demandeSuppression
 *
 * Parametres :  TypGraphe**, graphe courant
 *
 * Retour :      int, retourne 0 si on peut continuer les operations (graphe
 *                    supprime ou vide), retourne -1 si l'utilisateur n'a pas
 *                    voulu le supprimer, et donc les operations suivantes
 *                    doivent etre annulees
 *
 * Description : Si un graphe existe deja, on demande a l'utilisateur, s'il
 *               souhaite le supprimer afin d'en creer un nouveau
 *
 */
int demandeSuppression(TypGraphe** grapheCourant) {

	int retour = 0, reponse = 0;

	if((*grapheCourant) != NULL && grapheCourant != NULL){
		do {

			fprintf(stdout, "Un graphe existe deja, souhaitez-vous le ");
			fprintf(stdout, "supprimer ?\n");
			fprintf(stdout, "  1.Oui\n");
			fprintf(stdout, "  2.Non\n");
			scanf("%d", &reponse);
			clearScanf();
			if(reponse == 1)
				supprimerGraphe(grapheCourant);
			else
				retour = -1;
			if (( reponse != 2 ) && ( reponse != 1 ))
				fprintf(stdout, "Veuillez saisir un choix correct.\n");

		} while(( reponse != 2 ) && ( reponse != 1 ));

	}

	return retour;

}

/*
 * Fonction :    afficherErreur
 *
 * Parametres :  casErreur, code de l'erreur demande
 *
 * Retour :      rien
 *
 * Description : affiche une erreur suivant le code d'erreur donne en parametre
 *
 */
void afficherErreur(casErreur erreur) {
	char erreur2String[100];

	switch(erreur) {
		case ECHEC_ALLOC_PL : 
			strcpy(erreur2String, "Pointeur sur liste non alloue");
			break;
		case ECHEC_ALLOC_L : 
			strcpy(erreur2String, "Liste non allouee");
			break;
		case ECHEC_ALLOC_PG : 
			strcpy(erreur2String, "Pointeur sur graphe non alloue");
			break;
		case ECHEC_ALLOC_G : 
			strcpy(erreur2String, "Graphe non alloue");
			break;
		case INSERT_SOMMET_INCORRECT : 
			strcpy(erreur2String, "Le sommet est incorrect, dépassement de taille");
			break;
		case INSERT_ARETE_SOMMET_INCORRECT : 
			strcpy(erreur2String, "Un des sommets est incorrect, ");
			strcat(erreur2String, "dépassement de taille");
			break;
		case INSERT_ARETE_SOMMET_EXISTE_PAS : 
			strcpy(erreur2String, "Un des sommets n'a pas été ajouté");
			break;
		case SUPPR_SOMMET_SOMMET_INEXISTANT : 
			strcpy(erreur2String, "Le sommet est incorrect, dépassement de taille");
			break;
		case SUPPR_ARETE_SOMMET_INEXISTANT : 
			strcpy(erreur2String, "Un des sommets est incorrect, ");
			strcat(erreur2String, "dépassement de taille");
			break;
		case ECHEC_OUVERTURE_FICHIER : 
			strcpy(erreur2String, "Impossible d'ouvrir le fichier");
			break;
		case NB_SOMMET_INF_0 : 
			strcpy(erreur2String, "Le nombre de sommet maximum est superieur a 0");
			break;
		case STRUCT_FICHIER_INCORRECTE : 
			strcpy(erreur2String, "Structure de fichier incorrecte");
			break;
		case FICHIER_SOMMET_SUP_MAX : 
			strcpy(erreur2String, "Un des sommets est superieur ");
			strcat(erreur2String,  "a la valeur maximale autorisée");
			break;
		case FICHIER_REF_SOMMET_INEXISTANT : 
			strcpy(erreur2String, "Une arete fait reference a un sommet ");
			strcat(erreur2String, "qui n'a pas été créé");
			break;
		default : break;

	}

	fprintf(stderr, "%s\n",erreur2String);

}

/*
 * Fonction :    creationGraphe
 *
 * Parametres :  TypGraphe**, graphe courant
 *
 * Retour :      rien
 *
 * Description : gere dans le menu la creation d'un graphe
 *
 */
void creationGraphe(TypGraphe **grapheCourant){
	casErreur erreur = PAS_ERREUR;

	if(demandeSuppression(grapheCourant) == 0){
		int maxSommet;
		do{
			fprintf(stdout, "Combien de sommets, au maximum, ");
			fprintf(stdout, "possedera votre graphe ?\n");
			scanf("%d", &maxSommet);
			clearScanf();
		}while(maxSommet <= 0);

		erreur = initialisationGraphe(grapheCourant, maxSommet); 
		if(erreur == PAS_ERREUR)
			fprintf(stdout, "Creation du graphe effectuee\n");
		else {
			fprintf(stderr, "Erreur lors de la creation du graphe : ");
			afficherErreur(erreur);
		}
	}

	return;
}

/*
 * Fonction :    lectureFichierExistant
 *
 * Parametres :  TypGraphe**, graphe courant
 *
 * Retour :      rien
 *
 * Description : gere dans le menu la lecture d'un graphe dans un fichier texte
 *
 */
void lectureFichierExistant(TypGraphe** grapheCourant){
	casErreur erreur = PAS_ERREUR;

	if(demandeSuppression(grapheCourant) == 0) {
		char chemin[250];
		fprintf(stdout, "Veuillez entrer le chemin du fichier ");
		fprintf(stdout, "contenant le graphe\n");
		scanf("%s", chemin);
		clearScanf();

		erreur = chargementFichier(chemin, grapheCourant);
		if(erreur == PAS_ERREUR)
			fprintf(stdout, "Chargement du fichier '%s' effectuee\n", chemin);
		else {
			fprintf(stderr, "Erreur lors du chargement du fichier %s : ", 
				chemin);
			afficherErreur(erreur);
		}
	}

	return;
}

/*
 * Fonction :    insertionSommetGraphe
 *
 * Parametres :  TypGraphe**, graphe courant
 *
 * Retour :      rien
 *
 * Description : gere dans le menu l'insertion d'un sommet dans le graphe
 *
 */
void insertionSommetGraphe(TypGraphe** grapheCourant){
	casErreur erreur = PAS_ERREUR;

	int id = -1;
	do {

		fprintf(stdout, "Quel est le numero du sommet ?\n");
		scanf("%d", &id);
		clearScanf();

	} while (id >= (*grapheCourant)->nbMaxSommets || id<=0);

	erreur = insertionSommet(grapheCourant, id);
	if(erreur == PAS_ERREUR)
		fprintf(stdout, "Insertion d'un nouveau sommet %d effectuee\n",id);
	else {
		fprintf(stderr, "Erreur lors de l'insertion d'un nouveau sommet %d : ",
			id);
		afficherErreur(erreur);
	}

	return;
}

/*
 * Fonction :    insertionAreteGraphe
 *
 * Parametres :  TypGraphe**, graphe courant
 *
 * Retour :      rien
 *
 * Description : gere dans le menu l'insertion d'une arete entre deux sommets
 *
 */
void insertionAreteGraphe(TypGraphe** grapheCourant){
	casErreur erreur = PAS_ERREUR;

	int s1, s2, poids;
	char orientee;
	do {
		fprintf(stdout, "Quel est le premier sommet ?\n");
		scanf("%d", &s1);
		clearScanf();
	} while(s1 >= (*grapheCourant)->nbMaxSommets || s1<=0);

	do {
		fprintf(stdout, "Quel est le deuxieme sommet ?\n");
		scanf("%d", &s2);
		clearScanf();
	} while(s2 >= (*grapheCourant)->nbMaxSommets || s2<=0);

	do {
		fprintf(stdout, "Quel est le poids de l'arete ?\n");
		scanf("%d", &poids);
		clearScanf();
	} while(poids<0);

	do {
		fprintf(stdout, "S'agit-il d'une arete orientee (o) ou non (n)?\n");
		scanf("%c", &orientee);
		clearScanf();
	} while(orientee!='o' && orientee!='n');

	//verification que les sommets extremites existent et que
	//cette arete n'est pas deja presente dans le graphe
	erreur = insertionArete(grapheCourant, s1, s2, poids);
	if(orientee == 'n')
		erreur = insertionArete(grapheCourant, s2, s1, poids);

	if(erreur == PAS_ERREUR) {
		fprintf(stdout, "Insertion d'une arete entre les sommets %d et %d", 
			s1, s2);
		fprintf(stdout, " effectuee\n");
	}
	else {
		fprintf(stderr, "Erreur lors de l'insertion d'une arete ");
		fprintf(stderr, "entre les sommets %d et %d : ",s1,s2);
		afficherErreur(erreur);
	}

	return;
}

/*
 * Fonction :    supprimeSommetGraphe
 *
 * Parametres :  TypGraphe**, graphe courant
 *
 * Retour :      rien
 *
 * Description : gere dans le menu la suppression d'un sommet du graphe
 *
 */
void supprimeSommetGraphe(TypGraphe** grapheCourant){
	casErreur erreur = PAS_ERREUR;

	int id = -1;
	do {

		fprintf(stdout, "Quel est le numero du sommet a supprimer ?\n");
		scanf("%d", &id);
		clearScanf();

	} while (id >= (*grapheCourant)->nbMaxSommets || id<=0);

	erreur = suppressionSommet(grapheCourant, id);
	if(erreur == PAS_ERREUR)
		fprintf(stdout, "Suppression du sommet %d effectuee\n",id);
	else {
		fprintf(stderr, "Erreur lors de la suppression du sommet %d : ",id);
		afficherErreur(erreur);
	}

	return;
}

/*
 * Fonction :    supprimeAreteGraphe
 *
 * Parametres :  TypGraphe**, graphe courant
 *
 * Retour :      rien
 *
 * Description : gere dans le menu la suppression d'une arete entre deux
 *               sommets
 *
 */
void supprimeAreteGraphe(TypGraphe** grapheCourant){
	casErreur erreur = PAS_ERREUR;

	int s1, s2;
	do {
		fprintf(stdout, "Quel est le premier sommet ?\n");
		scanf("%d", &s1);
		clearScanf();
	} while(s1 >= (*grapheCourant)->nbMaxSommets || s1<=0);

	do {
		fprintf(stdout, "Quel est le deuxieme sommet ?\n");
		scanf("%d", &s2);
		clearScanf();
	} while(s2 >= (*grapheCourant)->nbMaxSommets || s2<=0);

	erreur = suppressionArete(grapheCourant, s1, s2);
	if(erreur == PAS_ERREUR) {
		fprintf(stdout, "Suppression de l'arete entre le sommet %d et %d", 
			s1, s2);
		fprintf(stdout, " effectuee\n");
	}
	else {
		fprintf(stderr, "Erreur lors de la suppression de l'arete ");
		fprintf(stderr, "entre le sommet %d et %d : ", s1, s2);
		afficherErreur(erreur);
	}

	return;
}

/*
 * Fonction :    sauvegardeGraphe
 *
 * Parametres :  TypGraphe**, graphe courant
 *
 * Retour :      rien
 *
 * Description : gere dans le menu la sauvegarde d'un graphe dans un fichier
 *               texte
 *
 */
void sauvegardeGraphe(TypGraphe** grapheCourant){
	casErreur erreur = PAS_ERREUR;

	char chemin[250];
	fprintf(stdout, "Donnez un chemin pour le fichier ? ");
	scanf("%s", chemin);
	clearScanf();
	erreur = ecritureFichier(chemin, (*grapheCourant));
	if(erreur == PAS_ERREUR)
		fprintf(stdout, "Ecriture du graphe dans le fichier '%s' effectuee\n",
		chemin);
	else {
		fprintf(stderr, "Erreur lors de l'ecriture du graphe dans le fichier");
		fprintf(stderr, " '%s' : ", chemin);
		afficherErreur(erreur);
	}

	return;
}

/*
 * Fonction :    quitterMenuGraphe
 *
 * Parametres :  TypGraphe**, graphe courant
 *
 * Retour :      rien
 *
 * Description : gere dans le menu la terminaison du programme
 *
 */
void quitterMenuGraphe(TypGraphe** grapheCourant){
	casErreur erreur = PAS_ERREUR;

	erreur = supprimerGraphe(grapheCourant);
	if(erreur == PAS_ERREUR)
		fprintf(stdout, "Vidage de la memoire effectue\n");
	else {
		fprintf(stderr, "Erreur lors du vidage de la memoire : ");
		afficherErreur(erreur);
	}
	fprintf(stdout, "Fermeture de l'application\n");
	exit(0);

	return;
}

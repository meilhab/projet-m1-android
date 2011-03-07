/*
******************************************************************************
*
* Programme : menu.c
*
* ecrit par : Guillaume MONTAVON & Benoit MEILHAC
*
* resume : permet l'affichage d'un menu donnant acces aux fonctionnalites
*          demandees (creation, lecture, insertion, ...)
*
* date : 24/02/2011
*
******************************************************************************
*/

#include "menu.h"

void clearScanf(void){                                                      
	char c;
	while((c = getchar()) != EOF && c != '\n'); 
	return;
}


void menu(TypGraphe** grapheCourant) { 
	while(1) {
		afficherMenu(grapheCourant);
		actionsMenu(grapheCourant);
	}
}

/**
 * Affiche le menu
 */
void afficherMenu(TypGraphe** grapheCourant) {

	printf("============== Menu =============\n\n");
	printf("Que souhaitez-vous faire?\n");
	printf("  1.Creation d'un graphe\n");
	printf("  2.Lecture d'un graphe dans un fichier texte\n");
	if((*grapheCourant) != NULL && grapheCourant != NULL) {
		printf("  3.Insertion d'un sommet dans le graphe\n");
		printf("  4.Insertion d'une arete entre deux sommets du graphe\n");
		printf("  5.Suppression d'un sommet du graphe\n");
		printf("  6.Suppression d'une arete entre deux sommets du graphe\n");
		printf("  7.Affichage du graphe\n");
		printf("  8.Sauvegarde du graphe dans un fichier\n");
		printf("  9.Quitter\n");
	}
	else
		printf("  3.Quitter\n");
	printf("=================================\n");

}

/**
 * Gere les differentes actions qui peuvent etre demandees par l'utilisateur
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
			printf("Erreur lors de la saisie de votre choix, ");
			printf("Veuillez recommencer.\n");
	}

}

/**
 * Si un graphe existe deja, on demande a l'utilisateur, s'il souhaite le
 * supprimer afin d'en creer un nouveau
 * retourne 0 si on peut continuer les operations (graphe supprime ou vide)
 * retourn -1 si l'utilisateur n'a pas voulu le supprimer, et donc
 * les operations suivantes doivent etre annulees
 */
int demandeSuppression(TypGraphe** grapheCourant) {

	int retour = 0, reponse = 0;

	if((*grapheCourant) != NULL && grapheCourant != NULL){
		do {

			printf("Un graphe existe deja, souhaitez-vous le supprimer ?\n");
			printf("  1.Oui\n");
			printf("  2.Non\n");
			scanf("%d", &reponse);
			clearScanf();
			if(reponse == 1)
				supprimerGraphe(grapheCourant);
			else
				retour = -1;
			if (( reponse != 2 ) && ( reponse != 1 ))
				printf("Veuillez saisir un choix correct.\n");

		} while(( reponse != 2 ) && ( reponse != 1 ));

	}

	return retour;

}

void afficherErreur(casErreur erreur) {
	char* erreur2String = "";

	switch(erreur) {
		case ECHEC_ALLOC_PL : 
			erreur2String = "Pointeur sur liste non alloue";
			break;
		case ECHEC_ALLOC_L : 
			erreur2String = "Liste non allouee";
			break;
		case ECHEC_ALLOC_PG : 
			erreur2String = "Pointeur sur graphe non alloue";
			break;
		case ECHEC_ALLOC_G : 
			erreur2String = "Graphe non alloue";
			break;
		case INSERT_SOMMET_INCORRECT : 
			erreur2String = "Le sommet est incorrect, dépassement de taille";
			break;
		case INSERT_ARETE_SOMMET_INCORRECT : 
			erreur2String = "Un des sommets est incorrect, dépassement de taille";
			break;
		case INSERT_ARETE_SOMMET_EXISTE_PAS : 
			erreur2String = "Un des sommets n'a pas été ajouté";
			break;
		case SUPPR_SOMMET_SOMMET_INEXISTANT : 
			erreur2String = "Le sommet est incorrect, dépassement de taille";
			break;
		case SUPPR_ARETE_SOMMET_INEXISTANT : 
			erreur2String = "Un des sommet est incorrect, dépassement de taille";
			break;
		case ECHEC_OUVERTURE_FICHIER : 
			erreur2String = "Impossible d'ouvrir le fichier";
			break;
		case NB_SOMMET_INF_0 : 
			erreur2String = "Le nombre de sommet maximum est superieur a 0";
			break;
		case STRUCT_FICHIER_INCORRECTE : 
			erreur2String = "Structure de fichier incorrecte";
			break;
		case FICHIER_SOMMET_SUP_MAX : 
			erreur2String = "Un des sommets est superieur a la valeur maximale autorise";
			break;
		case FICHIER_REF_SOMMET_INEXISTANT : 
			erreur2String = "une arete fait reference a un sommet qui n'a pas ete cree";
			break;
		default : break;

	}

	printf("%s\n",erreur2String);

}

void creationGraphe(TypGraphe **grapheCourant){
	casErreur erreur = PAS_ERREUR;

	if(demandeSuppression(grapheCourant) == 0){
		int maxSommet;
		do{
			printf("Combien de sommets, au maximum, ");
			printf("possedera votre graphe ?\n");
			scanf("%d", &maxSommet);
			clearScanf();
		}while(maxSommet <= 0);

		erreur = initialisationGraphe(grapheCourant, maxSommet); 
		if(erreur == PAS_ERREUR)
			printf("Creation du graphe effectuee\n");
		else {
			printf("Erreur lors de la creation du graphe : ");
			afficherErreur(erreur);
		}
	}

	return;
}

void lectureFichierExistant(TypGraphe** grapheCourant){
	casErreur erreur = PAS_ERREUR;

	if(demandeSuppression(grapheCourant) == 0) {
		char chemin[250];
		printf("Veuillez entrer le chemin du fichier ");
		printf("contenant le graphe\n");
		scanf("%s", chemin);
		clearScanf();

		erreur = chargementFichier(chemin, grapheCourant);
		if(erreur == PAS_ERREUR)
			printf("Chargement du fichier '%s' effectuee\n", chemin);
		else {
			printf("Erreur lors du chargement du fichier %s : ", chemin);
			afficherErreur(erreur);
		}
	}

	return;
}

void insertionSommetGraphe(TypGraphe** grapheCourant){
	casErreur erreur = PAS_ERREUR;

	int id = -1;
	do {

		printf("Quel est le numero du sommet ?\n");
		scanf("%d", &id);
		clearScanf();

	} while (id >= (*grapheCourant)->nbMaxSommets || id<=0);

	erreur = insertionSommet(grapheCourant, id);
	if(erreur == PAS_ERREUR)
		printf("Insertion d'un nouveau sommet %d effectuee\n",id);
	else {
		printf("Erreur lors de l'insertion d'un nouveau sommet %d : ",id);
		afficherErreur(erreur);
	}

	return;
}

void insertionAreteGraphe(TypGraphe** grapheCourant){
	casErreur erreur = PAS_ERREUR;

	int s1, s2, poids;
	char orientee;
	do {
		printf("Quel est le premier sommet ?\n");
		scanf("%d", &s1);
		clearScanf();
	} while(s1 >= (*grapheCourant)->nbMaxSommets || s1<=0);

	do {
		printf("Quel est le deuxieme sommet ?\n");
		scanf("%d", &s2);
		clearScanf();
	} while(s2 >= (*grapheCourant)->nbMaxSommets || s2<=0);

	do {
		printf("Quel est le poids de l'arete ?\n");
		scanf("%d", &poids);
		clearScanf();
	} while(poids<0);

	do {
		printf("S'agit-il d'une arete orientee (o) ou non (n)?\n");
		scanf("%c", &orientee);
		clearScanf();
	} while(orientee!='o' && orientee!='n');

	//verification que les sommets extremites existent et que
	//cette arete n'est pas deja presente dans le graphe
	erreur = insertionArete(grapheCourant, s1, s2, poids);
	if(orientee == 'n')
		erreur = insertionArete(grapheCourant, s2, s1, poids);

	if(erreur == PAS_ERREUR)
		printf("Insertion d'une arete entre les sommets %d et %d effectuee\n",s1,s2);
	else {
		printf("Erreur lors de l'insertion d'une arete entre les sommets %d et %d : ",s1,s2);
		afficherErreur(erreur);
	}

	return;
}

void supprimeSommetGraphe(TypGraphe** grapheCourant){
	casErreur erreur = PAS_ERREUR;

	int id = -1;
	do {

		printf("Quel est le numero du sommet a supprimer ?\n");
		scanf("%d", &id);
		clearScanf();

	} while (id >= (*grapheCourant)->nbMaxSommets || id<=0);

	erreur = suppressionSommet(grapheCourant, id);
	if(erreur == PAS_ERREUR)
		printf("Suppression du sommet %d effectuee\n",id);
	else {
		printf("Erreur lors de la suppression du sommet %d : ",id);
		afficherErreur(erreur);
	}

	return;
}

void supprimeAreteGraphe(TypGraphe** grapheCourant){
	casErreur erreur = PAS_ERREUR;

	int s1, s2;
	do {
		printf("Quel est le premier sommet ?\n");
		scanf("%d", &s1);
		clearScanf();
	} while(s1 >= (*grapheCourant)->nbMaxSommets || s1<=0);

	do {
		printf("Quel est le deuxieme sommet ?\n");
		scanf("%d", &s2);
		clearScanf();
	} while(s2 >= (*grapheCourant)->nbMaxSommets || s2<=0);

	erreur = suppressionArete(grapheCourant, s1, s2);
	if(erreur == PAS_ERREUR)
		printf("Suppression de l'arete entre le sommet %d et %d effectuee\n",s1,s2);
	else {
		printf("Erreur lors de la suppression de l'arete entre le sommet %d et %d : ",s1,s2);
		afficherErreur(erreur);
	}

	return;
}

void sauvegardeGraphe(TypGraphe** grapheCourant){
	casErreur erreur = PAS_ERREUR;

	char chemin[250];
	printf("Donnez un chemin pour le fichier ? ");
	scanf("%s", chemin);
	clearScanf();
	erreur = ecritureFichier(chemin, (*grapheCourant));
	if(erreur == PAS_ERREUR)
		printf("Ecriture du graphe dans le fichier '%s' effectuee\n",chemin);
	else {
		printf("Erreur lors de l'ecriture du graphe dans le fichier '%s' : ",chemin);
		afficherErreur(erreur);
	}

	return;
}

void quitterMenuGraphe(TypGraphe** grapheCourant){
	casErreur erreur = PAS_ERREUR;

	erreur = supprimerGraphe(grapheCourant);
	if(erreur == PAS_ERREUR)
		printf("Vidage de la memoire effectue\n");
	else {
		printf("Erreur lors du vidage de la memoire : ");
		afficherErreur(erreur);
	}
	printf("Fermeture de l'application\n");
	exit(0);

	return;
}


































/*
 ******************************************************************************
 *
 * Programme : libgraphe.c
 *
 * ecrit par : Guillaume MONTAVON & Benoit MEILHAC
 *
 * resume :    bibliothèque permettant la gestion du graphe (sommets)
 *
 * date :      24/02/2011
 *
 ******************************************************************************
 */

#include "libgraphe.h"

/*
 * Fonction :    initialisationGraphe
 *
 * Parametres :  TypGraphe**, graphe a initialiser
 *               int nbSommets, nombre de sommets maxi qu'acceptera le graphe 
 *
 * Retour :      casErreur, code d'erreur de la fonction
 *
 * Description : initialise le graphe en allouant la memoire du graphe et de
 *               la liste d'adjacence et en initialisant le nbSommets maximum
 *
 */
casErreur initialisationGraphe(TypGraphe **graphe, int nbSommets){
	(*graphe) = (TypGraphe*) malloc(sizeof(TypGraphe));

	casErreur erreur = verifAllocationPG(graphe);
	if(erreur != PAS_ERREUR)
		return erreur;

	if(nbSommets <= 0)
		return NB_SOMMET_INF_0;

	(*graphe)->nbMaxSommets = nbSommets;
	(*graphe)->listesAdjencences = 
		(TypVoisins**) malloc(nbSommets * sizeof(TypVoisins*));

//	erreur = verifAllocationL(*((*graphe)->listesAdjencences));
	if((*graphe)->listesAdjencences == NULL)
		return ECHEC_ALLOC_PL;

	if(erreur != PAS_ERREUR)
		return erreur;

	int i;
	for(i=0; i<nbSommets; i++)
		(*graphe)->listesAdjencences[i] = NULL;
	
	return PAS_ERREUR;
}

/*
 * Fonction :    insertionSommet
 *
 * Parametres :  TypGraphe**, graphe a modifier
 *               int sommet, numero du sommet a ajouter
 *
 * Retour :      casErreur, code d'erreur de la fonction
 *
 * Description : insere un sommet dans le graphe, puis initialsation de sa
 *               liste de voisins
 *
 */
casErreur insertionSommet(TypGraphe **graphe, int sommet){
	casErreur erreur = verifAllocationPG(graphe);
	if(erreur != PAS_ERREUR)
		return erreur;

	if(sommet - 1 > (*graphe)->nbMaxSommets || sommet - 1 < 0)
		return INSERT_SOMMET_INCORRECT;

	erreur = initialisationListe(&((*graphe)->listesAdjencences[sommet-1]));
	if(erreur != PAS_ERREUR)
		return erreur;
//	ajouterDebut(&((*graphe)->listesAdjencences[sommet-1]), 5, 2);
//	affichageListe((*graphe)->listesAdjencences[sommet-1]);

	return PAS_ERREUR;
}

/*
 * Fonction :    insertionArete
 *
 * Parametres :  TypGraphe**, graphe a modifier
 *               int sommetDebut, numero du premier sommet
 *               int sommetSuivant, numero du deuxieme sommet
 *               int poids, poids de l'arete a ajouter
 *
 * Retour :      casErreur, code d'erreur de la fonction
 *
 * Description : insère une arete entre deux sommets avec son poids
 *
 */
casErreur insertionArete(TypGraphe **graphe, int sommetDebut, 
		int sommetSuivant, int poids){
	casErreur erreur = verifAllocationPG(graphe);
	if(erreur != PAS_ERREUR)
		return erreur;

	int nbMaxSommets = (*graphe)->nbMaxSommets;
	if((sommetDebut-1 > nbMaxSommets) || (sommetSuivant-1 > nbMaxSommets) ||
			sommetDebut-1 < 0 || sommetSuivant-1 < 0)
		return INSERT_ARETE_SOMMET_INCORRECT;

	if((*graphe)->listesAdjencences[sommetDebut - 1] == NULL ||
		(*graphe)->listesAdjencences[sommetSuivant - 1] == NULL)
		return INSERT_ARETE_SOMMET_EXISTE_PAS;

	erreur = ajouterDebut(&((*graphe)->listesAdjencences[sommetDebut-1]), 
		sommetSuivant-1, poids);

	return erreur;
}

/*
 * Fonction :    suppressionSommet
 *
 * Parametres :  TypGraphe**, graphe a modifier
 *               int sommet, numero du sommet a supprimer
 *
 * Retour :      casErreur, code d'erreur de la fonction
 *
 * Description : supprime le sommet et tous les autres sommets qui ont une
 *               arete en commun avec lui
 *
 */
casErreur suppressionSommet(TypGraphe **graphe, int sommet){
	casErreur erreur = verifAllocationPG(graphe);
	if(erreur != PAS_ERREUR)
		return erreur;
	
	if(sommet - 1 > (*graphe)->nbMaxSommets || sommet - 1 < 0)
		return SUPPR_SOMMET_SOMMET_INEXISTANT;

	erreur = supprimerTout(&((*graphe)->listesAdjencences[sommet-1]));
	if(erreur != PAS_ERREUR)
		return erreur;
	
	int i;
	erreur = PAS_ERREUR;
	for(i = 0; i<(*graphe)->nbMaxSommets; i++)
		if((*graphe)->listesAdjencences[i] != NULL)
			if(existeVoisin((*graphe)->listesAdjencences[i], sommet - 1)
					== EXISTE){
				erreur = supprimerVoisin(
					&((*graphe)->listesAdjencences[i]), sommet-1);
			}
	
	return erreur;
}

/*
 * Fonction :    suppressionArete
 *
 * Parametres :  TypGraphe**, graphe a modifier
 *               int sommetDebut, numero du premier sommet
 *               int sommetSuivant, numero du deuxieme sommet
 *
 * Retour :      casErreur, code d'erreur de la fonction
 *
 * Description : supprime une arete entre deux sommets
 *
 */
casErreur suppressionArete(TypGraphe **graphe, 
		int sommetDebut, int sommetSuivant){
	casErreur erreur = verifAllocationPG(graphe);
	if(erreur != PAS_ERREUR)
		return erreur;
	
	if(sommetDebut - 1 > (*graphe)->nbMaxSommets || 
			sommetSuivant - 1 > (*graphe)->nbMaxSommets ||
			sommetDebut - 1 < 0 || sommetSuivant - 1 < 0)
		return SUPPR_ARETE_SOMMET_INEXISTANT;

	erreur = supprimerVoisin(&((*graphe)->listesAdjencences[sommetDebut-1]),
		sommetSuivant-1);

	return erreur;
}

/*
 * Fonction :    verifAllocationPG
 *
 * Parametres :  TypGraphe**, graphe a tester
 *
 * Retour :      casErreur, code d'erreur de la fonction
 *
 * Description : verifie qu'un pointeur sur graphe est bien initialise
 *
 */
casErreur verifAllocationPG(TypGraphe **graphe){	
	if(*graphe == NULL || graphe == NULL)
		return ECHEC_ALLOC_PG;

	return PAS_ERREUR;
}

/*
 * Fonction :    verifAllocationG
 *
 * Parametres :  TypGraphe**, graphe a tester
 *
 * Retour :      casErreur, code d'erreur de la fonction
 *
 * Description : verifie qu'une graphe est bien initialisee
 *
 */
casErreur verifAllocationG(TypGraphe *graphe){
	if(graphe == NULL)
		return ECHEC_ALLOC_G;

	return PAS_ERREUR;
}

/*
 * Fonction :    affichageGraphe
 *
 * Parametres :  TypGraphe*, graphe a afficher
 *
 * Retour :      casErreur, code d'erreur de la fonction
 *
 * Description : affiche un graphe dans le meme format que celui du fichier
 *               texte en entree
 *
 */
void affichageGraphe(TypGraphe *graphe){
	if(verifAllocationG(graphe) != PAS_ERREUR)
		return;

	int i;
	int taille = nombreMaxSommetsGraphe(graphe);

	fprintf(stdout, "# nombre maximum de sommets\n");
	fprintf(stdout, "%d\n", taille);
	fprintf(stdout, "# sommets : voisins\n");

	TypVoisins *liste;
	for(i=0; i<taille; i++){
		liste = graphe->listesAdjencences[i];
		if(estVide(liste) >= 0){
			fprintf(stdout, "%d : ", i + 1);
			while(liste->voisin != -1){
				if(liste->voisinSuivant->voisin == -1)
					fprintf(stdout, "(%d/%d)", 
						liste->voisin + 1, liste->poidsVoisin);
				else
					fprintf(stdout,"(%d/%d), ", 
						liste->voisin + 1, liste->poidsVoisin);
				liste = liste->voisinSuivant;
			}
			fprintf(stdout, "\n");
		}
	}
	return;
}

/*
 * Fonction :    supprimerGraphe
 *
 * Parametres :  TypGraphe**, graphe a supprimer
 *
 * Retour :      casErreur, code d'erreur de la fonction
 *
 * Description : supprime entierement un graphe en liberant toute la memoire
 *
 */
casErreur supprimerGraphe(TypGraphe **graphe){
	casErreur erreur = verifAllocationPG(graphe);
	if(erreur != PAS_ERREUR)
		return erreur;

	int i;
	erreur = PAS_ERREUR;
	for(i = 0; i<(*graphe)->nbMaxSommets; i++){
		/*technique de ninja !*/
		if(existeVoisin((*graphe)->listesAdjencences[i], -1) == EXISTE)
			erreur = supprimerTout(&((*graphe)->listesAdjencences[i]));
	}
	free((*graphe)->listesAdjencences);
	free((*graphe));
	(*graphe) = NULL;
	return erreur;
}

/*
 * Fonction :    nombreMaxSommetsGraphe
 *
 * Parametres :  TypGraphe*, graphe a supprimer
 *
 * Retour :      int, nombre de sommets maxi du graphe
 *
 * Description : donne le nombre de sommets maxi du graphe
 *
 */
int nombreMaxSommetsGraphe(TypGraphe *graphe){
	if(verifAllocationG(graphe) != PAS_ERREUR)
		return -1;

	return graphe->nbMaxSommets;
}

/*
 * Fonction :    passerCommentaire
 *
 * Parametres :  FILE*, fichier a importer
 *               int* ligne, numero de la ligne
 *
 * Retour :      casErreur, code d'erreur de la fonction
 *
 * Description : permet de sauter la ligne qui contient un commentaire 
 *
 */
casErreur passerCommentaire(FILE* fichier, int* ligne){
	char c;

	if((c = fgetc(fichier)) != '#'){
		fprintf(stderr,"Structure de fichier incorrecte à la ligne %d\n"
		, *ligne);
		return STRUCT_FICHIER_INCORRECTE;
	}

	while((c = fgetc(fichier)) != '\n'){}

	(*ligne)++;

	return PAS_ERREUR;
}

/*
 * Fonction :    controleCoherenceFichier
 *
 * Parametres :  char* nomFichier, chemin du fichier a tester
 *
 * Retour :      casErreur, code d'erreur de la fonction
 *
 * Description : controle si la structure du fichier est correct ou non
 *
 */
casErreur controleCoherenceFichier(char *nomFichier){
	int nbMaxSommets = 0, a;

	FILE *fichier = NULL;
	fichier = fopen(nomFichier, "r");
	if(fichier == NULL)
		return ECHEC_OUVERTURE_FICHIER;

	casErreur erreur = passerCommentaire(fichier, &a);
	if(erreur != PAS_ERREUR)
		return erreur;

	fscanf(fichier, "%d\n", &nbMaxSommets);
	if(nbMaxSommets <= 0)
		return NB_SOMMET_INF_0;

	erreur = passerCommentaire(fichier, &a);
	if(erreur != PAS_ERREUR)
		return erreur;

	int tabS[nbMaxSommets];
	int i;
	int sv, pv;

	i = 0;
	while(fscanf(fichier, "%d : ", &tabS[i]) != EOF){
		if(tabS[i] > nbMaxSommets)
			return FICHIER_SOMMET_SUP_MAX;

		while(fscanf(fichier, "(%d/%d), ", &sv, &pv)){
			if(sv > nbMaxSommets)
				return FICHIER_SOMMET_SUP_MAX;
		}
		i++;
	}
	
	fseek(fichier, 0, SEEK_SET);

	passerCommentaire(fichier, &a);
	fscanf(fichier, "%d\n", &nbMaxSommets);
	passerCommentaire(fichier, &a);

	int temp, terminer;
	while(fscanf(fichier, "%d : ", &temp) != EOF){
		while(fscanf(fichier, "(%d/%d), ", &sv, &pv)){
			i = terminer = 0;
			while(terminer == 0 && i < nbMaxSommets){
				if(tabS[i] == sv)
					terminer = 1;
				i++;
			}
			if(terminer != 1)
				return FICHIER_REF_SOMMET_INEXISTANT;
		}
	}

	fclose(fichier);

	return PAS_ERREUR;
}

/*
 * Fonction :    chargementFichier
 *
 * Parametres :  char* nomFichier, chemin du fichier a charger
 *               TypGraphe**, graphe qui contiendra le nouveau graphe importe
 *
 * Retour :      casErreur, code d'erreur de la fonction
 *
 * Description : charge le graphe contenu dans un fichier dans un graphe
 *               donne en parametre
 *
 */
casErreur chargementFichier(char *nomFichier, TypGraphe **graphe){
	FILE* fichier = NULL;
	int ligne = 0;
	int nbMaxSommets = 0;

	casErreur erreur = controleCoherenceFichier(nomFichier);
	if(erreur != PAS_ERREUR)
		return erreur;

	fichier = fopen(nomFichier, "r");
	if(fichier == NULL)
		return ECHEC_OUVERTURE_FICHIER;

	erreur = passerCommentaire(fichier, &ligne);
	if(erreur != PAS_ERREUR)
		return erreur;

	fscanf(fichier, "%d\n", &nbMaxSommets);
	ligne++;
	//printf("nombre de sommets : %d\n", nbMaxSommets);
	//
	erreur = initialisationGraphe(graphe, nbMaxSommets);
	if(erreur != PAS_ERREUR)
		return erreur;

	erreur = passerCommentaire(fichier, &ligne);
	if(erreur != PAS_ERREUR)
		return erreur;

	int tab[nbMaxSommets];
	int i = 0;
	int a, b;

	while(fscanf(fichier, "%d : ", &tab[i]) != EOF){
		fprintf(stdout, "sommet entré : %d\n", tab[i]);
		//
		erreur = insertionSommet(graphe, tab[i]);
			if(erreur != PAS_ERREUR)
				return erreur;
		//
		while(fscanf(fichier, "(%d/%d), ", &a, &b)){
			fprintf(stdout, "\t\tVers %d avec le poids %d\n", a, b);

			erreur = insertionSommet(graphe, a);
			//if(erreur != PAS_ERREUR)
			//	return erreur;

			//TODO revoir ici l'insertion d'élément n'existant pas
			erreur = insertionArete(graphe, tab[i], a, b);
			if(erreur != PAS_ERREUR)
				return erreur;
			//
		}
		i++;
		ligne++;
	}

	//printf("nombre de lignes du fichier : %d\n\n", ligne);
	fclose(fichier);

	return PAS_ERREUR;
}

/*
 * Fonction :    ecritureFichier
 *
 * Parametres :  char* nomFichier, chemin du fichier dans lequel on sauvegarde
 *               TypGraphe**, graphe qui contient le graphe a sauvegarder
 *
 * Retour :      casErreur, code d'erreur de la fonction
 *
 * Description : sauvegarde un graphe dans un fichier texte
 *
 */
casErreur ecritureFichier(char *nomFichier, TypGraphe *graphe){
	casErreur erreur = verifAllocationG(graphe);
	if(erreur != PAS_ERREUR)
		return erreur;

	FILE *fichier = NULL;
	fichier = fopen(nomFichier, "w");	
	if(fichier == NULL)
		return ECHEC_OUVERTURE_FICHIER;	

	int i;
	int taille = nombreMaxSommetsGraphe(graphe);

	fprintf(fichier, "# nombre maximum de sommets\n");
	fprintf(fichier, "%d\n", taille);
	fprintf(fichier, "# sommets : voisins\n");

	TypVoisins *liste;
	for(i=0; i<taille; i++){
		liste = graphe->listesAdjencences[i];
		if(estVide(liste) >= 0){
			fprintf(fichier, "%d : ", i + 1);
			while(liste->voisin != -1){
				if(liste->voisinSuivant->voisin == -1)
					fprintf(fichier, "(%d/%d)", 
						liste->voisin + 1, liste->poidsVoisin);
				else
					fprintf(fichier,"(%d/%d), ", 
						liste->voisin + 1, liste->poidsVoisin);
				liste = liste->voisinSuivant;
			}
			fprintf(fichier, "\n");
		}
	}


	return PAS_ERREUR;
}

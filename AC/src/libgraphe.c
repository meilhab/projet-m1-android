#include "libgraphe.h"

/* initialise le graphe avec nbSommets maximum*/
casErreur initialisationGraphe(TypGraphe **graphe, int nbSommets){
	(*graphe) = (TypGraphe*) malloc(sizeof(TypGraphe));

	casErreur erreur = verifAllocationPG(graphe);
	if(erreur != PAS_ERREUR)
		//printf("Probleme allocation memoire pour le graphe\n");
		return erreur;

	(*graphe)->nbMaxSommets = nbSommets;
	(*graphe)->listesAdjencences = 
		(TypVoisins**) malloc(nbSommets * sizeof(TypVoisins*));

	erreur = verifAllocationPL((*graphe)->listesAdjencences);
//	if((*graphe)->listesAdjencences == NULL){
//		printf("Probleme allocation memoire pour le graphe\n");
	if(erreur != PAS_ERREUR)
		return erreur;

	int i;
	for(i=0; i<nbSommets; i++)
		(*graphe)->listesAdjencences[i] = NULL;
	
	return 0;
}

/* insère le sommet dans le graphe : initialisation de sa liste */
casErreur insertionSommet(TypGraphe **graphe, int sommet){
	casErreur erreur = verifAllocationPG(graphe);
	if(erreur != PAS_ERREUR)
		return erreur;

	if(sommet - 1 > (*graphe)->nbMaxSommets && sommet - 1 < 0)
//		printf("Erreur dans l'insertion du sommet : %d\n", sommet);
//		printf("Sommet incorrect, dépassement de taille\n");
		return INSERT_SOMMET_INCORRECT;

	initialisationListe(&((*graphe)->listesAdjencences[sommet-1]));
//	ajouterDebut(&((*graphe)->listesAdjencences[sommet-1]), 5, 2);
//	affichageListe((*graphe)->listesAdjencences[sommet-1]);

	return PAS_ERREUR;
}

/* insère une arète entre deux sommets avec son poids */
/* fonction utile pour l'ajout manuel */
casErreur insertionArete(TypGraphe **graphe, int sommetDebut, 
		int sommetSuivant, int poids){
	casErreur erreur = verifAllocationPG(graphe);
	if(erreur != PAS_ERREUR)
		return erreur;

	int nbMaxSommets = (*graphe)->nbMaxSommets;
	if((sommetDebut-1 > nbMaxSommets) || (sommetSuivant-1 > nbMaxSommets) ||
			sommetDebut-1 < 0 || sommetSuivant-1 < 0)
//		printf("Erreur dans l'insertion d'une arete entre %d et %d\n", 
//			sommetDebut, sommetSuivant);
//		printf("Un des sommets est incorrect, dépassement de taille\n");
		return INSERT_ARETE_SOMMET_INCORRECT;

	if((*graphe)->listesAdjencences[sommetDebut - 1] == NULL ||
		(*graphe)->listesAdjencences[sommetSuivant - 1] == NULL)
//		printf("Erreur dans l'insertion d'une arete entre %d et %d\n", 
//			sommetDebut, sommetSuivant);
//		printf("Un des sommets n'a pas été ajouté\n");
		return INSERT_ARETE_SOMMET_EXISTE_PAS;

	ajouterDebut(&((*graphe)->listesAdjencences[sommetDebut-1]), 
		sommetSuivant-1, poids);

	return PAS_ERREUR;
}

/* supprime le sommet et tous les autres sommets qui ont une 
arète en commun avec lui */
casErreur suppressionSommet(TypGraphe **graphe, int sommet){
	casErreur erreur = verifAllocationPG(graphe);
	if(erreur != PAS_ERREUR)
		return erreur;
	
	if(sommet - 1 > (*graphe)->nbMaxSommets)
		//printf("Erreur dans la suppression du sommet : %d\n", sommet);
		//printf("Sommet incorrect, dépassement de taille\n");
		return SUPPR_SOMMET_SOMMET_INEXISTANT;

	supprimerTout(&((*graphe)->listesAdjencences[sommet-1]));

	int i;
	erreur=PAS_ERREUR;
	for(i=0; i<(*graphe)->nbMaxSommets; i++)
		if((*graphe)->listesAdjencences[i] != NULL)
			erreur=supprimerVoisin(&((*graphe)->listesAdjencences[i]), sommet-1);
	
	return erreur;
}

casErreur suppressionArete(TypGraphe **graphe, int sommetDebut, int sommetSuivant){
	casErreur erreur = verifAllocationPG(graphe);
	if(erreur != PAS_ERREUR)
		return erreur;
	
	if((sommetDebut - 1 > (*graphe)->nbMaxSommets) || 
			(sommetSuivant - 1 > (*graphe)->nbMaxSommets))
		//printf("Erreur dans la suppression d'une arete entre %d et %d\n",
			//sommetDebut, sommetSuivant);
		//printf("Sommet incorrect, dépassement de taille\n");
		return SUPPR_ARETE_SOMMET_INEXISTANT;

	erreur=supprimerVoisin(&((*graphe)->listesAdjencences[sommetDebut-1]), 
		sommetSuivant-1);

	return erreur;
}

/* vérifie qu'un pointeur sur graphe est bien initialisé */
casErreur verifAllocationPG(TypGraphe **graphe){	
	if(*graphe == NULL || graphe == NULL)
		//printf("Pointeur sur graphe non alloue\n");
		return ECHEC_ALLOC_PG;

	return PAS_ERREUR;
}

/* vérifie qu'une graphe est bien initialisée */
casErreur verifAllocationG(TypGraphe *graphe){
	if(graphe == NULL)
		//printf("Graphe non alloue\n");
		return ECHEC_ALLOC_G;

	return PAS_ERREUR;
}

void affichageGraphe(TypGraphe *graphe){
	if(verifAllocationG(graphe) != PAS_ERREUR)
		return;

	int i;
	TypVoisins *liste;
	for(i=0; i<graphe->nbMaxSommets; i++){
		liste = graphe->listesAdjencences[i];
		if(estVide(liste) > 0){
			printf("Sommet entré : %d\n", i+1);
			while(liste->voisin != -1){
				printf("\t\tVers %d avec le poids %d\n", (liste->voisin)+1, liste->poidsVoisin);
				liste = liste->voisinSuivant;
			}
		}
	}

	return;
}

casErreur supprimerGraphe(TypGraphe **graphe){
	casErreur erreur = verifAllocationPG(graphe);
	if(erreur != PAS_ERREUR)
		return erreur;

	int i;
	erreur=PAS_ERREUR;
	for(i=0; i<(*graphe)->nbMaxSommets; i++)
		erreur=supprimerTout(&((*graphe)->listesAdjencences[i]));

	(*graphe) = NULL;
	return erreur;
}

int nombreMaxSommetsGraphe(TypGraphe *graphe){
	if(verifAllocationG(graphe) != PAS_ERREUR)
		return -1;

	return graphe->nbMaxSommets;
}

#ifndef LIBGRAPHE_H
#define LIBGRAPHE_H

#include "libliste.h"

typedef enum casErreur{
	ECHEC_ALLOC_PL, ECHEC_ALLOC_L,
	ECHEC_ALLOC_PG, ECHEC_ALLOC_G,
	EXISTE, EXISTE_PAS,
	INSERT_SOMMET_INCORRECT,
	INSERT_ARETE_SOMMET_INCORRECT,
	INSERT_ARETE_SOMMET_EXISTE_PAS,
	PAS_ERREUR
} casErreur;

typedef struct TypGraphe{
	int nbMaxSommets;
	TypVoisins **listesAdjencences;
} TypGraphe;

int initialisationGraphe(TypGraphe **graphe, int nbSommets);
int insertionSommet(TypGraphe **graphe, int sommet);
int insertionArete(TypGraphe **graphe, int sommetDebut, 
	int sommetSuivant, int poids);
int suppressionSommet(TypGraphe **graphe, int sommet);
int suppressionArete(TypGraphe **graphe, int sommetDebut, int sommetSuivant);
void affichageGraphe(TypGraphe *graphe);
int verifAllocationPG(TypGraphe **graphe);	
int verifAllocationG(TypGraphe *graphe);	
int supprimerGraphe(TypGraphe **graphe);
int nombreMaxSommetsGraphe(TypGraphe *graphe);


#endif //LIBGRAPHE_H

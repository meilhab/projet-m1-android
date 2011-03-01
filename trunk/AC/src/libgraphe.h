#ifndef LIBGRAPHE_H
#define LIBGRAPHE_H

#include "libliste.h"

typedef struct TypGraphe{
	int nbMaxSommets;
	TypVoisins **listesAdjencences;
} TypGraphe;

casErreur initialisationGraphe(TypGraphe **graphe, int nbSommets);
casErreur insertionSommet(TypGraphe **graphe, int sommet);
casErreur insertionArete(TypGraphe **graphe, int sommetDebut, 
	int sommetSuivant, int poids);
casErreur suppressionSommet(TypGraphe **graphe, int sommet);
casErreur suppressionArete(TypGraphe **graphe, int sommetDebut, int sommetSuivant);
void affichageGraphe(TypGraphe *graphe);
casErreur verifAllocationPG(TypGraphe **graphe);	
casErreur verifAllocationG(TypGraphe *graphe);	
casErreur supprimerGraphe(TypGraphe **graphe);
int nombreMaxSommetsGraphe(TypGraphe *graphe);


#endif //LIBGRAPHE_H

#ifndef LIBGRAPHE_H
#define LIBGRAPHE_H

#include "libliste.h"

typedef struct TypGraphe{
	int nbMaxSommets;
	TypVoisins *listeAdjencences[nbMaxSommets];
};

void ajoutSommet();
void ajoutArete();
void supprimeSommet();
void supprimeArete();



#endif //LIBGRAPHE_H

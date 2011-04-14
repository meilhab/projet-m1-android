#ifndef PRIM_H
#define PRIM_H

#include "libgraphe.h"

int appartientTas(int *tas[], int sommetSuivant, int tailleTas);
int extraireMinTab(int *tas[], int cle[], int *tailleTas);
casErreur Prim(TypGraphe *graphe, int sommetDepart);

#endif // PRIM_H 

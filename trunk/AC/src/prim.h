#ifndef PRIM_H
#define PRIM_H

#include "libgraphe.h"

int appartientTab(int *tas[], int sommet, int tailleTas);
int extraireMinTab(int *tas[], int cle[], int *tailleTas);
casErreur Prim(TypGraphe *graphe, int sommetDepart);
int areteExiste(TypGraphe *graphe, int sommet, int voisin, int poids);
int estNonOriente(TypGraphe *graphe);

#endif // PRIM_H 

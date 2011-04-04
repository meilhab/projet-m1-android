#ifndef PRIM_H
#define PRIM_H

#include "libgraphe.h"

int pere(int i);
int filsGauche(int i);
int filsDroit(int i);
void entasserTas(int *tas[], int i, int poidsArete[], int tailleTas);
void construireTas(int *tas[], int poidsArete[], int tailleTas);
void triTas(int *tas[], int poidsArete[], int tailleTas);
int extraireMaxTas(int *tas[], int poidsArete[], int *tailleTas);
void insererTas(int *tas[], int x, int *tailleTas);
void Prim(TypGraphe *graphe);

#endif // PRIM_H 

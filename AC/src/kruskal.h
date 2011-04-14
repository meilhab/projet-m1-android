#ifndef KRUSKAL_H
#define KRUSKAL_H

#include "libgraphe.h"
#include "menu.h"


void creerEnsemble(int *pere[], int sommet);
int trouverEnsemble(int *pere[], int sommet);
void unionEnsemble(int *pere[], int sommet1, int sommet2);
void tri_a_bulle(TypVoisins *arete[], int nbArete, int *indiceSommetArete[]);
void echanger(TypVoisins *arete[], int *indiceSommetArete[], int a, int b);
int partitionner(TypVoisins *arete[], int *indiceSommetArete[], int p, int r);
void quickSort(TypVoisins *arete[], int *indiceSommetArete[], int debut, int nbArete);
casErreur Kruskal(TypGraphe *graphe);

#endif // KRUSKAL_H

#ifndef KRUSKAL_H
#define KRUSKAL_H

#include "libgraphe.h"


void creerEnsemble(int *pere[], int sommet);
int trouverEnsemble(int *pere, int sommet);
void unionEnsemble(int **pere, int sommet1, int sommet2);
void tri_a_bulle(TypVoisins *arete[], int nbArete);
void Kruskal(TypGraphe *graphe);

#endif // KRUSKAL_H
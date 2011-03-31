#include "libgraphe.h"

void creerEnsemble(int *pere[], int sommet){
	pere[sommet] = sommet;

	return;
}

int trouverEnsemble(int *pere, int sommet){
	int tmp, tmp2;	
	tmp = sommet;

	while(sommet != pere[sommet])
		sommet = pere[sommet];

	while(tmp != pere[tmp]){
		tmp2 = pere[tmp];
		pere[tmp] = sommet;
		tmp = tmp2;
	}

	return sommet;
}

void unionEnsemble(int **pere, int sommet1, int sommet2){
	if(trouverEnsemble(pere, sommet1) != trouverEnsemble(pere, sommet2))
		pere[trouverEnsemble(pere, sommet1)] = trouverEnsemble(pere, sommet2);

	return;
}
/*
typedef struct{
	int depart;
	int arrivee;
	int poids;
	struct Pile *suivant;
} Pile;

void initialiserPile(Pile** p){
	if(p == NULL || *p == NULL){
		(*p) = (*Pile) malloc(sizeof(Pile));
		if(p == NULL || *p == NULL){
			fprintf(stderr, "Erreur pile\n");
			exit(1);
		}

		(*p)->depart = -1;
		(*p)->arrivee = -1;
		(*p)->poids = -1;
		(*p)->suivant = NULL;
	}

	return;
}

void ajoutPile(Pile** p, int depart, int arrivee, int poids){
	if(p == NULL || *p == NULL){
		fprintf(stderr, "Erreur pile\n");
		exit(1);
	}

	if(depart < 0 || arrivee < 0 || poids < 0){
		fprintf(stderr, "Erreur paramètre ajout\n");
		exit(2);
	}

	Pile *pile = (*p);
	while(pile->depart != -1)
		pile = pile->suivant;

	Pile *element = (Pile*) malloc(sizeof(Pile));
	if(element == NULL){
		fprintf(stderr, "Erreur element\n");
		exit(3);
	}
	element->depart = depart;
	element->arrivee = arrivee;
	element->poids = poids;
	element->suivant = (*p);

	(*p) = element;

	return;
}

void prendrePile(Pile** p, int* depart, int* arrivee){
	if(p == NULL || *p == NULL){
		fprintf(stderr, "Erreur pile\n");
		exit(1);
	}
	


	return;
}
*/

void tri_a_bulle(TypVoisins *arete[], int nbArete, int *indiceSommetArete[]){
	fprintf(stdout, "icibulle\n");
	int fini = 0;
	int i;
	TypVoisins *tmp;
	int indiceTmp;

	while(fini == 0){
		fini = 1;
		for(i=0; i<nbArete - 1; i++){
			if(arete[i]->poidsVoisin > arete[i+1]->poidsVoisin){
				indiceTmp = indiceSommetArete[i];
				indiceSommetArete[i] = indiceSommetArete[i+1];
				indiceSommetArete[i+1] = indiceTmp;

				tmp = arete[i];
				arete[i] = arete[i+1];
				arete[i+1] = tmp;
				fini = 0;
			}
		}
	}

	return;
}

void Kruskal(TypGraphe *graphe){
	fprintf(stdout, "icikruskal\n");
	int nbMaxSommets = graphe->nbMaxSommets;
//	int tabEnsemble[nbMaxSommets];
	int pere[nbMaxSommets];
	int i;
	int nbArete = 0;
	int tailleS;

	//nombre d'arêtes du graphe
	for(i=0; i<nbMaxSommets; i++){
		tailleS = taille(graphe->listesAdjencences[i]);
		if(tailleS > 0)
			nbArete += tailleS;
	}

	int indiceSommetArete[nbArete];
	TypVoisins *arete[nbArete];
	TypVoisins *liste;
	int j = 0;

	//creation ensemble et récupération des arêtes
	for(i=0; i<nbMaxSommets; i++){
		indiceSommetArete[i] = -1;
		creerEnsemble(pere, -1);
		liste = graphe->listesAdjencences[i];
		if(estVide(liste) >= 0){
			creerEnsemble(pere, i);
			while(liste->voisin != -1){
				indiceSommetArete[j] = i;//TODO : erreur ici
				arete[j] = liste;
				liste = liste->voisinSuivant;
				j++;
			}
		}
	}

	tri_a_bulle(arete, nbArete, indiceSommetArete);

	fprintf(stdout, "iciaffichage\n");
	for(i=0; i<nbArete; i++){
		fprintf(stdout, "-> %d vers %d avec %d\n", 
			indiceSommetArete[i], arete[i]->voisin, arete[i]->poidsVoisin);
	}


	

}

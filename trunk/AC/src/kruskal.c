#include "libgraphe.h"

void creerEnsemble(int **pere, int sommet){
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

void Kruskal(TypGraphe *graphe){
	int nbMaxSommets = graphe->nbMaxSommets;
//	int tabEnsemble[nbMaxSommets];
	int pere[nbMaxSommets];
	int i;

/*	for(i=0; i<nbMaxSommets; i++){
		tabEnsemble[i] = -1;
		if(estVide(graphe->listesAdjencences[i]) >= 0)
			tabEnsemble[i] = i;
	}
*/
	for(i=0; i<nbMaxSommets; i++){
		pere[i] = -1;
		if(estVide(graphe->listesAdjencences[i]) >= 0)
			creerEnsemble(pere, i);
	}

	

}

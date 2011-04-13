#include "prim.h" 

int pere(int i) {
    return (i/2);
}
int filsGauche(int i) {
    return (2*i);
}
int filsDroit(int i) {
    return (2*i + 1);
}

//tas doit commencer a 1
void entasserTas(int *tas[], int i, int cle[], int tailleTas) {
	
	printf("debut entasserTas\n");
	int g=filsGauche(i);
	int d=filsDroit(i);
	int min=i;
	
	if(g <= tailleTas) {
		printf("1 : %d\n",tas[g]);
		printf("2 : %d\n",tas[min]);
		printf("3 : %d\n",g);
		printf("4 : %d\n",i);
		if(cle[(int)tas[g]] < cle[(int)tas[min]] && cle[(int)tas[g]] != -1)
			min=g; 
		if(d <= tailleTas && cle[(int)tas[d]] < cle[(int)tas[min]] && cle[(int)tas[d]] != -1)
			min=d;
		
		if( min!=i ) {
			int temp = tas[min];
			tas[min] = tas[i];
			tas[i] = temp;
			entasserTas(tas, min, cle, tailleTas);
		}
	}
	printf("fin entasserTas\n");

}



//tas doit commencer a 1
void construireTas(int *tas[], int cle[], int tailleTas) {
	
	printf("debut construireTas\n");

	int i;
	for(i = tailleTas/2; i>=1; i--)
		entasserTas(tas, i, cle, tailleTas);
	
	printf("fin construireTas\n");
	
}

void triTas(int *tas[], int cle[], int tailleTas) {
	
	printf("debut triTas\n");
	construireTas(tas, cle, tailleTas);
	
	int temp;
	int i;
	for(i = tailleTas; i>=1; i--) {
		temp = tas[1];
		tas[1] = tas[i];
		tas[i] = temp;
		entasserTas(tas, i, cle, tailleTas);
	}
	
	printf("fin triTas\n");
	
}

int extraireMinTas(int *tas[], int cle[], int *tailleTas) {
	
  	printf("debut extraireMinTas\n");
	int res = tas[1];
	tas[1] = tas[*tailleTas];
	entasserTas(tas, 1, cle, *tailleTas);
	(*tailleTas)--;
	printf("fin extraireMinTas\n");
	return res;
	
}

void insererTas(int *tas[], int x, int *tailleTas) {
	
	printf("debut insererTas\n");
	(*tailleTas)++;
	int i = *tailleTas;
	while(i > 1 && tas[pere(i)] < x) {
		tas[i] = tas[pere(i)];
		i = pere(i);	
	}
	tas[i] = x;
	printf("fin insererTas\n");
	
}

//a remplacer par un tableau
int appartientTas(int *tas[], int sommetSuivant, int tailleTas) {  

	int appartient = -1;
	int i;
	for(i = 0; i < tailleTas; i++)
		if(tas[i] == sommetSuivant)
			appartient = 1;
	
	return appartient;

}

int extraireMinTab(int *tas[], int cle[], int *tailleTas) {
	
	int i;
	int indiceMin = 0;
	for(i = 0;i<(*tailleTas); i++)
		if(cle[(int)tas[i]] < cle[(int)tas[indiceMin]] && cle[(int)tas[i]] != -1)
			indiceMin = i;
	
	int sommetMin = (int)tas[indiceMin];
	
	for(i = 0;i<(*tailleTas); i++)
		if(i>indiceMin)
			tas[i-1] = tas[i];
	(*tailleTas)--;
	return sommetMin;
	
}
/*
void afficherTab(int tas[], int tailleTas) {
	
	printf("tas : ");
	int i;
	for(i = 0;i<tailleTas; i++)
		printf("%d, ", tas[i]);
	printf("\n");
	
}
*/
void Prim(TypGraphe *graphe, int sommetDepart) {
	
	
	fprintf(stdout, "##################### ACM : ##################\n");
	
	int nbMaxSommets = graphe->nbMaxSommets;
	int nbArete = 0;
	int tailleS;
	int i;
	//nombre d'aretes du graphe
	for(i=0; i<nbMaxSommets; i++){
		tailleS = tailleTypVoisins(graphe->listesAdjencences[i]);
		if(tailleS > 0)
			nbArete += tailleS;
	}
	

	
	int sommet[nbArete];
	int voisin[nbArete];
	int poidsArete[nbArete];
	int cle[nbMaxSommets];
	int prec[nbMaxSommets];
	int tas[nbMaxSommets/*+1*/]; //commence a 1
	int tailleTas = nbMaxSommets/*+1*/; //commence a 1
	TypVoisins *liste;
	int j = 0;
	
	
	
	//creation ensemble et recuperation des aretes
	tailleTas = 0;
	for(i=0; i<nbMaxSommets; i++){
		cle[i] = -1;
		prec[i] = -1;
		//sommet[j] = -1;
		liste = graphe->listesAdjencences[i];
		if(estVide(liste) >= 0){
			tas[tailleTas] = i;
			tailleTas++;
			while(liste->voisin != -1){
				sommet[j] = i;
				voisin[j] = liste->voisin;
				poidsArete[j] = liste->poidsVoisin;
				liste = liste->voisinSuivant;
				j++;
			}
		}
	}
	
	tailleTas;
	
	
	
	
	cle[sommetDepart] = 0;
	prec[sommetDepart] = -1;
	
	/*for(i = 0; i < nbMaxSommets; i++)
		tas[i] = i;
		//insererTas(tas, i, &tailleTas);
	*/
	
	while(tailleTas != 0) {
		//triTas(tas, cle, tailleTas);
		
		//afficherTab(tas, tailleTas);
		int sommet = extraireMinTab(tas, cle, &tailleTas);
		
		TypVoisins *v = graphe->listesAdjencences[sommet];
		int sommetSuivant = v->voisin;
		
		while(sommetSuivant != -1) {
			if(appartientTas(tas, sommetSuivant, tailleTas) > 0 && (v->poidsVoisin < cle[sommetSuivant] || cle[sommetSuivant] == -1)) {
				prec[sommetSuivant] = sommet;
				cle[sommetSuivant] = v->poidsVoisin;
			}
			v = v->voisinSuivant;
			sommetSuivant = v->voisin;
		}
		if(sommet != sommetDepart)
			printf("-> %d vers %d\n", prec[sommet], sommet);
	}
	
	
	
}
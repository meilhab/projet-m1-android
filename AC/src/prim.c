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
void entasserTas(int *tas[], int i, int poidsArete[], int tailleTas) {
	
	int g=filsGauche(i);
	int d=filsDroit(i);
	int max=i;
	
	if(g <= tailleTas) {
		if(poidsArete[(int)tas[g]] > poidsArete[(int)tas[max]])
			max=g; 
		
		if(d <= tailleTas && poidsArete[(int)tas[d]] > poidsArete[(int)tas[max]])
			max=d;
		
		if( max!=i ) {
			int temp = tas[max];
			tas[max] = tas[i];
			tas[i] = temp;
			entasserTas(tas, max, poidsArete, tailleTas);
		}
	}
}



//tas doit commencer a 1
void construireTas(int *tas[], int poidsArete[], int tailleTas) {
	
	int i;
	for(i = tailleTas/2; i>=1; i--)
		entasserTas(tas, i, poidsArete, tailleTas);
	
}

void triTas(int *tas[], int poidsArete[], int tailleTas) {
	construireTas(tas, poidsArete, tailleTas);
	
	int temp;
	int i;
	for(i = tailleTas; i>=1; i--) {
		temp = tas[1];
		tas[1] = tas[i];
		tas[i] = temp;
		entasserTas(tas, i, poidsArete, tailleTas);
	}
}

int extraireMaxTas(int *tas[], int poidsArete[], int *tailleTas) {
	
	int res = tas[1];
	tas[1] = tas[*tailleTas];
	entasserTas(tas, 1, poidsArete, *tailleTas);
	(*tailleTas)--;
	return res;
	
}

void insererTas(int *tas[], int x, int *tailleTas) {
	
	(*tailleTas)++;
	int i = *tailleTas;
	while(i > 1 && tas[pere(i)] < x) {
		tas[i] = tas[pere(i)];
		i = pere(i);	
	}
	tas[i] = x;
	
}

void Prim(TypGraphe *graphe, int sommetDepart) {

	int nbMaxSommets = graphe->nbMaxSommets;
	int nbArete = 0;
	int tailleS;
	int i;
	//nombre d'aretes du graphe
	for(i=0; i<nbMaxSommets; i++){
		tailleS = taille(graphe->listesAdjencences[i]);
		if(tailleS > 0)
			nbArete += tailleS;
	}
	
	int sommet[nbArete];
	int voisin[nbArete];
	int poidsArete[nbArete];
	int cle[nbMaxSommets];
    int prec[nbMaxSommets];
	TypVoisins *liste;
	int j = 0;
	
	//creation ensemble et recuperation des aretes
	for(i=0; i<nbMaxSommets; i++){
		cle[i] = -1;
		sommet[j] = -1;
		liste = graphe->listesAdjencences[i];
		if(estVide(liste) >= 0){
			while(liste->voisin != -1){
				sommet[j] = i;
				voisin[j] = liste->voisin;
				poidsArete[j] = liste->poidsVoisin;
				liste = liste->voisinSuivant;
				j++;
			}
		}
	}

    cle[sommetDepart] = 0;
	prec[sommetDepart] = -1;

    while(nombreElement(F) != 0){
        int sommet = min(F);
        TypVoisins *v = graphe->listesAdjencences[sommet];
        int sommetSuivant;
        for(sommetSuivant = v->voisin; v->voisin != -1; v = v->voisinSuivant){
            if(appartient(F, sommetSuivant) == 0 && 
                    v->poidsVoisin < cle[sommetSuivant]){
                prec[sommetSuivant] = sommet;
                cle[sommetSuivant ] = v->poidsVoisin;
            }
        }
    }

	
}

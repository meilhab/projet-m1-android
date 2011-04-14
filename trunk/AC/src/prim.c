#include "prim.h" 

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

casErreur Prim(TypGraphe *graphe, int sommetDepart) {
	
	
	if(estNonOriente(graphe) < 1) {
		  fprintf(stdout, "Votre graphe est oriente et ne peut donc pas");
		  fprintf(stdout, " etre utilise par l'algorithme de prim\n");
		  return;
	}
	
	fprintf(stdout, "##################### ACM : ##################\n");
	
	int nbMaxSommets = graphe->nbMaxSommets;
	int i = 0;
	int j = 0;
	int cle[nbMaxSommets];
	int prec[nbMaxSommets];
	int tas[nbMaxSommets];
	int tailleTas = nbMaxSommets;
	TypVoisins *liste;
	
	//initialisation
	tailleTas = 0;
	for(i=0; i<nbMaxSommets; i++){
		cle[i] = -1;
		prec[i] = -1;
		liste = graphe->listesAdjencences[i];
		if(estVide(liste) >= 0){
			tas[tailleTas] = i;
			tailleTas++;
		}
	}

	cle[sommetDepart] = 0;
	prec[sommetDepart] = -1;

	while(tailleTas != 0) {
		int sommet = extraireMinTab(tas, cle, &tailleTas);
		
		TypVoisins *v = graphe->listesAdjencences[sommet];
		int sommetSuivant = v->voisin;
		
		while(sommetSuivant != -1) {
			if(appartientTas(tas, sommetSuivant, tailleTas) > 0 && 
			  (v->poidsVoisin < cle[sommetSuivant] || cle[sommetSuivant] == -1)) {
				prec[sommetSuivant] = sommet;
				cle[sommetSuivant] = v->poidsVoisin;
			}
			v = v->voisinSuivant;
			sommetSuivant = v->voisin;
		}
		if(sommet != sommetDepart)
			fprintf(stdout, "-> %d vers %d avec poids : %d\n", prec[sommet] + 1, sommet + 1, cle[sommet]);
	}
	
	
	
	TypGraphe *graphePrim;
	casErreur erreur;
	if((erreur = initialisationGraphe(&graphePrim, nbMaxSommets)) 
			!= PAS_ERREUR)
		return erreur;
	
	for(i=0; i<nbMaxSommets; i++){
		if(prec[i] != -1) {
			insertionSommet(&graphePrim, prec[i] + 1);
			insertionSommet(&graphePrim, i + 1);
			if((erreur = insertionArete(&graphePrim, prec[i] + 1, i + 1, 
				cle[i])) != PAS_ERREUR)
				return erreur;
		}
		//printf("%d <-> %d avec %d\n",prec[i]+1,i+1,cle[i]);
	}
	
	affichageGraphe(graphePrim);

	fprintf(stdout, "##############################################\n");
	char c;
	do{
		fprintf(stdout, "Enregistrer l'ACM dans un fichier (o/n) ?\n");
		scanf("%c", &c);
		clearScanf();
	}while(c != 'o' && c != 'n');

	if(c == 'o'){
		char chemin[250];
		fprintf(stdout, "Nom du fichier ? ");
		scanf("%s", chemin);
		if((erreur = ecritureFichier(chemin, graphePrim)) != PAS_ERREUR)
			return erreur;
		fprintf(stdout, "Enregistrement correctement effectue\n");
	}
	
	return supprimerGraphe(&graphePrim);
	
}


int areteExiste(TypGraphe *graphe, int sommet, int voisin, int poids) {
	
	int existe = -2;
	
	if(sommet < graphe->nbMaxSommets && voisin < graphe->nbMaxSommets) {
		TypVoisins *liste = graphe->listesAdjencences[sommet];
		if(estVide(liste) >= 0){
			while(liste->voisin != -1){
				if(liste->voisin == voisin) {
					if(liste->poidsVoisin == poids)
						existe = 1;
					else
						existe = -1;
				}
				liste = liste->voisinSuivant;
			}
		}
	}
	
	return existe;
	
}


int estNonOriente(TypGraphe *graphe) {
	
	int estNonOriente = 1;
	int i;
	int nbMaxSommets = graphe->nbMaxSommets;
	TypVoisins *liste;
	for(i=0; i<nbMaxSommets; i++){
		liste = graphe->listesAdjencences[i];
		if(estVide(liste) >= 0){
			while(liste->voisin != -1){
				int existe = areteExiste(graphe, liste->voisin, i, liste->poidsVoisin);
				if(existe == -2)
					estNonOriente = -2;
				else if(existe == -1 && (estNonOriente == -1 || estNonOriente == 1))
					estNonOriente = -1;
				liste = liste->voisinSuivant;
			}
		}
	}
	
	return estNonOriente;
	
}
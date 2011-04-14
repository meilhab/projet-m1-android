#include "kruskal.h"

void creerEnsemble(int *pere[], int sommet){
	pere[sommet] = sommet;

	return;
}

int trouverEnsemble(int *pere[], int sommet){
	int tmp, tmp2;	
	tmp = sommet;

	while(sommet != (int)pere[sommet])
		sommet = (int)pere[sommet];

	while(tmp != (int)pere[tmp]){
		tmp2 = (int)pere[tmp];
		pere[tmp] = sommet;
		tmp = tmp2;
	}

	return sommet;
}

void unionEnsemble(int *pere[], int sommet1, int sommet2){
	if(trouverEnsemble(pere, sommet1) != trouverEnsemble(pere, sommet2))
		pere[trouverEnsemble(pere, sommet1)] = trouverEnsemble(pere, sommet2);

	return;
}

void tri_a_bulle(TypVoisins *arete[], int nbArete, int *indiceSommetArete[]){
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

void echanger(TypVoisins *arete[], int *indiceSommetArete[], int a, int b){
	int indiceTmp;
	TypVoisins *tmp;

	indiceTmp = indiceSommetArete[a];
	indiceSommetArete[a] = indiceSommetArete[b];
	indiceSommetArete[b] = indiceTmp;

	tmp = arete[a];
	arete[a] = arete[b];
	arete[b] = tmp;

	return;
}

int partitionner(TypVoisins *arete[], int *indiceSommetArete[], int p, int r) {
	TypVoisins *pivot = arete[p];
	int i = p-1;
	int j = r+1;
	while (1) {
		do
			j--;
		while (arete[j]->poidsVoisin > pivot->poidsVoisin);
		do
			i++;
		while (arete[i]->poidsVoisin < pivot->poidsVoisin);
		if (i < j) {
			echanger(arete, indiceSommetArete, i, j);
		}
		else
			return j;
	}
}

void quickSort(TypVoisins *arete[], int *indiceSommetArete[], int p, int r) {
	int q;
	if (p < r) {
		q = partitionner(arete, indiceSommetArete, p, r);
		quickSort(arete, indiceSommetArete, p, q);
		quickSort(arete, indiceSommetArete, q+1, r);
	}
}

casErreur Kruskal(TypGraphe *graphe){
	int nbMaxSommets = graphe->nbMaxSommets;
	int pere[nbMaxSommets];
	int i, j = 0, indiceCourant = 0; // compteurs
	int nbArete = 0;
	int tailleS;

	//nombre d'aretes du graphe
	for(i=0; i<nbMaxSommets; i++){
		tailleS = tailleTypVoisins(graphe->listesAdjencences[i]);
		if(tailleS > 0)
			nbArete += tailleS;
	}

	int indiceSommetArete[nbArete];
	TypVoisins *arete[nbArete];
	TypVoisins *liste;

	//creation ensemble et recuperation des aretes
	for(i=0; i<nbMaxSommets; i++){
		indiceSommetArete[j] = -1;
		creerEnsemble(&pere, -1);
		liste = graphe->listesAdjencences[i];
		if(estVide(liste) >= 0){
			creerEnsemble(&pere, i);
			while(liste->voisin != -1){
				indiceSommetArete[j] = i;
				arete[j] = liste;
				liste = liste->voisinSuivant;
				j++;
			}
		}
	}
	printf("-----------------------------\n");
	for(i=0; i<28; i++){
		printf("de %d vers %d et poids %d\n", 
				indiceSommetArete[i]+1,
				arete[i]->voisin+1,
				arete[i]->poidsVoisin);
	}
	printf("-----------------------------\n");


	//	tri_a_bulle(arete, nbArete, indiceSommetArete);
	printf("%d\n", nbArete);
	quickSort(arete, indiceSommetArete, 0, nbArete-1);
	printf("-----------------------------\n");
	for(i=0; i<28; i++){
		printf("de %d vers %d et poids %d\n", 
				indiceSommetArete[i]+1,
				arete[i]->voisin+1,
				arete[i]->poidsVoisin);
	}
	printf("-----------------------------\n");


	int ensembleSolution[nbArete];
	for(i=0; i<nbArete; i++)
		ensembleSolution[i] = -1;

	for(i=0; i<nbArete; i++){
		if(trouverEnsemble(&pere, indiceSommetArete[i]) != 
				trouverEnsemble(&pere, arete[i]->voisin)) {
			ensembleSolution[indiceCourant] = i;
			indiceCourant++;
			unionEnsemble(&pere, indiceSommetArete[i], arete[i]->voisin);
		}
	}

	//affichage du chemin
	fprintf(stdout, "##################### ACM : ##################\n");
	i = 0;
	TypGraphe *grapheKruskal;
	casErreur erreur;
	if((erreur = initialisationGraphe(&grapheKruskal, nbMaxSommets)) 
			!= PAS_ERREUR)
		return erreur;

	while(ensembleSolution[i] != -1) {
		/*		fprintf(stdout, "-> %d vers %d avec %d\n", 
				indiceSommetArete[ensembleSolution[i]]+1, 
				arete[ensembleSolution[i]]->voisin+1, 
				arete[ensembleSolution[i]]->poidsVoisin);*/
		insertionSommet(&grapheKruskal, indiceSommetArete[ensembleSolution[i]] + 1);
		insertionSommet(&grapheKruskal, arete[ensembleSolution[i]]->voisin + 1);
		if((erreur = insertionArete(&grapheKruskal, 
						indiceSommetArete[ensembleSolution[i]] + 1, 
						arete[ensembleSolution[i]]->voisin + 1,
						arete[ensembleSolution[i]]->poidsVoisin)) != PAS_ERREUR)
			return erreur;

		i++;
	}

	affichageGraphe(grapheKruskal);

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
		if((erreur = ecritureFichier(chemin, grapheKruskal)) != PAS_ERREUR)
			return erreur;

		fprintf(stdout, "Enregistrement correctement effectue\n");
	}

	return supprimerGraphe(&grapheKruskal);
}

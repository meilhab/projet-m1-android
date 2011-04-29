/*
 ******************************************************************************
 *
 * Programme : kruskal.c
 *
 * ecrit par : Guillaume MONTAVON & Benoit MEILHAC
 *
 * resume :    calcul et affiche un arbre couvrant de poids minimal
 *             en utilisant l'algorithme de Kruskal
 *
 * date :      02/05/2011
 *
 ******************************************************************************
 */

#include "kruskal.h"

/*
 * Fonction :    creerEnsemble
 *
 * Parametres :  int *pere[], tableau des representants
 *               int sommet, unique sommet de l'ensemble
 *
 * Retour :      rien
 *
 * Description : creer un ensemble constitue d'un unique sommet qui est son
 *               propre representant
 *
 */
void creerEnsemble(int *pere[], int sommet){
	
	pere[sommet] = sommet;
	return;
	
}

/*
 * Fonction :    trouverEnsemble
 *
 * Parametres :  int *pere[], tableau des representants
 *               int sommet, sommet recherche
 *
 * Retour :      int, representant de l'ensemble auquel appartient sommet
 *
 * Description : retourne le representant de l'ensemble auquel appartient
 *               sommet
 *
 */
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

/*
 * Fonction :    unionEnsemble
 *
 * Parametres :  int *pere[], tableau des representants
 *               int sommet1, premier sommet
 *               int sommet2, deuxieme sommet
 *
 * Retour :      rien
 *
 * Description : union de 2 ensembles disjoints auquels appartient sommet1 et
 *               sommet2
 *
 */
void unionEnsemble(int *pere[], int sommet1, int sommet2){
	
	if(trouverEnsemble(pere, sommet1) != trouverEnsemble(pere, sommet2))
		pere[trouverEnsemble(pere, sommet1)] = trouverEnsemble(pere, sommet2);

	return;
	
}

/*
 * Fonction :    tri_a_bulle
 *
 * Parametres :  TypVoisins *arete[], tableau qui contient les voisins et le
 *                                    poids d'une arete
 *               int nbArete, nombre d'aretes
 *               int *indiceSommetArete[], tableau qui contient le premier
 *                                         sommet d'une arete
 *
 * Retour :      rien
 *
 * Description : effectue un tri a bulle sur les aretes en fonction de leur
 *               poids
 *
 */
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

/*
 * Fonction :    echanger
 *
 * Parametres :  TypVoisins *arete[], tableau qui contient les voisins et le
 *                                    poids d'une arete
 *               int *indiceSommetArete[], tableau qui contient le premier
 *                                         sommet d'une arete
 *               int a, indice a echanger
 *               int b, indice a echanger
 *
 * Retour :      rien
 *
 * Description : echange les aretes a et b, on echange donc dans les 2 tablaux
 *
 */
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

/*
 * Fonction :    partitionner
 *
 * Parametres :  TypVoisins *arete[], tableau qui contient les voisins et le
 *                                    poids d'une arete
 *               int *indiceSommetArete[], tableau qui contient le premier
 *                                         sommet d'une arete
 *               int p, premier
 *               int r, dernier
 *
 * Retour :      rien
 *
 * Description : partitionne les tableaux en 3 parties : pivot, partie gauche,
 *               partie droite
 *
 */
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
		if (i < j)
			echanger(arete, indiceSommetArete, i, j);
		else
			return j;
	}
	
}

/*
 * Fonction :    quickSort
 *
 * Parametres :  TypVoisins *arete[], tableau qui contient les voisins et le
 *                                    poids d'une arete
 *               int *indiceSommetArete[], tableau qui contient le premier
 *                                         sommet d'une arete
 *               int p, debut
 *               int r, fin
 *
 * Retour :      rien
 *
 * Description : effectue un tri rapide sur les aretes en fonction de leur
 *               poids
 *
 */
void quickSort(TypVoisins *arete[], int *indiceSommetArete[], int p, int r) {
	
	int q;
	if (p < r) {
		q = partitionner(arete, indiceSommetArete, p, r);
		quickSort(arete, indiceSommetArete, p, q);
		quickSort(arete, indiceSommetArete, q+1, r);
	}
	
}

/*
 * Fonction :    Kruskal
 *
 * Parametres :  TypGraphe *graphe, graphe courant pour lequel on doit
 *                                  chercher l'ACM
 *
 * Retour :      casErreur, code d'erreur de la fonction
 *
 * Description : lance le calcul et l'affichage d’un arbre couvrant de poids
 *               minimal en utilisant l'algorithme de Kruskal
 *
 */
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

	//une arete est compose d'un sommet (tableau indiceSommetArete) et d'un
	//voisin ainsi qu'un poids (tableau arete)
	
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
	
	/*
	fprintf(stdout, "-----------------------------\n");
	for(i=0; i < 28; i++)
		fprintf(stdout, "de %d vers %d et poids %d\n", 
			indiceSommetArete[i]+1, arete[i]->voisin+1, arete[i]->poidsVoisin);
	fprintf(stdout, "-----------------------------\n");
	
	fprintf(stdout, "nombre d'aretes : %d\n", nbArete);
	*/

	//tri_a_bulle(arete, nbArete, indiceSommetArete);
	quickSort(arete, indiceSommetArete, 0, nbArete-1);
	
	/*
	fprintf(stdout, "-----------------------------\n");
	for(i=0; i<28; i++)
		fprintf(stdout, "de %d vers %d et poids %d\n", 
			indiceSommetArete[i]+1, arete[i]->voisin+1, arete[i]->poidsVoisin);
	fprintf(stdout, "-----------------------------\n");
	*/

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
		fprintf(stdout, "-> %d vers %d avec %d\n", 
			indiceSommetArete[ensembleSolution[i]]+1, 
			arete[ensembleSolution[i]]->voisin+1, 
			arete[ensembleSolution[i]]->poidsVoisin);
		insertionSommet(&grapheKruskal, indiceSommetArete[ensembleSolution[i]] + 1);
		insertionSommet(&grapheKruskal, arete[ensembleSolution[i]]->voisin + 1);
		if((erreur = insertionArete(&grapheKruskal, 
						indiceSommetArete[ensembleSolution[i]] + 1, 
						arete[ensembleSolution[i]]->voisin + 1,
						arete[ensembleSolution[i]]->poidsVoisin)) != PAS_ERREUR)
			return erreur;
		i++;
	}

	//on affiche l'ACM
	affichageGraphe(grapheKruskal);

	//on demande a l'utilisateur s'il souhaite l'enregistrer
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

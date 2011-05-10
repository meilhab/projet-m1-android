/*
 ******************************************************************************
 *
 * Programme : prim.c
 *
 * ecrit par : Guillaume MONTAVON & Benoit MEILHAC
 *
 * resume :    calcul et affiche un arbre couvrant de poids minimal
 *             en utilisant l'algorithme de Prim
 *
 * date :      02/05/2011
 *
 ******************************************************************************
 */

#include "prim.h" 

/*
 * Fonction :    appartientTab
 *
 * Parametres :  int *tas[], tableau qui contients les sommets
 *               int sommet, sommet recherche
 *               int tailleTas, taille du tableau
 *
 * Retour :      int, 1 si le sommet appartient au tableau, -1 sinon
 *
 * Description : verifie qu'une valeur donnee est presente dans un tableau
 *
 */
int appartientTab(int *tas[], int sommet, int tailleTas) {  

	int appartient = -1;
	int i;
	for(i = 0; i < tailleTas; i++)
		if(tas[i] == sommet)
			appartient = 1;
	
	return appartient;

}

/*
 * Fonction :    extraireMinTab
 *
 * Parametres :  int *tas[], tableau qui contients les sommets
 *               int cle[], tableau qui contient les cles de chaque sommet
 *               int *tailleTas, taille du tableau
 *
 * Retour :      int, valeur minimale que contient le tableau
 *
 * Description : extrait la valeur minimale contenue dans un tableau :
 *               on cherche cette valeur, on la supprime, puis on decremente
 *               la taille du tableau
 *
 */
int extraireMinTab(int *tas[], int cle[], int *tailleTas) {
	
	int i;
	int indiceMin = 0;
	for(i = 0; i < (*tailleTas); i++)
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
 * Fonction :    Prim
 *
 * Parametres :  TypGraphe *graphe, graphe courant pour lequel on doit
 *                                  chercher l'ACM
 *               int sommetDepart, sommet choisi pour commencer l'algorithme
 *                                 de Prim
 *
 * Retour :      casErreur, code d'erreur de la fonction
 *
 * Description : lance le calcul et l'affichage d’un arbre couvrant de poids
 *               minimal en utilisant l'algorithme de Prim
 *
 */
casErreur Prim(TypGraphe *graphe, int sommetDepart) {
	
	if(estNonOriente(graphe) < 1) 
		  return NON_ORIENTE;
	
	fprintf(stdout, "##################### ACM : ##################\n");
	
	int nbMaxSommets = graphe->nbMaxSommets;
	int i = 0;
//	int j = 0;
	int cle[nbMaxSommets];
	int prec[nbMaxSommets];
	int tas[nbMaxSommets];
	int tailleTas = nbMaxSommets;
	TypVoisins *liste;
	
	//initialisation
	tailleTas = 0;
	for(i=0; i < nbMaxSommets; i++){
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
			if(appartientTab(tas, sommetSuivant, tailleTas) > 0 && 
			  (v->poidsVoisin < cle[sommetSuivant] || cle[sommetSuivant] == -1)) {
				prec[sommetSuivant] = sommet;
				cle[sommetSuivant] = v->poidsVoisin;
			}
			v = v->voisinSuivant;
			sommetSuivant = v->voisin;
		}
		if(sommet != sommetDepart)
			fprintf(stdout, "-> %d vers %d avec poids : %d\n",
					prec[sommet] + 1, sommet + 1, cle[sommet]);
	}
	
	
	//on ajoute les aretes dans un graphe (TypGraphe)
	TypGraphe *graphePrim;
	casErreur erreur;
	if((erreur = initialisationGraphe(&graphePrim, nbMaxSommets)) 
			!= PAS_ERREUR)
		return erreur;
	
	for(i=0; i < nbMaxSommets; i++){
		if(prec[i] != -1) {
			insertionSommet(&graphePrim, prec[i] + 1);
			insertionSommet(&graphePrim, i + 1);
			if((erreur = insertionArete(&graphePrim, prec[i] + 1, i + 1, 
				cle[i])) != PAS_ERREUR)
				return erreur;
		}
	}
	
	//on affiche l'ACM
	affichageGraphe(graphePrim);

	
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
		if((erreur = ecritureFichier(chemin, graphePrim)) != PAS_ERREUR)
			return erreur;
		fprintf(stdout, "Enregistrement correctement effectue\n");
	}
	
	return supprimerGraphe(&graphePrim);
	
}

/*
 * Fonction :    areteExiste
 *
 * Parametres :  TypGraphe *graphe, graphe courant
 *               int sommet, numero du sommet
 *               int voisin, numero du voisin
 *               int poids, poids de l'arete
 *
 * Retour :      int, 1 si il l'arete existe, -1 si elle existe mais avec un
 *                    mauvais poids, -2 si elle n'existe pas
 *
 * Description : verifie si une arete existe dans le graphe courant
 *
 */
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

/*
 * Fonction :    estNonOriente
 *
 * Parametres :  TypGraphe *graphe, graphe courant 
 *
 * Retour :      int, 1 si il est non oriente, -1 si il est non orienté mais
 *                    certaines aretes entre 2 meme sommets ont un poids
 *                    différents, -2 si il est oriente
 *
 * Description : verifie si le graphe est non oriente
 *
 */
int estNonOriente(TypGraphe *graphe) {
	
	int estNonOriente = 1;
	int i;
	int nbMaxSommets = graphe->nbMaxSommets;
	TypVoisins *liste;
	for(i=0; i < nbMaxSommets; i++){
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


#include "libgraphe.h"
//#include "menu.h"

casErreur chargementFichier(char*, TypGraphe**);
casErreur ecritureFichier(char*, TypGraphe*);

int main(){
	TypGraphe *graphe = NULL;
	printf("%d\n", chargementFichier("fichierTest.txt", &graphe));
//	affichageGraphe(graphe);

	ecritureFichier("fichierResultat.txt", graphe);

	supprimerGraphe(&graphe);
	
	/*
	while(1) {
		afficherMenu(graphe);
		actionsMenu(graphe);
	}
	*/
	return 0;

	/*
	   TypGraphe *graphe;
	   initialisationGraphe(&graphe, 5);
	   insertionSommet(&graphe, 1);
	 */
	/*
	   TypVoisins *liste;
	   liste = NULL;
	   initialisation(&liste);

	   ajouterDebut(&liste, 5, 10);
	   ajouterDebut(&liste, 2, 1);
	   ajouterDebut(&liste, 3, 8);

	   affichageListe(liste);
	   printf("taille : %d\n\n", taille(liste));

	   printf("%d\n", supprimerTout(&liste));

	   affichageListe(liste);
	   printf("taille : %d\n\n", taille(liste));
	 */

	return 0;
}




casErreur passerCommentaire(FILE* fichier, int* ligne){
	char c;

	if((c = fgetc(fichier)) != '#'){
		printf("Structure de fichier incorrecte à la ligne %d\n", *ligne);
		return STRUCT_FICHIER_INCORRECTE;
	}

	while((c = fgetc(fichier)) != '\n'){}

	(*ligne)++;

	return PAS_ERREUR;
}


casErreur controleCoherenceFichier(char *nomFichier){
	int nbMaxSommets = 0, a;

	FILE *fichier = NULL;
	fichier = fopen(nomFichier, "r");
	if(fichier == NULL)
		//printf("Probleme dans l'ouverture du fichier %s\n", nomFichier);
		return ECHEC_OUVERTURE_FICHIER;

	casErreur erreur = passerCommentaire(fichier, &a);
	if(erreur != PAS_ERREUR)
		return erreur;

	fscanf(fichier, "%d\n", &nbMaxSommets);
	if(nbMaxSommets <= 0)
		return NB_SOMMET_INF_0;

	erreur = passerCommentaire(fichier, &a);
	if(erreur != PAS_ERREUR)
		return erreur;

	int tabS[nbMaxSommets];
	int i;
	int sv, pv;

	i = 0;
	while(fscanf(fichier, "%d : ", &tabS[i]) != EOF){
		if(tabS[i] > nbMaxSommets)
			return FICHIER_SOMMET_SUP_MAX;

		while(fscanf(fichier, "(%d/%d), ", &sv, &pv)){
			if(sv > nbMaxSommets)
				return FICHIER_SOMMET_SUP_MAX;
		}
		i++;
	}
	
	fseek(fichier, 0, SEEK_SET);

	passerCommentaire(fichier, &a);
	fscanf(fichier, "%d\n", &nbMaxSommets);
	passerCommentaire(fichier, &a);

	int temp, terminer;
	while(fscanf(fichier, "%d : ", &temp) != EOF){
		while(fscanf(fichier, "(%d/%d), ", &sv, &pv)){
			i = terminer = 0;
			while(terminer == 0 && i < nbMaxSommets){
				if(tabS[i] == sv)
					terminer = 1;
				i++;
			}
			if(terminer != 1)
				return FICHIER_REF_SOMMET_INEXISTANT;
		}
	}

	fclose(fichier);

	return PAS_ERREUR;
}


casErreur chargementFichier(char *nomFichier, TypGraphe **graphe){
	FILE* fichier = NULL;
	int ligne = 0;
	int nbMaxSommets = 0;

	casErreur erreur = controleCoherenceFichier(nomFichier);
	if(erreur != PAS_ERREUR)
		return erreur;

	fichier = fopen(nomFichier, "r");
	if(fichier == NULL)
		//printf("Probleme dans l'ouverture du fichier %s\n", nomFichier);
		return ECHEC_OUVERTURE_FICHIER;

	erreur = passerCommentaire(fichier, &ligne);
	if(erreur != PAS_ERREUR)
		return erreur;

	fscanf(fichier, "%d\n", &nbMaxSommets);
	ligne++;
	printf("nombre de sommets : %d\n", nbMaxSommets);
	//
	initialisationGraphe(graphe, nbMaxSommets);
	//

	erreur = passerCommentaire(fichier, &ligne);
	if(erreur != PAS_ERREUR)
		return erreur;

	int tab[nbMaxSommets];
	int i = 0;
	int a, b;

	while(fscanf(fichier, "%d : ", &tab[i]) != EOF){
		printf("sommet entré : %d\n", tab[i]);
		//
		erreur = insertionSommet(graphe, tab[i]);
			if(erreur != PAS_ERREUR)
				return erreur;
		//
		while(fscanf(fichier, "(%d/%d), ", &a, &b)){
			printf("\t\tVers %d avec le poids %d\n", a, b);
			//TODO revoir ici l'insertion d'élément n'existant pas
			erreur = insertionArete(graphe, tab[i], a, b);
			if(erreur != PAS_ERREUR)
				return erreur;
			//
		}
		i++;
		ligne++;
	}

	printf("nombre de lignes du fichier : %d\n\n", ligne);
	fclose(fichier);

	return PAS_ERREUR;
}

casErreur ecritureFichier(char *nomFichier, TypGraphe *graphe){
	casErreur erreur = verifAllocationG(graphe);
	if(erreur != PAS_ERREUR)
		return erreur;

	FILE *fichier = NULL;
	fichier = fopen(nomFichier, "w");	
	if(fichier == NULL)
		//printf("Probleme dans l'ouverture du fichier %s\n", nomFichier);
		return ECHEC_OUVERTURE_FICHIER;	

	int i;
	int taille = nombreMaxSommetsGraphe(graphe);

	fprintf(fichier, "# nombre maximum de sommets\n");
	fprintf(fichier, "%d\n", taille);
	fprintf(fichier, "# sommets : voisins\n");

	TypVoisins *liste;
	for(i=0; i<taille; i++){
		liste = graphe->listesAdjencences[i];
		if(estVide(liste) > 0){
			fprintf(fichier, "%d : ", i + 1);
			while(liste->voisin != -1){
				if(liste->voisinSuivant->voisin == -1)
					fprintf(fichier, "(%d/%d)", 
						liste->voisin + 1, liste->poidsVoisin);
				else
					fprintf(fichier,"(%d/%d), ", 
						liste->voisin + 1, liste->poidsVoisin);
				liste = liste->voisinSuivant;
			}
			fprintf(fichier, "\n");
		}
	}


	return PAS_ERREUR;
}


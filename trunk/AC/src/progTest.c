#include "libgraphe.h"

int chargementFichier(char*, TypGraphe**);
int ecritureFichier(char*, TypGraphe*);

int main(){
	TypGraphe *graphe = NULL;
	printf("%d\n", chargementFichier("fichierTest.txt", &graphe));
//	affichageGraphe(graphe);

	ecritureFichier("fichierResultat.txt", graphe);

	supprimerGraphe(&graphe);
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




int passerCommentaire(FILE* fichier, int* ligne){
	char c;

	if((c = fgetc(fichier)) != '#'){
		printf("Structure de fichier incorrecte à la ligne %d\n", *ligne);
		return -1;
	}

	while((c = fgetc(fichier)) != '\n'){}

	(*ligne)++;

	return 0;
}


int controleCoherenceFichier(char *nomFichier){
	int nbMaxSommets = 0, a;

	FILE *fichier = NULL;
	fichier = fopen(nomFichier, "r");
	if(fichier == NULL){
		printf("Probleme dans l'ouverture du fichier %s\n", nomFichier);
		return -1;
	}

	if(passerCommentaire(fichier, &a) < 0)
		return -1;

	fscanf(fichier, "%d\n", &nbMaxSommets);
	if(nbMaxSommets <= 0)
		return -1;

	if(passerCommentaire(fichier, &a) < 0)
		return -1;

	int tabS[nbMaxSommets];
	int i;
	int sv, pv;

	i = 0;
	while(fscanf(fichier, "%d : ", &tabS[i]) != EOF){
		if(tabS[i] > nbMaxSommets)
			return -1;

		while(fscanf(fichier, "(%d/%d), ", &sv, &pv)){
			if(sv > nbMaxSommets)
				return -1;
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
				return -1;
		}
	}

	fclose(fichier);

	return 0;
}


int chargementFichier(char *nomFichier, TypGraphe **graphe){
	FILE* fichier = NULL;
	int ligne = 0;
	int nbMaxSommets = 0;


	if(controleCoherenceFichier(nomFichier) < 0){
		printf("Structure de fichier incorrecte ou erreur interne\n");
		return -1;
	}

	fichier = fopen(nomFichier, "r");
	if(fichier == NULL){
		printf("Probleme dans l'ouverture du fichier %s\n", nomFichier);
		return -1;
	}

	if(passerCommentaire(fichier, &ligne) < 0)
		return -1;

	fscanf(fichier, "%d\n", &nbMaxSommets);
	ligne++;
	printf("nombre de sommets : %d\n", nbMaxSommets);
	//
	initialisationGraphe(graphe, nbMaxSommets);
	//

	if(passerCommentaire(fichier, &ligne) < 0)
		return -1;

	int tab[nbMaxSommets];
	int i = 0;
	int a, b;

	while(fscanf(fichier, "%d : ", &tab[i]) != EOF){
		printf("sommet entré : %d\n", tab[i]);
		//
		insertionSommet(graphe, tab[i]);
		//
		while(fscanf(fichier, "(%d/%d), ", &a, &b)){
			printf("\t\tVers %d avec le poids %d\n", a, b);
			//TODO revoir ici l'insertion d'élément n'existant pas
			insertionArete(graphe, tab[i], a, b);
			//
		}
		i++;
		ligne++;
	}

	printf("nombre de lignes du fichier : %d\n\n", ligne);
	fclose(fichier);

	return 0;
}

int ecritureFichier(char *nomFichier, TypGraphe *graphe){
	if(verifAllocationG(graphe) < 0)
		return -1;

	FILE *fichier = NULL;
	fichier = fopen(nomFichier, "w");	
	if(fichier == NULL){
		printf("Probleme dans l'ouverture du fichier %s\n", nomFichier);
		return -1;	
	}

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


	return 0;
}


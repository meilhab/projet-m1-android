#include "libgraphe.h"

/* initialise le graphe avec nbSommets maximum*/
casErreur initialisationGraphe(TypGraphe **graphe, int nbSommets){
	(*graphe) = (TypGraphe*) malloc(sizeof(TypGraphe));

	casErreur erreur = verifAllocationPG(graphe);
	if(erreur != PAS_ERREUR)
		return erreur;

	(*graphe)->nbMaxSommets = nbSommets;
	(*graphe)->listesAdjencences = 
		(TypVoisins**) malloc(nbSommets * sizeof(TypVoisins*));

//	erreur = verifAllocationL(*((*graphe)->listesAdjencences));
	if((*graphe)->listesAdjencences == NULL)
		return ECHEC_ALLOC_PL;
//		printf("Probleme allocation memoire pour le graphe\n");
	if(erreur != PAS_ERREUR)
		return erreur;

	int i;
	for(i=0; i<nbSommets; i++)
		(*graphe)->listesAdjencences[i] = NULL;
	
	return PAS_ERREUR;
}

/* insère le sommet dans le graphe : initialisation de sa liste */
casErreur insertionSommet(TypGraphe **graphe, int sommet){
	casErreur erreur = verifAllocationPG(graphe);
	if(erreur != PAS_ERREUR)
		return erreur;

	if(sommet - 1 > (*graphe)->nbMaxSommets && sommet - 1 < 0)
		return INSERT_SOMMET_INCORRECT;

	erreur = initialisationListe(&((*graphe)->listesAdjencences[sommet-1]));
	if(erreur != PAS_ERREUR)
		return erreur;
//	ajouterDebut(&((*graphe)->listesAdjencences[sommet-1]), 5, 2);
//	affichageListe((*graphe)->listesAdjencences[sommet-1]);

	return PAS_ERREUR;
}

/* insère une arète entre deux sommets avec son poids */
/* fonction utile pour l'ajout manuel */
casErreur insertionArete(TypGraphe **graphe, int sommetDebut, 
		int sommetSuivant, int poids){
	casErreur erreur = verifAllocationPG(graphe);
	if(erreur != PAS_ERREUR)
		return erreur;

	int nbMaxSommets = (*graphe)->nbMaxSommets;
	if((sommetDebut-1 > nbMaxSommets) || (sommetSuivant-1 > nbMaxSommets) ||
			sommetDebut-1 < 0 || sommetSuivant-1 < 0)
		return INSERT_ARETE_SOMMET_INCORRECT;

	if((*graphe)->listesAdjencences[sommetDebut - 1] == NULL ||
		(*graphe)->listesAdjencences[sommetSuivant - 1] == NULL)
		return INSERT_ARETE_SOMMET_EXISTE_PAS;

	erreur = ajouterDebut(&((*graphe)->listesAdjencences[sommetDebut-1]), 
		sommetSuivant-1, poids);

	return erreur;
}

/* supprime le sommet et tous les autres sommets qui ont une 
arète en commun avec lui */
casErreur suppressionSommet(TypGraphe **graphe, int sommet){
	casErreur erreur = verifAllocationPG(graphe);
	if(erreur != PAS_ERREUR)
		return erreur;
	
	if(sommet - 1 > (*graphe)->nbMaxSommets)
		return SUPPR_SOMMET_SOMMET_INEXISTANT;

	erreur = supprimerTout(&((*graphe)->listesAdjencences[sommet-1]));
	if(erreur != PAS_ERREUR)
		return erreur;
	
	int i;
	erreur = PAS_ERREUR;
	for(i = 0; i<(*graphe)->nbMaxSommets; i++)
		if((*graphe)->listesAdjencences[i] != NULL)
			if(existeVoisin((*graphe)->listesAdjencences[i], sommet - 1)
					== EXISTE){
				erreur = supprimerVoisin(
					&((*graphe)->listesAdjencences[i]), sommet-1);
			}
	
	return erreur;
}

casErreur suppressionArete(TypGraphe **graphe, 
		int sommetDebut, int sommetSuivant){
	casErreur erreur = verifAllocationPG(graphe);
	if(erreur != PAS_ERREUR)
		return erreur;
	
	if((sommetDebut - 1 > (*graphe)->nbMaxSommets) || 
			(sommetSuivant - 1 > (*graphe)->nbMaxSommets))
		return SUPPR_ARETE_SOMMET_INEXISTANT;

	erreur = supprimerVoisin(&((*graphe)->listesAdjencences[sommetDebut-1]),
		sommetSuivant-1);

	return erreur;
}

/* vérifie qu'un pointeur sur graphe est bien initialisé */
casErreur verifAllocationPG(TypGraphe **graphe){	
	if(*graphe == NULL || graphe == NULL)
		return ECHEC_ALLOC_PG;

	return PAS_ERREUR;
}

/* vérifie qu'une graphe est bien initialisée */
casErreur verifAllocationG(TypGraphe *graphe){
	if(graphe == NULL)
		return ECHEC_ALLOC_G;

	return PAS_ERREUR;
}

void affichageGraphe(TypGraphe *graphe){
	if(verifAllocationG(graphe) != PAS_ERREUR)
		return;

	int i;
	TypVoisins *liste;
	for(i=0; i<graphe->nbMaxSommets; i++){
		liste = graphe->listesAdjencences[i];
		if(estVide(liste) >= 0){
			printf("Sommet entré : %d\n", i+1);
			while(liste->voisin != -1){
				printf("\t\tVers %d avec le poids %d\n", 
					(liste->voisin)+1, liste->poidsVoisin);
				liste = liste->voisinSuivant;
			}
		}
	}

	return;
}

casErreur supprimerGraphe(TypGraphe **graphe){
	casErreur erreur = verifAllocationPG(graphe);
	if(erreur != PAS_ERREUR)
		return erreur;

	int i;
	erreur = PAS_ERREUR;
	for(i = 0; i<(*graphe)->nbMaxSommets; i++)
		erreur = supprimerTout(&((*graphe)->listesAdjencences[i]));

	(*graphe) = NULL;
	return erreur;
}

int nombreMaxSommetsGraphe(TypGraphe *graphe){
	if(verifAllocationG(graphe) != PAS_ERREUR)
		return -1;

	return graphe->nbMaxSommets;
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
		return ECHEC_OUVERTURE_FICHIER;

	erreur = passerCommentaire(fichier, &ligne);
	if(erreur != PAS_ERREUR)
		return erreur;

	fscanf(fichier, "%d\n", &nbMaxSommets);
	ligne++;
	printf("nombre de sommets : %d\n", nbMaxSommets);
	//
	erreur = initialisationGraphe(graphe, nbMaxSommets);
	if(erreur != PAS_ERREUR)
		return erreur;

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

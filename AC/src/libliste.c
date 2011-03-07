/*
 ******************************************************************************
 *
 * Programme : libliste.c
 *
 * ecrit par : Guillaume MONTAVON & Benoit MEILHAC
 *
 * resume :    biblioth�que permettant la gestion des ar�tes
 *
 * date :      24/02/2011
 *
 ******************************************************************************
 */

#include "libliste.h"

/*
 * Fonction :    initialisationListe
 *
 * Parametres :  TypVoisins**, liste des voisins du sommets
 *
 * Retour :      casErreur, code d'erreur de la fonction
 *
 * Description : initialise la liste des voisins allouant la m�moire et
 *               en pla�ant la sentinelle
 *
 */
/* initialisation de la liste avec la sentinelle */
casErreur initialisationListe(TypVoisins **liste){
	(*liste) = (TypVoisins*) malloc(sizeof(TypVoisins));
	casErreur erreur = verifAllocationPL(liste);
	if(erreur != PAS_ERREUR)
		//printf("Echec dans l'allocation lors de l'initialisation ");
		//printf("d'une structure de liste\n");
		//exit(1);
		return erreur;

	(*liste)->voisin = -1;
	(*liste)->poidsVoisin = -1;
	(*liste)->voisinSuivant = NULL;

	return PAS_ERREUR;
}


/*
 * Fonction : ajouterDebut
 *
 * Parametres : TypVoisins**, liste des voisins du sommets
 *              int, num�ro de sommet
 *              int, poids du sommet
 *
 * Retour : casErreur, code d'erreur de la fonction
 *
 * Description : ajoute un sommet avec son poids associ� au d�but d'une liste existante en 
d�calant les �l�ments, au minimum la sentinelle
 *
 */
/*  */
casErreur ajouterDebut(TypVoisins **liste, int voisin, int poidsVoisin){
	casErreur erreur = verifAllocationPL(liste);
	if(erreur != PAS_ERREUR)
		return erreur;

	TypVoisins *element = (TypVoisins*) malloc(sizeof(TypVoisins));
	erreur = verifAllocationL(element);
	if(erreur != PAS_ERREUR)
//		printf("Echec d'allocation lors de l'ajout d'�l�ment\n");
//		exit(1);
		return erreur;
	
	element->voisin = voisin;
	element->poidsVoisin = poidsVoisin;
	element->voisinSuivant = (*liste);

	(*liste) = element;

	return PAS_ERREUR;
}

/*
 * Fonction : 
 *
 * Parametres : 
 *
 * Retour : casErreur, code d'erreur de la fonction
 *
 * Description :
 *
 */
/* supprime un voisin s'il existe d'une liste */
casErreur supprimerVoisin(TypVoisins **liste, int voisin){	
	casErreur erreur = verifAllocationPL(liste);
	if(erreur != PAS_ERREUR)
		return erreur;

	erreur = existeVoisin((*liste), voisin);
	if(erreur != EXISTE)
		return erreur;

	if((*liste)->voisin == voisin){
		(*liste) = (*liste)->voisinSuivant;	
		return PAS_ERREUR;
	}

	TypVoisins *element = (*liste);
	TypVoisins *pelement = (*liste);

	while(element->voisin != -1 && element->voisin != voisin){ 
	//controler le && ou ||
		pelement = element;
		element = element->voisinSuivant;
	}

	pelement->voisinSuivant = element->voisinSuivant;
	free(element);

	return PAS_ERREUR;
}

/*
 * Fonction : 
 *
 * Parametres : 
 *
 * Retour : casErreur, code d'erreur de la fonction
 *
 * Description :
 *
 */
/* vide la structure et lib�re la m�moire */
casErreur nettoyerTout(TypVoisins **liste){
	casErreur erreur = verifAllocationPL(liste);
	if(erreur != PAS_ERREUR)
		return erreur;

	TypVoisins *element = (*liste);
	TypVoisins *pelement = (*liste);

	while(element->voisin != -1){
		pelement = element;
		element = element->voisinSuivant;
		free(pelement);
	}
	
	(*liste) = element;

	return PAS_ERREUR;
}

/*
 * Fonction : 
 *
 * Parametres : 
 *
 * Retour : casErreur, code d'erreur de la fonction
 *
 * Description :
 *
 */
/* supprimer totalement la liste */
casErreur supprimerTout(TypVoisins **liste){
	casErreur erreur = verifAllocationPL(liste);
	if(erreur != PAS_ERREUR)
		return erreur;

	TypVoisins *element = (*liste);
	TypVoisins *pelement = (*liste);

	while(element->voisin != -1){
		pelement = element;
		element = element->voisinSuivant;
		free(pelement);
	}
	
	free(element);
	(*liste) = NULL;

	return PAS_ERREUR;
}

/*
 * Fonction : 
 *
 * Parametres : 
 *
 * Retour : casErreur, code d'erreur de la fonction
 *
 * Description :
 *
 */
// retourne la taille de la liste
int taille(TypVoisins *liste){
	if(verifAllocationL(liste) != PAS_ERREUR)
		return -1;

	int i = 0;
	while(liste->voisin != -1){
		liste = liste->voisinSuivant;
		i++;
	}

	return i;
}

/*
 * Fonction : 
 *
 * Parametres : 
 *
 * Retour : casErreur, code d'erreur de la fonction
 *
 * Description :
 *
 */
/* simple fonction d'affichage pour une structure donn�e */
void affichageListe(TypVoisins *liste){
	if(verifAllocationL(liste) != PAS_ERREUR)
		return ;

	printf("->");
	while(liste->voisin != -1){
		printf(" %d", liste->voisin);
		liste = liste->voisinSuivant;
	}
	printf("\n");

	return;
}

/*
 * Fonction : 
 *
 * Parametres : 
 *
 * Retour : casErreur, code d'erreur de la fonction
 *
 * Description :
 *
 */
/* v�rifie qu'un pointeur sur liste est bien initialis� */
casErreur verifAllocationPL(TypVoisins **liste){	
	if(*liste == NULL || liste == NULL)
		return ECHEC_ALLOC_PL;

	return PAS_ERREUR;
}

/*
 * Fonction : 
 *
 * Parametres : 
 *
 * Retour : casErreur, code d'erreur de la fonction
 *
 * Description :
 *
 */
/* v�rifie qu'une liste est bien initialis�e */
casErreur verifAllocationL(TypVoisins *liste){
	if(liste == NULL)
		return ECHEC_ALLOC_L;

	return PAS_ERREUR;
}

/*
 * Fonction : 
 *
 * Parametres : 
 *
 * Retour : casErreur, code d'erreur de la fonction
 *
 * Description :
 *
 */
/* donne l'existence ou non d'un voisin dans une liste */
casErreur existeVoisin(TypVoisins *liste, int voisin){
	casErreur erreur = verifAllocationL(liste);
	if(erreur != PAS_ERREUR)
		return erreur;

	while(liste->voisin != -1 && liste->voisin != voisin)
		liste = liste->voisinSuivant;

	if(liste->voisin == voisin)
		return EXISTE;
	
	return EXISTE_PAS;
}


/*
 * Fonction : 
 *
 * Parametres : 
 *
 * Retour : casErreur, code d'erreur de la fonction
 *
 * Description :
 *
 */
// 0 : est vide
// >0 : pas vide
// -1 : pas initialis�
int estVide(TypVoisins *liste){
	return taille(liste);
}

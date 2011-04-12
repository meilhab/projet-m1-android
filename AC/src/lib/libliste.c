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
 * Parametres :  TypVoisins**, liste des voisins
 *
 * Retour :      casErreur, code d'erreur de la fonction
 *
 * Description : initialise la liste des voisins allouant la m�moire et
 *               en pla�ant la sentinelle
 *
 */
casErreur initialisationListe(TypVoisins **liste){
	casErreur erreur = verifAllocationL(*liste);
	if(erreur == PAS_ERREUR)
		return PAS_ERREUR;

	(*liste) = (TypVoisins*) malloc(sizeof(TypVoisins));
	erreur = verifAllocationPL(liste);
	if(erreur != PAS_ERREUR)
		return erreur;

	(*liste)->voisin = -1;
	(*liste)->poidsVoisin = -1;
	(*liste)->voisinSuivant = NULL;

	return PAS_ERREUR;
}


/*
 * Fonction :    ajouterDebut
 *
 * Parametres :  TypVoisins**, liste des voisins
 *               int voisin, num�ro de sommet
 *               int poidsVoisin, poids du sommet
 *
 * Retour :      casErreur, code d'erreur de la fonction
 *
 * Description : ajoute un sommet avec son poids associ� 
 *               au d�but d'une liste existante en 
 *               d�calant les �l�ments, au minimum la sentinelle
 *
 */
casErreur ajouterDebut(TypVoisins **liste, int voisin, int poidsVoisin){
	casErreur erreur = verifAllocationPL(liste);
	if(erreur != PAS_ERREUR)
		return erreur;

	TypVoisins *element = (TypVoisins*) malloc(sizeof(TypVoisins));
	erreur = verifAllocationL(element);
	if(erreur != PAS_ERREUR)
		return erreur;
	
	element->voisin = voisin;
	element->poidsVoisin = poidsVoisin;
	element->voisinSuivant = (*liste);

	(*liste) = element;

	return PAS_ERREUR;
}

/*
 * Fonction : supprimerVoisin
 *
 * Parametres : TypVoisins**, liste des voisins
 *              int voisin, num�ro du sommet
 *
 * Retour : casErreur, code d'erreur de la fonction
 *
 * Description : supprime un sommet s'il existe dans la liste
 *
 */
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
 * Fonction :    nettoyerTout
 *
 * Parametres :  TypVoisins**, liste des voisins
 *
 * Retour :      casErreur, code d'erreur de la fonction
 *
 * Description : vide la structure et lib�re la m�moire, ne gardant
 *               que la sentinelle
 *
 */
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
 * Fonction :    supprimerTout
 *
 * Parametres :  TypVoisins**, liste des voisins
 *
 * Retour :      casErreur, code d'erreur de la fonction
 *
 * Description : supprime totalement la liste et la remet � NULL
 *
 */
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
 * Fonction :    tailleTypVoisins
 *
 * Parametres :  TypVoisins*, liste des voisins
 *
 * Retour :      int, taille de la liste en param�tre
 *
 * Description : retourne la taille de la liste pass�e en param�tre
 *
 */
int tailleTypVoisins(TypVoisins *liste){
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
 * Fonction :    affichageListe
 *
 * Parametres :  TypVoisins*, liste des voisins
 *
 * Retour :      rien
 *
 * Description : fonction d'affichage pour la structure
 *
 */
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
 * Fonction :    verifAllocationPL
 *
 * Parametres :  TypVoisins**, liste des voisins
 *
 * Retour :      casErreur, code d'erreur de la fonction
 *
 * Description : v�rifie qu'un pointeur sur le pointeur de la liste 
 *               est bien initialis�
 *
 */
casErreur verifAllocationPL(TypVoisins **liste){	
	if(*liste == NULL || liste == NULL)
		return ECHEC_ALLOC_PL;

	return PAS_ERREUR;
}

/*
 * Fonction :    verifAllocationL
 *
 * Parametres :  TypVoisins*, liste des voisins
 *
 * Retour :      casErreur, code d'erreur de la fonction
 *
 * Description : v�rifie qu'un pointeur sur la liste est bien initialis�
 *
 */
casErreur verifAllocationL(TypVoisins *liste){
	if(liste == NULL)
		return ECHEC_ALLOC_L;

	return PAS_ERREUR;
}

/*
 * Fonction :    existeVoisin
 *
 * Parametres :  TypVoisins*, liste des voisins
 *               int voisin, num�ro du sommet
 *
 * Retour :      casErreur, code d'erreur de la fonction
 *
 * Description : v�rifie si un sommet donn� appartient ou non
 *               � la liste donn�e en param�tre
 *
 */
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
 * Fonction :    estVide
 *
 * Parametres :  TypVoisins*, liste des voisins
 *
 * Retour :      int, informe si la liste est vide ou non
 *                    0 : est vide
 *                    >0 : pas vide
 *                    -1 : pas initialis�
 *
 * Description : permet de savoir rapidement si la liste est vide ou non sans 
 *               contrainte de code d'erreur
 *
 */
int estVide(TypVoisins *liste){
	return tailleTypVoisins(liste);
}

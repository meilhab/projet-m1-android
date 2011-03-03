#include "libgraphe.h"
#include "menu.h"

int main(){
	TypGraphe *graphe = NULL;
	casErreur erreur = chargementFichier("fichierTest.txt", &graphe);
	if(erreur==PAS_ERREUR)
		printf("Pas d'erreur\n");
	else
		afficherErreur(erreur);
	affichageGraphe(graphe);

	ecritureFichier("fichierResultat.txt", graphe);
	
	supprimerGraphe(&graphe);
	
	
	graphe = NULL;
	
	menu(graphe);

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
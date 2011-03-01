#include "menu.h"

/*
******************************************************************************
*
* Programme : menu.c
*
* ecrit par : Guillaume MONTAVON & Benoit MEILHAC
*
* resume : permet l’affichage d’un menu donnant acces aux fonctionnalites
*          demandees (creation, lecture, insertion, ...)
*
* date : 24/02/2011
*
******************************************************************************
*/

/**
 * Affiche le menu
 */
void afficherMenu(TypGraphe* grapheCourant) {

	printf("============== Menu =============\n\n");
	printf("Que souhaitez-vous faire?\n");
	printf("  1.Creation d’un graphe\n");
	printf("  2.Lecture d’un graphe dans un fichier texte\n");
	if(grapheCourant != NULL) {
		printf("  3.Insertion d’un sommet dans le graphe\n");
		printf("  4.Insertion d’une arete entre deux sommets du graphe\n");
		printf("  5.Suppression d’un sommet du graphe\n");
		printf("  6.Suppression d’une arete entre deux sommets du graphe\n");
		printf("  7.Affichage du graphe\n");
		printf("  8.Sauvegarde du graphe dans un fichier\n");
		printf("  9.compute maximum flow\n");
		printf("  10.Modifier le poids d'une arete\n");
		printf("  11.insert an labeled edge in the graph\n");
		printf("  12.Quitter\n");
	}
	else
		printf("  3.Quitter\n");
	printf("=================================\n");

}

/**
 * Gere les differentes actions qui peuvent etre demandees par l'utilisateur
 */
void actionsMenu(TypGraphe* grapheCourant) {

	int reponse = 0;
	
	scanf("%d",&reponse);
	if(grapheCourant == NULL) {
		if(reponse<1 || reponse>3)
			reponse=-1;
		if(reponse==3)
			reponse=12;
	}
	
	switch(reponse) {
		case 1 : //Creation d’un graphe
			int maxSommet;
			if(demandeSuppression(grapheCourant) == 0){
				printf("Combien de sommets, au maximum, ");
				printf("possedera votre graphe ?\n");
				scanf("%d", &maxSommet);
				initialisationGraphe(&grapheCourant, maxSommet); 
			}
			break;
		case 2 : //Lecture d’un graphe dans un fichier texte
			if(demandeSuppression(grapheCourant) == 0) {
				char chemin[250];
				printf("Veuillez entrer le chemin du fichier ");
				printf("contenant le graphe\n");
				scanf("%s", chemin);
				chargementFichier(chemin,&grapheCourant);
			}
			break;
		case 3 : //Insertion d’un sommet dans le graphe
			if(grapheCourant != NULL) {
				int id=-1;
				do {
					
					printf("Quel est le numero du sommet ?\n");
					scanf("%d", &id);
					
				} while (id>=grapheCourant->nbMaxSommets || id<0);
				
				nouveauSommet(grapheCourant, id);
			}
			else
				printf("Aucun graphe n'existe, veuillez en creer un.\n");
			break;
		case 4 : //Insertion d’une arete entre deux sommets du graphe
			if(grapheCourant != NULL){
				int s1, s2, poids, orientee;
				do {
					printf("Quel est le premier sommet ?\n");
					scanf("%d", &s1);
				} while(s1>=grapheCourant->nbMaxSommets || s1<0);
				
				do {
					printf("Quel est le deuxieme sommet ?\n");
					scanf("%d", &s2);
				} while(s2>=grapheCourant->nbMaxSommets || s2<0);
				
				do {
					printf("Quel est le poids de l'arete ?\n");
					scanf("%d", &poids);
				} while(poids<0);
				
				do {
					printf("S'agit-il d'une arete orientee (o) ou non (n)?\n");
					scanf("%d", &orientee);
				} while(orientee!='o' && orientee!='n');
				
				//verification que les sommets extremites existent et que
				//cette arete n’est pas deja presente dans le graphe
				
				insertionArete(&grapheCourant, s1, s2, poids, orientee);
			}
			else
				printf("Aucun graphe n'existe, veuillez en creer un.\n");
			break;
		case 5 : //Suppression d’un sommet du graphe
			if(grapheCourant != NULL){
				int id=-1;
				do {
					
					printf("Quel est le numero du sommet a supprimer ?\n");
					scanf("%d", &id);
					
				} while (id>=grapheCourant->nbMaxSommets || id<0);
				
				supprimerSommet(grapheCourant, id);
			}
			else
				printf("Aucun graphe n'existe, veuillez en creer un.\n");
			break;
		case 6 : //Suppression d’une arete entre deux sommets du graphe
			if(grapheCourant != NULL){
				int s1, s2;
				do {
					printf("Quel est le premier sommet ?\n");
					scanf("%d", &s1);
				} while(s1>=grapheCourant->nbMaxSommets || s1<0);
				
				do {
					printf("Quel est le deuxieme sommet ?\n");
					scanf("%d", &s2);
				} while(s2>=grapheCourant->nbMaxSommets || s2<0);
				
				supprimerArete(grapheCourant, s1, s2);
			}
			else
				printf("Aucun graphe n'existe, veuillez en creer un.\n");
			break;
			break;
		case 7 : //Affichage du graphe
			if(grapheCourant != NULL)
				afficherGraphe(grapheCourant);
			else
				printf("Aucun graphe n'existe, veuillez en creer un.\n");
			break;
		case 8 : //Sauvegarde du graphe dans un fichier
			if(grapheCourant != NULL){
				char chemin[250];
				printf("Donnez un chemin pour le fichier ? ");
				scanf("%s", chemin);
				ecritureFichier(chemin, grapheCourant);
			}
			else
				printf("No graph is defined\n");
			break;
		case 9 :
			break;
		case 10 :
			break;
		case 11 :
			break;
		case 12 : //Quitter
			if(grapheCourant != NULL){
				supprimerGraphe(&grapheCourant);
			}
			exit(0);
			break;
		default : //mauvais choix
			printf("Erreur lors de la saisie de votre choix, ");
			printf("Veuillez recommencer.\n");
			break;
	}

}

/**
 * Si un graphe existe deja, on demande a l'utilisateur, s'il souhaite le
 * supprimer afin d'en creer un nouveau
 * retourne 0 si on peut continuer les operations (graphe supprime ou vide)
 * retourn -1 si l'utilisateur n'a pas voulu le supprimer, et donc
 * les operations suivantes doivent etre annulees
 */
int demandeSuppression(TypGraphe* grapheCourant) {

	int retour = 0, reponse=0;
	
	if(grapheCourant != NULL){
		do {
			
			printf("Un graphe existe deja, souhaitez-vous le supprimer ?\n");
			printf("  1.Oui\n");
			printf("  2.Non\n");
			scanf("%d", &reponse);
			if(reponse == 1)
				supprimerGraphe(&grapheCourant);
			else
				retour=-1;
			if (( reponse != 2 ) && ( reponse != 1 ))
				printf("Veuillez saisir un choix correct.\n");
			
		} while(( reponse != 2 ) && ( reponse != 1 ));

	}
	
	return retour;
	
}

/*
int main() { 
	while(1) {
		afficherMenu();
		actionsMenu();
	}
	return 0;
}
*/
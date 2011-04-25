#ifndef CONNEXIONARBITRE_H
#define CONNEXIONARBITRE_H

#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <string.h>


typedef enum {
	ECHEC_CONNEXION,
	ECHEC_DECONNEXION,
	ECHEC_ENVOI_IDENTIFICATION,
	ECHEC_RECEPTION_IDENTIFICATION,
    ECHEC_CONFIRMATION_IDENTIFICATION,
	ECHEC_ENVOI_NOUVELLE_PARTIE,
	ECHEC_RECEPTION_NOUVELLE_PARTIE,
    ECHEC_RETOUR_NOUVELLE_PARTIE,
	ECHEC_CODE_FINTOURNOI_INCONNU,
	ECHEC_CODE_PREMIER_INCONNU,
    ECHEC_ENVOI_DERNIER_COUP,
    ECHEC_RECEPTION_DERNIER_COUP,
    ECHEC_RECEPTION_DERNIER_COUP_ADVERSAIRE,
    ECHEC_CONFIRMATION_DERNIER_COUP,
    ECHEC_CONFIRMATION_DERNIER_COUP_ADVERSAIRE,
    ECHEC_RECEPTION_COUP_VALIDE_ADVERSAIRE,
    ECHEC_CODE_VALIDCOUP_INCONNU,
    ECHEC_CODE_PROPCOUP_INCONNU,
    RETOUR_PARTIE_NULLE,
    RETOUR_TIMEOUT_FIN_PARTIE,
    RETOUR_TRICHE_FIN_PARTIE,
    RETOUR_VICTOIRE_NOUVELLE_PARTIE,
    RETOUR_DEFAITE_NOUVELLE_PARTIE,
    FIN_DE_JEU,
    CODE_OK
} RetourFonction;


void clearScanf(void);
RetourFonction creationConnexion(char *machine, int port, int *sock);
RetourFonction deconnexion(int sock);
RetourFonction identification(int sock, int *identifiant);
RetourFonction demandeNouvellePartie(int sock, int identifiant);
RetourFonction debutePartie(int sock);
RetourFonction attendPremierCoup(int sock);
RetourFonction jouerUnCoup(int sock, int *numeroCoup);
RetourFonction recevoirUnCoup(int sock, int *numeroCoup);
TypLigne retournerTypLigne(int ligne);
TypColonne retournerTypColonne(int colonne);
void traitementSiErreur(RetourFonction retour);
int main(int argc, char **argv);


#endif //CONNEXIONARBITRE_H


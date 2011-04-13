#ifndef CONNEXIONARBITRE_H
#define CONNEXIONARBITRE_H

#include <stdio.h>
#include <stdlib.h>


typedef enum {
	ECHEC_CONNEXION,
	ECHEC_DECONNEXION,
	ECHEC_ENVOI_IDENTIFICATION,
	ECHEC_RECEPTION_IDENTIFICATION,
	ECHEC_ENVOI_NOUVELLE_PARTIE,
	ECHEC_RECEPTION_NOUVELLE_PARTIE,
    ECHEC_RETOUR_NOUVELLE_PARTIE,
	ECHEC_CODE_FINTOURNOI_INCONNU,
	ECHEC_CODE_PREMIER_INCONNU,
    CODE_OK
} RetourFonction;


void clearScanf(void);
RetourFonction creationConnexion(int *sock);
RetourFonction deconnexion(int sock);
RetourFonction identification(int sock, int *identifiant);
RetourFonction demandeNouvellePartie(int sock, int identifiant);
int main(void);


#endif //CONNEXIONARBITRE_H


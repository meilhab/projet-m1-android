#ifndef MENU_H
#define MENU_H

#include "libgraphe.h"

void clearScanf(void);
void menu(TypGraphe** grapheCourant);
void afficherMenu(TypGraphe** grapheCourant);
void actionsMenu(TypGraphe** grapheCourant);
int demandeSuppression(TypGraphe** grapheCourant);
void afficherErreur(casErreur erreur);

#endif //MENU_H

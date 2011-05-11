/*
 ******************************************************************************
 *
 * Programme : connexionArbitre.c
 *
 * ecrit par : Guillaume MONTAVON & Benoit MEILHAC
 *
 * resume :    realise la connexion a l'arbitre ainsi que la connexion au
 *             moteur Java et donc a l'IA
 *
 * date :      02/05/2011
 *
 ******************************************************************************
 */

#include "protocolArbitre.h"
#include "fonctionsSocket.h"
#include "connexionArbitre.h"

/*
 * Fonction :    creationConnexion
 *
 * Parametres :  char *machine, nom de la machine distante
 *               int port, port de la machine distante
 *               int *sock, socket cree
 *
 * Retour :      RetourFonction, code d'erreur de la fonction
 *
 * Description : creer une socket avec le port et la machine donnee en
 *               parametre
 *
 */
RetourFonction creationConnexion(char *machine, int port, int *sock){
	fprintf(stdout, "C : Procedure de connexion avec %s, port %d\n", machine, port);

	(*sock) = socketClient(machine, port);
	if((*sock) < 0)
		return ECHEC_CONNEXION;

	fprintf(stdout, "C : Connexion effectuee avec %s, port %d\n", machine, port);

	return CODE_OK;
}

/*
 * Fonction :    deconnexion
 *
 * Parametres :  int sock, socket a deconnecter
 *
 * Retour :      RetourFonction, code d'erreur de la fonction
 *
 * Description : ferme la connexion d'une socket
 *
 */
RetourFonction deconnexion(int sock){
	int err;

	fprintf(stdout, "C : Procedure de deconnexion\n");

	shutdown(sock, 2);
	err = close(sock);
	
	if(err < 0)
		return ECHEC_DECONNEXION;

	fprintf(stdout, "C : Deconnexion effectuee\n");

	return CODE_OK;
}

/*
 * Fonction :    identification
 *
 * Parametres :  int sock, socket de l'arbitre
 *               int *identifiant, identifiant que possedera le nouveau joueur
 *
 * Retour :      RetourFonction, code d'erreur de la fonction
 *
 * Description : identifie un nouveau joueur aupres de l'arbitre
 *
 */
RetourFonction identification(int sock, int *identifiant){
	TypIdentificationReq req;
	TypIdentificationRep rep;
	char login[TAIL_CHAIN] = LOGIN;
	int err;
	
	req.idRequest = IDENTIFICATION;
	strcpy(req.nom, login);

	fprintf(stdout, "C : Procedure d'identification\n");

	err = send(sock, (void*) &req, sizeof(req), 0);
	if(err < 0)
		return ECHEC_ENVOI_IDENTIFICATION;

	err = recv(sock, (void*) &rep, sizeof(rep), 0);
	if(err < 0)
		return ECHEC_RECEPTION_IDENTIFICATION;

	if(rep.err != ERR_OK)
		return ECHEC_CONFIRMATION_IDENTIFICATION;

	(*identifiant) = rep.joueur;

	fprintf(stdout, "C : Identification effectuee\n");
	
	return CODE_OK;
}

/*
 * Fonction :    demandeNouvellePartie
 *
 * Parametres :  int sock, socket de l'arbitre
 *               int sockMoteurJava, socket du moteur Java
 *               int identifiant, identifiant du joueur
 *
 * Retour :      RetourFonction, code d'erreur de la fonction
 *
 * Description : demande une nouvelle partie a l'arbitre
 *
 */
RetourFonction demandeNouvellePartie(int sock, int sockMoteurJava, int identifiant){
	TypPartieReq req;
	TypPartieRep rep;
	int err;
	
	req.idRequest = PARTIE;
	req.joueur = identifiant;
	
	rep.premier = FAUX;
	
	fprintf(stdout, "C : Procedure de demande de nouvelle partie ..... \n\n");
	
	err = send(sock, (void*) &req, sizeof(req), 0);
	
	int err2 = recv(sock, (void*) &rep, sizeof(rep), 0);
	
	if(err < 0)
		return ECHEC_ENVOI_NOUVELLE_PARTIE;
	if(err2 < 0)
		return ECHEC_RECEPTION_NOUVELLE_PARTIE;
	
	if(rep.err != ERR_OK)
		return ECHEC_RETOUR_NOUVELLE_PARTIE;
	
	switch(rep.finTournoi){
		case VRAI:
			deconnexion(sock);
			return FIN_DE_JEU;
		case FAUX:
			fprintf(stdout, "C : Demande de nouvelle partie effectuee\n");
			switch(rep.premier){
				case VRAI:
					return debutePartie(sock, sockMoteurJava);
				case FAUX:
					return attendPremierCoup(sock, sockMoteurJava);
				default:
					return ECHEC_CODE_PREMIER_INCONNU; 
			}
		default:
			return ECHEC_CODE_FINTOURNOI_INCONNU;
	}

	return CODE_OK;
}

/*
 * Fonction :    debutePartie
 *
 * Parametres :  int sock, socket de l'arbitre
 *               int sockMoteurJava, socket du moteur Java
 *
 * Retour :      RetourFonction, code d'erreur de la fonction
 *
 * Description : debute la partie contre l'adversaire
 *
 */
RetourFonction debutePartie(int sock, int sockMoteurJava){
	
	int numeroCoup = 0;
	RetourFonction retour;

	retour = envoiMoteurJavaDebute(sockMoteurJava, 1);
	
	do {
		retour = jouerUnCoup(sock, sockMoteurJava, &numeroCoup);
		if(retour == CODE_OK)
			retour = recevoirUnCoup(sock, sockMoteurJava, &numeroCoup);
	} while(retour == CODE_OK);

	return retour;
}

/*
 * Fonction :    attendPremierCoup
 *
 * Parametres :  int sock, socket de l'arbitre
 *               int sockMoteurJava, socket du moteur Java
 *
 * Retour :      RetourFonction, code d'erreur de la fonction
 *
 * Description : attend un coup de l'adversaire
 *
 */
RetourFonction attendPremierCoup(int sock, int sockMoteurJava){
	int numeroCoup = 0;
	RetourFonction retour;

	retour = envoiMoteurJavaDebute(sockMoteurJava, 0);

	do {
		retour = recevoirUnCoup(sock, sockMoteurJava, &numeroCoup);
		if(retour == CODE_OK)
			retour = jouerUnCoup(sock, sockMoteurJava, &numeroCoup);
	} while(retour == CODE_OK);

	return retour;
}

/*
 * Fonction :    jouerUnCoup
 *
 * Parametres :  int sock, socket de l'arbitre
 *               int sockMoteurJava, socket du moteur Java
 *               int *numeroCoup, numero du coup actuel
 *
 * Retour :      RetourFonction, code d'erreur de la fonction
 *
 * Description : recoi un coup a jouer du moteur java, puis l'envoi a l'arbitre
 *
 */
RetourFonction jouerUnCoup(int sock, int sockMoteurJava, int *numeroCoup){

	TypCoupReq req;
	int err;
	//R�cup�ration du coup � jouer
	char stringReq[TAIL_CHAIN] = "";
	err = recv(sockMoteurJava, (void*) stringReq, TAIL_CHAIN, 0);
	if(err < 0)
		return ECHEC_RECEPTION_DERNIER_COUP;
	req = stringToTypCoupReq(stringReq);
	
	fprintf(stdout, "C : Envoie a l'arbitre du coup : %s\n", stringReq);
	
	TypCoupRep rep;
	
	req.idRequest = COUP;

	if(req.propCoup == PRISE)
		(*numeroCoup) = 0;
	else
		(*numeroCoup) ++;

	if((*numeroCoup) >= NB_COUPS_MAX)
		req.propCoup = NULLE;
	
	req.numeroDuCoup  = (*numeroCoup);
	
	// envoi du coup joue par l'ia
	err = send(sock, (void*) &req, sizeof(req), 0);
	if(err < 0){
		fprintf(stderr, "C : Echec d'envoi du dernier coup\n");
		return ECHEC_ENVOI_DERNIER_COUP;
	}

	// reception de l'arbitre concernant la validite du coup
	err = recv(sock, (void*) &rep, sizeof(rep), 0);
	if(err < 0)
		return ECHEC_RECEPTION_DERNIER_COUP;

	if(rep.err != ERR_OK)
		return ECHEC_CONFIRMATION_DERNIER_COUP;
	
	// traitement de la validite du coup
	switch(rep.validCoup){
		case VALID:
			if(req.propCoup == GAGNE)
				return RETOUR_VICTOIRE_NOUVELLE_PARTIE;
			if(req.propCoup == NULLE)
				return RETOUR_NULLE_NOUVELLE_PARTIE;
			else
				fprintf(stdout, "C : Coup valide, c'est le tour de l'adversaire\n");
			break;
		case TIMEOUT:
			return RETOUR_TIMEOUT_FIN_PARTIE;
			break;
		case TRICHE:
			return RETOUR_TRICHE_FIN_PARTIE;
			break;
		default:
			return ECHEC_CODE_VALIDCOUP_INCONNU;
	}

	//fprintf(stdout, "C : Nombre de coups joues : %d\n", (*numeroCoup));

	return CODE_OK;
}

/*
 * Fonction :    envoiMoteurJavaDebute
 *
 * Parametres :  int sock, socket du moteur Java
 *               int commence, 1 si le joueur commence la partie 
 *
 * Retour :      RetourFonction, code d'erreur de la fonction
 *
 * Description : envoi au moteur Java une requete qui lui dira si le joueur
 *               commence a jouer ou attend un coup de l'adversaire
 *
 */
RetourFonction envoiMoteurJavaDebute(int sock, int commence) {

	int err;
	char envoi[TAIL_CHAIN] = "";
	if(commence == 1) {
		strcpy(envoi, "commence\n");
		fprintf(stdout, "C : Le joueur va commencer\n");
	}
	else {
		strcpy(envoi, "attend\n");
		fprintf(stdout, "C : Le joueur va attendre le coup de l'adversaire\n\n\n");
	}
	err = send(sock, (void*) envoi, strlen(envoi), 0);
	if(err < 0){
		fprintf(stderr, "C : Echec d'envoi au moteur Java si il commence\n\n\n");
		return ECHEC_ENVOI_COMMENCE;
	}
	
	return CODE_OK;
	
}

/*
 * Fonction :    envoiMoteurJavaRestart
 *
 * Parametres :  int sock, socket du moteur Java
 *
 * Retour :      RetourFonction, code d'erreur de la fonction
 *
 * Description : demande au moteur Java de demarrer une nouvelle partie
 *
 */
RetourFonction envoiMoteurJavaRestart(int sock) {

	int err;
	char envoi[TAIL_CHAIN] = "";
	strcpy(envoi, "restart");
	fprintf(stdout, "C : Une nouvelle partie commence\n");

	err = send(sock, (void*) envoi, strlen(envoi), 0);
	if(err < 0){
		fprintf(stderr, "C : Echec d'envoi au moteur Java qu'il doit redemarrer une partie\n");
		return ECHEC_ENVOI_COMMENCE;
	}
	
	return CODE_OK;
	
}

/*
 * Fonction :    recevoirUnCoup
 *
 * Parametres :  int sock, socket de l'arbitre
 *               int sockMoteurJava, socket du moteur Java
 *               int *numeroCoup, numero du coup actuel
 *
 * Retour :      RetourFonction, code d'erreur de la fonction
 *
 * Description : recoi un coup de l'adversaire par l'arbitre puis envoi ce
 *               dernier au moteur Java
 *
 */
RetourFonction recevoirUnCoup(int sock, int sockMoteurJava, int *numeroCoup){
	TypCoupReq req;
	TypCoupRep rep;
	int err;
	
	printf("C : Attente d'un coup adverse .....\n\n\n");
	
	// reception de l'arbitre concernant la validite du coup de l'adversaire
	err = recv(sock, (void*) &rep, sizeof(rep), 0);
	if(err < 0)
		return ECHEC_RECEPTION_DERNIER_COUP_ADVERSAIRE;
	
	if(rep.validCoup != RETOUR_TIMEOUT_FIN_PARTIE && rep.validCoup != RETOUR_TRICHE_FIN_PARTIE) {
		// reception du coup de l'adversaire
		err = recv(sock, (void*) &req, sizeof(req), 0);
		if(rep.err != ERR_OK)
			return ECHEC_CONFIRMATION_DERNIER_COUP_ADVERSAIRE;
		if(err < 0)
			return ECHEC_RECEPTION_COUP_VALIDE_ADVERSAIRE;
	}
	// traitement de la validite du coup
	switch(rep.validCoup){
		case VALID:
			if(req.propCoup == GAGNE)
				return RETOUR_DEFAITE_NOUVELLE_PARTIE;
			else if(req.propCoup == NULLE)
				return RETOUR_NULLE_NOUVELLE_PARTIE;
			else
				fprintf(stdout, "C : Coup adversaire valide, reception de ce coup\n");
			break;
		case TIMEOUT:
			return RETOUR_TIMEOUT_FIN_PARTIE;
			break;
		case TRICHE:
			return RETOUR_TRICHE_FIN_PARTIE;
			break;
		default:
			return ECHEC_CODE_VALIDCOUP_INCONNU;
	}
	

	if(req.propCoup == PRISE)
		(*numeroCoup) = 0;
	else
		(*numeroCoup) ++;
	
	//fprintf(stdout, "taille req recu : %d\n", err);

	//traitement de la reception avec le moteur java
	char reqToString[TAIL_CHAIN] = "";
	typCoupReqToString(req, reqToString);

	fprintf(stdout, "C : On recoit %s\n", reqToString);
	err = send(sockMoteurJava, (void*) &reqToString, strlen(reqToString), 0);
	if(err < 0){
		fprintf(stderr, "C : Echec d'envoi du dernier coup au moteur Java\n");
		return ECHEC_ENVOI_DERNIER_COUP;
	}

	//fprintf(stdout, "C : Nombre de coups joues : %d\n", (*numeroCoup));

	return CODE_OK;
}

/*
 * Fonction :    identificationMoteurJava
 *
 * Parametres :  int sock, socket du moteur Java
 *
 * Retour :      RetourFonction, code d'erreur de la fonction
 *
 * Description : envoi un mot de passe au moteur Java afin de se connecter et
 *               d'etre sur que le joueur est bien celui qui est associe au
 *               moteur Java
 *
 */
RetourFonction identificationMoteurJava(int sock) {
	
	int err;
	char motPasse[TAIL_CHAIN] = MOT_PASSE;
	// envoi du mot de passe
	err = send(sock, (void*) motPasse, strlen(motPasse), 0);
	if(err < 0){
		fprintf(stderr, "Echec d'envoi du mot de passe\n");
		return ECHEC_ENVOI_MOT_PASSE;
	}
	
	char repMotPasse[TAIL_CHAIN] = "";
	// reception du moteur java
	err = recv(sock, (void*) &repMotPasse, TAIL_CHAIN, 0);
	if(err < 0)
		return ECHEC_RECEPTION_MOT_PASSE;

	//fprintf(stdout, "Mot de passe : %s\n", repMotPasse);
	
	if(strcmp(repMotPasse, "OK") != 0)
		return ECHEC_CONFIRMATION_MOT_PASSE;
	else
		fprintf(stdout, "identification aupres du moteur Java effectuee\n");
	
	return CODE_OK;

}

/*
 * Fonction :    retournerTypLigne
 *
 * Parametres :  int ligne, ligne demandee
 *
 * Retour :      TypLigne, ligne convertie en TypLigne
 *
 * Description : converti une ligne (en entier) en une ligne de type
 *               TypLigne
 *
 */
TypLigne retournerTypLigne(int ligne){
	switch(ligne){
		case 0:
			return LI_ZERO;
		case 1:
			return LI_UN;
		case 2:
			return LI_DEUX;
		case 3:
			return LI_TROIS;
		case 4:
			return LI_QUATRE;
		default:
			return LI_MAIN;
	}
}

/*
 * Fonction :    retournerIntLigne
 *
 * Parametres :  int colonne, colonne demandee
 *
 * Retour :      TypColonne, colonne convertie en TypColonne
 *
 * Description : converti une colonne (en entier) en une colonne de type
 *               TypColonne
 *
 */
TypColonne retournerTypColonne(int colonne){
	switch(colonne){
		case 0:
			return CO_ZERO;
		case 1:
			return CO_UN;
		case 2:
			return CO_DEUX;
		case 3:
			return CO_TROIS;
		case 4:
			return CO_QUATRE;
		case 5:
			return CO_CINQ;
		default:
			return CO_MAIN;
	}
}

/*
 * Fonction :    stringToTypCoupReq
 *
 * Parametres :  char reponse[], requete recue du moteur Java
 *
 * Retour :      TypCoupReq, requete qui sera envoyee a l'arbitre
 *
 * Description : converti une requete recue du moteur Java (chaine de
 *               caracteres), en une requete de type TypCoupReq qui sera
 *               envoyee a l'arbitre
 *
 */
TypCoupReq stringToTypCoupReq(char reponse[]) {
	
	TypCoupReq req;
	TypPosition positionDepart;
	TypPosition positionArrivee;
	TypPosition positionPiecePrise2;
	
	req.idRequest = COUP;
	
	char chaineTemp[2];
	int entierTemp = -1;
	
	if(strlen(reponse) > 0) {
		chaineTemp[0] = reponse[0];
		entierTemp = atoi(chaineTemp);
	}
	
	switch(entierTemp) {
		case 0:
			req.propCoup = POSE;
			break;
		case 1:
			req.propCoup = DEPLACE;
			break;
		case 2:
			req.propCoup = PRISE;
			break;
		case 4:
			req.propCoup = GAGNE;
			break;
		default:
			break;
	}
	
	if(strlen(reponse) > 6) {
		chaineTemp[0] = reponse[3];
		entierTemp = atoi(chaineTemp);
		positionDepart.ligne = retournerTypLigne(entierTemp);
		chaineTemp[0] = reponse[5];
		entierTemp = atoi(chaineTemp);
		positionDepart.colonne = retournerTypColonne(entierTemp);
		req.caseDepart = positionDepart;
	}
	if(strlen(reponse) > 12) {
		chaineTemp[0] = reponse[9];
		entierTemp = atoi(chaineTemp);
		positionArrivee.ligne = retournerTypLigne(entierTemp);
		chaineTemp[0] = reponse[11];
		entierTemp = atoi(chaineTemp);
		positionArrivee.colonne = retournerTypColonne(entierTemp);
		req.caseArrivee = positionArrivee;
	}
	if(strlen(reponse) > 18) {
		chaineTemp[0] = reponse[15];
		entierTemp = atoi(chaineTemp);
		positionPiecePrise2.ligne = retournerTypLigne(entierTemp);
		chaineTemp[0] = reponse[17];
		entierTemp = atoi(chaineTemp);
		positionPiecePrise2.colonne = retournerTypColonne(entierTemp);
		req.piecePrise2 = positionPiecePrise2;
	}
	
	return req;
	
}

/*
 * Fonction :    retournerIntLigne
 *
 * Parametres :  TypLigne ligne, ligne demandee
 *
 * Retour :      int, numero de ligne en entier
 *
 * Description : converti une ligne (TypLigne) en une ligne de type
 *               entier
 *
 */
int retournerIntLigne(TypLigne ligne){
	switch(ligne){
		case LI_ZERO:
			return 0;
		case LI_UN:
			return 1;
		case LI_DEUX:
			return 2;
		case LI_TROIS:
			return 3;
		case LI_QUATRE:
			return 4;
		default:
			return 5;
	}
}

/*
 * Fonction :    retournerIntColonne
 *
 * Parametres :  TypColonne colonne, colonne demandee
 *
 * Retour :      int, numero de colonne en entier
 *
 * Description : converti une colonne (TypColonne) en une colonne de type
 *               entier
 *
 */
int retournerIntColonne(TypColonne colonne){
	switch(colonne){
		case CO_ZERO:
			return 0;
		case CO_UN:
			return 1;
		case CO_DEUX:
			return 2;
		case CO_TROIS:
			return 3;
		case CO_QUATRE:
			return 4;
		case CO_CINQ:
			return 5;
		default:
			return 6;
	}
}

/*
 * Fonction :    typCoupReqToString
 *
 * Parametres :  TypCoupReq req, requete recue de l'arbitre
 *               char* reqToString, chaine qui sera envoyee au moteur Java
 *
 * Retour :      rien
 *
 * Description : converti une requete recue de l'arbitre en une chaine de
 *               caracteres, qui sera envoyee au moteur Java
 *
 */
void typCoupReqToString(TypCoupReq req, char* reqToString) {
	
	char charTemp[2];
	int entierTemp = -1;
	
	switch(req.propCoup) {
		case POSE:
			entierTemp = 0;
			break;
		case DEPLACE:
			entierTemp = 1;
			break;
		case PRISE:
			entierTemp = 2;
			break;
		case NULLE:
			entierTemp = 3;
			break;
		case GAGNE:
			entierTemp = 4;
			break;
		default:
			fprintf(stdout, "L'arbitre a envoye n'importe quoi\n");
			break;
	}
	sprintf(charTemp, "%d", entierTemp);
	
	strcat(reqToString, charTemp);
	strcat(reqToString, "-");
	
	strcat(reqToString, "[");
	sprintf(charTemp, "%d", retournerIntLigne(req.caseDepart.ligne));
	strcat(reqToString, charTemp);
	strcat(reqToString, ",");
	sprintf(charTemp, "%d", retournerIntColonne(req.caseDepart.colonne));
	strcat(reqToString, charTemp);
	strcat(reqToString, "]");

	strcat(reqToString, "-");
	
	strcat(reqToString, "[");
	sprintf(charTemp, "%d", retournerIntLigne(req.caseArrivee.ligne));
	strcat(reqToString, charTemp);
	strcat(reqToString, ",");
	sprintf(charTemp, "%d", retournerIntColonne(req.caseArrivee.colonne));
	strcat(reqToString, charTemp);
	strcat(reqToString, "]");

	strcat(reqToString, "-");
	
	strcat(reqToString, "[");
	sprintf(charTemp, "%d", retournerIntLigne(req.piecePrise2.ligne));
	strcat(reqToString, charTemp);
	strcat(reqToString, ",");
	sprintf(charTemp, "%d", retournerIntColonne(req.piecePrise2.colonne));
	strcat(reqToString, charTemp);
	strcat(reqToString, "]");

}


/*
 * Fonction :    traitementSiErreur
 *
 * Parametres :  RetourFonction retour, code d'erreur
 *
 * Retour :      rien
 *
 * Description : affiche un message d'erreur en fonction du code d'erreur
 *               donne en parametre
 *
 */
void traitementSiErreur(RetourFonction retour){
	
	switch(retour){
		case ECHEC_CONNEXION :
			fprintf(stderr, "C : Echec de connexion a l'arbitre\n");
			break;
		case ECHEC_DECONNEXION :
			fprintf(stderr, "C : Probl�me de deconnexion a l'arbitre\n");
			break;
		case ECHEC_ENVOI_IDENTIFICATION :
			fprintf(stderr, "C : Echec d'identification sur l'arbitre\n");
			break;
		case ECHEC_RECEPTION_IDENTIFICATION :
			fprintf(stderr, "C : Echec reception identification de l'arbitre\n");
			break;
		case ECHEC_CONFIRMATION_IDENTIFICATION :
			fprintf(stderr, "C : Erreur identification wesson\n");
			break;
		case ECHEC_ENVOI_NOUVELLE_PARTIE :
			fprintf(stderr, "C : Echec de demande de nouvelle partie sur l'arbitre\n");
			break;
		case ECHEC_RECEPTION_NOUVELLE_PARTIE :
			fprintf(stderr, "C : Echec reception de la demande de nouvelle partie de l'arbitre\n");
			break;
		case ECHEC_RETOUR_NOUVELLE_PARTIE :
			fprintf(stderr, "C : Erreur de la demande de nouvelle partie\n");
			break;
		case ECHEC_CODE_FINTOURNOI_INCONNU :
			fprintf(stderr, "C : Code de reponse de 'finTournoi' inconnu\n");
			break;
		case ECHEC_CODE_PREMIER_INCONNU :
			fprintf(stderr, "C : Code de reponse de 'premier' inconnu\n");
			break;
		case ECHEC_ENVOI_DERNIER_COUP :
			fprintf(stderr, "C : Echec d'envoi du dernier coup\n");
			break;
		case ECHEC_RECEPTION_DERNIER_COUP :
			fprintf(stderr, "C : Echec reception de la confirmation du dernier coup\n");
			break;
		case ECHEC_RECEPTION_DERNIER_COUP_ADVERSAIRE :
			fprintf(stderr, "C : Echec reception de la confirmation du dernier coup\n");
			break;
		case ECHEC_CONFIRMATION_DERNIER_COUP :
			fprintf(stderr, "C : Echec de l'envoi du dernier coup\n");
			break;
		case ECHEC_CONFIRMATION_DERNIER_COUP_ADVERSAIRE :
			fprintf(stderr, "C : Echec de l'envoi du dernier coup\n");
			break;
		case ECHEC_RECEPTION_COUP_VALIDE_ADVERSAIRE :
			fprintf(stderr, "C : Echec reception du coup de l'adversaire\n");
			break;
		case ECHEC_CODE_VALIDCOUP_INCONNU :
			fprintf(stderr, "C : Code de reponse pour 'validCoup' inconnu\n");
			break;
		case ECHEC_CODE_PROPCOUP_INCONNU :
			fprintf(stderr, "C : Code de reponse pour 'propCoup' inconnu\n");
			break;
		case RETOUR_PARTIE_NULLE :
			fprintf(stderr, "C : %d coups sans prise : partie nulle\n", NB_COUPS_MAX);
			break;
		case RETOUR_TIMEOUT_FIN_PARTIE :
			fprintf(stderr, "C : Temps d'attente depasse : fin de partie\n");
			break;
		case RETOUR_TRICHE_FIN_PARTIE :
			fprintf(stderr, "C : Triche detectee : fin de partie\n");
			break;
		case RETOUR_VICTOIRE_NOUVELLE_PARTIE :
			fprintf(stdout, "\n!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!\n");
			fprintf(stdout, "!!!!!!!!!!!! Victoire de l'IA !!!!!!!!!!\n");
			fprintf(stdout, "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!\n\n");
			break;
		case RETOUR_DEFAITE_NOUVELLE_PARTIE :
			fprintf(stdout, "\n!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!\n");
			fprintf(stdout, "!!!!!!!!!!!! Defaite de l'IA !!!!!!!!!!!\n");
			fprintf(stdout, "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!\n\n");
			break;
		case RETOUR_NULLE_NOUVELLE_PARTIE :
			fprintf(stdout, "\n!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!\n");
			fprintf(stdout, "!!!!! Match nul entre les 2 joueurs !!!!\n");
			fprintf(stdout, "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!\n");
			break;
		case FIN_DE_JEU :
			fprintf(stdout, "C : Toutes les parties ont deja ete effectuees\n");
			break;
		case CODE_OK :
			break;
		case ECHEC_ENVOI_MOT_PASSE :
			fprintf(stderr, "C : Echec lors de l'envoi du mot de passe au moteur Java\n");
			break;
		case ECHEC_RECEPTION_MOT_PASSE :
			fprintf(stderr, "C : Echec lors de la reception de la validation du mot de passe par le moteur Java\n");
			break;
		case ECHEC_CONFIRMATION_MOT_PASSE :
			fprintf(stderr, "C : Le mot de passe envoye au moteur Java est incorrect\n");
			break;
		case ECHEC_ENVOI_COMMENCE :
			fprintf(stderr, "C : Echec lors de l'envoi au moteur Java si il commence\n");
			break;
		default:
			fprintf(stderr, "C : Code d'erreur inconnu\n");
	}

	return;
	
}

/*
 * Fonction :    main
 *
 * Parametres :  int argc, arguments du programme
 *               char **argv, arguments du programme
 *
 * Retour :      int
 *
 * Description : fonction principale
 *
 */
int main(int argc, char **argv){
	
	fprintf(stdout, "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!\n");
	fprintf(stdout, "!!!!!!!!!!!! Projet MOIA-SCS : Jeu de Yote !!!!!!!!!!!\n");
	fprintf(stdout, "!! Realise par Guillaume MONTAVON et Benoit MEILHAC !!\n");
	fprintf(stdout, "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!\n\n");
	
	if(argc != 3 && argc != 4 && argc != 5){
		fprintf(stderr, "Usage : ./connexionArbitre adresse_arbitre port_arbitre\n");
		fprintf(stderr, "   ou : ./connexionArbitre adresse_arbitre port_arbitre portMoteurJava\n");
		fprintf(stderr, "   ou : ./connexionArbitre adresse_arbitre port_arbitre port_moteur_java adresse_moteur_java\n");
		exit(1);
	}
	
	char machine[TAIL_CHAIN];
	char moteurJava[TAIL_CHAIN];
	strcpy(machine, argv[1]);
	strcpy(moteurJava, "localhost");
	int port = atoi(argv[2]);
	int portMoteurJava;
	if(argc == 4 || argc == 5)
		portMoteurJava = atoi(argv[3]);
	else
		portMoteurJava = 2202;
	
	if(argc == 5)
		strcpy(moteurJava, argv[4]);
	else
		strcpy(moteurJava, "localhost");
	int socket;
	int socketMoteurJava;
	int idJoueur;
	RetourFonction retour;
	
	do {
		fprintf(stdout, "\nC : Connexion au moteur Java\n");
		retour = creationConnexion(moteurJava, portMoteurJava, &socketMoteurJava);
		traitementSiErreur(retour);
		if(retour != CODE_OK)
			sleep(1);
	} while(retour != CODE_OK);
	
	do {
		retour = identificationMoteurJava(socketMoteurJava);
		traitementSiErreur(retour);
		if(retour != CODE_OK)
			sleep(1);
	} while(retour != CODE_OK);
	
	
	do {
		retour = creationConnexion(machine, port, &socket);
		traitementSiErreur(retour);
		if(retour != CODE_OK)
			sleep(1);
	} while(retour != CODE_OK);

	do {
		retour = identification(socket, &idJoueur);
		traitementSiErreur(retour);
		if(retour != CODE_OK)
			sleep(1);
	} while(retour != CODE_OK);

	do {
		retour = demandeNouvellePartie(socket, socketMoteurJava, idJoueur);
		traitementSiErreur(retour);
		if(retour != CODE_OK && retour != FIN_DE_JEU && retour != RETOUR_NULLE_NOUVELLE_PARTIE 
		  && retour != RETOUR_VICTOIRE_NOUVELLE_PARTIE && retour != RETOUR_DEFAITE_NOUVELLE_PARTIE)
			sleep(1);
		if(retour != FIN_DE_JEU)
			envoiMoteurJavaRestart(socketMoteurJava);
	} while(retour != FIN_DE_JEU);
	
	fprintf(stdout, "C : Deconnexion du moteur Java\n");
	deconnexion(socketMoteurJava);

	return 0;
}

#include "protocolArbitre.h"
#include "fonctionsSocket.h"
#include "connexionArbitre.h"


void clearScanf(void){
	char c;
	while((c = getchar()) != EOF && c != '\n');

	return;
}

RetourFonction creationConnexion(char *machine, int port, int *sock){
	fprintf(stdout, "Procedure de connexion\n");

	(*sock) = socketClient(machine, port);
	if((*sock) < 0)
		return ECHEC_CONNEXION;

	fprintf(stdout, "Connexion effectuee\n");

	return CODE_OK;
}

RetourFonction deconnexion(int sock){
	int err;

	fprintf(stdout, "Procedure de deconnexion\n");

	shutdown(sock, 2);
	err = close(sock);
	
	if(err < 0)
		return ECHEC_DECONNEXION;

	fprintf(stdout, "Deconnexion effectuee\n");

	return CODE_OK;
}

RetourFonction identification(int sock, int *identifiant){
	TypIdentificationReq req;
	TypIdentificationRep rep;
	char login[TAIL_CHAIN] = "bmeilhac";
	int err;
	
	req.idRequest = IDENTIFICATION;
	strcpy(req.nom, login);

	fprintf(stdout, "Procedure d'identification\n");

	err = send(sock, (void*) &req, sizeof(req), 0);
	if(err < 0)
		return ECHEC_ENVOI_IDENTIFICATION;

	err = recv(sock, (void*) &rep, sizeof(rep), 0);
	if(err < 0)
		return ECHEC_RECEPTION_IDENTIFICATION;

	if(rep.err != ERR_OK)
		return ECHEC_CONFIRMATION_IDENTIFICATION;

	(*identifiant) = rep.joueur;

	fprintf(stdout, "Identification effectuee\n");
	
	return CODE_OK;
}

RetourFonction demandeNouvellePartie(int sock, int sockMoteurJava, int identifiant){
	TypPartieReq req;
	TypPartieRep rep;
	int err;

	req.idRequest = PARTIE;
	req.joueur = identifiant;

	fprintf(stdout, "Procedure de demande de nouvelle partie\n");

	err = send(sock, (void*) &req, sizeof(req), 0);
	if(err < 0)
		return ECHEC_ENVOI_NOUVELLE_PARTIE;

	err = recv(sock, (void*) &rep, sizeof(rep), 0);
	if(err < 0)
		return ECHEC_RECEPTION_NOUVELLE_PARTIE;

	if(rep.err != ERR_OK)
		return ECHEC_RETOUR_NOUVELLE_PARTIE;

	switch(rep.finTournoi){
		case VRAI:
			deconnexion(sock);
			return FIN_DE_JEU;
		case FAUX:
			fprintf(stdout, "Demande de nouvelle partie effectuee\n");
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

RetourFonction jouerUnCoup(int sock, int sockMoteurJava, int *numeroCoup){
	fprintf(stdout, "L'IA commence a jouer un coup\n");

	TypCoupReq req;
	int err;
	//R�cup�ration du coup � jouer
	char stringReq[TAIL_CHAIN] = "";
	err = recv(sockMoteurJava, (void*) stringReq, TAIL_CHAIN, 0);
	if(err < 0)
		return ECHEC_RECEPTION_DERNIER_COUP;
	req = stringToTypCoupReq(stringReq);
	
	TypCoupRep rep;
	/*
	TypPosition positionDepart;
	TypPosition positionArrivee;
	*/

	req.idRequest = COUP;

	if((*numeroCoup) >= 50)
		req.propCoup = NULLE;
	/*else //TODO : type coup si pas NULLE
		req.propCoup = POSE;

	// traitement en fonction du coup a jouer
	switch(req.propCoup){
		case POSE:
			positionDepart.ligne = retournerTypLigne(-1);
			positionDepart.colonne = retournerTypColonne(-1);
			req.caseDepart = positionDepart;

			//TODO : positon arriv�e
			positionArrivee.ligne = retournerTypLigne(0);
			positionArrivee.colonne = retournerTypColonne(0);
			req.caseArrivee = positionArrivee;

			break;
		case DEPLACE:
			//TODO : positon d�part
			positionDepart.ligne = retournerTypLigne(0);
			positionDepart.colonne = retournerTypColonne(0);
			req.caseDepart = positionDepart;

			//TODO : positon arriv�e
			positionArrivee.ligne = retournerTypLigne(1);
			positionArrivee.colonne = retournerTypColonne(0);
			req.caseArrivee = positionArrivee;

			break;
		case PRISE://TODO : Prise deuxi�me obligatoire ?
			//TODO : positon d�part
			positionDepart.ligne = retournerTypLigne(0);
			positionDepart.colonne = retournerTypColonne(0);
			req.caseDepart = positionDepart;

			//TODO : positon arriv�e
			positionArrivee.ligne = retournerTypLigne(2);
			positionArrivee.colonne = retournerTypColonne(0);
			req.caseArrivee = positionArrivee;


			//TODO : position piecePrise2
			TypPosition positionPiecePrise2;
			positionPiecePrise2.ligne = retournerTypLigne(3);
			positionPiecePrise2.colonne = retournerTypColonne(0);

			(*numeroCoup) = 0;

			break;
		case GAGNE: //TODO : Prise ??
			//TODO : positon d�part
			positionDepart.ligne = retournerTypLigne(0);
			positionDepart.colonne = retournerTypColonne(0);
			req.caseDepart = positionDepart;

			//TODO : positon arriv�e
			positionArrivee.ligne = retournerTypLigne(2);
			positionArrivee.colonne = retournerTypColonne(0);
			req.caseArrivee = positionArrivee;

			break;
		case NULLE:
			return RETOUR_PARTIE_NULLE;
			break;
		default:
			return ECHEC_CODE_PROPCOUP_INCONNU;

	}
	*/
	if(req.propCoup == PRISE)
		(*numeroCoup) = 0;
	else
		(*numeroCoup) ++;
	
	req.numeroDuCoup  = (*numeroCoup);

	// envoi du coup joue par l'ia
	err = send(sock, (void*) &req, sizeof(req), 0);
	if(err < 0){
		fprintf(stderr, "Echec d'envoi du dernier coup\n");
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
			else
				fprintf(stdout, "Coup valide, c'est le tour de l'adversaire\n");
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

	return CODE_OK;
}

RetourFonction envoiMoteurJavaDebute(int sock, int commence) {

	int err;
	char envoi[TAIL_CHAIN] = "";
	if(commence == 1) {
		strcpy(envoi, "commence\n");
		fprintf(stdout, "Le joueur va commencer\n");
	}
	else {
		strcpy(envoi, "attend\n");
		fprintf(stdout, "Le joueur va attendre le coup de l'adversaire\n");
	}
	err = send(sock, (void*) envoi, strlen(envoi), 0);
	if(err < 0){
		fprintf(stderr, "Echec d'envoi au moteur Java si il commence\n");
		return ECHEC_ENVOI_COMMENCE;
	}
	
	return CODE_OK;
	
}

RetourFonction envoiMoteurJavaRestart(int sock) {

	int err;
	char envoi[TAIL_CHAIN] = "";
	strcpy(envoi, "restart");
	fprintf(stdout, "Une nouvelle partie commence\n");

	err = send(sock, (void*) envoi, strlen(envoi), 0);
	if(err < 0){
		fprintf(stderr, "Echec d'envoi au moteur Java qu'il doit redemarrer une partie\n");
		return ECHEC_ENVOI_COMMENCE;
	}
	
	return CODE_OK;
	
}

RetourFonction recevoirUnCoup(int sock, int sockMoteurJava, int *numeroCoup){
	TypCoupReq req;
	TypCoupRep rep;
	int err;

	// reception de l'arbitre concernant la validite du coup de l'adversaire
	err = recv(sock, (void*) &rep, sizeof(rep), 0);
	if(err < 0)
		return ECHEC_RECEPTION_DERNIER_COUP_ADVERSAIRE;

	if(rep.err != ERR_OK)
		return ECHEC_CONFIRMATION_DERNIER_COUP_ADVERSAIRE;

	// traitement de la validite du coup
	switch(rep.validCoup){
		case VALID:
			if(req.propCoup == GAGNE)
				return RETOUR_DEFAITE_NOUVELLE_PARTIE;
			else
				fprintf(stdout, "Coup adversaire valide, reception de ce coup\n");
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

	// reception du coup de l'adversaire
	err = recv(sock, (void*) &req, sizeof(req), 0);
	if(err < 0)
		return ECHEC_RECEPTION_COUP_VALIDE_ADVERSAIRE;

	if(req.propCoup == PRISE)
		(*numeroCoup) = 0;
	else
		(*numeroCoup) ++;
	
	//traitement de la reception avec le moteur java
	char reqToString[TAIL_CHAIN] = "";
	typCoupReqToString(req, reqToString);

	printf("\nEnvoi au moteur de %s\n", reqToString);
	err = send(sockMoteurJava, (void*) &reqToString, strlen(reqToString), 0);
	if(err < 0){
		fprintf(stderr, "Echec d'envoi du dernier coup au moteur Java\n");
		return ECHEC_ENVOI_DERNIER_COUP;
	}

	return CODE_OK;
}

RetourFonction identificationMoteurJava(int sock) {
	
	int err;
	char motPasse[TAIL_CHAIN];
	strcpy(motPasse, "chaussette");
	// envoi du mot de passe
	err = send(sock, (void*) motPasse, strlen(motPasse), 0);
	if(err < 0){
		fprintf(stderr, "Echec d'envoi du mot de passe\n");
		return ECHEC_ENVOI_MOT_PASSE;
	}
	
	char repMotPasse[TAIL_CHAIN];
	// reception du moteur java
	err = recv(sock, (void*) &repMotPasse, TAIL_CHAIN, 0);
	if(err < 0)
		return ECHEC_RECEPTION_MOT_PASSE;

	printf("mot de passe : %s\n", repMotPasse);
	
	if(strcmp(repMotPasse, "OK") != 0)
		return ECHEC_CONFIRMATION_MOT_PASSE;
	else
		fprintf(stdout, "identification aupres du moteur Java effectuee\n");
	
	return CODE_OK;

}

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
		case 3:
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

int retournerIntColonne(TypLigne colonne){
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
		case GAGNE:
			entierTemp = 3;
			break;
		default:
			entierTemp = -1;
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

void traitementSiErreur(RetourFonction retour){
	
	switch(retour){
		case ECHEC_CONNEXION :
			fprintf(stderr, "Echec de connexion a l'arbitre\n");
			break;
		case ECHEC_DECONNEXION :
			fprintf(stderr, "Probl�me de deconnexion a l'arbitre\n");
			break;
		case ECHEC_ENVOI_IDENTIFICATION :
			fprintf(stderr, "Echec d'identification sur l'arbitre\n");
			break;
		case ECHEC_RECEPTION_IDENTIFICATION :
			fprintf(stderr, "Echec reception identification de l'arbitre\n");
			break;
		case ECHEC_CONFIRMATION_IDENTIFICATION :
			fprintf(stderr, "Erreur identification wesson\n");
			break;
		case ECHEC_ENVOI_NOUVELLE_PARTIE :
			fprintf(stderr, "Echec de demande de nouvelle partie sur l'arbitre\n");
			break;
		case ECHEC_RECEPTION_NOUVELLE_PARTIE :
			fprintf(stderr, "Echec reception de la demande de nouvelle partie de l'arbitre\n");
			break;
		case ECHEC_RETOUR_NOUVELLE_PARTIE :
			fprintf(stderr, "Erreur de la demande de nouvelle partie\n");
			break;
		case ECHEC_CODE_FINTOURNOI_INCONNU :
			fprintf(stderr, "Code de reponse de 'finTournoi' inconnu\n");
			break;
		case ECHEC_CODE_PREMIER_INCONNU :
			fprintf(stderr, "Code de reponse de 'premier' inconnu\n");
			break;
		case ECHEC_ENVOI_DERNIER_COUP :
			fprintf(stderr, "Echec d'envoi du dernier coup\n");
			break;
		case ECHEC_RECEPTION_DERNIER_COUP :
			fprintf(stderr, "Echec reception de la confirmation du dernier coup\n");
			break;
		case ECHEC_RECEPTION_DERNIER_COUP_ADVERSAIRE :
			fprintf(stderr, "Echec reception de la confirmation du dernier coup\n");
			break;
		case ECHEC_CONFIRMATION_DERNIER_COUP :
			fprintf(stderr, "Echec de l'envoi du dernier coup\n");
			break;
		case ECHEC_CONFIRMATION_DERNIER_COUP_ADVERSAIRE :
			fprintf(stderr, "Echec de l'envoi du dernier coup\n");
			break;
		case ECHEC_RECEPTION_COUP_VALIDE_ADVERSAIRE :
			fprintf(stderr, "Echec reception du coup de l'adversaire\n");
			break;
		case ECHEC_CODE_VALIDCOUP_INCONNU :
			fprintf(stderr, "Code de reponse pour 'validCoup' inconnu\n");
			break;
		case ECHEC_CODE_PROPCOUP_INCONNU :
			fprintf(stderr, "Code de reponse pour 'propCoup' inconnu\n");
			break;
		case RETOUR_PARTIE_NULLE :
			fprintf(stderr, "50 coups sans prise : partie nulle\n");
			break;
		case RETOUR_TIMEOUT_FIN_PARTIE :
			fprintf(stderr, "Temps d'attente depasse : fin de partie\n");
			break;
		case RETOUR_TRICHE_FIN_PARTIE :
			fprintf(stderr, "Triche detectee : fin de partie\n");
			break;
		case RETOUR_VICTOIRE_NOUVELLE_PARTIE :
			fprintf(stdout, "Victoire de l'IA !!!\n");
			break;
		case RETOUR_DEFAITE_NOUVELLE_PARTIE :
			fprintf(stdout, "defaite de l'IA.\n");
			break;
		case FIN_DE_JEU :
			fprintf(stdout, "Toutes les parties ont deja ete effectuees\n");
			break;
		case CODE_OK :
			break;
		case ECHEC_ENVOI_MOT_PASSE :
			fprintf(stderr, "Echec lors de l'envoi du mot de passe au moteur Java\n");
			break;
		case ECHEC_RECEPTION_MOT_PASSE :
			fprintf(stderr, "Echec lors de la reception de la validation du mot de passe par le moteur Java\n");
			break;
		case ECHEC_CONFIRMATION_MOT_PASSE :
			fprintf(stderr, "Le mot de passe envoye au moteur Java est incorrect\n");
			break;
		case ECHEC_ENVOI_COMMENCE :
			fprintf(stderr, "Echec lors de l'envoi au moteur Java si il commence\n");
			break;
		default:
			fprintf(stderr, "Code d'erreur inconnu\n");
	}

	return;
	
}

int main(int argc, char **argv){
	
	if(argc != 3 && argc != 4){
		fprintf(stderr, "Usage : ./connexionArbitre machine port\n");
		fprintf(stderr, "ou : ./connexionArbitre machine port portMoteurJava\n");
		exit(1);
	}

	char machine[TAIL_CHAIN];
	char moteurJava[TAIL_CHAIN];
	strcpy(machine, argv[1]);
	strcpy(moteurJava, "localhost");
	int port = atoi(argv[2]);
	int portMoteurJava;
	if(argc == 4)
		portMoteurJava = atoi(argv[3]);
	else
		portMoteurJava = 2202;

	int socket;
	int socketMoteurJava;
	int idJoueur;
	RetourFonction retour;
	
	do {
		fprintf(stdout, "Connexion au moteur Java\n");
		retour = creationConnexion(moteurJava, portMoteurJava, &socketMoteurJava);
		traitementSiErreur(retour);
	} while(retour != CODE_OK);

	retour = identificationMoteurJava(socketMoteurJava);
	traitementSiErreur(retour);
	
	do {
		retour = creationConnexion(machine, port, &socket);
		traitementSiErreur(retour);
	} while(retour != CODE_OK);

	do {
		retour = identification(socket, &idJoueur);
		traitementSiErreur(retour);
	} while(retour != CODE_OK);

	//do {
		retour = demandeNouvellePartie(socket, socketMoteurJava, idJoueur);
		traitementSiErreur(retour);
		/*
		if(retour != FIN_DE_JEU)
		envoiMoteurJavaRestart(socketMoteurJava);
	} while(retour != FIN_DE_JEU);
	*/
	fprintf(stdout, "Deconnexion du moteur Java\n");
	deconnexion(socketMoteurJava);

	return 0;
}

#include "protocolArbitre.h"
#include "fonctionsSocket.h"


void clearScanf(void){
    char c;
    while((c = getchar()) != EOF && c != '\n');

    return;
}

RetourFonction creationConnexion(int *sock){
//	char machine[TAIL_CHAIN] = "localhost";
//	int port = 2001;

	fprintf(stdout, "Proc�dure de connexion � l'arbitre\n");

    fprintf(stdout, "Nom de la machine distante ?\n-> ");
    fscanf(stdin, "%s", machine);
    clearScanf();

    fprintf(stdout, "Num�ro du port ?\n-> ");
    fscanf(stdin, "%d", sock);
    clearScanf();

	sock = socketClient(machine, port);
	if(sock < 0){
		fprintf(stderr, "Echec de connexion � l'arbitre\n");
		return ECHEC_CONNEXION;
	}

	fprintf(stdout, "Connexion effectu�e\n");

	return CODE_OK;
}

RetourFonction deconnexion(int sock){
	int err;

	fprintf(stdout, "Proc�dure de d�connexion � l'arbitre\n");

	shutdown(sock, 2);
	err = close(sock);
	
	if(err < 0){
		fprintf(stderr, "Probl�me de d�connexion � l'arbitre\n");
		return ECHEC_DECONNEXION;
	}

	fprintf(stdout, "D�connexion effectu�e\n");

	return CODE_OK;
}

RetourFonction identification(int sock, int *identifiant){
	TypIdentificationReq req;
	TypIdentificationRep rep;
	char login[TAIL_CHAIN] = "bmeilhac";
	int err;
	
	req.idRequest = IDENTIFICATION;
	req.nom = login;

	fprintf(stdout, "Proc�dure d'identification\n");

	err = send(sock, (void*) &req, sizeof(req), 0);
	if(err < 0){
		fprintf(stderr, "Echec d'identification sur l'arbitre\n");
		return ECHEC_ENVOI_IDENTIFICATION;
	}

	err = recv(sock, (void*) &rep, sizeof(rep), 0);
	if(err < 0){
		fprintf(stderr, "Echec r�ception identification de l'arbitre\n");
		return ECHEC_RECEPTION_IDENTIFICATION;
	}

	if(rep.err != ERR_OK){
		fprintf(stderr, "Erreur identification wesson\n");
		exit(3);
	}

	identifiant = rep.joueur;

	fprintf(stdout, "Identification effectu�e\n");
	
	return CODE_OK;
}

RetourFonction demandeNouvellePartie(int sock, int identifiant){
	TypPartieReq req;
	TypPartieRep rep;
	int err;

	req.idRequest = PARTIE;
	req.joueur = identifiant;

	fprintf(stdout, "Proc�dure de demande de nouvelle partie\n");

	err = send(sock, (void*) &req, sizeof(req), 0);
	if(err < 0){
		fprintf(stderr, "Echec de demande de nouvelle partie sur l'arbitre\n");
		return ECHEC_ENVOI_NOUVELLE_PARTIE;
	}

	err = recv(sock, (void*) &rep, sizeof(rep), 0);
	if(err < 0){
		fprintf(stderr, "Echec r�ception de la demande de nouvelle partie de l'arbitre\n");
		return ECHEC_RECEPTION_NOUVELLE_PARTIE;
	}

	if(rep.err != ERR_OK){
		fprintf(stderr, "Erreur de la demande de nouvelle partie\n");
		return ECHEC_RETOUR_NOUVELLE_PARTIE;
	}

	switch(rep.finTournoi){
		case VRAI:
			fprintf(stdout, "Toutes les parties ont d�j� �t� effectu�es\n");
			deconnexion(sock);
			break;
		case FAUX:
			fprintf(stdout, "Demande de nouvelle partie effectu�e\n");
			switch(rep.premier){
				case VRAI:
					debutePartie(sock, identifiant, rep.adversaire);
					break;
				case FAUX:
					attendPremierCoup(sock, identifiant, rep.adversaire);
					break;
				default:
					fprintf(stderr, "Code de r�ponse de premier inconnu\n");
					return ECHEC_CODE_PREMIER_INCONNU; 
			}
		default:
			fprintf(stderr, "Code de r�ponse de finTournoi inconnu\n");
			return ECHEC_CODE_FINTOURNOI_INCONNU;
	}

	fprintf(stdout, "Partie termin�e\n");

	return CODE_OK;
}

int main(void){
	int socket;
	int idJoueur;
	creationConnexion(&socket);
	identification(socket, &idJoueur);
	demandeNouvellePartie(socket);


	return 0;
}

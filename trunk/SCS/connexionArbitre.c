#include "protocolArbitre.h"
#include "fonctionsSocket.h"


typedef enum {
	ECHEC_CONNEXION,
	ECHEC_DECONNEXION,
	ECHEC_ENVOI_IDENTIFICATION,
	ECHEC_RECEPTION_IDENTIFICATION,
	ECHEC_ENVOI_NOUVELLE_PARTIE,
	ECHEC_RECEPTION_NOUVELLE_PARTIE,
	CODE_FINTOURNOI_INCONNU,
	CODE_PREMIER_INCONNU,


void creationConnexion(int *sock){
	char machine[TAIL_CHAIN] = "localhost";
	int port = 2001;

	fprintf(stdout, "Procédure de connexion à l'arbitre\n");

	sock = socketClient(machine, port);
	if(sock < 0){
		fprintf(stderr, "Echec de connexion à l'arbitre\n");
		exit(1);
	}

	fprintf(stdout, "Connexion effectuée\n");

	return;
}

void deconnexion(int sock){
	int err;

	fprintf(stdout, "Procédure de déconnexion à l'arbitre\n");

	shutdown(sock, 2);
	err = close(sock);
	
	if(err < 0){
		fprintf(stderr, "Problème de déconnexion à l'arbitre\n");
		exit(1);
	}

	fprintf(stdout, "Déconnexion effectuée\n");

	return;
}

void identification(int sock, int *identifiant){
	TypIdentificationReq req;
	TypIdentificationRep rep;
	char login[TAIL_CHAIN] = "bmeilhac";
	int err;
	
	req.idRequest = IDENTIFICATION;
	req.nom = login;

	fprintf(stdout, "Procédure d'identification\n");

	err = send(sock, (void*) &req, sizeof(req), 0);
	if(err < 0){
		fprintf(stderr, "Echec d'identification sur l'arbitre\n");
		exit(1);
	}

	err = recv(sock, (void*) &rep, sizeof(rep), 0);
	if(err < 0){
		fprintf(stderr, "Echec réception identification de l'arbitre\n");
		exit(2);
	}

	if(rep.err != ERR_OK){
		fprintf(stderr, "Erreur identification wesson\n");
		exit(3);
	}

	identifiant = rep.joueur;

	fprintf(stdout, "Identification effectuée\n");
	
	return;
}

void demandeNouvellePartie(int sock, int identifiant){
	TypPartieReq req;
	TypPartieRep rep;
	int err;

	req.idRequest = PARTIE;
	req.joueur = identifiant;

	fprintf(stdout, "Procédure de demande de nouvelle partie\n");

	err = send(sock, (void*) &req, sizeof(req), 0);
	if(err < 0){
		fprintf(stderr, "Echec de demande de nouvelle partie sur l'arbitre\n");
		exit(1);
	}

	err = recv(sock, (void*) &rep, sizeof(rep), 0);
	if(err < 0){
		fprintf(stderr, "Echec réception de la demande de nouvelle partie de l'arbitre\n");
		exit(2);
	}

	if(rep.err != ERR_OK){
		fprintf(stderr, "Erreur de la demande de nouvelle partie\n");
		exit(3);
	}

	switch(rep.finTournoi){
		case VRAI:
			fprintf(stdout, "Toutes les parties ont déjà été effectuées\n");
			deconnexion(sock);
			break;
		case FAUX:
			fprintf(stdout, "Demande de nouvelle partie effectuée\n");
			switch(rep.premier){
				case VRAI:
					debutePartie(sock, identifiant, rep.adversaire);
					break;
				case FAUX:
					attendPremierCoup(sock, identifiant, rep.adversaire);
					break;
				default:
					fprintf(stderr, "Code de réponse de premier inconnu\n");
					exit(4);
			}
		default:
			fprintf(stderr, "Code de réponse de finTournoi inconnu\n");
			exit(5);
	}

	fprintf(stdout, "Partie terminée\n");

	return;
}

int main(){
	int socket;
	int idJoueur;
	creationConnexion(&socket);
	identification(socket, &idJoueur);
	demandeNouvellePartie(socket);


	return 0;
}

#include "protocolArbitre.h"
#include "fonctionsSocket.h"
#include "connexionArbitre.h"


void clearScanf(void){
    char c;
    while((c = getchar()) != EOF && c != '\n');

    return;
}

RetourFonction creationConnexion(char *machine, int port, int *sock){
	fprintf(stdout, "Procedure de connexion a l'arbitre\n");

	(*sock) = socketClient(machine, port);
	if((*sock) < 0){
		fprintf(stderr, "Echec de connexion a l'arbitre\n");
		return ECHEC_CONNEXION;
	}

	fprintf(stdout, "Connexion effectuee\n");

	return CODE_OK;
}

RetourFonction deconnexion(int sock){
	int err;

	fprintf(stdout, "Procedure de deconnexion a l'arbitre\n");

	shutdown(sock, 2);
	err = close(sock);
	
	if(err < 0){
		fprintf(stderr, "Problème de deconnexion a l'arbitre\n");
		return ECHEC_DECONNEXION;
	}

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
	if(err < 0){
		fprintf(stderr, "Echec d'identification sur l'arbitre\n");
		return ECHEC_ENVOI_IDENTIFICATION;
	}

	err = recv(sock, (void*) &rep, sizeof(rep), 0);
	if(err < 0){
		fprintf(stderr, "Echec reception identification de l'arbitre\n");
		return ECHEC_RECEPTION_IDENTIFICATION;
	}

	if(rep.err != ERR_OK){
		fprintf(stderr, "Erreur identification wesson\n");
		return ECHEC_CONFIRMATION_IDENTIFICATION;
	}

	(*identifiant) = rep.joueur;

	fprintf(stdout, "Identification effectuee\n");
	
	return CODE_OK;
}

RetourFonction demandeNouvellePartie(int sock, int identifiant){
	TypPartieReq req;
	TypPartieRep rep;
	int err;

	req.idRequest = PARTIE;
	req.joueur = identifiant;

	fprintf(stdout, "Procedure de demande de nouvelle partie\n");

	err = send(sock, (void*) &req, sizeof(req), 0);
	if(err < 0){
		fprintf(stderr, "Echec de demande de nouvelle partie sur l'arbitre\n");
		return ECHEC_ENVOI_NOUVELLE_PARTIE;
	}

	err = recv(sock, (void*) &rep, sizeof(rep), 0);
	if(err < 0){
		fprintf(stderr, "Echec reception de la demande de nouvelle partie de l'arbitre\n");
		return ECHEC_RECEPTION_NOUVELLE_PARTIE;
	}

	if(rep.err != ERR_OK){
		fprintf(stderr, "Erreur de la demande de nouvelle partie\n");
		return ECHEC_RETOUR_NOUVELLE_PARTIE;
	}

	switch(rep.finTournoi){
		case VRAI:
			fprintf(stdout, "Toutes les parties ont deja ete effectuees\n");
			deconnexion(sock);
			break;
		case FAUX:
			fprintf(stdout, "Demande de nouvelle partie effectuee\n");
			switch(rep.premier){
				case VRAI:
					debutePartie(sock);
					break;
				case FAUX:
					attendPremierCoup(sock);
					break;
				default:
					fprintf(stderr, "Code de reponse de 'premier' inconnu\n");
					return ECHEC_CODE_PREMIER_INCONNU; 
			}
		default:
			fprintf(stderr, "Code de reponse de 'finTournoi' inconnu\n");
			return ECHEC_CODE_FINTOURNOI_INCONNU;
	}

	fprintf(stdout, "Partie terminee\n");

	return CODE_OK;
}

void debutePartie(int sock){
    int numeroCoup = 0;

    while(1){

        jouerUnCoup(sock, &numeroCoup);

        recevoirUnCoup(sock, &numeroCoup);

    }

}

void attendPremierCoup(int sock){
    sock = 0;

    return;
}

RetourFonction jouerUnCoup(int sock, int *numeroCoup){
    fprintf(stdout, "L'IA commence a jouer un coup\n");

    //TODO : récupération du coup à jouer

    TypCoupReq req;
    TypCoupRep rep;
    TypPosition positionDepart;
    TypPosition positionArrivee;
    int err;

    req.idRequest = COUP;

    if((*numeroCoup) >= 50)
        req.propCoup = NULLE;
    else //TODO : type coup si pas NULLE
        req.propCoup = POSE;


    switch(req.propCoup){
        case POSE:
            positionDepart.ligne = retournerTypLigne(-1);
            positionDepart.colonne = retournerTypColonne(-1);;
            req.caseDepart = positionDepart;

            //TODO : positon arrivée
            positionArrivee.ligne = retournerTypLigne(0);
            positionArrivee.colonne = retournerTypColonne(0);
            req.caseArrivee = positionArrivee;

            break;
        case DEPLACE:
            //TODO : positon départ
            positionDepart.ligne = retournerTypLigne(0);
            positionDepart.colonne = retournerTypColonne(0);
            req.caseDepart = positionDepart;

            //TODO : positon arrivée
            positionArrivee.ligne = retournerTypLigne(1);
            positionArrivee.colonne = retournerTypColonne(0);
            req.caseArrivee = positionArrivee;

            break;
        case PRISE://TODO : Prise deuxième obligatoire ?
            //TODO : positon départ
            positionDepart.ligne = retournerTypLigne(0);
            positionDepart.colonne = retournerTypColonne(0);
            req.caseDepart = positionDepart;

            //TODO : positon arrivée
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
            //TODO : positon départ
            positionDepart.ligne = retournerTypLigne(0);
            positionDepart.colonne = retournerTypColonne(0);
            req.caseDepart = positionDepart;

            //TODO : positon arrivée
            positionArrivee.ligne = retournerTypLigne(2);
            positionArrivee.colonne = retournerTypColonne(0);
            req.caseArrivee = positionArrivee;

            break;
        case NULLE:
            fprintf(stderr, "50 coups sans prise : partie nulle\n");
            return RETOUR_PARTIE_NULLE;
            break;
        default:
            fprintf(stderr, "Code de reponse pour 'propCoup' inconnu\n");
            return ECHEC_CODE_PROPCOUP_INCONNU;

    }

    (*numeroCoup) ++;
    req.numeroDuCoup  = (*numeroCoup);


	err = send(sock, (void*) &req, sizeof(req), 0);
	if(err < 0){
		fprintf(stderr, "Echec d'envoi du dernier coup\n");
		return ECHEC_ENVOI_NOUVELLE_PARTIE;
	}

    err = recv(sock, (void*) &rep, sizeof(rep), 0);
	if(err < 0){
		fprintf(stderr, "Echec reception de la confirmation du dernier coup\n");
		return ECHEC_RECEPTION_DERNIER_COUP;
	}

    if(rep.err != ERR_OK){
        fprintf(stderr, "Echec de l'envoi du dernier coup\n");
        return ECHEC_CONFIRMATION_DERNIER_COUP;
    }

    switch(rep.validCoup){
        case VALID:
            if(req.propCoup == GAGNE)
                fprintf(stdout, "Victoire de l'IA !!!\n");
            else
                fprintf(stdout, "Coup valide, c'est le tour de l'adversaire\n");
            break;
        case TIMEOUT:
            fprintf(stderr, "Temps d'attente depasse : fin de partie\n");
            return RETOUR_TIMEOUT_FIN_PARTIE;
            break;
        case TRICHE:
            fprintf(stderr, "Triche detectee : fin de partie\n");
            return RETOUR_TRICHE_FIN_PARTIE;
            break;
        default:
            fprintf(stderr, "Code de reponse pour 'validCoup' inconnu\n");
            return ECHEC_CODE_VALIDCOUP_INCONNU;
    }

    return CODE_OK;
}


RetourFonction recevoirUnCoup(int sock, int *numeroCoup){
/*   TypCoupReq req;
   TypCoupRep rep;
   int err;*/
   sock = 0;
   (*numeroCoup) = 0;


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

int main(int argc, char **argv){
    if(argc != 3){
        fprintf(stderr, "Usage : ./connexionArbitre machine port\n");
        exit(1);
    }

    char machine[TAIL_CHAIN];
    strcpy(machine, argv[1]);
    int port = atoi(argv[2]);

    int socket;
    int idJoueur;

    creationConnexion(machine, port, &socket);
    identification(socket, &idJoueur);
    demandeNouvellePartie(socket, idJoueur);


    return 0;
}

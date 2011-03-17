#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <string.h>
#include "fonctionsSocket.h"

//1 : serveur et connexion ?
//autre : connexion suivant et ensuite serveur
void saisieClavier(int *port, char *nom){
	printf("Saisir le numéro de port : (>1024)\n->");
	scanf("%d", port);

	printf("Saisir le nom de la machine :\n");
	//scanf("%s", nom);
	printf("Par défaut : localhost\n");
	strcpy(nom, "localhost");

	return;
}

void serveur(int numPort){
	char buffer[256];
	int sock, sockTransfert, err;
	printf("Lancement Serveur\n");
	sock = socketServeur(numPort);
	if(sock < 0){
		printf("Problème de création de socket serveur\n");
		exit(2);
	}

	sockTransfert = accept(sock, NULL, NULL);
	if(sockTransfert < 0){
		printf("Problème d'ouverture de socket\n");
		exit(3);
	}

	err = recv(sockTransfert, (void*) buffer, 256, 0);
	if(err < 0){
		printf("Problème de réception dans le buffer\n");
		shutdown(sockTransfert, 2);
		exit(4);
	}

	printf("Message recu : %s\n", buffer);
/*
	err = send(sockTransfert, (void*) buffer, strlen(buffer), 0);
	if(err < 0){
		printf("Problème d'envoi du buffer\n");
		shutdown(sockTransfert, 2);
		exit(4);
	}
*/
	printf("Fin serveur\n");
	shutdown(sockTransfert, 2);
	close(sock);

	return;
}

void client(){
	int sock, err;
	char *buffer = (char*) malloc(256 * sizeof(char));
	char *machine = (char*) malloc(sizeof(char));
	int numPort;
	printf("Lancement client\n");

	buffer = "Test socket!";
	saisieClavier(&numPort, machine);
	printf("%s\n", machine);
	sock = socketClient(machine, numPort);
	if(sock < 0){
		printf("Problème de création de socket client\n");
		exit(5);
	}
/*
	err = recv(sock, (void*) buffer, strlen(buffer), 0);
	if(err < 0){
		printf("Problème de réception dans le buffer\n");
		shutdown(sock, 2);
		exit(6);
	}
*/
	err = send(sock, (void*) buffer, strlen(buffer), 0);
	if(err < 0){
		printf("Problème d'envoi du buffer\n");
		shutdown(sock, 2);
		exit(7);
	}

	printf("Fin client\n");
	shutdown(sock, 2);
	close(sock);

//	free(buffer);
//	free(machine);

	return;
}


int main(int argc, char **argv){
	if(argc != 3){
		printf("Nombre de paramètres insuffisant : %d (2 requis)\n", argc - 1);
		exit(1);
	}
	int numPort = atoi(argv[1]);
	int positionAnneau = atoi(argv[2]);

	if(positionAnneau == 1){
		serveur(numPort);
		client();
	}
	else{
		client();
		serveur(numPort);
	}
	
	return 0;
}



























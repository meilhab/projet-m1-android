/*
 **********************************************************
 *
 *  Programme : fonctionsSocket.h
 *
 *  ecrit par : LP.
 *
 *  resume :    entete des fonction d'initialisation des sockets en mode 
 *              connecte
 *
 *  date :      25 / 01 / 06
 *
 ***********************************************************
 */

/* include generaux */
#include <sys/types.h>
#include <strings.h>
#include <stdio.h>

/* include socket */
#include <sys/socket.h>
#include <netinet/in.h>
#include <netdb.h>
#include <errno.h>

extern int h_errno;


/*
 **********************************************************
 *
 *  Function  : fonctionsSocket.c
 *
 *  resume :    creer la socket du serveur et la retourne
 *              socketServeur ( numero de port )
 *
 ***********************************************************
 */
  int socketServeur ( ushort port );

/*
 **********************************************************
 *
 *  Fonction : socketClient
 *
 *  resume :    fonction de connexion d'une socket au serveur
 *              socketClient( nom de machine serveur, numero de port )
 *
 ***********************************************************
 */

  int socketClient( char  *nomMachine, ushort    port );


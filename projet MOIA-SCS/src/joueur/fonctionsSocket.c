/*
 **********************************************************
 *
 *  Programme : fonctionsSocket.c
 *
 *  ecrit par : LP.
 *
 *  resume :    fonction d'initialisation des sockets en mode 
 *              connecte
 *
 *  date :      25 / 01 / 06
 *
 ***********************************************************
 */

/* include generaux */
#include <sys/types.h>
#include <string.h>
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
  int
socketServeur ( 
              ushort port
              )
{
          
    int             sock,               /* descipteur de la socket */
                    err;	        /* code d'erreur */

    struct sockaddr_in nom;	        /* adresse de la sochet */

    socklen_t      size_addr;  	/* taille de l'adresse d'une socket */


    size_addr = sizeof(struct sockaddr_in);

    /* Creation de la socket, protocole TCP */
    sock = socket( AF_INET, SOCK_STREAM, 0);
    if (sock < 0) {
	perror("testInterServ, erreur de socket\n");
	return(-1);
    }

    /* 
     * Initialisation de l'adresse de la socket 
     */
    nom.sin_family = AF_INET;
    nom.sin_port = htons( port );
    nom.sin_addr.s_addr = INADDR_ANY;
    bzero(nom.sin_zero, 8);

    /*
     * Mode SO_REUSEADDR pour eviter les: "address already in use"
     */
    int on = 1;
    err = setsockopt( sock, SOL_SOCKET, SO_REUSEADDR, &on, sizeof( on));
    if ( err < 0 ) {

      perror("serveur : erreur setsockopt");
    }    

    /* 
     * Attribution de l'adresse a la socket
     */
    err = bind(sock, (struct sockaddr *)&nom, size_addr);
    if (err < 0) {
	perror("testInterServ, erreur sur le bind");
	return(-2);
    }

    /* 
     * Utilisation en socket de controle, puis attente de demandes de 
     * connexion.
     */
    err = listen(sock, 1);
    if (err < 0) {
	perror("testInterServ, erreur dans listen");
	return(-3);
    }

    /*
     * retourne le descripteur de socket
     */
    return sock;
}

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

  int
socketClient(
             char  *nomMachine, 
             ushort    port
            )
{

  int sock,               /* descipteur de la socket */
      err;                /* code d'erreur */

  struct hostent*    host; /* description de la machine serveur */
  struct sockaddr_in nom;  /* adresse de la sochet du serveur */

  socklen_t size_addr_in = sizeof(struct sockaddr_in);


  /* 
   * Creation d'une socket, domaine AF_INET, protocole TCP 
   */
  sock = socket( AF_INET, SOCK_STREAM, 0);
  if (sock<0) {
      perror("fonctionClient : erreur sur la creation de socket");
      return(-1);
  }

  /* Initialisation de l'adresse de la socket */
  nom.sin_family = AF_INET;
  bzero(nom.sin_zero, 8);
  nom.sin_port = htons( port );

  /* 
   * Recherche de l'adresse de la machine
   */
  host = gethostbyname ( nomMachine );
  if ( host == NULL ) {
    
    printf("fonctionClient : erreur gethostbyname %d\n", h_errno );
    return(-2);
  }

  /*
   * Recopie de l'adresse IP
   */
  nom.sin_addr.s_addr = ((struct in_addr *) (host->h_addr_list[0]))->s_addr;
					     
  /* 
   * Connection au serveur 
   */
  err = connect ( sock, (struct sockaddr *)&nom, size_addr_in );
  if ( err<0 ) {
      perror("fonctionClient : erreur a la connection de socket\n");
      return (-3);
  }
 
  /*
   * On retourne le descripteur de socket
   */
  return sock;

}
 


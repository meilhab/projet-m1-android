#!/bin/bash

if [ $# -ne 0 -a $# -ne 2 -a $# -ne 3 ] # si le nombre de paramètres n'est pas 2
then
	echo Ce programme lance le moteur Java ainsi que le joueur
	echo Usage: $0
	echo    ou: $0 adresse_arbitre port_arbitre
	echo    ou: $0 adresse_arbitre port_arbitre port_moteur_Java
	exit 1
fi

if [ $# -eq 2 -o $# -eq 3 ]
then
	portArbitre=$2
	adresseArbitre=$1
else
	portArbitre=2222
	adresseArbitre=localhost
fi

if [ $# -eq 3 ]
then
	portMoteurJava=$3
else
	portMoteurJava=2202
fi

adresseMoteurJava=localhost

#export LD_LIBRARY_PATH=/applis/sicstus-3.11.2/lib/
#export CLASSPATH=.:bin/moteurJava:/applis/sicstus-3.11.2/lib/sicstus-3.11.2/bin/jasper.jar #ou -cp <classpath>

export LD_LIBRARY_PATH=/applis/sicstus-4.1.3/lib/
export CLASSPATH=.:bin/moteurJava:/applis/sicstus-4.1.3/lib/sicstus-4.1.3/bin/jasper.jar #ou -cp <classpath>



#on lance le moteur Java
#echo java Lanceur ${portMoteurJava}
(java Lanceur ${portMoteurJava}) &
sleep 2

#on lance le joueur
#echo bin/joueur/connexionArbitre ${adresseArbitre} ${portArbitre} ${portMoteurJava} ${adresseMoteurJava}
bin/joueur/connexionArbitre ${adresseArbitre} ${portArbitre} ${portMoteurJava} ${adresseMoteurJava}


exit 0

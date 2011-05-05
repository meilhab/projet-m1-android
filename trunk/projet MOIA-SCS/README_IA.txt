###############################################################################
#                            Projet : Le jeu de Yote                          #
#                     Guillaume MONTAVON & Benoit MEILHAC                     #
#                           (Master 1 Informatique)                           #
###############################################################################
L'IA du jeu de yote est gere par le fichier prolog : ia.pl.
Elle permet de retourner un coup à jouer en donnant en parametre la liste des pions des 2 joueurs.
Le principal prédicat est jouer, il est le seul qui sera appele par le joueur.

###############################################################################
#                   Description du principal prédicat : jouer                 #
###############################################################################
Predicat jouer(ListePionsJ1, ListePionsJ2, NbPionsJ1, DernierDeplacement, TypeCoups, CaseDepart, CaseArrivee, Prend2emePion).
	exemple : jouer([[0,0]], [[0,1], [0,3]], 11, [[1,0],[0,0]], TypeCoups, CaseDepart, CaseArrivee, Prend2emePion).

	parametres :
		ListePionsJ1 : Liste des coordonnees des pions que possede le joueur 1 (nous)
		ListePionsJ2 : Liste des coordonnees des pions que possede le joueur 2 (adversaire)
		NbPionsJ1 : nombre de pions qu'il reste dans la main du joueur 1 (nous)
		DernierDeplacement : ensemble de deux coordonnees : case de depart du dernier deplacement et 
															case d'arrivee du dernier deplacement
		TypeCoups : type de coups que l'IA a choisi de jouer
		CaseDepart : case de depart du coups que l'IA a choisi de jouer
		CaseArrivee : case d'arrivee du coups que l'IA a choisi de jouer
		Prend2emePion : 2eme pion que l'IA a choisi de prendre



###############################################################################
#              Exemple d'utilisation du principal prédicat : jouer            #
###############################################################################

	- Pour une prise de pion adverse
		jouer([[0,0]], [[0,1], [0,3]], 11, [], TypeDuCoupJoue, CaseOrigine, CaseArrivee, Deuxiemeprise).
		ici on a l'exemple d'un pion adverse pouvant être pris.
		prendPremierPossible([[0,0]], [[0,1], [0,3]], CaseDepart, Pris, CaseArrivee). permet de retourner les positions d'un pion après avoir pris un pion adverse.
		prendPionAdversaire([[0,3]], Prend2emePion). permet de prendre un pion restant après la première prise s'il y en a un disponible.
		Il prend le premier de la liste.

	- Pour un déplacement du pion sous la menace
		jouer([[0,3]], [[0,2], [0,1]], 11, [], TypeDuCoupJoue, CaseOrigine, CaseArrivee, Deuxiemeprise).
		ici on a l'exemple d'un pion menacé par un autre.
		De ce fait, il va être déplacé.
		peutEtrePrisPremierPossible([[0,3]], [[0,2], [0,1]], 11, CaseDepart). retournera la case du pion menacé.
		deplacementPossible([0,3], [[0,3]], [[0,2], [0,1]], 11, CaseArrivee). retournera une case de déplacement sécurisé pour le pion.
		Si pas de case sécurisé trouvée, le pion est laissé à son triste sort.

	- Pour une pose
		jouer([[0,0]], [[0,2], [0,1]], 11, [], TypeDuCoupJoue, CaseOrigine, CaseArrivee, Deuxiemeprise).
		ici le prédicat poserPions permettra de retourner une case possible pour poser un pion en sécurité
		on peut l'appeler via :
		poserPion([[0,0]], [[0,2], [0,1]], 11, CaseArrivee).


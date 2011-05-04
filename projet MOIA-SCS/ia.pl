% ============================================================================
%                            Projet : Le jeu de Yote
%                     Guillaume MONTAVON & Benoit MEILHAC
%                           (Master 1 Informatique)
% ============================================================================



% =============================================================================
% ============ Predicats vus en TP qui serviront pour la suite ================
% =============================================================================

% Ajoute un element a la fin d'une liste 
ajout_fin(X,[],[X]).
ajout_fin(X,[Y|L1],[Y|L2]):-
	ajout_fin(X,L1,L2).

% inverse les elements contenus dans une ligne
renverse_liste([],[]).
renverse_liste([X|L1],L2):-
	renverse_liste(L1,L3),
	ajout_fin(X,L3,L2).

% ajoute un element au debut d'une liste
ajoutDebut(X,L,[X|L]).

% supprime un element dans une liste
supprimer(_,[],[]).
supprimer(X,[X|L1],L1).
supprimer(X,[Y|L1],L2):-
	X\==Y,
	ajoutDebut(Y,L3,L2),
	supprimer(X,L1,L3).




hauteur(5).
largeur(6).

plateau([[0,0], [0,1], [0,2], [0,3], [0,4], [0,5],
        [1,0], [1,1], [1,2], [1,3], [1,4], [1,5],
        [2,0], [2,1], [2,2], [2,3], [2,4], [2,5],
        [3,0], [3,1], [3,2], [3,3], [3,4], [3,5],
        [4,0], [4,1], [4,2], [4,3], [4,4], [4,5]]).


% =============================================================================
% ===================================== Pose ==================================
% =============================================================================

% pose dans les coins
poser([0,0]). poser([4,0]). poser([4,5]). poser([0,5]).
% pose dans les bords
poser([0,1]). poser([0,2]). poser([0,3]). poser([0,4]).
poser([4,1]). poser([4,2]). poser([4,3]). poser([4,4]).
poser([1,0]). poser([2,0]). poser([3,0]).
poser([1,5]). poser([2,5]). poser([3,5]).
% pose au milieu
poser([1,1]). poser([1,2]). poser([1,3]). poser([1,4]).
poser([3,1]). poser([3,2]). poser([3,3]). poser([3,4]).
poser([2,1]). poser([2,4]).
poser([2,2]). poser([2,3]).


choisirPoser(Acc, ListePionsPossible, ListePionsJ1, ListePionsJ2):-
    poser([X,Y]),
    \+ member([X,Y], Acc),
    \+ member([X,Y], ListePionsJ1),
    \+ member([X,Y], ListePionsJ2),
    choisirPoser([[X,Y] | Acc], ListePionsPossible, ListePionsJ1, ListePionsJ2).
choisirPoser(Acc, AccReverse, _, _):-
	renverse_liste(Acc,AccReverse).

poserPions(ListePionsJ1, ListePionsJ2, NbPionsJ1, CaseJouee):-
    NbPionsJ1 > 0,
    choisirPoser([], Liste, ListePionsJ1, ListePionsJ2),
	poseSansEtrePris(ListePionsJ1, ListePionsJ2, Liste, CaseJouee).

% pose meme si on est pris (a supprimer)
poserPions(ListePionsJ1, ListePionsJ2, NbPionsJ1, CaseJouee):-
    NbPionsJ1 > 0,
    choisirPoser([], [CaseJouee|_], ListePionsJ1, ListePionsJ2).
	
poseSansEtrePris(ListePionsJ1, ListePionsJ2, [Pion|_], CaseJouee):-
	\+ peutEtrePris(Pion, ListePionsJ1, ListePionsJ2),
	CaseJouee = Pion,!.

poseSansEtrePris(ListePionsJ1, ListePionsJ2, [_|Liste], CaseJouee):-
	poseSansEtrePris(ListePionsJ1, ListePionsJ2, Liste, CaseJouee).



% =============================================================================
% =============================== Peut etre pris ==============================
% =============================================================================
peutEtrePrisPremierPossible(ListePionsJ1, ListePionsJ2, Case):-
	verifierPeutEtrePris(ListePionsJ1, ListePionsJ2, [Case|_]).
	
verifierPeutEtrePris(ListePionsJ1, ListePionsJ2, Res):-
	verifierPeutEtrePris(ListePionsJ1, ListePionsJ1, ListePionsJ2, [], Res).

verifierPeutEtrePris([], _, _, Acc, Res):-
	renverse_liste(Acc,Res).

verifierPeutEtrePris([Pion|Acc], ListePionsJ1, ListePionsJ2, AccRes, Res):-
	peutEtrePris(Pion, ListePionsJ1, ListePionsJ2),
	verifierPeutEtrePris(Acc, ListePionsJ1, ListePionsJ2, [Pion|AccRes], Res).

verifierPeutEtrePris([_|Acc], ListePionsJ1, ListePionsJ2, AccRes, Res):-
	verifierPeutEtrePris(Acc, ListePionsJ1, ListePionsJ2, AccRes, Res).

verifierPeutEtrePris([Pion|_], ListePionsJ1, ListePionsJ2, [Pion|_], _):-
	peutEtrePris(Pion, ListePionsJ1, ListePionsJ2).	

peutEtrePris([X,Y], ListePionsJ1, ListePionsJ2):-
	pionACote([X,Y], [X1,Y1]),
	member([X1,Y1], ListePionsJ2),
	X2 is X + (X-X1),
	Y2 is Y + (Y-Y1),
	hauteur(XMax),
	largeur(YMax),
	X2 >= 0,
	Y2 >= 0,
	X2 < XMax,
	Y2 < YMax,
	\+ member([X2,Y2], ListePionsJ1),
	\+ member([X2,Y2], ListePionsJ2).


% =============================================================================
% =============================================================================
% ========================== 4 cases autour d'un pion =========================
% =============================================================================
% =============================================================================

% ===================== Predicat pionACote(Pion, CaseACote) ===================
% Donne les cases qui se trouvent a cote d'une case (droite, gauche, haut, bas)
% (soit 4 cases possibles, soit 3, soit 2 selon l'emplacement de cette case)
% =============================================================================
pionACote([X,Y], [X1,Y]):-
	X1 is X-1,
	X1 >= 0.
pionACote([X,Y], [X1,Y]):-
	X1 is X+1,
	hauteur(XMax),
	X1 < XMax.
pionACote([X,Y], [X,Y1]):-
	Y1 is Y-1,
	Y1 >= 0.
pionACote([X,Y], [X,Y1]):-
	Y1 is Y+1,
	largeur(YMax),
	Y1 < YMax.


% =============================================================================
% =============================================================================
% ================================ Deplacement ================================
% =============================================================================
% =============================================================================

deplacePremierPossible(ListePionsJ1, ListePionsJ2, DernierDeplacement, CaseDepart, CaseArrivee):-
	tsDeplacementPossible(ListePionsJ1, ListePionsJ2, DernierDeplacement, ListePossible),
	deplaceSansEtrePris(ListePionsJ1, ListePionsJ2, ListePossible, CaseDepart, CaseArrivee).
% deplace meme si on peut etre pris
deplacePremierPossible(ListePionsJ1, ListePionsJ2, DernierDeplacement, CaseDepart, CaseArrivee):-
	tsDeplacementPossible(ListePionsJ1, ListePionsJ2, DernierDeplacement, [[CaseDepart,CaseArrivee]|_]).


% == Predicat tsDeplacementPossible(ListePionsJ1, ListePionsJ2, DernierDeplacement, Res) ==
% choisi un pion adverse que l'on va prendre comme 2eme pion lors d'une prise
%==============================================================================
tsDeplacementPossible(ListePionsJ1, ListePionsJ2, DernierDeplacement, Res):-
	tsDeplacementPossible(ListePionsJ1, ListePionsJ1, ListePionsJ2, DernierDeplacement, [], Res).

tsDeplacementPossible([], _, _, _, Acc, Res):-
	renverse_liste(Acc,Res).

tsDeplacementPossible([Pion|Acc], ListePionsJ1, ListePionsJ2, DernierDeplacement, AccRes, Res):-
	deplacementPossible(Pion, ListePionsJ1, ListePionsJ2, DernierDeplacement, Case),
	tsDeplacementPossible(Acc, ListePionsJ1, ListePionsJ2, DernierDeplacement, [[Pion,Case]|AccRes], Res).

tsDeplacementPossible([_|Acc], ListePionsJ1, ListePionsJ2, DernierDeplacement, AccRes, Res):-
	tsDeplacementPossible(Acc, ListePionsJ1, ListePionsJ2, DernierDeplacement, AccRes, Res).

tsDeplacementPossible([Pion|_], ListePionsJ1, ListePionsJ2, DernierDeplacement, [[Pion,Case]|_], _):-
	deplacementPossible(Pion, ListePionsJ1, ListePionsJ2, DernierDeplacement, Case).


% == Predicat deplacementPossible(A, ListePionsJ1, ListePionsJ2, DernierDeplacement, B) ==
% verifie si un pion peut etre deplace
%==============================================================================
deplacementPossible(A, ListePionsJ1, ListePionsJ2, DernierDeplacement, B):-
	pionACote(A, B),
	member(A, ListePionsJ1),
	\+ DernierDeplacement = [B,A],
	\+ member(B, ListePionsJ1),
	\+ member(B, ListePionsJ2).


% == Predicat deplaceSansEtrePris(ListePionsJ1, ListePionsJ2, ListeDeplacementPossibles, CaseDepartJouee, CaseArriveeJouee) ==
% deplace un pion sans qu'il puisse etre pris sur la case ou il arrivera,
% prend en parametre une liste de couples de deplacements possibles (CaseDepart,CaseArrivee)
%==============================================================================
deplaceSansEtrePris(ListePionsJ1, ListePionsJ2, [[CaseDepart,CaseArrivee]|_], CaseDepartJouee, CaseArriveeJouee):-
	ajoutDebut(CaseArrivee,ListePionsJ1,ListePionsJ1Ajout),
	supprimer(CaseDepart,ListePionsJ1Ajout,ListePionsJ1Suppr),
	\+ peutEtrePris(CaseArrivee, ListePionsJ1Suppr, ListePionsJ2),
	CaseDepartJouee = CaseDepart,
	CaseArriveeJouee = CaseArrivee,!.

deplaceSansEtrePris(ListePionsJ1, ListePionsJ2, [_|Liste], CaseDepartJouee, CaseArriveeJouee):-
	deplaceSansEtrePris(ListePionsJ1, ListePionsJ2, Liste, CaseDepartJouee, CaseArriveeJouee).





% =============================================================================
% =============================================================================
% =================================== Prise ===================================
% =============================================================================
% =============================================================================


% ========== Predicat prendPionAdversaire(ListePionsJ1, PionChoisi) ===========
% choisi un pion adverse que l'on va prendre comme 2eme pion lors d'une prise
%==============================================================================
prendPionAdversaire([Case|_], Case).
prendPionAdversaire(_, [5,6]).


% == Predicat prendPremierPossible(ListePionsJ1, ListePionsJ2, CaseDepart, Pris, CaseArrivee) ==
% retourne un coup qui va prendre le premier pion adverse possible
%==============================================================================
prendPremierPossible(ListePionsJ1, ListePionsJ2, CaseDepart, Pris, CaseArrivee):-
	tsPrisePossible(ListePionsJ1, ListePionsJ2, [[CaseDepart,Pris,CaseArrivee]|_]).	
	% verifier que \+ peutEtrePris(CaseArrivee, ListePionsJ1, ListePionsJ2).

% ========= Predicat tsPrisePossible(ListePionsJ1, ListePionsJ2, Res) =========
% permet d'obtenir la liste de toutes les prises possibles du joueur 1 (nous)
% sur le plateau (liste de couples caseDepart, caseArrivee)
%==============================================================================
tsPrisePossible(ListePionsJ1, ListePionsJ2, Res):-
	tsPrisePossible(ListePionsJ1, ListePionsJ1, ListePionsJ2, [], Res).

tsPrisePossible([], _, _, Acc, Res):-
	renverse_liste(Acc,Res).

tsPrisePossible([Pion|Acc], ListePionsJ1, ListePionsJ2, AccRes, Res):-
	prisePossible(Pion, ListePionsJ1, ListePionsJ2, Pris, Case),
	tsPrisePossible(Acc, ListePionsJ1, ListePionsJ2, [[Pion,Pris,Case]|AccRes], Res).

tsPrisePossible([_|Acc], ListePionsJ1, ListePionsJ2, AccRes, Res):-
	tsPrisePossible(Acc, ListePionsJ1, ListePionsJ2, AccRes, Res).

tsPrisePossible([Pion|_], ListePionsJ1, ListePionsJ2, [[Pion,Pris,Case]|_], _):-
	prisePossible(Pion, ListePionsJ1, ListePionsJ2, Pris, Case).


% == Predicat prisePossible(Pion, ListePionsJ1, ListePionsJ2, PionPris ,CaseArrivee) ==
% verifie si un pion donne en parametre peut prendre un pion adverse
% retourne la case du pion qui sera pris, ainsi que la case d'arrivee de notre pion
%==============================================================================
prisePossible([AX,AY], ListePionsJ1, ListePionsJ2, [BX, BY] ,[CX,CY]):-
	pionACote([AX,AY], [BX,BY]),
	member([AX,AY], ListePionsJ1),
	\+ member([BX,BY], ListePionsJ1),
	member([BX,BY], ListePionsJ2),
	CX is BX + (BX-AX),
	CY is BY + (BY-AY),
	hauteur(XMax),
	largeur(YMax),
	CX >= 0,
	CY >= 0,
	CX < XMax,
	CY < YMax,
	\+ member([CX,CY], ListePionsJ1),
	\+ member([CX,CY], ListePionsJ2).



% =============================================================================
% =============================================================================
% ================================ Joue un coup ===============================
% =============================================================================
% =============================================================================
% Predicat jouer(ListePionsJ1, ListePionsJ2, NbPionsJ1, DernierDeplacement, TypeCoups, CaseDepart, CaseArrivee, Prend2emePion).
%     exemple : jouer([[0,0]], [[0,1], [0,3]], 11, [[1,0],[0,0]], TypeCoups, CaseDepart, CaseArrivee, Prend2emePion).
%
%   parametres :
%     ListePionsJ1 : Liste des coordonnees des pions que possede le joueur 1 (nous)
%     ListePionsJ2 : Liste des coordonnees des pions que possede le joueur 2 (adversaire)
%     NbPionsJ1 : nombre de pions qu'il reste dans la main du joueur 1 (nous)
%     DernierDeplacement : ensemble de deux coordonnees : case de depart du dernier deplacement et 
%                                                         case d'arrivee du dernier deplacement
%     TypeCoups : type de coups que l'IA a choisi de jouer
%     CaseDepart : case de depart du coups que l'IA a choisi de jouer
%     CaseArrivee : case d'arrivee du coups que l'IA a choisi de jouer
%     Prend2emePion : 2eme pion que l'IA a choisi de prendre

% Essaye de prendre un pion adverse si possible, sinon ne fait rien
jouer(ListePionsJ1, ListePionsJ2, _, _, TypeCoups, CaseDepart, CaseArrivee, Prend2emePion):-
	prendPremierPossible(ListePionsJ1, ListePionsJ2, CaseDepart, Pris, CaseArrivee),
	supprimer(Pris,ListePionsJ2,ListePionsJ2Suppr),
	prendPionAdversaire(ListePionsJ2Suppr, Prend2emePion),
	TypeCoups = prise.

% Si un pion adverse attaque, le pion attaque se deplace sur une case ou il ne pourra pas etre pris
jouer(ListePionsJ1, ListePionsJ2, _, DernierDeplacement, TypeCoups, CaseDepart, CaseArrivee, Prend2emePion):-
	peutEtrePrisPremierPossible(ListePionsJ1, ListePionsJ2, CaseDepart),
	deplacementPossible(CaseDepart, ListePionsJ1, ListePionsJ2, DernierDeplacement, CaseArrivee),
	Prend2emePion = [5,6],
	TypeCoups = deplace.

% Essaye de poser un pion sur une case ou il ne peut pas etre pris
jouer(ListePionsJ1, ListePionsJ2, NbPionsJ1, _, TypeCoups, CaseDepart, CaseArrivee, Prend2emePion):-
	poserPions(ListePionsJ1, ListePionsJ2, NbPionsJ1, CaseArrivee),
	CaseDepart = [5,6],
	Prend2emePion = [5,6],
	TypeCoups = pose.

% Deplace un des pions sur une case ou il ne peut pas etre pris
jouer(ListePionsJ1, ListePionsJ2, _, DernierDeplacement, TypeCoups, CaseDepart, CaseArrivee, Prend2emePion):-
	deplacePremierPossible(ListePionsJ1, ListePionsJ2, DernierDeplacement, CaseDepart, CaseArrivee),
	Prend2emePion = [5,6],
	TypeCoups = deplace.

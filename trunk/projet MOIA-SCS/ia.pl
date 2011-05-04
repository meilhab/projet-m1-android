hauteur(5).
largeur(6).

ajout_fin(X,[],[X]).
ajout_fin(X,[Y|L1],[Y|L2]):-
	ajout_fin(X,L1,L2).

renverse_liste([],[]).
renverse_liste([X|L1],L2):-
	renverse_liste(L1,L3),
	ajout_fin(X,L3,L2).
	
ajoutDebut(X,L,[X|L]).
supprimer(_,[],[]).
supprimer(X,[X|L1],L1). 
supprimer(X,[Y|L1],L2):-
	X\==Y,
	ajoutDebut(Y,L3,L2),
	supprimer(X,L1,L3). 


plateau([[0,0], [0,1], [0,2], [0,3], [0,4], [0,5],
        [1,0], [1,1], [1,2], [1,3], [1,4], [1,5],
        [2,0], [2,1], [2,2], [2,3], [2,4], [2,5],
        [3,0], [3,1], [3,2], [3,3], [3,4], [3,5],
        [4,0], [4,1], [4,2], [4,3], [4,4], [4,5]]).


% =============================================================================
% ===================================== POSE ==================================
% =============================================================================

poser([0,0]). poser([4,0]). poser([4,5]). poser([0,5]).
poser([0,1]). poser([0,2]). poser([0,3]). poser([0,4]).
poser([4,1]). poser([4,2]). poser([4,3]). poser([4,4]).
poser([1,0]). poser([2,0]). poser([3,0]).
poser([1,5]). poser([2,5]). poser([3,5]).
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


prendPionAdversaire([Case|_], Case).
prendPionAdversaire(_, [5,6]).



% =============================================================================
% =============================== PEUT ETRE PRIS ==============================
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
% ========================== 4 CASES AUTOUR D UN PION =========================
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
% ================================ DEPLACEMENT ================================
% =============================================================================

deplacePremierPossible(ListePionsJ1, ListePionsJ2, DernierDeplacement, CaseDepart, CaseArrivee):-
	tsDeplacementPossible(ListePionsJ1, ListePionsJ2, DernierDeplacement, ListePossible),
	deplaceSansEtrePris(ListePionsJ1, ListePionsJ2, ListePossible, CaseDepart, CaseArrivee).
	
% deplace meme si on peut etre pris
deplacePremierPossible(ListePionsJ1, ListePionsJ2, DernierDeplacement, CaseDepart, CaseArrivee):-
	tsDeplacementPossible(ListePionsJ1, ListePionsJ2, DernierDeplacement, [[CaseDepart,CaseArrivee]|_]).

	
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

	
deplacementPossible(A, ListePionsJ1, ListePionsJ2, DernierDeplacement, B):-
	pionACote(A, B),
	member(A, ListePionsJ1),
	\+ DernierDeplacement = [B,A],
	\+ member(B, ListePionsJ1),
	\+ member(B, ListePionsJ2).


deplaceSansEtrePris(ListePionsJ1, ListePionsJ2, [[CaseDepart,CaseArrivee]|_], CaseDepartJouee, CaseArriveeJouee):-
	ajoutDebut(CaseArrivee,ListePionsJ1,ListePionsJ1Ajout),
	supprimer(CaseDepart,ListePionsJ1Ajout,ListePionsJ1Suppr),
	\+ peutEtrePris(CaseArrivee, ListePionsJ1Suppr, ListePionsJ2),
	CaseDepartJouee = CaseDepart,
	CaseArriveeJouee = CaseArrivee,!.

deplaceSansEtrePris(ListePionsJ1, ListePionsJ2, [_|Liste], CaseDepartJouee, CaseArriveeJouee):-
	deplaceSansEtrePris(ListePionsJ1, ListePionsJ2, Liste, CaseDepartJouee, CaseArriveeJouee).

	

% =============================================================================
% =================================== PRISE ===================================
% =============================================================================

prendPremierPossible(ListePionsJ1, ListePionsJ2, CaseDepart, Pris, CaseArrivee):-
	tsPrisePossible(ListePionsJ1, ListePionsJ2, [[CaseDepart,Pris,CaseArrivee]|_]).	
	% verifier que \+ peutEtrePris(CaseArrivee, ListePionsJ1, ListePionsJ2).

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
% =================================== JOUER ===================================
% =============================================================================
% essaye de prendre un pion adverse si possible
jouer(ListePionsJ1, ListePionsJ2, _, _, TypeCoups, CaseDepart, CaseArrivee, Prend2emePion):-
	prendPremierPossible(ListePionsJ1, ListePionsJ2, CaseDepart, Pris, CaseArrivee),
	supprimer(Pris,ListePionsJ2,ListePionsJ2Suppr),
	prendPionAdversaire(ListePionsJ2Suppr, Prend2emePion),
	TypeCoups = prise.

% se deplace si un pion adverse attaque
jouer(ListePionsJ1, ListePionsJ2, _, DernierDeplacement, TypeCoups, CaseDepart, CaseArrivee, Prend2emePion):-
	peutEtrePrisPremierPossible(ListePionsJ1, ListePionsJ2, CaseDepart),
	deplacementPossible(CaseDepart, ListePionsJ1, ListePionsJ2, DernierDeplacement, CaseArrivee),
	Prend2emePion = [5,6],
	TypeCoups = deplace.

% essaye de poser un pion
jouer(ListePionsJ1, ListePionsJ2, NbPionsJ1, _, TypeCoups, CaseDepart, CaseArrivee, Prend2emePion):-
	poserPions(ListePionsJ1, ListePionsJ2, NbPionsJ1, CaseArrivee),
	CaseDepart = [5,6],
	Prend2emePion = [5,6],
	TypeCoups = pose.

% se deplace
jouer(ListePionsJ1, ListePionsJ2, _, DernierDeplacement, TypeCoups, CaseDepart, CaseArrivee, Prend2emePion):-
	deplacePremierPossible(ListePionsJ1, ListePionsJ2, DernierDeplacement, CaseDepart, CaseArrivee),
	Prend2emePion = [5,6],
	TypeCoups = deplace.
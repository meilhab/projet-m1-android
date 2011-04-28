hauteur(5).
largeur(6).

ajout_fin(X,[],[X]).
ajout_fin(X,[Y|L1],[Y|L2]):-
	ajout_fin(X,L1,L2).

renverse_liste([],[]).
renverse_liste([X|L1],L2):-
	renverse_liste(L1,L3),
	ajout_fin(X,L3,L2).


plateau([[0,0], [0,1], [0,2], [0,3], [0,4], [0,5],
        [1,0], [1,1], [1,2], [1,3], [1,4], [1,5],
        [2,0], [2,1], [2,2], [2,3], [2,4], [2,5],
        [3,0], [3,1], [3,2], [3,3], [3,4], [3,5],
        [4,0], [4,1], [4,2], [4,3], [4,4], [4,5]]).

poser([0,0]).
poser([4,0]).
poser([4,5]).
poser([0,5]).
poser([0,1]).
poser([0,2]).
poser([0,3]).
poser([0,4]).
poser([4,1]).
poser([4,2]).
poser([4,3]).
poser([4,4]).
poser([1,0]).
poser([2,0]).
poser([3,0]).
poser([1,5]).
poser([2,5]).
poser([3,5]).
poser([1,1]).
poser([1,2]).
poser([1,3]).
poser([1,4]).
poser([3,1]).
poser([3,2]).
poser([3,3]).
poser([3,4]).
poser([2,1]).
poser([2,4]).
poser([2,2]).
poser([2,3]).

choisirPoser(Acc, ListePionsPossible, ListePionsJ1, ListePionsJ2):-
    poser([X,Y]),
    \+ member([X,Y], Acc),
    \+ member([X,Y], ListePionsJ1),
    \+ member([X,Y], ListePionsJ2),
    choisirPoser([[X,Y] | Acc], ListePionsPossible, ListePionsJ1, ListePionsJ2).
choisirPoser(Acc, AccReverse, _, _):-
	renverse_liste(Acc,AccReverse).

poserPions(ListePionsJ1, ListePionsJ2, NbPionsJ1, CaseJouee):-
    NbPionsJ1 > 1,
    choisirPoser([], [CaseJouee|ListePionsPossible], ListePionsJ1, ListePionsJ2).


prendrePiece(ListePionsJ1, ListePionsJ2):-
	5 = 6.

verifierPeutPrendrePiece(ListePionsJ1, ListePionsJ2, Res):-
	5 = 6.


peutEtrePris([X,Y], ListePionsJ1, ListePionsJ2):-
	pionACote([X,Y], [X1,Y1]),
	member([X1,Y1], ListePionsJ2),
	X2 is X + (X-X1),
	Y2 is Y + (Y-Y1),
	\+ member([X2,Y2], ListePionsJ1),
	\+ member([X2,Y2], ListePionsJ2).

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
	hauteur(YMax),
	Y1 < YMax.


deplacePremierPossible(ListePionsJ1, ListePionsJ2, DernierDeplacement, CaseDepart, CaseArrivee):-
	tsDeplacementPossible(ListePionsJ1, ListePionsJ2, DernierDeplacement, [[CaseDepart,CaseArrivee]|_]).

tsDeplacementPossible(ListePionsJ1, ListePionsJ2, DernierDeplacement, Res):-
	tsDeplacementPossible(ListePionsJ1, ListePionsJ1, ListePionsJ2, DernierDeplacement, [], Res).

tsDeplacementPossible([], _, _, DernierDeplacement, Acc, Res):-
	renverse_liste(Acc,Res),
	write(Res).

tsDeplacementPossible([Pion|Acc], ListePionsJ1, ListePionsJ2, DernierDeplacement, AccRes, Res):-
	deplacementPossible(Pion, ListePionsJ1, ListePionsJ2, DernierDeplacement, Case),
	tsDeplacementPossible(Acc, ListePionsJ1, ListePionsJ2, DernierDeplacement, [[Pion,Case]|AccRes], Res).

tsDeplacementPossible([_|Acc], ListePionsJ1, ListePionsJ2, DernierDeplacement, AccRes, Res):-
	tsDeplacementPossible(Acc, ListePionsJ1, ListePionsJ2, DernierDeplacement, AccRes, Res).

tsDeplacementPossible([Pion|Acc], ListePionsJ1, ListePionsJ2, DernierDeplacement, [[Pion,Case]|AccRes], Res):-
	deplacementPossible(Pion, ListePionsJ1, ListePionsJ2, DernierDeplacement, Case).

deplacementPossible(A, ListePionsJ1, ListePionsJ2, DernierDeplacement, B):-
	pionACote(A, B),
	member(A, ListePionsJ1),
	\+ DernierDeplacement = [B,A],
	\+ member(B, ListePionsJ1),
	\+ member(B, ListePionsJ2).

jouer(ListePionsJ1, ListePionsJ2, _, _, TypeCoups, CaseDepart, CaseArrivee, Prend2emePion):-
	prendrePiece(ListePionsJ1, ListePionsJ2),
	CaseDepart = [0,0],
	CaseArrivee = [0,0],
	Prend2emePion = [0,0],
	TypeCoups = prise.

jouer(ListePionsJ1, ListePionsJ2, _, DernierDeplacement, TypeCoups, CaseDepart, CaseArrivee, Prend2emePion):-
	verifierPeutPrendrePiece(ListePionsJ2, ListePionsJ1, Res),
	%deplacementPossible(...),
	CaseDepart = [0,0],
	CaseArrivee = [0,0],
	Prend2emePion = [0,0],
	TypeCoups = deplace.

jouer(ListePionsJ1, ListePionsJ2, NbPionsJ1, _, TypeCoups, CaseDepart, CaseArrivee, Prend2emePion):-
	poserPions(ListePionsJ1, ListePionsJ2, NbPionsJ1, CaseArrivee),
	CaseDepart = [5,6],
	Prend2emePion = [5,6],
	TypeCoups = pose.
	
jouer(ListePionsJ1, ListePionsJ2, _, DernierDeplacement, TypeCoups, CaseDepart, CaseArrivee, Prend2emePion):-
	deplacePremierPossible(ListePionsJ1, ListePionsJ2, DernierDeplacement, CaseDepart, CaseArrivee),
	Prend2emePion = [5,6],
	TypeCoups = deplace.
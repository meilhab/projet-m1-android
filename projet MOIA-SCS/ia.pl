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
    NbPionsJ1 > 1,
    choisirPoser([], [CaseJouee|ListePionsPossible], ListePionsJ1, ListePionsJ2).
	% verifier que \+ peutEtrePris(CaseArrivee, ListePionsJ1, ListePionsJ2).


prendrePiece(ListePionsJ1, ListePionsJ2):-
	5 = 6.


prendPionAdversaire([Case|ListePionsJ2], Case).
prendPionAdversaire(_, [5,6]).


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

verifierPeutEtrePris([Pion|Acc], ListePionsJ1, ListePionsJ2, [Pion|AccRes], Res):-
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


deplacePremierPossible(ListePionsJ1, ListePionsJ2, DernierDeplacement, CaseDepart, CaseArrivee):-
	tsDeplacementPossible(ListePionsJ1, ListePionsJ2, DernierDeplacement, [[CaseDepart,CaseArrivee]|_]).	
	% verifier que \+ peutEtrePris(CaseArrivee, ListePionsJ1, ListePionsJ2).

tsDeplacementPossible(ListePionsJ1, ListePionsJ2, DernierDeplacement, Res):-
	tsDeplacementPossible(ListePionsJ1, ListePionsJ1, ListePionsJ2, DernierDeplacement, [], Res).

tsDeplacementPossible([], _, _, DernierDeplacement, Acc, Res):-
	renverse_liste(Acc,Res).

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
	
	

	



prendPremierPossible(ListePionsJ1, ListePionsJ2, CaseDepart, CaseArrivee):-
	tsPrisePossible(ListePionsJ1, ListePionsJ2, [[CaseDepart,CaseArrivee]|_]).	
	% verifier que \+ peutEtrePris(CaseArrivee, ListePionsJ1, ListePionsJ2).

tsPrisePossible(ListePionsJ1, ListePionsJ2, Res):-
	tsPrisePossible(ListePionsJ1, ListePionsJ1, ListePionsJ2, [], Res).

tsPrisePossible([], _, _, Acc, Res):-
	renverse_liste(Acc,Res).

tsPrisePossible([Pion|Acc], ListePionsJ1, ListePionsJ2, AccRes, Res):-
	prisePossible(Pion, ListePionsJ1, ListePionsJ2, Case),
	tsPrisePossible(Acc, ListePionsJ1, ListePionsJ2, [[Pion,Case]|AccRes], Res).

tsPrisePossible([_|Acc], ListePionsJ1, ListePionsJ2, AccRes, Res):-
	tsPrisePossible(Acc, ListePionsJ1, ListePionsJ2, AccRes, Res).

tsPrisePossible([Pion|Acc], ListePionsJ1, ListePionsJ2, [[Pion,Case]|AccRes], Res):-
	prisePossible(Pion, ListePionsJ1, ListePionsJ2, Case).

prisePossible([AX,AY], ListePionsJ1, ListePionsJ2, [CX,CY]):-
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





	


jouer(ListePionsJ1, ListePionsJ2, _, _, TypeCoups, CaseDepart, CaseArrivee, Prend2emePion):-
	prendPremierPossible(ListePionsJ1, ListePionsJ2, CaseDepart, CaseArrivee),
	prendPionAdversaire(ListePionsJ2, Prend2emePion),
	TypeCoups = prise.

jouer(ListePionsJ1, ListePionsJ2, _, DernierDeplacement, TypeCoups, CaseDepart, CaseArrivee, Prend2emePion):-
	peutEtrePrisPremierPossible(ListePionsJ1, ListePionsJ2, CaseDepart),
	deplacementPossible(CaseDepart, ListePionsJ1, ListePionsJ2, DernierDeplacement, CaseArrivee),
	Prend2emePion = [5,6],
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
plateau([[0,0], [0,1], [0,2], [0,3], [0,4], [0,5],
        [1,0], [1,1], [1,2], [1,3], [1,4], [1,5],
        [2,0], [2,1], [2,2], [2,3], [2,4], [2,5],
        [3,0], [3,1], [3,2], [3,3], [3,4], [3,5],
        [4,0], [4,1], [4,2], [4,3], [4,4], [4,5]]).


poserAngle([0,0]).
poserAngle([4,0]).
poserAngle([4,5]).
poserAngle([0,5]).


choisirPoser(Acc, ListePionsPossible, ListePionsJ1, ListePionsJ2):-
    X >= 0,
    X <= 4,
    Y >= 0,
    Y <= 5,
    \+ member([X,Y], Acc),
    \+ member([X,Y], ListePionsJ1),
    \+ member([X,Y], ListePionsJ2),
    choisirPoser([[X,Y] | Acc], ListePionsPossible, ListePionsJ1, ListePionsJ2).
choisirPoser(Acc, Acc, _1, _2).

poserPions(ListePionsJ1, ListePionsJ2, NbPionsJ1, CaseJouee):-
    NbPionsJ1 > 1,
    choisirPoser([], ListePionsPossible, ListePionsJ1, ListePionsJ2),
    write(ListePionsPossible).

    

%jouerCoup(listePionsJ1, listePionsJ2, nbPionsJ1, nbPionsJ2, dernierCoupJ1, dernierCoupJ2, caseJouee):-
    


%coupJoue(listePionsJ1, listePionsJ2, nbPionsJ1, nbPionsJ2, dernierCoupJ1, dernierCoupJ2, caseJouee):-
%    jouerCoupJ2(listePionsJ2, nbPionsJ2, dernierCoupJ2),

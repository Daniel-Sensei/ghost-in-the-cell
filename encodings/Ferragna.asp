%OTTENGO LE FABBRICHE MITTENTI
ownedFactory(ID,C,PR) :- factory(ID,P,C,PR), player(P).

%OTTENGO LE FABBRICHE NEUTRE
neutralFactory(ID,C,PR) :- factory(ID,P,C,PR), P=0.

%FABBRICHE ATTACCABILI (NEUTRE E NEMICHE)
potentialFactory(ID,C,PR) :- factory(ID,P,C,PR), player(P1), P!=P1.


%NON è POSSIBILE ATTACCARE DA UNA FABBRICA CON PIù TRUPPE DI QUELLE DISPONIBILI
:- S = #sum{T,ID1,ID2 : attack(ID1,C1,_,ID2,_,_,T)}, attack(ID1,C1,_,_,_,_,_), S > (C1-1).

%GUESS SU UN POSSIBILE ATTACCO VERSO UNA FABBRICA POTENZIALE
attack(ID1,C1,PR1,ID2,C2,PR2,T) | noAttack(ID1,C1,PR1,ID2,C2,PR2,T) :- ownedFactory(ID1,C1,PR1), potentialFactory(ID2,C2,PR2), &rand(C1/5+1,(C1-1);T).


%WEAK CONSTRAINS DI PRIORITà MASSIMA (3)


% è PREFERIBILE CHE NON CI SIA UN NON ATTACCO VERSO UNA FABBRICA POTENZIALE, PENALIZZANDO CON LA PRODUZIONE DELLA FABBRICA NON ATTACCATA.
:~ noAttack(ID1,C1,PR1,ID2,C2,PR2,T), potentialFactory(ID2,C2,PR2). [PR2@3, ID1,ID2]

% è PREFERIBILE CHE NON CI SIA UN ATTACCO PENALIZZANDO CON LA DISTANZA.
:~ attack(ID1,C1,PR1,ID2,C2,PR2,T), edge(ID1,ID2,D). [D@3, ID1,ID2]

% è PREFERIBILE CHE NON CI SIA UN NON ATTACCO NELLA FASE NOENEMYFACTORY
:~ noAttack(ID1,C1,PR1,ID2,C2,PR2,T), potentialFactory(ID,C,PR), noEnemyFactory. [10@3, ID1,ID2]

% è PREFERIBILE CHE NON CI SIA UN NON ATTACCO NELLA FASE FEWWNWMTTROOPS
:~ noAttack(ID1,C1,PR1,ID2,C2,PR2,T), potentialFactory(ID,C,PR), fewEnemyTroops. [6@3, ID1,ID2]

% è PREFERIBILE CHE IL NUMERO DI ATTACCHI SIA MAGGIORE DI 1
:~ #count{ID1 : attack(ID1,_,_,_,_,_,_)}<1. [10@3]


%WEAK CONSTRAINS DI PRIORITà MEDIA (2)

% è PREFERIBILE UN ATTACCO DOVE LE TRUPPE CHE INVIO SONO MAGGIORI DI QUELLE DELLA FABBRICA ATTACCATA
:~ S = #sum{T,ID2 : attack(_,_,_,ID2,C2,PR2,T)}, attack(_,_,_,ID2,C2,PR2,_), S<C2. [C2-S@2, ID2]

% è PREFERIBILE ATTACCARE LE FABBRICHE CON UN MINOR NUMERO DI CYBORG
:~ attack(ID1,C1,PR1,ID2,C2,PR2,T). [C2@2, ID1,ID2]


%WEAK CONSTRAIN DI PRIORITà BASSA (1)

% è PREFERIBILE NON ATTACCARE SE ARRIVANO TRUPPE NEMICHE VERSO QUELLA FABBRICA
:~ attack(ID1,C1,PR1,_,_,_,T), #sum{C2 : arrivingTroop(P,_,ID1,D,C2)}>T, arrivingTroop(P,_,ID1,_,_), player(P1), P!=P1. [T@1]


%GENERATORE DI TRANSITTROOP DATO UN ATTACCO
transitTroop(P,ID1,ID2,0,T) :- attack(ID1,C1,PR1,ID2,C2,PR2,T), player(P).


%REGOLE PER DIFFERENZIARE ALCUNI STATI DEL GIOCO


fewEnemyTroops :- #sum{T,ID1,ID2 : arrivingTroop(P,ID1,ID2,_,T)}<10, arrivingTroop(P,_,_,_,_), player(P1), P1!=P.

% CONSIDERO IL NEMICO SENZA FABBRICHE QUANDO POSSIEDE SOLO FABBRICHE CON PRODUZIONE 0
noEnemyFactory :- #sum{PR : factory(ID,P,_,PR)}=0, factory(_,P,_,_), player(P1), P!=P1.
%GUESS
cy(1).
cy(2).
cy(3).
cy(4).
cy(5).
cy(6).
cy(7).

transitTroop(Player, F1, F2, 0, C) | notTransitTroop(Player, F1, F2, 0, C) :- player(Player), factory(F1, Player, _, _), factory(F2, _, _, _), cy(C), F1 != F2.

% da una fabbrica non posso partire due truppe dirette alla stessa fabbrica contemporaneamente nel tempo t, perchè,
% giustamente, bisogna mandare tutti i cyborg necessari in un'unica truppa al tempo t
:- transitTroop(P, F1, F2, 0, C), transitTroop(P, F1, F2, 0, C1), player(P), C!=C1.

%non mandare cyborg alle tue fabbriche se non sono attaccate
arr(F):- arrivingTroop(P2, _,F,_,_), player(P), P!=P2.
:- transitTroop(P, F1, F2, 0, C), player(P), factory(F2, P, _, _), not arr(F2).

% Nel caso in cui una propria fabbrica è attaccata non bisogna mandare più cyborg di quelli che servono per difenderla
% Il primo constraint è necessario affinchè non si mandino truppe di difesa verso una fabbrica che si può difendere da sola:
% il numero di cyborg al suo interno sono maggiori dei cyborg che la stanno per attaccare
% Il secondo constraint tiene conto delle truppe già inviate, per difendere una propria fabbrica attaccata, e delle truppe avversarie
% in arrivo su di essa. Le truppe necessarie per difendere sono date dalla differenza tra le truppe avversarie in arrivo
% e quelle dentro la fabbrica attaccata e si fa in modo che per difesa si mandino un numero di cyborg pari a Necessari+1
:- transitTroop(P, F1, F2, 0, C), player(P), factory(F2, P, N,_), #sum{C2,F2,F : arrivingTroop(P2,F,F2,_,C2), P2!=P} = S2, N > S2.
:- transitTroop(P, F1, F2, 0, C), #sum{C1,F2,F3 : arrivingTroop(P,F3,F2,_,C1)} = S1, player(P), factory(F2, P, N,_), #sum{C2,F2,F : arrivingTroop(P2,F,F2,_,C2), P2!=P} = S2, S2 > N, Necessari = S2 - N, Mandati = S1+C, Mandati > Necessari+1.

%ogni fabbrica manda unità di difesa solo a quelle che si trovano a un massimo di 5 unità di distanza
:- transitTroop(P, F1, F2, 0, C), player(P), factory(F2, P, _, _), edge(F1,F2,D), Dist = D, D>5, arrivingTroop(P2,_,F2,_,_), P!=P2.

%in totale da una fabbrica non possono uscire piu cyborgs di quelli disponibili
:- factory(F1, P, N, _), player(P), #sum{C,F2: transitTroop(P, F1, F2, 0, C)} = S, S>N.

%mantenere al massimo 3 cyborg, al fine di massimizzare le truppe da inviare
:~ player(P), factory(F1, P, N, Pr), #sum{C,F1,F2: transitTroop(P, F1, F2, 0, C)} = S, Z = N - S, Z >= 3. [Z@9,Z]

%non lasciarti morire, se vieni attaccato contraccambia
:~ arrivingTroop(P2,F1,F2,_,_), factory(F2,P,N,_), player(P), P!=P2, notTransitTroop(P,F2,F1,_,_), N!=0. [1@8,F2]

% Nel caso si rimane senza fabbriche faccio pagare
:~ #count{F : factory(F, P, _, _), player(P)} = C1, C1 = 0. [1@1]

%Preferisco gli answerset con meno notTransitTroop possibili
:~ notTransitTroop(P, F1, _, 0, C), player(P). [C@7, F1]

%Faccio attaccare la fabbrica con più cyborg, facendo pagare gli answer set che contengono fabbriche di partenza con meno cyborg.
:~ transitTroop(P, F1, _, 0, _), factory(F1, _, N, Prod), turn(T), player(P),  max = 30 + (Prod * T), Costo = max - N. [Costo@3, F1]

%Attaccare prima le fabbriche neutre
:~ transitTroop(P, _, F2, 0, C), factory(F2, Player, _, _), player(P), Player != 0. [C@6, F2]

%Attacco fabbriche con produzione maggiore non di mia proprietà
:~ transitTroop(P, _, F2, 0, _), factory(F2, P2, _, Pr), player(P), P!=P2, C = 3 - Pr. [C@4, F2]

%Attacco prima le fabbriche piu vicine
:~ transitTroop(P, F1, F2, 0, _), edge(F1, F2, D), player(P), factory(F2, Player, _, _), Player != P, Dist = D. [Dist@5, F1, F2]

%inviare piu cyborg possibili senza superare la soglia massima (per soglia massima intendiamo il numero di cyborg interni alla fabbrica attaccata piu 2 da giocare in difesa)
:~ transitTroop(P, _, F2, 0, C), player(P), factory(F2, P1, N, _), Costo = C-N+2, Costo > 0. [Costo@1, F2]

%calcolare la somma dei transitTroop del player(p) e selezionare gli answerset dove tale somma è pari
%al numero di cyborg attualmente presenti nella fabbrica e far pagare 3-Produzione,
%così se la produzione è massima può mandarli tutti e quindi paga 0.
:~ factory(F1, _, N, Pr), #sum{C,F2: transitTroop(P, F1, F2, 0, C), player(P)} = N, Costo = 3-Pr. [Costo@6, F1]

%se una fabbrica che voglio difendere è destinata a perdere
%(la somma dei cyborg che stanno per attaccarla è maggiore dei cyborg che io potrei mandare in difesa e
% la fabbrica avversaria che sta attaccando è più vicina rispetto alle fabbriche che potrebbero difenderla), lasciala all'avversario
:~ transitTroop(P, F1, F2, 0, C), factory(F2, P, N, _), player(P), edge(F1, F2, D), Dist = D + 2, #sum{C1,F3 : arrivingTroop(P1, F3, F2, _, C1), P1 != P, edge(F3, F2, D2), D2 <= Dist} = S, Difesa = C + N, S > N, X = S - Difesa, &abs(X; Z). [Z@2, Difesa]
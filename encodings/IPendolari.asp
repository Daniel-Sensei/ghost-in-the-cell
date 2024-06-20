%guess
transitTroop(P, F1, F2, 0, SC) | notTransitTroop(P, F1, F2, 0, SC) :- factory(F1, P, C, Prod), factory(F2, _, C2, Prod2), player(P), F1 != F2, SC = C/4, SC < C.

%non devono esistere transittroop con 0 cyborg
:- transitTroop(P, _, _, 0, 0), player(P).

%ogni fabbrica attacca 2 fabbriche alla volta
:~ #count{F2: transitTroop(_, _, F2, 0, _)} != 2. [1@6]
%prendiamo quello con meno notTransitTroop
:~ #count{P, F1, F2, SC: notTransitTroop(P, F1, F2, 0, SC)} = NTT. [NTT@5, NTT]
%preferiamo non rinforzare le nostre fabbriche
:~ transitTroop(P, _, F2, 0, _), factory(F2, P2, _, _), player(P), P2 == P. [1@4, P, F2, P2]
%preferiamo attaccare le fabbriche più vicine
:~ transitTroop(P, F1, F2, 0, _), edge(F1, F2, D), player(P). [D@2, P, F1, F2, D]
%preferiamo attaccare le fabbriche più produttive
:~ transitTroop(P, _, F2, 0, _), factory(F2, _, _, Prod), player(P). [4-Prod@3, P, F2, Prod]
%preferiamo attaccare le fabbriche che hanno meno cyborg a protezione
:~ transitTroop(P, _, F2, 0, _), factory(F2, _, C, _), player(P). [C@1, P, F2, C]
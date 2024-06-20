%%%INPUT%%%

%Player (1 or -1)
player(P).

%Current turn
turn(N)

%Factory_ID, Player (1, 0, -1), Number of cyborg, Production per turn
factory(ID, Player, N, Prod)

%Factory1, Factory2, Distance expressed in turn
edge(F1, F2, D)

%Player, Factory1, Factory2, Distance, Number of cyborg sent and contained in the current troop
arrivingTroop(Player, F1, F2, D, N)

%%%OUTPUT%%%
%Returns a list of "transit" predicates, each one representing a troop movement

%Player, Factory1, Factory2, Current Turn (starts by 0 when the move is done), Number of cyborg sent in a troop
transitTroop(Player, F1, F2, T, N)
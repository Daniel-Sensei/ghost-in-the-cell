%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% TURN 0 %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

% In the first turn we want to conquer the factories with the highest production and which can be conquered for sure.
% Also, we consider to pass through an intermediate factory in order to conquer as soon as possible the target factory. We will call this intermediate factory "Bridge", and the passing by it will be called "path".
% If we choose to use a path, we can get a discount of sent troops by considering the production of the Bridge.
path(F1, F2, F3, D, N13) :- turn(0), edge(F1, F2, D12), edge(F2, F3, D23), edge(F1, F3, D13), D = D12 + D23, D < D13, factory(F1, P, N1, _), player(P), factory(F2, 0, N2, Prod2), factory(F3, 0, N3, Prod3), Prod3 > 0, N13 = N2+1 + N23,  N23 = N3+1 - Prod2, N23>0.
path(F1, F2, F3, D, N13) :- turn(0), edge(F1, F2, D12), edge(F2, F3, D23), edge(F1, F3, D13), D = D12 + D23, D < D13, factory(F1, P, N1, _), player(P), factory(F2, 0, N2, Prod2), factory(F3, 0, N3, Prod3), Prod3 > 0, N13 = N2+1,  N23 = N3+1 - Prod2, N23<=0.
:~ path(F1, F2, F3, D, N13). [D@1, F1, F2, F3]

% We create two sends, one for the case in which we send troops from a factory to another and one for the case in which we use a path.
% We want to conquer a factory with the minimum number of troops possible (hence the contained troops +1).
% We consider to conquer only the factories which are near to us and which can be conquered for sure.
sendZero(F1, F2, N) | noSendZero(F1, F2) :- turn(0), player(P), factory(F1, P, N1, _), factory(F2, P2, N2, Prod2), P2 = 0, N1 > N2, Prod2 > 0, N = N2 +1, edge(F1, F2, D12), factory(F3, P3, _, _), P3=P*(-1), edge(F3, F2, D32), D32>=D12.
sendBridgeZero(F1, Bridge, F2, N) | noSendBridgeZero(F1, Bridge, F2) :- turn(0), player(P), factory(F1, P, N1, _), factory(F2, P2, _, Prod2), P2 = 0, N1 > N12, Prod2 > 0, N = N12 - 1, path(F1, Bridge, F2, D12, N12), factory(F3, P3, _, _), P3=P*(-1), edge(F3, F2, D32), D32>=D12.

% There could be only one kind of send for each couple of factories. (sendZero OR sendBridgeZero)
:- sendZero(F1, F2, _), sendBridgeZero(F1, _, F2, _).
:- sendZero(F1, F2, _), sendBridgeZero(F1, F2, _, _).

% The sum of the troops sent must be less or equal to the number of troops in the factory: I can't send 30 troops if I've only got 28.
:- #sum{NEdge, F1, F2: sendZero(F1, F2, NEdge)} = Sum1, #sum{NPath, F1, Bridge, F2: sendBridgeZero(F1, Bridge, F2, NPath)} = Sum2, factory(F1, P, N1, _), player(P), Sum1+Sum2 > N1.

%%%%%%%%%%%%%% WEAK CONSTRAINTS %%%%%%%%%%%%%%
%%% LVL 2 --> Maximize the production of the conquered factories.
%%% LVl 1 --> Minimize the distances of the sends. (Attack the nearest factories)
prodTot(X) :- #sum{Prod,F: factory(F, _, _, Prod)} = X.
:~ #sum{Prod,F2: factory(F2, _, _, Prod), sendZero(F1, F2, _)} = Sum1, #sum{Prod,F2: factory(F2, _, _, Prod), sendBridgeZero(F1, Bridge, F2, _)} = Sum2, prodTot(X). [X-Sum1-Sum2@2]
:~ sendZero(F1, F2, _), edge(F1, F2, D). [D@1, F1, F2]

%%%%%%%%%%%%%% OUTPUT %%%%%%%%%%%%%%
transitTroop(P, F1, F2, 0, N) :- sendZero(F1, F2, N), player(P).
transitTroop(P, F1, Bridge, 0, N) :- sendBridgeZero(F1, Bridge, F2, N), player(P).

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% END TURN 0 %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%



%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% TURN > 0 %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

% After the first turn, we have knowledge of the enemy's moves so we can consider them in our strategy.
% The following atoms are used to store the information about the troops that are going to arrive in a factory.

% TIPS: 
% F1 is usually intended ad the starting factory, F2 is the destination factory, 
% D is the distance between the two factories, N is the number of troops that are going to arrive in F2.
% A is the arrival time of the nearest/farthest attack. (How many turns it will take to arrive in F2)

%%% ENEMY TROOPS %%%
nearestEnemyAttack(F2, A, NTot):- player(P), factory(F2, _, _, _), arrivingTroop(P*(-1), _, F2, _, _), #min{D, F2: arrivingTroop(P*(-1), _, F2, D, _)} = A, #sum{N, F3, F2: arrivingTroop(P*(-1), F3, F2, A, N)} = NTot.
totalEnemyAttack(F2, NTot):- player(P), factory(F2, _, _, _), arrivingTroop(P*(-1), _, F2, _, _), #sum{N, F3, F2, Dist: arrivingTroop(P*(-1), F3, F2, Dist, N)} = NTot.

%%% ALLIED TROOPS %%%
nearestAlliedAttack(F2, A, NTot):- player(P), factory(F2, _, _, _), arrivingTroop(P, _, F2, _, _), #min{D, F2: arrivingTroop(P, _, F2, D, _)} = A, #sum{N, F3, F2: arrivingTroop(P, F3, F2, A, N)} = NTot.
farthestAlliedAttack(F2, A, NTot):- player(P), factory(F2, _, _, _), arrivingTroop(P, _, F2, _, _), #max{D, F2: arrivingTroop(P, _, F2, D, _)} = A, #sum{N, F3, F2, Dist: arrivingTroop(P, F3, F2, Dist, N)} = NTot.
totalAlliedAttack(F2, NTot) :- player(P), factory(F2, _, _, _), arrivingTroop(P, _, F2, _, _), #sum{N, F1, F2, Dist: arrivingTroop(P, F1, F2, Dist, N)} = NTot.

%%% SERVICE ATOMS %%%
underEnemyAttack(F) :- player(P), arrivingTroop(P*(-1), _, F, _, _).
underAlliedAttack(F) :- player(P), arrivingTroop(P, _, F, _, _).


%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% STRATEGY EXPLANATION %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

% The SUPPORT operation are managed thanks to the implementation of a sort of "Trench" strategy.
% The Trench strategy is based on the idea of creating a sort of "domino effect".
% We do this by considering as "inTrench" the factory whose distance from the nearest enemy factory is lower or equal to the average.
% All the other factory are going to send all of their troops to the weakest factory in the Trench.
% Using these method we grant that the factories which are far from the conflicts are going to be used only to produce troops and to support the factories on the front line making themselfs way more useful.
% The effectiveness of this strategy lies in the fact that being in the Trench allows us to:
% 1) Attack enemy factory faster, so they'll produce less cyborgs and we'll need less cyborgs as well to conquer it.
% 2) Defend our factories more effectively, since our Trench will be in almost all cases nearer than the enemy.

% tl;dr
% the factory far from the front line are going to produce troops and support the factories on the front line.
% The factories on the front line are going to attack the enemy factories or defend our own.

% In general we create 4 different types of sends:
% 1) atkS: send troops from a SINGLE factory to another in order to conquer it.
% 2) atkM: send troops from MULTIPLE factories to another in order to conquer it.
% 3) def: send troops from a single factory (or from multiple factories) to another in order to defend it.
% 4) support: send troops from a factory to another in order to support it (boost the number of cyborgs inside it).



%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% ALMOST CONQUERED/LOST %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

% The logic behind the almostConquered and almostLost atoms works by checking different factors such as:
% 1) the number of allied cyborgs that are currently traveling to a factory.
% 2) the number of enemy cyborgs that are currently traveling to the factory.
% 3) the number of cyborgs contained in that factory.
% 4) the production of said factory (may be ignored in some cases).


% With the "almostConquered" atoms we want to consider the factories that are almost conquered by us.

%%% ENEMY FACTORY %%%

% There are enemy troops incoming.
almostConquered(F2) :- player(P), factory(F2, P2, N2, Prod2), P2=P*(-1), underEnemyAttack(F2), underAlliedAttack(F2), totalEnemyAttack(F2, EnemyN), farthestAlliedAttack(F2, AlliedA, AlliedN), AlliedN > N2+EnemyN+(AlliedA*Prod2).
% There are no enemy troops incoming.
almostConquered(F2) :- player(P), factory(F2, P2, N2, Prod2), P2=P*(-1), not underEnemyAttack(F2), underAlliedAttack(F2), farthestAlliedAttack(F2, AlliedA, AlliedN), AlliedN > N2+(AlliedA*Prod2).

%%% NEUTRAL FACTORY %%%

% We arrive before or with the enemy (we can't consider a discount based on the production of the factory)
almostConquered(F2) :- player(P), factory(F2, P2, N2, Prod2), P2=0, underEnemyAttack(F2), underAlliedAttack(F2), totalEnemyAttack(F2, EnemyN), nearestEnemyAttack(F2, EnemyA, _), farthestAlliedAttack(F2, AlliedA, AlliedN), AlliedA<=EnemyA, AlliedN>EnemyN+N2.
% We arrive after the enemy (we need to add the production of the factory to the number of troops that are needed to conquer it)
almostConquered(F2) :- player(P), factory(F2, P2, N2, Prod2), P2=0, underEnemyAttack(F2), underAlliedAttack(F2), totalEnemyAttack(F2, EnemyN), nearestEnemyAttack(F2, EnemyA, _), farthestAlliedAttack(F2, AlliedA, AlliedN), AlliedA>EnemyA, AlliedN>(EnemyN-N2)+(AlliedA-EnemyA)*Prod2.
% There are no enemy troops incoming, only allied troops
almostConquered(F2) :- player(P), factory(F2, P2, N2, _), P2=0, not underEnemyAttack(F2), underAlliedAttack(F2), totalAlliedAttack(F2, N), N>N2.


% With the almostLost atoms we want to consider the factories that are almost conquered by the enemy.

%%% ALLIED FACTORY %%%

% There are enemy troops incoming and we are not defending said factory.
almostLost(F2) :- player(P), factory(F2, P, N2, Prod2), underEnemyAttack(F2), not underAlliedAttack(F2), totalEnemyAttack(F2, EnemyN), nearestEnemyAttack(F2, EnemyA, _), N2+(Prod2*EnemyA)<EnemyN.
% There are enemy troops incoming and we are defending said factory.
almostLost(F2) :- player(P), factory(F2, P, N2, Prod2), underEnemyAttack(F2), underAlliedAttack(F2), totalEnemyAttack(F2, EnemyN), nearestEnemyAttack(F2, EnemyA, _), farthestAlliedAttack(F2, AlliedA, AlliedN), AlliedN+N2+(Prod2*EnemyA)<EnemyN.


%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% SUPPORT %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

% We calculate the average distance between our factoris and enemy's factories.
edgeMin(F1, MinD) :- player(P), #min{D, F1, F2: edge(F1, F2, D), factory(F2, P*(-1), _, _)} = MinD, factory(F1, P, _, _).
avgDistance(D):- player(P), #sum{MinD, F1: edgeMin(F1, MinD)} = Sum, #count{F1 : factory(F1, P, _, _)} = C, D = Sum/C.

futureTroop(F2, N) :- player(P), turn(T), T > 0, factory(F2, P, N2, Prod2), underAlliedAttack(F2), nearestAlliedAttack(F2, A, _), totalAlliedAttack(F2, NTot), N = N2+NTot+(Prod2*A).
futureTroop(F2, N2) :- player(P), turn(T), T > 0, factory(F2, P, N2, _), not underAlliedAttack(F2).

% Is similar to the almostConquered atom but we consider the case in which the factory is going to be supported by the future troops.
alreadySupported(F2) :- player(P), turn(T), T > 0, factory(F2, P, _, _), inTrench(F2), #min{N3, F3 : futureTroop(F3, N3), inTrench(F3)} = NMin, futureTroop(F2, N), N > NMin.
% A factory is in Trench if the minimum distance between it and the enemy's factories is less or equal to the average distance.
inTrench(F2) :- player(P), turn(T), T > 0, factory(F2, P, _, _), edgeMin(F2, MinD2), avgDistance(AVG), MinD2<=AVG.

% If a factory produces troops, it stops supporting if it is under attack. (We don't want to loose a production factory)
support(F1, F2, N) | noSupport(F1, F2) :- turn(T), T > 0, player(P), factory(F1, P, N, Prod1), N > 0, Prod1 <> 0, not underEnemyAttack(F1), not inTrench(F1), factory(F2, P, _, _), inTrench(F2), not alreadySupported(F2).
% If a factory doesn't produce troops, it continues to support even if it is under attack. (We consider it a "sacrificiable" factory to wast enemy forces)
support(F1, F2, N) | noSupport(F1, F2) :- turn(T), T > 0, player(P), factory(F1, P, N, Prod1), N > 0, Prod1 = 0, not inTrench(F1), factory(F2, P, _, _), inTrench(F2), not alreadySupported(F2).

% Technically only a factory not in Trench can support another.
% Technically only a factory in Trench can be supported by another.
% But we want to be sure to not have missed some cases in the guesses 
% So we'll leave this constraint to avoid the case in which two factories support each other.
:- support(F1, F2, _), support(F2, F1, _), F1<>F2.



%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% ATK %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

% The attack is divided in two types: single (atkS) and multiple (atkM).
% All the attacks are generated ONLY by the factories in Trench.
% A factory in Trench can't attack if it is under enemy attack.
% A factory CAN be attacked ONLY if it is NOT almostConquered.


%%%%%%%%%%%% ATK-S %%%%%%%%%%%%

%% F2 has no attacks incoming from either player %%
atkS(F1, F2, N) | noAtkS(F1, F2) :- not underEnemyAttack(F1), turn(T), T > 0, player(P), not almostConquered(F2), not underEnemyAttack(F2), not underAlliedAttack(F2), factory(F1, P, N1, _), inTrench(F1), N1 >= N, factory(F2, P*(-1), N2, Prod2), edge(F1, F2, D), N = N2+1+(Prod2*D).
atkS(F1, F2, N) | noAtkS(F1, F2) :- not underEnemyAttack(F1), turn(T), T > 0, player(P), not almostConquered(F2), not underEnemyAttack(F2), not underAlliedAttack(F2), factory(F1, P, N1, _), inTrench(F1), N1 >= N, factory(F2, 0, N2, Prod2), N = N2+1.

%% F2 is currently under enemy attack %%
% We consider the production of the ENEMY factory we are attacking
atkS(F1, F2, N) | noAtkS(F1, F2) :- not underEnemyAttack(F1), turn(T), T > 0, player(P), not almostConquered(F2), underEnemyAttack(F2), not underAlliedAttack(F2), factory(F1, P, N1, _), inTrench(F1), N1 >= N, factory(F2, P*(-1), N2, Prod2), edge(F1, F2, D), totalEnemyAttack(F2, EnemyN), N = N2+1+(Prod2*D)+EnemyN.
% We give ourselves a discount because we arrive to the NEUTRAL factory before the enemy and we'll start producing cyborgs
atkS(F1, F2, N) | noAtkS(F1, F2) :- not underEnemyAttack(F1), turn(T), T > 0, player(P), not almostConquered(F2), underEnemyAttack(F2), not underAlliedAttack(F2), factory(F1, P, N1, _), inTrench(F1), N1 >= N, factory(F2, 0, N2, Prod2), nearestEnemyAttack(F2, EnemyA, _), totalEnemyAttack(F2, EnemyN), edge(F1, F2, D), D <= EnemyA, N = N2+1+EnemyN-((EnemyA-D)*Prod2). %discount because we arrive before the enemy
% We need to add some troops because we arrive to the NEUTRAL factory after the enemy so they'll start to produce cyborgs
atkS(F1, F2, N) | noAtkS(F1, F2) :- not underEnemyAttack(F1), turn(T), T > 0, player(P), not almostConquered(F2), underEnemyAttack(F2), not underAlliedAttack(F2), factory(F1, P, N1, _), inTrench(F1), N1 >= N, factory(F2, 0, N2, Prod2), nearestEnemyAttack(F2, EnemyA, _), totalEnemyAttack(F2, EnemyN), edge(F1, F2, D), D > EnemyA, N = N2+1+EnemyN+((D-EnemyA)*Prod2). %add because we arrive after the enemy

%% Both players are attacking an enemy factory %%
% The allied troops that are already traveling will arrive after or together with the enemy's one so we can't do any discount
atkS(F1, F2, N) | noAtkS(F1, F2) :- not underEnemyAttack(F1), turn(T), T > 0, player(P), not almostConquered(F2), underEnemyAttack(F2), underAlliedAttack(F2), factory(F1, P, N1, _), inTrench(F1), N1 >= N, factory(F2, P*(-1), N2, Prod2), nearestEnemyAttack(F2, EnemyA, _), totalEnemyAttack(F2, EnemyN), nearestAlliedAttack(F2, AlliedA, _), totalAlliedAttack(F2, AlliedN), edge(F1, F2, D), AlliedA < EnemyA, D <= EnemyA, N = N2+1-((EnemyA-AlliedA)*Prod2)+EnemyN-AlliedN.
% The allied troops that are already traveling will arrive before the enemy's one so we can give ourselfs a discount
% We consider to add or remove any more troops based on the distance between THIS attacking factory and the NEAREST enemy attack
atkS(F1, F2, N) | noAtkS(F1, F2) :- not underEnemyAttack(F1), turn(T), T > 0, player(P), not almostConquered(F2), underEnemyAttack(F2), underAlliedAttack(F2), factory(F1, P, N1, _), inTrench(F1), N1 >= N, factory(F2, P*(-1), N2, Prod2), nearestEnemyAttack(F2, EnemyA, _), totalEnemyAttack(F2, EnemyN), nearestAlliedAttack(F2, AlliedA, _), totalAlliedAttack(F2, AlliedN), edge(F1, F2, D), AlliedA < EnemyA, D > EnemyA, N = N2+1-((EnemyA-AlliedA)*Prod2)+((D-EnemyA)*Prod2)+EnemyN-AlliedN.
atkS(F1, F2, N) | noAtkS(F1, F2) :- not underEnemyAttack(F1), turn(T), T > 0, player(P), not almostConquered(F2), underEnemyAttack(F2), underAlliedAttack(F2), factory(F1, P, N1, _), inTrench(F1), N1 >= N, factory(F2, P*(-1), N2, Prod2), nearestEnemyAttack(F2, EnemyA, _), totalEnemyAttack(F2, EnemyN), nearestAlliedAttack(F2, AlliedA, _), totalAlliedAttack(F2, AlliedN), edge(F1, F2, D), AlliedA >= EnemyA, N = N2+1+(D*Prod2)+EnemyN-AlliedN.

%% Both players are attacking a neutral factory %%
% These contains combinations of discounts and increases based on all the calculated distances as before
atkS(F1, F2, N) | noAtkS(F1, F2) :- not underEnemyAttack(F1), turn(T), T > 0, player(P), not almostConquered(F2), underEnemyAttack(F2), underAlliedAttack(F2), factory(F1, P, N1, _), inTrench(F1), N1 >= N, factory(F2, 0, N2, Prod2), nearestEnemyAttack(F2, EnemyA, _), totalEnemyAttack(F2, EnemyN), nearestAlliedAttack(F2, AlliedA, _), totalAlliedAttack(F2, AlliedN), edge(F1, F2, D), AlliedA < EnemyA, D <= EnemyA, N = N2+1+EnemyN-AlliedN-((EnemyA-AlliedA)*Prod2)-((EnemyA-D)*Prod2). %discount because we arrive before the enemy
atkS(F1, F2, N) | noAtkS(F1, F2) :- not underEnemyAttack(F1), turn(T), T > 0, player(P), not almostConquered(F2), underEnemyAttack(F2), underAlliedAttack(F2), factory(F1, P, N1, _), inTrench(F1), N1 >= N, factory(F2, 0, N2, Prod2), nearestEnemyAttack(F2, EnemyA, _), totalEnemyAttack(F2, EnemyN), nearestAlliedAttack(F2, AlliedA, _), totalAlliedAttack(F2, AlliedN), edge(F1, F2, D), AlliedA < EnemyA, D > EnemyA, N = N2+1+EnemyN-AlliedN-((EnemyA-AlliedA)*Prod2)+((D-EnemyA)*Prod2). %add because we arrive after the enemy
atkS(F1, F2, N) | noAtkS(F1, F2) :- not underEnemyAttack(F1), turn(T), T > 0, player(P), not almostConquered(F2), underEnemyAttack(F2), underAlliedAttack(F2), factory(F1, P, N1, _), inTrench(F1), N1 >= N, factory(F2, 0, N2, Prod2), nearestEnemyAttack(F2, EnemyA, _), totalEnemyAttack(F2, EnemyN), nearestAlliedAttack(F2, AlliedA, _), totalAlliedAttack(F2, AlliedN), edge(F1, F2, D), EnemyA <= AlliedA, D <= EnemyA, N = N2+1+EnemyN-AlliedN+((AlliedA-EnemyA)*Prod2)-((EnemyA-D)*Prod2). %discount because we arrive before the enemy
atkS(F1, F2, N) | noAtkS(F1, F2) :- not underEnemyAttack(F1), turn(T), T > 0, player(P), not almostConquered(F2), underEnemyAttack(F2), underAlliedAttack(F2), factory(F1, P, N1, _), inTrench(F1), N1 >= N, factory(F2, 0, N2, Prod2), nearestEnemyAttack(F2, EnemyA, _), totalEnemyAttack(F2, EnemyN), nearestAlliedAttack(F2, AlliedA, _), totalAlliedAttack(F2, AlliedN), edge(F1, F2, D), EnemyA <= AlliedA, D > EnemyA, N = N2+1+EnemyN-AlliedN+((AlliedA-EnemyA)*Prod2)+((D-EnemyA)*Prod2). %add because we arrive after the enemy

% F2 can be attacked ONLY by one factory
:- atkS(F1, F2, _), atkS(F3, F2, _), F1 <> F3.


%%%%%%%%%%%% ATK-M %%%%%%%%%%%%
% The following atom is used because we want to generate atkM only if there are not atkS directed to F2.
% This helps us with the MULTIPLE ATTACK STRATEGY: if we can't conquer it alone maybe we can together.
atkd(F2):- atkS(_, F2, _).

% TIPS:
% Y=X+1, &int(X,Y;N) is used because we want to conquer a factory with the minimum number of troops possible.
% By using this method we grant the fact that the factories F1 involved into a multiple attack send the same number of troops to F2, 
% except for a factory which will send one more troop in order to conquer it.

% In the following we implemented discounts and increases based on a variation of the previous cases.

% No attack incoming from either player to an enemy factory         
atkM(F1, F2, N) | noAtkM(F1, F2, N) :- not underEnemyAttack(F1), turn(T), T > 0, player(P), not atkd(F2), not almostConquered(F2), not underEnemyAttack(F2), not underAlliedAttack(F2), factory(F1, P, N1, _), inTrench(F1), edge(F1, F2, D12), D12 <= AVG, N1 >= N, factory(F2, P*(-1), N2, Prod2), #count{F3: inTrench(F3), edge(F3, F2, D32), D32 <= AVG, not underEnemyAttack(F3)} = C, avgDistance(AVG), X = (N2+(Prod2*AVG))/C, Y=X+1, &int(X,Y;N).
:- #sum{N, F1, F2: atkM(F1, F2, N)} = Sum, player(P), factory(F2, P*(-1), N2, Prod2), not underEnemyAttack(F2), not underAlliedAttack(F2), avgDistance(AVG), atkM(_, F2, _), Sum<>N2+(Prod2*AVG)+1.

% Enemy is supporting its factory
atkM(F1, F2, N) | noAtkM(F1, F2, N) :- not underEnemyAttack(F1), turn(T), T > 0, player(P), not atkd(F2), not almostConquered(F2), underEnemyAttack(F2), not underAlliedAttack(F2), factory(F1, P, N1, _), inTrench(F1), edge(F1, F2, D12), D12 <= AVG, N1 >= N, factory(F2, P*(-1), N2, Prod2), #count{F3: inTrench(F3), edge(F3, F2, D32), D32 <= AVG, not underEnemyAttack(F3)} = C, avgDistance(AVG), totalEnemyAttack(F2, EnemyN), X = (N2+EnemyN+(Prod2*AVG))/C, Y=X+1, &int(X,Y;N).
:- #sum{N, F1, F2: atkM(F1, F2, N)} = Sum, player(P), factory(F2, P*(-1), N2, Prod2), underEnemyAttack(F2), not underAlliedAttack(F2), avgDistance(AVG), atkM(_, F2, _), totalEnemyAttack(F2, EnemyN), Sum<>N2+EnemyN+(Prod2*AVG)+1.

% Both players are attacking the enemy factory
atkM(F1, F2, N) | noAtkM(F1, F2, N) :- not underEnemyAttack(F1), turn(T), T > 0, player(P), not atkd(F2), not almostConquered(F2), underEnemyAttack(F2), underAlliedAttack(F2), factory(F1, P, N1, _), inTrench(F1), N1 >= N, edge(F1, F2, D12), D12 <= AVG, factory(F2, P*(-1), N2, Prod2), #count{F3: inTrench(F3), edge(F3, F2, D32), D32 <= AVG, not underEnemyAttack(F3)} = C, avgDistance(AVG), totalEnemyAttack(F2, EnemyN), totalAlliedAttack(F2, AlliedN), X = (N2+EnemyN-AlliedN+(Prod2*AVG))/C, Y=X+1, &int(X,Y;N).
:- #sum{N, F1, F2: atkM(F1, F2, N)} = Sum, player(P), factory(F2, P*(-1), N2, Prod2), underEnemyAttack(F2), underAlliedAttack(F2), avgDistance(AVG), atkM(_, F2, _), totalEnemyAttack(F2, EnemyN), totalAlliedAttack(F2, AlliedN), Sum<>N2+EnemyN-AlliedN+(Prod2*AVG)+1.

% No attack incoming from both player to a neutral factory
atkM(F1, F2, N) | noAtkM(F1, F2, N) :- not underEnemyAttack(F1), turn(T), T > 0, player(P), not atkd(F2), not almostConquered(F2), not underEnemyAttack(F2), not underAlliedAttack(F2), factory(F1, P, N1, _), inTrench(F1), edge(F1, F2, D12), D12 <= AVG, N1 >= N, factory(F2, 0, N2, Prod2), #count{F3: inTrench(F3), edge(F3, F2, D32), D32 <= AVG, not underEnemyAttack(F3)} = C, avgDistance(AVG), X = N2/C, Y=X+1, &int(X,Y;N).
:- #sum{N, F1, F2: atkM(F1, F2, N)} = Sum, factory(F2, 0, N2, _), not underEnemyAttack(F2), not underAlliedAttack(F2), atkM(_, F2, _), Sum<>N2+1.

% Enemy is attacking a neutral factory
atkM(F1, F2, N) | noAtkM(F1, F2, N) :- not underEnemyAttack(F1), turn(T), T > 0, player(P), not atkd(F2), not almostConquered(F2), underEnemyAttack(F2), not underAlliedAttack(F2), factory(F1, P, N1, _), inTrench(F1), edge(F1, F2, D12), D12 <= AVG, N1 >= N, factory(F2, 0, N2, Prod2), #count{F3: inTrench(F3), edge(F3, F2, D32), D32 <= AVG, not underEnemyAttack(F3)} = C, avgDistance(AVG), nearestEnemyAttack(F2, EnemyA, _), totalEnemyAttack(F2, EnemyN), EnemyA < AVG, X = (N2+EnemyN+(Prod2*(AVG-EnemyA)))/C, Y=X+1, &int(X,Y;N).
:- #sum{N, F1, F2: atkM(F1, F2, N)} = Sum, player(P), factory(F2, P*(-1), N2, Prod2), underEnemyAttack(F2), not underAlliedAttack(F2), avgDistance(AVG), atkM(_, F2, _), nearestEnemyAttack(F2, EnemyA, _), totalEnemyAttack(F2, EnemyN), EnemyA < AVG, Sum<>N2+EnemyN+(Prod2*(AVG-EnemyA))+1.
atkM(F1, F2, N) | noAtkM(F1, F2, N) :- not underEnemyAttack(F1), turn(T), T > 0, player(P), not atkd(F2), not almostConquered(F2), underEnemyAttack(F2), not underAlliedAttack(F2), factory(F1, P, N1, _), inTrench(F1), edge(F1, F2, D12), D12 <= AVG, N1 >= N, factory(F2, 0, N2, Prod2), #count{F3: inTrench(F3), edge(F3, F2, D32), D32 <= AVG, not underEnemyAttack(F3)} = C, avgDistance(AVG), nearestEnemyAttack(F2, EnemyA, _), totalEnemyAttack(F2, EnemyN), EnemyA >= AVG, X = (N2+EnemyN-(Prod2*(EnemyA-AVG)))/C, Y=X+1, &int(X,Y;N).
:- #sum{N, F1, F2: atkM(F1, F2, N)} = Sum, player(P), factory(F2, P*(-1), N2, Prod2), underEnemyAttack(F2), not underAlliedAttack(F2), avgDistance(AVG), atkM(_, F2, _), nearestEnemyAttack(F2, EnemyA, _), totalEnemyAttack(F2, EnemyN), EnemyA >= AVG, Sum<>N2+EnemyN-(Prod2*(EnemyA-AVG))+1.

% Both players are attacking a neutral factory
atkM(F1, F2, N) | noAtkM(F1, F2, N) :- not underEnemyAttack(F1), turn(T), T > 0, player(P), not atkd(F2), not almostConquered(F2), underEnemyAttack(F2), underAlliedAttack(F2), factory(F1, P, N1, _), inTrench(F1), edge(F1, F2, D12), D12 <= AVG, N1 >= N, factory(F2, 0, N2, Prod2), #count{F3: inTrench(F3), edge(F3, F2, D32), D32 <= AVG, not underEnemyAttack(F3)} = C, avgDistance(AVG), nearestEnemyAttack(F2, EnemyA, _), totalEnemyAttack(F2, EnemyN), totalAlliedAttack(F2, AlliedN), EnemyA < AVG, X = (N2+EnemyN-AlliedN+(Prod2*(AVG-EnemyA)))/C, Y=X+1, &int(X,Y;N).
:- #sum{N, F1, F2: atkM(F1, F2, N)} = Sum, player(P), factory(F2, P*(-1), N2, Prod2), underEnemyAttack(F2), underAlliedAttack(F2), avgDistance(AVG), atkM(_, F2, _), nearestEnemyAttack(F2, EnemyA, _), totalEnemyAttack(F2, EnemyN), totalAlliedAttack(F2, AlliedN), EnemyA < AVG, Sum<>N2+EnemyN-AlliedN+(Prod2*(AVG-EnemyA))+1.
atkM(F1, F2, N) | noAtkM(F1, F2, N) :- not underEnemyAttack(F1), turn(T), T > 0, player(P), not atkd(F2), not almostConquered(F2), underEnemyAttack(F2), underAlliedAttack(F2), factory(F1, P, N1, _), inTrench(F1), edge(F1, F2, D12), D12 <= AVG, N1 >= N, factory(F2, 0, N2, Prod2), #count{F3: inTrench(F3), edge(F3, F2, D32), D32 <= AVG, not underEnemyAttack(F3)} = C, avgDistance(AVG), nearestEnemyAttack(F2, EnemyA, _), totalEnemyAttack(F2, EnemyN), totalAlliedAttack(F2, AlliedN), EnemyA >= AVG, X = (N2+EnemyN-AlliedN-(Prod2*(EnemyA-AVG)))/C, Y=X+1, &int(X,Y;N).
:- #sum{N, F1, F2: atkM(F1, F2, N)} = Sum, player(P), factory(F2, P*(-1), N2, Prod2), underEnemyAttack(F2), underAlliedAttack(F2), avgDistance(AVG), atkM(_, F2, _), nearestEnemyAttack(F2, EnemyA, _), totalEnemyAttack(F2, EnemyN), totalAlliedAttack(F2, AlliedN), EnemyA >= AVG, Sum<>N2+EnemyN-AlliedN-(Prod2*(EnemyA-AVG))+1.

% Technically this should already be excluded by using the "atkd()" atom, but we want to be sure to not have missed some cases in the guesses
:- atkS(_, F2, _), atkM(_, F2, _).
% We don't want to use the MULTIPLE ATTACK STRATEGY on an empty factory,
% We know we could put this into the guesses but we preferred to leave it here for readability purposes.
:- atkM(_, F2, _), factory(F2, _, 0, _).
% We generate only a send to F2 for each factory F1 involved in a multiple attack. (We don't want to send too many troops to F2)
% Once again: this should already be excluded but we want to be sure to not have missed some cases in the guesses
:- atkM(F1, F2, N1), atkM(F1, F2, N2), N1 <> N2.



%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% DEF %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% The defense is used to defend a factory from the enemy's attack.
% It is more similar to the atkM than atkS because we want to defend a factory with all the F1 in Trench. (Faster and Cheaper)

def(F1, F2, N) | noDef(F1, F2, N) :- turn(T), T > 0, player(P), almostLost(F2), underEnemyAttack(F2), not underAlliedAttack(F2), factory(F1, P, N1, _), inTrench(F1), edge(F1, F2, D12), nearestEnemyAttack(F2, EnemyA, EnemyN), D12 <= EnemyA, N1 >= N, factory(F2, P, N2, Prod2), #count{F3: inTrench(F3), edge(F3, F2, D32), D32 <= EnemyA} = C, X = (EnemyN-(N2+(Prod2*EnemyA)))/C, Y=X+1, &int(X,Y;N).
:- #sum{N, F1, F2: def(F1, F2, N)} = Sum, player(P), factory(F2, P, N2, Prod2), underEnemyAttack(F2), not underAlliedAttack(F2), nearestEnemyAttack(F2, EnemyA, EnemyN), def(_, F2, _), Sum<>EnemyN-(N2+(Prod2*EnemyA))+1.

def(F1, F2, N) | noDef(F1, F2, N) :- turn(T), T > 0, player(P), almostLost(F2), underEnemyAttack(F2), underAlliedAttack(F2), factory(F1, P, N1, _), inTrench(F1), edge(F1, F2, D12), nearestEnemyAttack(F2, EnemyA, EnemyN), nearestAlliedAttack(F2, AlliedA, AlliedN), D12 <= EnemyA, AlliedA <= EnemyA, N1 >= N, factory(F2, P, N2, Prod2), #count{F3: inTrench(F3), edge(F3, F2, D32), D32 <= EnemyA} = C, X = (EnemyN-AlliedN-(N2+(Prod2*EnemyA)))/C, Y=X+1, &int(X,Y;N).
:- #sum{N, F1, F2: def(F1, F2, N)} = Sum, player(P), factory(F2, P, N2, Prod2), underEnemyAttack(F2), underAlliedAttack(F2), nearestEnemyAttack(F2, EnemyA, EnemyN), nearestAlliedAttack(F2, AlliedA, AlliedN), AlliedA <= EnemyA, def(_, F2, _), Sum<>EnemyN-AlliedN-(N2+(Prod2*EnemyA))+1.

def(F1, F2, N) | noDef(F1, F2, N) :- turn(T), T > 0, player(P), almostLost(F2), underEnemyAttack(F2), underAlliedAttack(F2), factory(F1, P, N1, _), inTrench(F1), edge(F1, F2, D12), nearestEnemyAttack(F2, EnemyA, EnemyN), nearestAlliedAttack(F2, AlliedA, AlliedN), D12 <= EnemyA, AlliedA > EnemyA, N1 >= N, factory(F2, P, N2, Prod2), #count{F3: inTrench(F3), edge(F3, F2, D32), D32 <= EnemyA} = C, X = (EnemyN-(N2+(Prod2*EnemyA)))/C, Y=X+1, &int(X,Y;N).
:- #sum{N, F1, F2: def(F1, F2, N)} = Sum, player(P), factory(F2, P, N2, Prod2), underEnemyAttack(F2), underAlliedAttack(F2), nearestEnemyAttack(F2, EnemyA, EnemyN), nearestAlliedAttack(F2, AlliedA, AlliedN), AlliedA > EnemyA, def(_, F2, _), Sum<>EnemyN-(N2+(Prod2*EnemyA))+1.

:- def(_, _, N), N = 0.



%%%%%%%%%%%%%%%%%%%%%%%%%% OPTIMIZATION & OUTPUT %%%%%%%%%%%%%%%%%%%%%%%%%%

% We merge the support, atkS, atkM and def predicates in a single predicate called send.
% We optimize all the moves done by considering this atom.
send(F1, F2, N) :- support(F1, F2, N), turn(T), T > 0.

send(F1, F2, N) :- atkS(F1, F2, N), turn(T), T > 0.
send(F1, F2, N) :- atkM(F1, F2, N), turn(T), T > 0.

send(F1, F2, N) :- def(F1, F2, N), turn(T), T > 0.

% It is not possible to send 0 cyborgs from F1 to F2. (would be counted as an invalid move and would skip the turn)
:- send(F1, F2, 0).

% Generated for safety reason in the optimization process. (when "send()" had disjuctive rules)
sent(F1, F2) :- send(F1, F2, _).

% Checking if The sum of the sent troops from F1 to F2 is less or equal to the number of troops in F1.
% You can't send more troops than you own!
sentSum(F1, Sum):- sent(F1, _), #sum{N, F1, F2: send(F1, F2, N)} = Sum, factory(F1, P, N1, _), player(P).
:- sentSum(F1, Sum), factory(F1, P, N1, _), player(P), Sum > N1.


%%%%%%%%%%%%%% WEAK CONSTRAINTS %%%%%%%%%%%%%%
%%% LVL 2 --> Maximize the production of the factories
%%% LVl 1 --> Minimize the distances of the sends. (Attack with the nearest factories)

maxDist(D):- #max{Dist: edge(_, _, Dist)} = D.

% We pay based on the sum of the production of the factories that we didn't attack/defend/support.
:~ #sum{Prod,F2: factory(F2, _, _, Prod)} = Sum, not sent(F1, F2), edge(F1, F2, _), turn(T), T > 0. [Sum@2, F1,F2]

% We pay based on the distance of the sends. (We want to minimize the distance so we do Maximum Distance - Send Distance)
:~ not sent(F1, F2), edge(F1, F2, D), turn(T), T > 0, maxDist(DIST). [DIST-D@1, F1, F2]

% transitTroop() is the atom expected by Java to send the troops.
% All the new transitTroops starts by turn 0.
transitTroop(P, F1, F2, 0, N) :- send(F1, F2, N), player(P).
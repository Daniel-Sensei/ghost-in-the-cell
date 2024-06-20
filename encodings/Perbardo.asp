transitTroop(P, FacS, FacT, 0, CybAtt) | notransitTroop(P, FacS, FacT, 0, CybAtt) :- factory(FacS, P, CybS, ProdS), factory(FacT, _, CybT, ProdT), player(P), FacS != FacT, CybAtt = CybS/5, CybAtt < CybS, CybAtt != 0.
% Generazione AnswerSets, Lista di Attachi da Intraprendere (transitTroop)



:~ transitTroop(P, FacS, FacT, 0, CybAtt), factory(FacT, _, CybT, ProdT), player(P). [CybAtt@2, P, FacT, CybAtt]
:~ transitTroop(P, FacS, FacT, 0, CybAtt), factory(FacT, _, CybT, ProdT), player(P). [ProdT-4@3, P, FacT, ProdT]
:~ transitTroop(P, FacS, FacT, 0, CybAtt), edge(FacS, FacT, Dist), player(P). [Dist@4, P, FacS, FacT, Dist]
:~ #count{P, FacS, FacT, CybAtt: notransitTroop(P, FacS, FacT, 0, CybAtt)} = InvTra. [InvTra@6, InvTra]
:~ #count{FacT: transitTroop(_, _, FacT, 0, _)} != 3. [1@7]
:~ transitTroop(P, FacS, FacT, 0, CybAtt), factory(FacT, P, CybT, ProdT). [1@5, P, FacT]
% Weak Costraints:
%  Prioritizzare Factory meno protette
%  Prioritizzare Factory produttive
%  Prioritizzare Factory vicine
%  Massimizzare il numero di attacchi inviati in un turno
%  Preferire AS con Attacchi composti da 3 transitTroop (per Factory)
%  Disincentivare rinforzo delle fabriche gia possedute
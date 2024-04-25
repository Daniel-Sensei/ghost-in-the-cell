package org.example.model.objects;

import it.unical.mat.embasp.languages.Id;
import it.unical.mat.embasp.languages.Param;

@Id("transitTroop")
public class TransitTroop {

    @Param(0)
    private int player;

    @Param(1)
    private int f1; // id_factory1

    @Param(2)
    private int f2; // id_factory2

    @Param(3)
    private int currentTurn;

    @Param(4)
    private int cyborgs;

    public TransitTroop() {
    }

    public TransitTroop(int player, int f1, int f2, int currentTurn, int cyborgs) {
        this.player = player;
        this.f1 = f1;
        this.f2 = f2;
        this.currentTurn = currentTurn;
        this.cyborgs = cyborgs;
    }

    public int getPlayer() {
        return player;
    }

    public void setPlayer(int player) {
        this.player = player;
    }

    public int getF1() {
        return f1;
    }

    public void setF1(int f1) {
        this.f1 = f1;
    }

    public int getF2() {
        return f2;
    }

    public void setF2(int f2) {
        this.f2 = f2;
    }

    public int getCurrentTurn() {
        return currentTurn;
    }

    public void setCurrentTurn(int currentTurn) {
        this.currentTurn = currentTurn;
    }

    public int getCyborgs() {
        return cyborgs;
    }

    public void setCyborgs(int cyborgs) {
        this.cyborgs = cyborgs;
    }

    @Override
    public String toString() {
        return "transitTroop(" + player + "," + f1 + "," + f2 + "," + currentTurn + "," + cyborgs + ")";
    }
}

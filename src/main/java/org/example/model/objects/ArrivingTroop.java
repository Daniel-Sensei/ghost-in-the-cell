package org.example.model.objects;

import it.unical.mat.embasp.languages.Id;
import it.unical.mat.embasp.languages.Param;

@Id("arrivingTroop")
public class ArrivingTroop {
    @Param(0)
    private int player;

    @Param(1)
    private int f1; // id_factory1

    @Param(2)
    private int f2; // id_factory2

    @Param(3)
    private int distance;

    @Param(4)
    private int cyborgs;

    public ArrivingTroop(){
    }

    public ArrivingTroop(TransitTroop transitTroop){
        player = transitTroop.getPlayer();
        f1 = transitTroop.getF1();
        f2 = transitTroop.getF2();
        distance = transitTroop.getDistance() - transitTroop.getCurrentTurn();
        cyborgs = transitTroop.getCyborgs();
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

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public int getCyborgs() {
        return cyborgs;
    }

    public void setCyborgs(int cyborgs) {
        this.cyborgs = cyborgs;
    }

    @Override
    public String toString() {
        return "ArrivingTroop{" +
                "player=" + player +
                ", f1=" + f1 +
                ", f2=" + f2 +
                ", distance=" + distance +
                ", cyborgs=" + cyborgs +
                '}';
    }
}

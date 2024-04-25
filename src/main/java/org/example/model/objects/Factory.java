package org.example.model.objects;
import it.unical.mat.embasp.languages.Id;
import it.unical.mat.embasp.languages.Param;
import org.example.model.Position;

@Id("factory")
public class Factory {

    @Param(0)
    private int id;

    @Param(1)
    private int player;

    @Param(2)
    private int cyborgs;

    @Param(3)
    private int production; // number between 0 and 3 -- production per turn

    private Position position;

    public Factory() {
    }

    public Factory(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPlayer() {
        return player;
    }

    public void setPlayer(int player) {
        this.player = player;
    }

    public int getCyborgs() {
        return cyborgs;
    }

    public void setCyborgs(int cyborgs) {
        this.cyborgs = cyborgs;
    }

    public int getProduction() {
        return production;
    }

    public void setProduction(int production) {
        this.production = production;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    @Override
    public String toString() {
        return "Factory{" +
                "id=" + id +
                ", player=" + player +
                ", cyborgs=" + cyborgs +
                ", production=" + production +
                ", position=" + position +
                '}';
    }
}

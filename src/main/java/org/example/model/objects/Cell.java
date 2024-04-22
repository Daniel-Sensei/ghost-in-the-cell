package org.example.model.objects;
import it.unical.mat.embasp.languages.Id;
import it.unical.mat.embasp.languages.Param;

@Id("cell")
public class Cell {

    @Param(0)
    private int x;

    @Param(1)
    private int y;

    public Cell() {
    }

    public Cell(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    @Override
    public String toString() {
        return "Cell{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }
}

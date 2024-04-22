package org.example.model.objects;
import it.unical.mat.embasp.languages.Id;
import it.unical.mat.embasp.languages.Param;

@Id("inMatrix")
public class InMatrix {

    @Param(0)
    private int x;

    @Param(1)
    private int y;

    @Param(2)
    private int id;

    public InMatrix() {
    }

    public InMatrix(int x, int y, int id) {
        this.x = x;
        this.y = y;
        this.id = id;
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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "inMatrix{" +
                "x=" + x +
                ", y=" + y +
                ", id=" + id +
                '}';
    }
}

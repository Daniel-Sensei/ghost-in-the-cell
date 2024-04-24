package org.example.model.objects;
import it.unical.mat.embasp.languages.Id;
import it.unical.mat.embasp.languages.Param;

@Id("edge")
public class Edge {

    @Param(0)
    private int f1; // id_factory1

    @Param(1)
    private int f2; // id_factory2

    @Param(2)
    private int distance; // distance between f1 and f2 (expressed in turns)

    public Edge() {
    }

    public Edge(int f1, int f2, int distance) {
        this.f1 = f1;
        this.f2 = f2;
        this.distance = distance;
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

    @Override
    public String toString() {
        return "Distance{" +
                "f1=" + f1 +
                ", f2=" + f2 +
                ", distance=" + distance +
                '}';
    }
}

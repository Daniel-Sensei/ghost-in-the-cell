package org.example.model.objects;
import it.unical.mat.embasp.languages.Id;
import it.unical.mat.embasp.languages.Param;

@Id("distance")
public class Distance {

    @Param(0)
    private int f1;

    @Param(1)
    private int f2;

    @Param(2)
    private int distance;

    public Distance() {
    }

    public Distance(int f1, int f2, int distance) {
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

package org.example.model.objects;
import it.unical.mat.embasp.languages.Id;
import it.unical.mat.embasp.languages.Param;

@Id("factory")
public class Factory {

    @Param(0)
    private int id;

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

    @Override
    public String toString() {
        return "Factory{" +
                "id=" + id +
                '}';
    }
}

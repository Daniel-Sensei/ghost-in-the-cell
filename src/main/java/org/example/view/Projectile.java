package org.example.view;

import org.example.model.Position;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

import org.example.config.Settings;

public class Projectile {
    private ArrayList<Position> path = new ArrayList<>();
    private int currentPosition = 0; //index of the path
    private Color color;
    private boolean reachedDestination;

    public Projectile(ArrayList<Position> path, Color color) {
        this.path = path;
        this.color = color;
        this.reachedDestination = false;
    }

    public void move() {
        //Muoviti nella prossima posizione del path
        this.currentPosition += 1;

        System.out.println("path " + this.path);
        System.out.println("position current = " + this.currentPosition);

        if(this.currentPosition == this.path.size()){
            this.reachedDestination = true;
        }
    }

    public boolean isReachedDestination() {
        return reachedDestination;
    }

    public void draw(Graphics g) {
        if(!this.reachedDestination) {
            int blockSize = Settings.BLOCK_SIZE;
            int diameter = blockSize / 4; // Diametro del proiettile
            int xOffset = (blockSize - diameter) / 2; // Offset orizzontale per centrare il proiettile
            int yOffset = (blockSize - diameter) / 2; // Offset verticale per centrare il proiettile

            g.setColor(color);
            g.fillOval(
                    this.path.get(currentPosition).x() * blockSize + xOffset, // Aggiungi l'offset orizzontale
                    this.path.get(currentPosition).y() * blockSize + yOffset, // Aggiungi l'offset verticale
                    diameter, diameter // Usa il diametro anziché la dimensione del blocco
            );
        }
    }

    public int getCurrentPosition(){
        return this.currentPosition;
    }


}

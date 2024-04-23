package org.example.view;

import org.example.model.Position;
import javax.swing.*;
import java.awt.*;
import org.example.config.Settings;

public class Projectile {
    private Position currentPosition;
    private Position destination;
    private Color color;
    private boolean reachedDestination;

    public Projectile(Position start, Position destination, Color color) {
        this.currentPosition = start;
        this.destination = destination;
        this.color = color;
        this.reachedDestination = false;
    }

    public void move() {
        // Calcola la direzione del movimento
        int deltaX = Integer.compare(destination.x(), currentPosition.x());
        int deltaY = Integer.compare(destination.y(), currentPosition.y());

        // Muovi il proiettile nella direzione corretta
        currentPosition = new Position(
                currentPosition.x() + deltaX,
                currentPosition.y() + deltaY
        );

        // Controlla se il proiettile ha raggiunto la destinazione
        if (currentPosition.equals(destination)) {
            reachedDestination = true;
        }
    }

    public boolean isReachedDestination() {
        return reachedDestination;
    }

    public void draw(Graphics g) {
        int blockSize = Settings.BLOCK_SIZE;
        int diameter = blockSize / 4; // Diametro del proiettile
        int xOffset = (blockSize - diameter) / 2; // Offset orizzontale per centrare il proiettile
        int yOffset = (blockSize - diameter) / 2; // Offset verticale per centrare il proiettile

        g.setColor(color);
        g.fillOval(
                currentPosition.x() * blockSize + xOffset, // Aggiungi l'offset orizzontale
                currentPosition.y() * blockSize + yOffset, // Aggiungi l'offset verticale
                diameter, diameter // Usa il diametro anziché la dimensione del blocco
        );
    }


}

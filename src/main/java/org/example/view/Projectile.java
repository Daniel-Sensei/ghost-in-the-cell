package org.example.view;

import org.example.model.Position;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

import org.example.config.Settings;

public class Projectile {
    private ArrayList<Position> path = new ArrayList<>();
    private int currentStep = 0; // Passo corrente all'interno del blocco
    private double stepProgress = 0.0; // Progresso del passo corrente (da 0 a 1)
    private Color color;
    private boolean reachedDestination;

    public Projectile(ArrayList<Position> path, Color color) {
        this.path = path;
        this.color = color;
        this.reachedDestination = false;
    }

    public void move() {
        // Aumenta il progresso del passo corrente
        stepProgress += 0.05; // Regola questa costante per controllare la velocità del movimento

        // Se il passo corrente è completo, passa al successivo
        if (stepProgress >= 1.0) {
            currentStep++;
            stepProgress = 0.0;
        }

        // Controlla se il proiettile ha raggiunto la fine del percorso
        if (currentStep >= path.size() - 1) {
            reachedDestination = true;
        }
    }

    public boolean isReachedDestination() {
        return reachedDestination;
    }

    public void draw(Graphics g) {
        if (!reachedDestination) {
            int blockSize = Settings.BLOCK_SIZE;
            int diameter = blockSize / 4; // Diametro del proiettile
            int xOffset = (blockSize - diameter) / 2; // Offset orizzontale per centrare il proiettile
            int yOffset = (blockSize - diameter) / 2; // Offset verticale per centrare il proiettile

            // Calcola la posizione intermedia tra il blocco corrente e il successivo
            double x = interpolate(path.get(currentStep).x(), path.get(currentStep + 1).x(), stepProgress);
            double y = interpolate(path.get(currentStep).y(), path.get(currentStep + 1).y(), stepProgress);

            g.setColor(color);
            g.fillOval(
                    (int) (x * blockSize + xOffset), // Aggiungi l'offset orizzontale
                    (int) (y * blockSize + yOffset), // Aggiungi l'offset verticale
                    diameter, diameter // Usa il diametro anziché la dimensione del blocco
            );
        }
    }

    // Funzione per l'interpolazione lineare tra due valori
    private double interpolate(double start, double end, double progress) {
        return start + (end - start) * progress;
    }
}

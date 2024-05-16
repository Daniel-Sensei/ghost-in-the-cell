package org.example.view;

import org.example.model.Game;
import org.example.model.Position;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

import org.example.config.Settings;

public class Projectile {
    private ArrayList<Position> path = new ArrayList<>();
    private int currentStep = 0;
    private double stepProgress = 0.0;
    private Color color;
    private boolean reachedDestination;
    private int cyborgs;

    private Image image;

    public Projectile(ArrayList<Position> path, Color color, int cyborgs) {
        this.path = path;
        this.color = color;
        this.reachedDestination = false;
        this.cyborgs = cyborgs;
    }

    public Projectile(ArrayList<Position> path, ImageIcon image, int cyborgs) {
        this.path = path;
        this.image = image.getImage();
        this.reachedDestination = false;
        this.cyborgs = cyborgs;
    }

    public void move() {
        // Add a constant to the step progress
        stepProgress += 0.05;

        if (stepProgress >= 1.0) {
            currentStep++;
            stepProgress = 0.0;
        }

        // Check if the projectile has reached the destination
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
            int diameter = (int) (blockSize / 2.5); // Diameter of the projectile
            int xOffset = (blockSize - diameter) / 2; // Hotizontal offset to center the projectile
            int yOffset = (blockSize - diameter) / 2; // Vertical offset to center the projectile

            // Calculate the current position of the projectile
            double x = interpolate(path.get(currentStep).x(), path.get(currentStep + 1).x(), stepProgress);
            double y = interpolate(path.get(currentStep).y(), path.get(currentStep + 1).y(), stepProgress);

            // Draw the projectile
            if (image != null) {
                g.drawImage(
                        image,
                        (int) (x * blockSize + xOffset), // Aggiungi l'offset orizzontale
                        (int) (y * blockSize + yOffset), // Aggiungi l'offset verticale
                        diameter, diameter, null
                );
            }

            g.setColor(Color.BLACK);
            g.setFont(new Font("Comic Sans MS", Font.BOLD, 18));
            g.drawString(String.valueOf(cyborgs), (int) (x * blockSize + blockSize / 2 - 7), (int) (y * blockSize + blockSize / 2 + 5));
        }
    }

    private double interpolate(double start, double end, double progress) {
        return start + (end - start) * progress;
    }

    public ArrayList<Position> getPath() {
        return path;
    }
}

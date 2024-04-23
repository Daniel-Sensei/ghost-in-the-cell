package org.example.view;

import org.antlr.v4.runtime.misc.Pair;
import org.example.config.Settings;
import org.example.model.Game;
import org.example.model.Position;
import org.example.model.World;


import javax.swing.*;
import java.awt.*;
import java.awt.geom.CubicCurve2D;
import java.util.ArrayList;

public class GamePanel extends JPanel {
    private ArrayList<Position> factories;
    private ArrayList<Pair<Position, Position>> edges;

    public GamePanel() {
        reset();
    }

    public void reset() {
        this.setBackground(Color.WHITE);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Game game = Game.getGame();

        World world = game.getWorld();

        //Crea un array di factories
        factories = new ArrayList<>();
        for (int i = 0; i < world.getSize(); i++) {
            for (int j = 0; j < world.getSize(); j++) {
                Position p = new Position(i, j);
                if (world.isFactory(p)) {
                    factories.add(p);
                }
            }
        }

        //Imposta collegamenti casuali tra factories casuali in edges
        edges = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            int index1 = (int) (Math.random() * factories.size());
            int index2 = (int) (Math.random() * factories.size());
            while (index1 == index2) {
                index2 = (int) (Math.random() * factories.size());
            }
            Position factory1 = factories.get(index1);
            Position factory2 = factories.get(index2);
            edges.add(new Pair<>(factory1, factory2));
            //break;
        }

        //Crea un array di colori pari al numero di edges
        Color[] edgeColors = new Color[edges.size()];
        for (int i = 0; i < edges.size(); i++) {
            edgeColors[i] = new Color((int) (Math.random() * 0x1000000));
        }

        //Disegna le linee tra gli edges
        for (Pair<Position, Position> edge : edges) {
            drawStraightLine(g, edge.a, edge.b, edgeColors[edges.indexOf(edge)]);
        }

        // Disegna le fabbriche
        for (Position factory : factories) {
            Color c = Color.GREEN;
            g.setColor(c);
            g.fillOval(factory.x() * Settings.BLOCK_SIZE, factory.y() * Settings.BLOCK_SIZE, Settings.BLOCK_SIZE,
                    Settings.BLOCK_SIZE);

            /*
            g.setColor(Color.BLACK);
            g.drawString(factory.toString(), factory.x() * Settings.BLOCK_SIZE + Settings.BLOCK_SIZE / 2,
                    factory.y() * Settings.BLOCK_SIZE + Settings.BLOCK_SIZE / 2);

             */
        }
    }

    private void drawStraightLine(Graphics g, Position p1, Position p2, Color color) {
        ArrayList<Position> path = calculatePath(p1, p2);

        if (path.size() > 1) {
            Position prev = path.get(0);
            for (int i = 1; i < path.size(); i++) {
                Position current = path.get(i);
                drawLinearLine(g, prev, current, color);
                prev = current;
            }
        }
    }

    private void drawLinearLine(Graphics g, Position start, Position end, Color color) {
        int startX = start.x() * Settings.BLOCK_SIZE + Settings.BLOCK_SIZE / 2;
        int startY = start.y() * Settings.BLOCK_SIZE + Settings.BLOCK_SIZE / 2;
        int endX = end.x() * Settings.BLOCK_SIZE + Settings.BLOCK_SIZE / 2;
        int endY = end.y() * Settings.BLOCK_SIZE + Settings.BLOCK_SIZE / 2;

        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(color);
        g2d.setStroke(new BasicStroke(2));
        g2d.drawLine(startX, startY, endX, endY);
    }

    private ArrayList<Position> calculatePath(Position start, Position end) {
        //System.out.println("Path tra " + start + " e " + end);
        ArrayList<Position> path = new ArrayList<>();
        path.add(start);

        int x = start.x();
        int y = start.y();

        while (x != end.x() || y != end.y()) {
            int deltaX = Integer.compare(end.x(), x);
            int deltaY = Integer.compare(end.y(), y);

            if (deltaX != 0 && deltaY != 0) {
                // Controlla la diagonale
                if (!Game.getGame().getWorld().isFactory(new Position(x + deltaX, y + deltaY)) || (x + deltaX == end.x() && y + deltaY == end.y())){
                    x += deltaX;
                    y += deltaY;
                } else if (!Game.getGame().getWorld().isFactory(new Position(x + deltaX, y)) || (x + deltaX == end.x() && y == end.y())) {
                    // Sposta orizzontalmente se possibile
                    x += deltaX;
                } else if (!Game.getGame().getWorld().isFactory(new Position(x, y + deltaY)) || (y + deltaY == end.y())) {
                    // Sposta verticalmente se possibile
                    y += deltaY;
                } else {
                    // Se non è possibile muoversi né in orizzontale né in verticale, termina il calcolo
                    System.out.println("Non è possibile muoversi 1");
                    break;
                }
            } else if (deltaX != 0) {
                // Sposta orizzontalmente se non si è ancora sulla stessa colonna
                if (!Game.getGame().getWorld().isFactory(new Position(x + deltaX, y)) || (x + deltaX == end.x())) {
                    x += deltaX;
                } else {
                    // Se non è possibile muoversi orizzontalmente, prova a muoversi in diagonale o verticalmente
                    if (!Game.getGame().getWorld().isFactory(new Position(x + deltaX, y + deltaY))) {
                        // Sposta in diagonale
                        x += deltaX;
                        y += deltaY;
                    } else if (!Game.getGame().getWorld().isFactory(new Position(x, y + deltaY)) || (y + deltaY == end.y())) {
                        // Sposta verticalmente
                        y += deltaY;
                    } else {
                        // Se non è possibile muoversi diagonalmente o verticalmente, termina il calcolo
                        System.out.println("Path tra " + start + " e " + end);
                        System.out.println("Non è possibile muoversi 2");
                        break;
                    }
                }
            } else if (deltaY != 0) {
                // Sposta verticalmente se non si è ancora sulla stessa riga
                if (!Game.getGame().getWorld().isFactory(new Position(x, y + deltaY)) || (y + deltaY == end.y())) {
                    y += deltaY;
                } else {
                    // Se non è possibile muoversi verticalmente, prova a muoversi in diagonale
                    if (!Game.getGame().getWorld().isFactory(new Position(x + deltaX, y + deltaY))) {
                        // Sposta in diagonale
                        x += deltaX;
                        y += deltaY;
                    } else if (!Game.getGame().getWorld().isFactory(new Position(x + deltaX, y)) || (x + deltaX == end.x())){
                        // Sposta orizzontalmente
                        x += deltaX;
                    } else {
                        // Se non è possibile muoversi diagonalmente, termina il calcolo
                        System.out.println("Path tra " + start + " e " + end);
                        System.out.println("Non è possibile muoversi 3");
                        break;
                    }
                }
            }
            else {
                // Se non ci sono spostamenti necessari, termina il calcolo
                System.out.println("Non ci sono spostamenti necessari");
                break;
            }

            // Aggiunge la posizione corrente al percorso solo se non è già presente
            Position currentPos = new Position(x, y);
            if (!path.contains(currentPos)) {
                path.add(currentPos);
            } else {
                System.out.println("Path tra " + start + " e " + end);
                System.out.println("Sovrapposizione");
                //break;

                //Aggiunge il prossimo passo del percorso senza considerare le factories
                x += deltaX;
                y += deltaY;
            }
        }

        //System.out.println("Path: " + path);
        return path;
    }

    /*
    private void drawCurvedLine(Graphics g, Position start, Position end, Color color) {
        ArrayList<Position> path = calculatePath(start, end);

        if (path.size() > 1) {
            Position prev = path.get(0);
            for (int i = 1; i < path.size(); i++) {
                Position current = path.get(i);
                drawBezierCurve(g, prev, current, color);
                prev = current;
            }
        }
    }

     */


    /*
    private void drawBezierCurve(Graphics g, Position start, Position end, Color color) {
        int startX = start.x() * Settings.BLOCK_SIZE + Settings.BLOCK_SIZE / 2;
        int startY = start.y() * Settings.BLOCK_SIZE + Settings.BLOCK_SIZE / 2;
        int endX = end.x() * Settings.BLOCK_SIZE + Settings.BLOCK_SIZE / 2;
        int endY = end.y() * Settings.BLOCK_SIZE + Settings.BLOCK_SIZE / 2;

        int controlX1 = startX;
        int controlY1 = startY + (endY - startY) / 2;
        int controlX2 = endX;
        int controlY2 = endY - (endY - startY) / 2;

        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(color);
        g2d.setStroke(new BasicStroke(2));
        g2d.draw(new CubicCurve2D.Float(startX, startY, controlX1, controlY1, controlX2, controlY2, endX, endY));
    }
     */



}

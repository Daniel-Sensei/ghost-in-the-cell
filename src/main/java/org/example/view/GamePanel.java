package org.example.view;

import org.antlr.v4.runtime.misc.Pair;
import org.example.config.Settings;
import org.example.model.Game;
import org.example.model.Position;
import org.example.model.World;


import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.CubicCurve2D;
import java.util.ArrayList;

public class GamePanel extends JPanel {
    private ArrayList<Position> factories;
    private ArrayList<Pair<Position, Position>> edges;
    private ArrayList<ArrayList<Position>> paths = new ArrayList<>();
    private ArrayList<Projectile> projectiles;
    private Timer gameTimer;

    private JPanel matrixPanel;
    private JPanel bannerPanel;

    private void moveProjectiles() {
        for (Projectile projectile : projectiles) {
            if (!projectile.isReachedDestination()) {
                projectile.move();
            }
        }
    }

    private String player1Name = "Player 1";
    private String player2Name = "Player 2";
    private JButton nextTurnButton;

    private void updateGamePanel() {
        // Imposta il timer a 30 FPS
        int delay = 1000 / 30;
        gameTimer = new Timer(delay, e -> {
            repaint();
        });
        gameTimer.start();
    }

    public GamePanel() {
        reset();

        // Imposta il layout del pannello come BorderLayout
        setLayout(new BorderLayout());

        // Aggiungi il banner nella parte superiore del pannello
        add(createBanner(), BorderLayout.NORTH);
        add(createMatrix(), BorderLayout.CENTER);

        updateGamePanel();
    }

    private JPanel createBanner() {
        bannerPanel = new JPanel();
        bannerPanel.setBackground(Color.GRAY); // Imposta il colore di sfondo del banner

        // Layout per il banner
        bannerPanel.setLayout(new BorderLayout());

        // Etichetta per il nome del giocatore 1 a sinistra
        JLabel player1Label = new JLabel(player1Name);
        player1Label.setHorizontalAlignment(SwingConstants.LEFT);
        bannerPanel.add(player1Label, BorderLayout.WEST);

        // Etichetta per il nome del giocatore 2 a destra
        JLabel player2Label = new JLabel(player2Name);
        player2Label.setHorizontalAlignment(SwingConstants.RIGHT);
        bannerPanel.add(player2Label, BorderLayout.EAST);

        // Aggiungi un pulsante per andare al turno successivo al centro del banner
        // Aggiungi un pulsante per andare al turno successivo al centro del pannello
        nextTurnButton = new JButton("Turno successivo");
        nextTurnButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Crea un nuovo timer per muovere gradualmente i proiettili
                Timer movementTimer = new Timer(10, new ActionListener() {
                    double count = 0.0;
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        // Itera attraverso tutti i proiettili e muovili di un passo
                        for (Projectile projectile : projectiles) {
                            projectile.move();
                        }
                        // Richiama repaint() per aggiornare il pannello dopo ogni movimento
                        repaint();
                        count += 0.05;
                        // Se tutti i proiettili hanno raggiunto la destinazione, ferma il timer
                        if (count >= 1.0) {
                            ((Timer) e.getSource()).stop();
                            System.out.println("Turno successivo");
                        }
                    }
                });
                // Avvia il timer per muovere gradualmente i proiettili
                movementTimer.start();
            }
        });

        bannerPanel.add(nextTurnButton, BorderLayout.CENTER);
        return bannerPanel;
    }

    private JPanel createMatrix() {
        initializeFactories();
        initializeEdges();
        initializePaths();
        initializeProjectiles();

        matrixPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);

                // Disegna le linee tra gli edges
                for (Pair<Position, Position> edge : edges) {
                    drawStraightLine(g, edge.a, edge.b, Color.PINK);
                }

                // Disegna le fabbriche
                for (Position factory : factories) {
                    Color c = Color.GREEN;
                    g.setColor(c);
                    g.fillOval(factory.x() * Settings.BLOCK_SIZE, factory.y() * Settings.BLOCK_SIZE, Settings.BLOCK_SIZE,
                            Settings.BLOCK_SIZE);
                }

                // Disegna i proiettili
                for (Projectile projectile : projectiles) {
                    projectile.draw(g);
                }

                //per ogni blocco stampa le sue coordinate
                for (int i = 0; i < Game.getGame().getWorld().getSize(); i++) {
                    for (int j = 0; j < Game.getGame().getWorld().getSize(); j++) {
                        g.setColor(Color.BLACK);
                        g.drawString(i + "," + j, i * Settings.BLOCK_SIZE + Settings.BLOCK_SIZE / 2, j * Settings.BLOCK_SIZE + Settings.BLOCK_SIZE / 2);
                    }
                }
            }
        };

        // Imposta il layout del pannello della matrice come GridLayout con una riga e una colonna
        matrixPanel.setLayout(new GridLayout(1, 1));

        // Imposta il colore di sfondo del pannello della matrice
        matrixPanel.setBackground(Color.WHITE);

        return matrixPanel;
    }


    private void initializeFactories(){
        World world = Game.getGame().getWorld();

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
    }

    private void initializeEdges(){
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
    }

    private void initializePaths(){
        for (Pair<Position, Position> edge : edges) {
            paths.add(calculatePath(edge.a, edge.b));
        }
    }

    private void initializeProjectiles(){
        //Per ogni arco in edges genera un proiettile
        projectiles = new ArrayList<>();
        for (ArrayList<Position> path : paths) {
            projectiles.add(new Projectile(path, Color.RED));
        }
    }

    public void reset() {
        this.setBackground(Color.WHITE);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        //Game game = Game.getGame();
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
                //System.out.println("Path tra " + start + " e " + end);
                //System.out.println("Sovrapposizione");
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

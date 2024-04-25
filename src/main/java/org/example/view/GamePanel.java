package org.example.view;

import org.example.config.Settings;
import org.example.model.Game;
import org.example.model.Position;
import org.example.model.objects.Factory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class GamePanel extends JPanel {

    private Timer gameTimer;

    private JPanel matrixPanel;
    private JPanel bannerPanel;

    private String player1Name = "Player 1";
    private String player2Name = "Player 2";
    private JButton nextTurnButton;

    public GamePanel() {
        reset();

        setLayout(new BorderLayout());

        add(createBanner(), BorderLayout.NORTH);
        add(createMatrix(), BorderLayout.CENTER);

        updateGamePanel();
    }

    public void reset() {
        this.setBackground(Color.WHITE);
    }

    private void updateGamePanel() {
        // Imposta il timer a 30 FPS
        int delay = 1000 / 30;
        gameTimer = new Timer(delay, e -> {
            // Delete old projectiles
            ArrayList<Projectile> toRemove = new ArrayList<>();
            for(Projectile projectile : Game.getGame().getWorld().getProjectiles()) {
                if(projectile.isReachedDestination()) {
                    toRemove.add(projectile);
                }
            }
            Game.getGame().getWorld().getProjectiles().removeAll(toRemove);

            // Delete old active paths
            Game.getGame().getWorld().getActivePaths().clear();
            ArrayList<ArrayList<Position>> activePaths = new ArrayList<>();
            for(Projectile projectile : Game.getGame().getWorld().getProjectiles()) {
                activePaths.add(projectile.getPath());
            }
            Game.getGame().getWorld().setActivePaths(activePaths);

            repaint();
        });
        gameTimer.start();
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
        nextTurnButton = new JButton("Turno successivo: " + Game.getGame().getTurn());
        nextTurnButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Crea un nuovo timer per muovere gradualmente i proiettili
                Game.getGame().nextTurn();

                Timer movementTimer = new Timer(10, new ActionListener() {
                    double count = 0.0;
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        // Itera attraverso tutti i proiettili e muovili di un passo
                        for (Projectile projectile : Game.getGame().getWorld().getProjectiles()) {
                            projectile.move();
                        }
                        // Richiama repaint() per aggiornare il pannello dopo ogni movimento
                        repaint();
                        count += 0.05;
                        // Se tutti i proiettili hanno raggiunto la destinazione, ferma il timer
                        if (count >= 1.0) {
                            ((Timer) e.getSource()).stop();
                            //System.out.println("Fine transizione proiettili");
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

        matrixPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);

                // Disegna le linee per i percorsi
                for (ArrayList<Position> path : Game.getGame().getWorld().getActivePaths()) {
                    drawStraightLine(g, path, Color.BLUE);
                }

                // Disegna le fabbriche
                for (Factory factory : Game.getGame().getWorld().getFactories()) {
                    Color c = Color.GRAY;
                    if (factory.getPlayer() == 1) {
                        c = new Color(128, 0, 128);
                    } else if (factory.getPlayer() == -1) {
                        c = Color.ORANGE;
                    }
                    g.setColor(c);
                    g.fillOval(factory.getPosition().x() * Settings.BLOCK_SIZE, factory.getPosition().y() * Settings.BLOCK_SIZE, Settings.BLOCK_SIZE,
                            Settings.BLOCK_SIZE);
                    // Disegna l'id, la produzione e il numero di cyborgs all'interno della fabbrica
                    //usa questo formato: (id, produzione, cyborgs)
                    //centra il testo all'interno del cerchio
                    g.setColor(Color.BLACK);
                    g.drawString("(" + factory.getId() + ", " + factory.getProduction() + ", " + factory.getCyborgs() + ")",
                            factory.getPosition().x() * Settings.BLOCK_SIZE + Settings.BLOCK_SIZE / 2 - 20,
                            factory.getPosition().y() * Settings.BLOCK_SIZE + Settings.BLOCK_SIZE / 2);
                }

                // Disegna i proiettili
                for (Projectile projectile : Game.getGame().getWorld().getProjectiles()) {
                    projectile.draw(g);
                }
            }
        };

        // Imposta il layout del pannello della matrice come GridLayout con una riga e una colonna
        matrixPanel.setLayout(new GridLayout(1, 1));

        // Imposta il colore di sfondo del pannello della matrice
        matrixPanel.setBackground(Color.WHITE);

        return matrixPanel;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (Game.getGame().isEndGame()) {
            System.out.println("GAME OVER!");
            drawEnd("Game over!");
            gameTimer.stop();
            return;
        }
    }

    private void drawStraightLine(Graphics g, ArrayList<Position> path, Color color) {
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

    private void drawEnd(String message) {
        this.removeAll(); // Rimuove tutti i componenti esistenti dal pannello

        // Imposta il colore di sfondo del pannello
        this.setBackground(Color.DARK_GRAY);

        // Crea un nuovo JLabel con il messaggio specificato
        JLabel endLabel = new JLabel(message);
        endLabel.setFont(new Font("Arial", Font.PLAIN, 20));
        endLabel.setForeground(Color.WHITE);

        // Aggiungi il JLabel al pannello e lo posiziona al centro
        this.setLayout(new GridBagLayout());
        this.add(endLabel, new GridBagConstraints());
    }



}

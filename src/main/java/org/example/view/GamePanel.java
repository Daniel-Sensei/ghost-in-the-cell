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
    private Timer movementTimer;

    private JPanel matrixPanel;
    private JPanel bannerPanel;

    private String player1Name = "Player 1";
    private String player2Name = "Player 2";
    private JButton playPauseButton;

    private boolean gameEnded = false;

    public GamePanel() {
        reset();

        setLayout(new BorderLayout());

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
        playPauseButton = new JButton("Stop");
        playPauseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Crea un nuovo timer per muovere gradualmente i proiettili
                //startProjectilesAnimation();
                if(gameTimer.isRunning()) {
                    gameTimer.stop();
                    playPauseButton.setText("Play");
                } else {
                    gameTimer.start();
                    playPauseButton.setText("Stop");
                }
            }
        });

        bannerPanel.add(playPauseButton, BorderLayout.CENTER);
        return bannerPanel;
    }

    private JPanel createMatrix() {
        matrixPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);

                // Disegna le linee per i percorsi
                for (ArrayList<Position> path : Game.getGame().getWorld().getActivePaths()) {
                    drawStraightLine(g, path, Color.BLACK);
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
                    // Formato: (id, produzione, cyborgs)
                    // Centra il testo all'interno del cerchio
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

    public void reset() {
        this.setBackground(Color.WHITE);
    }

    private void updateGamePanel() {
        // Imposta il timer
        int delay = 1000/30; // 30 FPS
        gameTimer = new Timer(delay, e -> {
            // Delete old projectiles
            deleteOldProjectiles();

            // Delete old active paths
            deleteOldActivePaths();

            if(movementTimer == null) {
                // Crea un nuovo timer per muovere gradualmente i proiettili
                startProjectilesAnimation();
            }

            if (!gameEnded) {
                repaint();
            }
        });
        gameTimer.start();
    }

    private void deleteOldProjectiles() {
        ArrayList<Projectile> toRemove = new ArrayList<>();
        for(Projectile projectile : Game.getGame().getWorld().getProjectiles()) {
            if(projectile.isReachedDestination()) {
                toRemove.add(projectile);
            }
        }
        Game.getGame().getWorld().getProjectiles().removeAll(toRemove);
    }

    private void deleteOldActivePaths() {
        Game.getGame().getWorld().getActivePaths().clear();
        ArrayList<ArrayList<Position>> activePaths = new ArrayList<>();
        for(Projectile projectile : Game.getGame().getWorld().getProjectiles()) {
            activePaths.add(projectile.getPath());
        }
        Game.getGame().getWorld().setActivePaths(activePaths);
    }

    private void startProjectilesAnimation(){
        Game.getGame().nextTurn();

        movementTimer = new Timer(Settings.GAME_SPEED, new ActionListener() {
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
                    movementTimer = null;
                    //System.out.println("Fine transizione proiettili");
                }
            }
        });
        // Avvia il timer per muovere gradualmente i proiettili
        movementTimer.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (!gameEnded && Game.getGame().isEndGame()) {
            System.out.println("GAME OVER!");
            gameTimer.stop();
            gameEnded = true;

            // Chiudi la finestra del gioco attuale
            JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
            frame.dispose();

            // Mostra la finestra del vincitore
            String winnerName = "(VINCITORE)"; // Metodo per determinare il vincitore
            new WinnerDialog(winnerName);
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

    private class WinnerDialog extends JFrame {
        private JLabel winnerLabel;

        public WinnerDialog(String winnerName) {
            setTitle("Game Over");
            setSize(300, 200);
            setLocationRelativeTo(null); // Centra la finestra
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            winnerLabel = new JLabel("Il vincitore è: " + winnerName);
            add(winnerLabel);

            setVisible(true);
        }
    }

    private void drawEnd(Graphics g, String message) {
        // Disegna un rettangolo grigio che copre l'intero pannello
        g.setColor(Color.DARK_GRAY);
        g.fillRect(0, 0, getWidth(), getHeight());

        // Imposta il font e il colore per il testo
        g.setFont(new Font("Arial", Font.PLAIN, 20));
        g.setColor(Color.WHITE);

        // Ottieni le dimensioni del testo per centrarlo sul pannello
        FontMetrics fm = g.getFontMetrics();
        int messageWidth = fm.stringWidth(message);
        int messageHeight = fm.getHeight();

        // Calcola le coordinate per centrare il testo
        int x = (getWidth() - messageWidth) / 2;
        int y = (getHeight() - messageHeight) / 2 + fm.getAscent();

        // Disegna il testo centrato sul pannello
        g.drawString(message, x, y);
    }

}

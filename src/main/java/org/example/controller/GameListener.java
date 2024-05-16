package org.example.controller;

import org.example.view.GamePanel;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class GameListener extends KeyAdapter {
    private final GamePanel gamePanel;
    public GameListener(GamePanel gamePanel) {
        this.gamePanel = gamePanel;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        // ESC key to exit the game
        if(e.getKeyCode() == KeyEvent.VK_ESCAPE)
            System.exit(0);

        // SPACE key to pause the game
        if(e.getKeyCode() == KeyEvent.VK_SPACE) {
            gamePanel.switchGameTimer();
        }

        // -> key to speed up the game
        if(e.getKeyCode() == KeyEvent.VK_RIGHT) {
            gamePanel.speedUpGame();
        }

        // <- key to slow down the game
        if(e.getKeyCode() == KeyEvent.VK_LEFT) {
            gamePanel.slowDownGame();
        }
    }
}

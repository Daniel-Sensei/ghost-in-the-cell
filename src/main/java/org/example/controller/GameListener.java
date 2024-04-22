package org.example.controller;

import org.example.model.Game;
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
        if(e.getKeyCode() == KeyEvent.VK_Q)
            System.exit(0);

        /*
        if(Game.getGame().isAlive() && !Game.getGame().win()) {
            switch(e.getKeyCode()) {
            case KeyEvent.VK_RIGHT -> Game.getGame().move(Game.MOVE_RIGHT);
            }
            gamePanel.repaint();
        }

         */
    }
}

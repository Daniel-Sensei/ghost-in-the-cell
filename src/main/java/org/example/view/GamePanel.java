package org.example.view;

import org.example.config.Settings;
import org.example.model.Game;
import org.example.model.Position;
import org.example.model.World;


import javax.swing.*;
import java.awt.*;

public class GamePanel extends JPanel {

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
        for (int i = 0; i < world.getSize(); i++) {
            for (int j = 0; j < world.getSize(); j++) {
                Position p = new Position(i, j);
                if(world.isEmpty(p))
                    continue;

                Color c = Color.BLACK;
                if(world.isFactory(p)) c = Color.GREEN;

                g.setColor(c);
                g.fillOval(i * Settings.BLOCK_SIZE, j * Settings.BLOCK_SIZE, Settings.BLOCK_SIZE,
                        Settings.BLOCK_SIZE);
            }
        }
    }
}

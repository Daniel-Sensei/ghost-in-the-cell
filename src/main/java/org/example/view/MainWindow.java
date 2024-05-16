package org.example.view;

import org.example.config.Settings;
import org.example.controller.GameListener;

import javax.swing.*;
import java.awt.*;

public class MainWindow {
    public static void launch() {
        JFrame f = new JFrame();
        f.setSize(Settings.WINDOW_SIZE * 2 - 50, Settings.WINDOW_SIZE + 70); // 22 is the height of the title bar
        // Set location of JFrame
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        f.setLocation(dim.width/2 - f.getSize().width/2, dim.height/2 - f.getSize().height/2);
        GamePanel panel = new GamePanel();
        f.add(panel);
        f.setResizable(false);
        panel.addKeyListener(new GameListener(panel));
        panel.setFocusable(true);
        /*
        JOptionPane.showMessageDialog(f, "Use arrow keys to move" + System.lineSeparator()
                + "Press SPACE to start/stop the game" + System.lineSeparator() + "Press ESC to quit", "Instructions", JOptionPane.INFORMATION_MESSAGE);
         */
        f.setVisible(true);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}

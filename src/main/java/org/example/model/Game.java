package org.example.model;

public class Game {
    public final static int FORWARD = 0;
    private static Game game = null;
    private boolean end;
    private final World world;

    private Game() {
        end = false;
        world = new World();
    }

    public World getWorld() {
        return world;
    }

    public static Game getGame() {
        if (game == null)
            game = new Game();
        return game;
    }
}

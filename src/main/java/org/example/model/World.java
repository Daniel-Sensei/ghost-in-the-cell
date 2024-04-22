package org.example.model;

import org.example.config.Settings;

public class World {
    private enum Block { EMPTY, FACTORY }
    private final Block[][] blocks;
    //private final Factory[] factories;

    public World() {
        blocks = new Block[Settings.WORLD_SIZE][Settings.WORLD_SIZE];
        for (int i = 0; i < blocks.length; i++) {
            for (int j = 0; j < blocks.length; j++) {
                blocks[i][j] = Block.EMPTY;
            }
        }
        //genera un numero da 7 a 15
        int numFactories = (int) (Math.random() * 8) + 7;
        System.out.println("numFactories: " + numFactories);
        generateFactory(numFactories);
        //print in standard output
        //0 for empty, 1 for factory
        for (int i = 0; i < blocks.length; i++) {
            for (int j = 0; j < blocks.length; j++) {
                if (blocks[i][j] == Block.EMPTY)
                    System.out.print("0 ");
                else
                    System.out.print("1 ");
            }
            System.out.println();
        }
    }

    private void generateFactory(int N) {
        int count = 0;
        generateFactoryRecursive(N, count);
    }

    private void generateFactoryRecursive(int N, int count) {
        if (count == N)
            return;

        int x = (int) (Math.random() * blocks.length);
        int y = (int) (Math.random() * blocks.length);
        Position p = new Position(x, y);
        if (isEmpty(p) && !hasAdjacentFactory(p)) {
            setType(p, Block.FACTORY);
            count++;
        }
        generateFactoryRecursive(N, count);
    }

    private boolean hasAdjacentFactory(Position p) {
        Position[] adjacentPositions = {
                new Position(p.x(), p.y() - 1), // up
                new Position(p.x(), p.y() + 1), // down
                new Position(p.x() - 1, p.y()), // left
                new Position(p.x() + 1, p.y())  // right
        };

        for (Position adjacent : adjacentPositions) {
            if (!isInvalidPosition(adjacent) && isFactory(adjacent)) {
                return true;
            }
        }
        return false;
    }


    private boolean isInvalidPosition(Position p) {
        return p.x() < 0 || p.x() >= blocks.length || p.y() < 0 || p.y() >= blocks.length;
    }

    private boolean isType(Position p, Block block) {
        if(isInvalidPosition(p))
            throw new IllegalArgumentException("Invalid position " + p);
        return blocks[p.x()][p.y()] == block;
    }

    private void setType(Position p, Block type) {
        if(isInvalidPosition(p))
            throw new IllegalArgumentException("Invalid position " + p);
        blocks[p.x()][p.y()] = type;
    }

    public boolean isFactory(Position p) {
        return isType(p, Block.FACTORY);
    }
    public boolean isEmpty(Position p) {
        return isType(p, Block.EMPTY);
    }
    public int getSize() {
        return blocks.length;
    }
}

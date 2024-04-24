package org.example.model;

import org.antlr.v4.runtime.misc.Pair;
import org.example.config.Settings;
import org.example.view.Projectile;

import java.awt.*;
import java.util.ArrayList;

public class World {
    private enum Block { EMPTY, FACTORY }
    private Block[][] blocks;

    private int numFactories;

    private ArrayList<Position> factories;
    private ArrayList<Pair<Position, Position>> edges;
    private ArrayList<ArrayList<Position>> paths = new ArrayList<>();
    private ArrayList<Projectile> projectiles;

    public World() {
        initializeBlocks();

        //Set the number of factories
        numFactories = (int) (Math.random() * Settings.MIN_FACTORIES + 1) + Settings.MAX_FACTORIES - Settings.MIN_FACTORIES;
        System.out.println("numFactories: " + numFactories);

        putRandomFactoriesOnMatrix(numFactories);

        initializeFactories();
        initializeEdges();
        initializePaths();
        initializeProjectiles();
    }

    private void initializeBlocks(){
        blocks = new Block[Settings.WORLD_SIZE][Settings.WORLD_SIZE];
        for (int i = 0; i < blocks.length; i++) {
            for (int j = 0; j < blocks.length; j++) {
                blocks[i][j] = Block.EMPTY;
            }
        }
    }

    private void putRandomFactoriesOnMatrix(int N) {
        int count = 0;
        putRandomFactoriesOnMatrixRecursive(N, count);
    }

    private void putRandomFactoriesOnMatrixRecursive(int N, int count) {
        if (count == N)
            return;

        int x = (int) (Math.random() * blocks.length);
        int y = (int) (Math.random() * blocks.length);
        Position p = new Position(x, y);
        if (isEmpty(p) && !hasAdjacentFactory(p)) {
            setType(p, Block.FACTORY);
            count++;
        }
        putRandomFactoriesOnMatrixRecursive(N, count);
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

    private void initializeFactories(){
        //Crea un array di factories
        factories = new ArrayList<>();
        for (int i = 0; i < blocks.length; i++) {
            for (int j = 0; j < blocks.length; j++) {
                Position p = new Position(i, j);
                if (isFactory(p)) {
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
                if (!isFactory(new Position(x + deltaX, y + deltaY)) || (x + deltaX == end.x() && y + deltaY == end.y())){
                    x += deltaX;
                    y += deltaY;
                } else if (!isFactory(new Position(x + deltaX, y)) || (x + deltaX == end.x() && y == end.y())) {
                    // Sposta orizzontalmente se possibile
                    x += deltaX;
                } else if (!isFactory(new Position(x, y + deltaY)) || (y + deltaY == end.y())) {
                    // Sposta verticalmente se possibile
                    y += deltaY;
                } else {
                    // Se non è possibile muoversi né in orizzontale né in verticale, termina il calcolo
                    System.out.println("Non è possibile muoversi 1");
                    break;
                }
            } else if (deltaX != 0) {
                // Sposta orizzontalmente se non si è ancora sulla stessa colonna
                if (!isFactory(new Position(x + deltaX, y)) || (x + deltaX == end.x())) {
                    x += deltaX;
                } else {
                    // Se non è possibile muoversi orizzontalmente, prova a muoversi in diagonale o verticalmente
                    if (!isFactory(new Position(x + deltaX, y + deltaY))) {
                        // Sposta in diagonale
                        x += deltaX;
                        y += deltaY;
                    } else if (!isFactory(new Position(x, y + deltaY)) || (y + deltaY == end.y())) {
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
                if (!isFactory(new Position(x, y + deltaY)) || (y + deltaY == end.y())) {
                    y += deltaY;
                } else {
                    // Se non è possibile muoversi verticalmente, prova a muoversi in diagonale
                    if (!isFactory(new Position(x + deltaX, y + deltaY))) {
                        // Sposta in diagonale
                        x += deltaX;
                        y += deltaY;
                    } else if (!isFactory(new Position(x + deltaX, y)) || (x + deltaX == end.x())){
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

    // GETTERS
    public ArrayList<Position> getFactories() {
        return factories;
    }

    public ArrayList<Pair<Position, Position>> getEdges() {
        return edges;
    }

    public ArrayList<ArrayList<Position>> getPaths() {
        return paths;
    }

    public ArrayList<Projectile> getProjectiles() {
        return projectiles;
    }
}

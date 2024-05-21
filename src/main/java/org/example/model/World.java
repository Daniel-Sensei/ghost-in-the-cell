package org.example.model;

import org.antlr.v4.runtime.misc.Pair;
import org.example.config.Settings;
import org.example.model.objects.Edge;
import org.example.model.objects.Factory;
import org.example.model.objects.TransitTroop;
import org.example.view.Projectile;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Arrays;

public class World {
    private enum Block { EMPTY, FACTORY }
    private Block[][] blocks;

    private int numFactories;

    private ArrayList<Factory> factories;

    private ArrayList<Pair<Position, Position>> edgesPosition;
    private ArrayList<Edge> edgesObject = new ArrayList<>();

    private ArrayList<ArrayList<Position>> paths = new ArrayList<>();
    private ArrayList<ArrayList<Position>> activePaths = new ArrayList<>();
    private ArrayList<Projectile> projectiles = new ArrayList<>();

    public World() {
        initializeBlocks();

        //Set the number of factories
        numFactories = (int) (Math.random() * Settings.MIN_FACTORIES + 1) + Settings.MAX_FACTORIES - Settings.MIN_FACTORIES;

        putRandomFactoriesOnMatrix(numFactories);

        initializeFactories();
        initializeEdges();
        initializePaths();
    }

    private void initializeBlocks(){
        blocks = new Block[Settings.WORLD_SIZE_X][Settings.WORLD_SIZE_Y];
        for (int i = 0; i < blocks.length; i++) {
            for (int j = 0; j < blocks[0].length; j++) {
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
        int y = (int) (Math.random() * blocks[0].length);
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
        factories = new ArrayList<>();
        int initialTroops = (int) (Math.random() * (Settings.MIN_INITIAL_TROOPS + 1)) + Settings.MAX_INITIAL_TROOPS - Settings.MIN_INITIAL_TROOPS;
        int initialProduction = (int) (Math.random() * (Settings.MIN_INITIAL_PRODUCTION + 1)) + Settings.MAX_INITIAL_PRODUCTION - Settings.MIN_INITIAL_PRODUCTION;
        int id = 0;
        for (int i = 0; i < blocks.length; i++) {
            for (int j = 0; j < blocks[0].length; j++) {
                Position p = new Position(i, j);
                if (isFactory(p)) {
                    Factory f = new Factory();

                    f.setId(id);
                    if(id == Settings.SPAWN_OFFSET){
                        f.setPlayer(1);
                        f.setCyborgs(initialTroops);
                        f.setProduction(initialProduction);
                    } else if (id == numFactories - 1 - Settings.SPAWN_OFFSET){
                        f.setPlayer(-1);
                        f.setCyborgs(initialTroops);
                        f.setProduction(initialProduction);
                    } else {
                        f.setPlayer(0);
                        f.setCyborgs((int) (Math.random() * (Settings.MAX_NEUTRAL_TROOPS)) + Settings.MIN_NEUTRAL_TROOPS);
                        f.setProduction((int) (Math.random() * (Settings.MAX_PRODUCTION + 1)));
                        if(f.getProduction() == 0){
                            f.setCyborgs(0);
                        }
                    }
                    id++;

                    f.setPosition(p);

                    factories.add(f);
                }
            }
        }
        if(!checkFairness()){
            factories = null;
            initializeFactories();
        }
    }

    private boolean checkFairness(){
        //Check the first half of the world
        int prodHalf1=0;
        int cyborgsHalf1 = 0;
        int prodHalf2=0;
        int cyborgsHalf2 = 0;
        for (int i = 0; i < blocks.length / 2; i++) {
            for (int j = 0; j < blocks[0].length; j++) {
                Position p = new Position(i, j);
                if (isFactory(p)) {
                    //find the factory in factories by position and sum the production into prodHalf1
                    for (Factory f : factories) {
                        if(f.getPosition().equals(p)){
                            prodHalf1 += f.getProduction();
                            cyborgsHalf1 += f.getCyborgs();
                        }
                    }
                }
            }
        }

        //Check the second half of the world
        for (int i = blocks.length / 2; i < blocks.length; i++) {
            for (int j = 0; j < blocks[0].length; j++) {
                Position p = new Position(i, j);
                if (isFactory(p)) {
                    //find the factory in factories by position and sum the production into prodHalf2
                    for (Factory f : factories) {
                        if(f.getPosition().equals(p)){
                            prodHalf2 += f.getProduction();
                            cyborgsHalf2 += f.getCyborgs();
                        }
                    }
                }
            }
        }
        return prodHalf1 == prodHalf2 && cyborgsHalf1 == cyborgsHalf2;
    }

    private void initializeEdges(){
        edgesPosition = new ArrayList<>();
        for (int i = 0; i < factories.size(); i++) {
            for (int j = i + 1; j < factories.size(); j++) {
                edgesPosition.add(new Pair<>(factories.get(i).getPosition(), factories.get(j).getPosition()));
            }
        }
    }

    private void initializePaths(){
        for (Pair<Position, Position> edge : edgesPosition) {
            calculateBidirectionalPath(edge.a, edge.b);
        }
    }

    private void calculateBidirectionalPath(Position start, Position end) {
        ArrayList<Position> path = new ArrayList<>();
        path.add(start);

        int distance = 0;

        int x = start.x();
        int y = start.y();

        while (x != end.x() || y != end.y()) {
            int deltaX = Integer.compare(end.x(), x);
            int deltaY = Integer.compare(end.y(), y);

            if (deltaX != 0 && deltaY != 0) {
                // Check if it is possible to move diagonally
                if (!isFactory(new Position(x + deltaX, y + deltaY)) || (x + deltaX == end.x() && y + deltaY == end.y())){
                    x += deltaX;
                    y += deltaY;
                } else if (!isFactory(new Position(x + deltaX, y)) || (x + deltaX == end.x() && y == end.y())) {
                    // Move horizontally if possible
                    x += deltaX;
                } else if (!isFactory(new Position(x, y + deltaY)) || (y + deltaY == end.y())) {
                    // Move vertically if possible
                    y += deltaY;
                } else {
                    // If it is not possible to move diagonally, horizontally or vertically, stop the calculation
                    break;
                }
            } else if (deltaX != 0) {
                // Move horizontally if not on the same row
                if (!isFactory(new Position(x + deltaX, y)) || (x + deltaX == end.x())) {
                    x += deltaX;
                } else {
                    // If it is not possible to move horizontally, try to move diagonally
                    if (!isFactory(new Position(x + deltaX, y + deltaY))) {
                        // Move diagonally
                        x += deltaX;
                        y += deltaY;
                    } else if (!isFactory(new Position(x, y + deltaY)) || (y + deltaY == end.y())) {
                        // Move vertically
                        y += deltaY;
                    } else {
                        // If it is not possible to move horizontally, diagonally or vertically, stop the calculation
                        break;
                    }
                }
            } else if (deltaY != 0) {
                // Move vertically if not on the same column
                if (!isFactory(new Position(x, y + deltaY)) || (y + deltaY == end.y())) {
                    y += deltaY;
                } else {
                    // If it is not possible to move vertically, try to move diagonally
                    if (!isFactory(new Position(x + deltaX, y + deltaY))) {
                        // Move diagonally
                        x += deltaX;
                        y += deltaY;
                    } else if (!isFactory(new Position(x + deltaX, y)) || (x + deltaX == end.x())){
                        // Move horizontally
                        x += deltaX;
                    }
                }
            }

            // Add the current position to the path
            Position currentPos = new Position(x, y);
            if (!path.contains(currentPos)) {
                path.add(currentPos);
            } else {
                // Add the current position to the path only if it is not already in the path
                x += deltaX;
                y += deltaY;
                path.add(new Position(x, y));
            }
            distance++;
        }

        this.paths.add(path);
        // Add the reverse path
        ArrayList<Position> reversePath = new ArrayList<>();
        for(int i = path.size() - 1; i >= 0; i--){
            reversePath.add(path.get(i));
        }
        this.paths.add(reversePath);

        edgesObject.add(addEdgeByPosition(start, end, distance));
        edgesObject.add(addEdgeByPosition(end, start, distance));
    }

    private Edge addEdgeByPosition(Position start, Position end, int distance){
        Edge e = new Edge();
        e.setF1(factories.get(getFactoryIndexByPosition(start)).getId());
        e.setF2(factories.get(getFactoryIndexByPosition(end)).getId());
        e.setDistance(distance);
        return e;
    }

    private int getFactoryIndexByPosition(Position p){
        for (int i = 0; i < factories.size(); i++) {
            if(factories.get(i).getPosition().equals(p)){
                return i;
            }
        }
        return -1;
    }

    private boolean isInvalidPosition(Position p) {
        return p.x() < 0 || p.x() >= blocks.length || p.y() < 0 || p.y() >= blocks[0].length;
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

    // GETTERS
    public ArrayList<Factory> getFactories() {
        return factories;
    }

    public Factory getFactoryById(int id){
        for (Factory f : factories) {
            if(f.getId() == id){
                return f;
            }
        }
        return null;
    }

    public ArrayList<Edge> getEdgesObject() {
        return edgesObject;
    }

    public ArrayList<ArrayList<Position>> getActivePaths() {
        return activePaths;
    }

    public void setActivePaths(ArrayList<ArrayList<Position>> activePaths) {
        this.activePaths = activePaths;
    }

    public ArrayList<Projectile> getProjectiles() {
        return projectiles;
    }

    public void addProjectiles(ArrayList<TransitTroop> moves){
        for(TransitTroop move : moves){

            for(Edge edge : edgesObject){
                if(edge.getF1() == move.getF1() && edge.getF2() == move.getF2()){
                    //find the path
                    for(ArrayList<Position> path : paths){
                        if(path.get(0).equals(factories.get(edge.getF1()).getPosition()) && path.get(path.size() - 1).equals(factories.get(edge.getF2()).getPosition())){
                            ImageIcon image = null;
                            int random = (int) (Math.random() * 4);
                            String imagePath = "assets/ball-";
                            if (move.getPlayer() == 1)
                                image = new ImageIcon(imagePath + "1-" + random + ".png");
                            else if (move.getPlayer() == -1)
                                image = new ImageIcon(imagePath + "2-" + random + ".png");
                            Projectile projectile = new Projectile(path, image, move.getCyborgs());
                            projectiles.add(projectile);
                            activePaths.add(path);
                        }
                    }
                }
            }
        }
    }

    public int getDistanceByFactoriesId(int f1, int f2){
        for(Edge edge : edgesObject){
            if(edge.getF1() == f1 && edge.getF2() == f2){
                return edge.getDistance();
            }
        }
        return -1;
    }
}

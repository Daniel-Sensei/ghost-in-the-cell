package org.example.model;

import it.unical.mat.embasp.base.Output;
import it.unical.mat.embasp.languages.IllegalAnnotationException;
import it.unical.mat.embasp.languages.ObjectNotValidException;
import it.unical.mat.embasp.languages.asp.ASPMapper;
import it.unical.mat.embasp.languages.asp.AnswerSet;
import it.unical.mat.embasp.languages.asp.AnswerSets;
import org.example.controller.EmbASPManager;
import org.example.model.objects.Edge;
import org.example.model.objects.Factory;
import org.example.model.objects.TransitTroop;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import static java.lang.Math.abs;

public class Game {
    public final static int FORWARD = 0;
    private static Game game = null;
    private boolean endGame;
    private final World world;

    private int turn = 0;
    private ArrayList<TransitTroop> transitTroops = new ArrayList<>();

    private ArrayList<TransitTroop> tempMovesPlayer1 = new ArrayList<>();
    private ArrayList<TransitTroop> tempMovesPlayer2 = new ArrayList<>();

    private Game() {
        endGame = false;
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

    private String readEncoding(String filePath) {
        String encoding = "";
        try {
            FileReader fileReader = new FileReader(filePath);
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            //put all the lines of the file in the string encoding
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                encoding += line + "\n";
            }

            bufferedReader.close();
        } catch (IOException e) {
            e.printStackTrace();
            // Handle file reading error
        }
        return encoding;
    }

    public void nextTurn(){
        checkEndGame();

        // UPDATE WORLD
        if(turn > 0) produceCyborgs();

        updateFactoryCyborgReceived();

        String filePath = "encodings/encoding1.asp";
        int player = 1; //player 1 starts
        for (int i = 0; i < 2; i++) {
            //CLEAR ALL
            EmbASPManager.getInstance().getProgram().clearAll();
            EmbASPManager.getInstance().getHandler().removeAll();

            // INPUT
            try {
                passInputToOracle(filePath, player);
            } catch (Exception e) {
                e.printStackTrace();
            }

            // OUTPUT
            try {
                ArrayList<TransitTroop> moves = getOutputFromOracle(player);
                System.out.println("TempMovesPlayer1: " + tempMovesPlayer1);
                System.out.println("TempMovesPlayer2: " + tempMovesPlayer2);

                if (player == -1){

                    if(checkValidMoves(tempMovesPlayer1, 1)){
                        moves.addAll(tempMovesPlayer1);
                        tempMovesPlayer1.clear();
                    }
                    else{
                        System.out.println("Mosse non valide player 1: " + tempMovesPlayer1);
                    }
                    if(checkValidMoves(tempMovesPlayer2, -1)){
                        moves.addAll(tempMovesPlayer2);
                        tempMovesPlayer2.clear();
                    }
                    else {
                        System.out.println("Mosse non valide player 2: " + tempMovesPlayer2);
                    }

                }

                // UPDATE WORLD
                updateFactoryCyborgsSent(moves);
                transitTroops.addAll(moves);
                world.addProjectiles(moves);
                turn++;

            } catch (ObjectNotValidException | IllegalAnnotationException e) {
                e.printStackTrace();
            }
            filePath = "encodings/encoding2.asp";
            player = -1; //change player
        }

        System.out.println("Moves: " + transitTroops);
    }

    private void produceCyborgs() {
        for (Factory factory : world.getFactories()) {
            if (factory.getPlayer() == 1 || factory.getPlayer() == -1){
                factory.setCyborgs(factory.getCyborgs() + factory.getProduction());
            }
        }
    }

    private void updateFactoryCyborgsSent(ArrayList<TransitTroop> moves) {
        // UPDATE FACTORIES
        for(TransitTroop move : moves){
            Factory factory = world.getFactoryById(move.getF1());
            factory.setCyborgs(factory.getCyborgs() - move.getCyborgs());
        }
    }

    private void moveTroops(ArrayList<TransitTroop> moves) {
        for (TransitTroop move : moves) {
            move.setCurrentTurn(move.getCurrentTurn() + 1);
        }
    }

    private void updateFactoryCyborgReceived() {
        // UPDATE FACTORIES
        for(TransitTroop move : transitTroops){
            move.setCurrentTurn(move.getCurrentTurn() + 1);

            if (move.getCurrentTurn() >= move.getDistance()) {

                //checks if there are troops that will reach the same factory in the same turn, in which case they'll just battle each other, eliminating the weaker one.
                //experimental feature
                //not completely tested
                for (TransitTroop move2 : transitTroops){
                    if(move.getF2() == move2.getF2() && move.getPlayer() != move2.getPlayer() && move2.getCurrentTurn() >= move2.getDistance()){
                        if(move.getCyborgs() > move2.getCyborgs()){
                            move.setCyborgs(move.getCyborgs() - move2.getCyborgs());
                            move2.setCyborgs(0);
                        }
                        else if(move.getCyborgs() < move2.getCyborgs()){
                            move2.setCyborgs(move2.getCyborgs() - move.getCyborgs());
                            move.setCyborgs(0);
                        }
                        else{
                            move.setCyborgs(0);
                            move2.setCyborgs(0);
                        }
                    }
                }


                if (move.getCyborgs()>0){
                    //PLAYER 1
                    if (move.getPlayer() == 1 && (world.getFactoryById(move.getF2()).getPlayer() == -1 || world.getFactoryById(move.getF2()).getPlayer() == 0)) {
                        //ATTACCO
                        world.getFactoryById(move.getF2()).setCyborgs(world.getFactoryById(move.getF2()).getCyborgs() - move.getCyborgs());
                        //CASO IN CUI IL GIOCATORE 1 VINCE
                        if (world.getFactoryById(move.getF2()).getCyborgs() < 0) {
                            world.getFactoryById(move.getF2()).setPlayer(1);
                            world.getFactoryById(move.getF2()).setCyborgs(abs(world.getFactoryById(move.getF2()).getCyborgs()));
                        }
                    } else if (move.getPlayer() == 1 && world.getFactoryById(move.getF2()).getPlayer() == 1) {
                        //RINFORZO
                        world.getFactoryById(move.getF2()).setCyborgs(world.getFactoryById(move.getF2()).getCyborgs() + move.getCyborgs());
                    }

                    //PLAYER -1
                    if (move.getPlayer() == -1 && (world.getFactoryById(move.getF2()).getPlayer() == 1 || world.getFactoryById(move.getF2()).getPlayer() == 0)) {
                        //ATTACCO
                        world.getFactoryById(move.getF2()).setCyborgs(world.getFactoryById(move.getF2()).getCyborgs() - move.getCyborgs());
                        //CASO IN CUI IL GIOCATORE -1 VINCE
                        if (world.getFactoryById(move.getF2()).getCyborgs() < 0) {
                            world.getFactoryById(move.getF2()).setPlayer(-1);
                            world.getFactoryById(move.getF2()).setCyborgs(abs(world.getFactoryById(move.getF2()).getCyborgs()));
                        }
                    } else if (move.getPlayer() == -1 && world.getFactoryById(move.getF2()).getPlayer() == -1) {
                        //RINFORZO
                        world.getFactoryById(move.getF2()).setCyborgs(world.getFactoryById(move.getF2()).getCyborgs() + move.getCyborgs());
                    }
                }
            }
        }
        // REMOVE TROOPS
        transitTroops.removeIf(move -> move.getCurrentTurn() == move.getDistance());
    }

    private void passInputToOracle(String filePath, int player) throws Exception {
        String encoding = readEncoding(filePath);
        EmbASPManager.getInstance().getProgram().addProgram(encoding);

        // PLAYER
        EmbASPManager.getInstance().getProgram().addProgram("player(" + player + ").");

        // TURN
        EmbASPManager.getInstance().getProgram().addProgram("turn(" + turn + ").");;

        // FACTORIES
        for (Factory factory : world.getFactories()) {
            EmbASPManager.getInstance().getProgram().addObjectInput(factory);
        }

        // EDGES
        for (Edge edge : world.getEdgesObject()) {
            EmbASPManager.getInstance().getProgram().addObjectInput(edge);
        }

        // TROOPS
        for (TransitTroop transitTroop : transitTroops) {
            EmbASPManager.getInstance().getProgram().addObjectInput(transitTroop);
        }

        System.out.println(EmbASPManager.getInstance().getProgram().getPrograms());
        EmbASPManager.getInstance().getHandler().addProgram(EmbASPManager.getInstance().getProgram());
    }

    private ArrayList<TransitTroop> getOutputFromOracle(int player) throws ObjectNotValidException, IllegalAnnotationException {
        // GET OUTPUT
        Output output = EmbASPManager.getInstance().getHandler().startSync();
        ASPMapper.getInstance().registerClass(TransitTroop.class);

        ArrayList<TransitTroop> result = new ArrayList<>();

        AnswerSets answersets = (AnswerSets) output;
        for(AnswerSet a: answersets.getAnswersets()) {

            //System.out.println(a.toString());
            try {
                for (Object obj : a.getAtoms()) {
                    if (!(obj instanceof TransitTroop)) continue;
                    TransitTroop transitTroop = (TransitTroop) obj;
                    System.out.println(transitTroop);
                    if(transitTroop.getCurrentTurn() == 0) {
                        transitTroop.setDistance(world.getDistanceByFactoriesId(transitTroop.getF1(), transitTroop.getF2()));
                        if(player == 1)
                            tempMovesPlayer1.add(transitTroop);
                        else {
                            transitTroop.setPlayer(player);
                            tempMovesPlayer2.add(transitTroop);
                        }
                        //result.add(transitTroop);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            //Only the first answerSet is needed
            break;
        }

        return result;
    }

    private boolean checkValidMoves(ArrayList<TransitTroop> moves, int player){
        if (moves.isEmpty()) return false;

        HashMap<Integer, Integer> cyborgsSent = new HashMap<>(); //the key is the factory id, the value is the total cyborgs sent
        for (TransitTroop move : moves) {
            //check player
            if (move.getPlayer() == 0) return false;
            if (move.getPlayer() != player) return false;

            //check if cyborgs > 0
            if (move.getCyborgs() <= 0) return false;

            //update cyborgsSent
            cyborgsSent.put(move.getF1(), cyborgsSent.getOrDefault(move.getF1(), 0) + move.getCyborgs());
        }
        System.out.println(cyborgsSent);

        //check if the player has enough cyborgs to send
        for (Factory factory : world.getFactories()) {
            if (factory.getPlayer() == 1) {
                int cyborgs = factory.getCyborgs();
                if (cyborgs < cyborgsSent.getOrDefault(factory.getId(), 0)) return false;
            }
        }

        return true;
    }

    public boolean isEndGame() {
        return endGame;
    }

    public int getTurn() {
        return turn;
    }

    private boolean checkEndGame() {
        if(turn == 200) {
            endGame = true;
            return true;
        }
        int cont = 0;
        //check if player 1 or player 2 has all the factories
        for (Factory factory : world.getFactories()) {
            if (factory.getPlayer() == 1) cont++;
        }
        if (cont == world.getFactories().size()) {
            endGame = true;
            return true;
        }

        cont = 0;
        for (Factory factory : world.getFactories()) {
            if (factory.getPlayer() == -1) cont++;
        }
        if (cont == world.getFactories().size()) {
            endGame = true;
            return true;
        }
        return false;
    }
}

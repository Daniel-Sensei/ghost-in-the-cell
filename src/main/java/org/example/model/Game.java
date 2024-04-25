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

public class Game {
    public final static int FORWARD = 0;
    private static Game game = null;
    private boolean endGame;
    private final World world;

    private int turn = 0;
    private ArrayList<TransitTroop> transitTroops = new ArrayList<>();

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
        //CLEAR ALL
        EmbASPManager.getInstance().getProgram().clearAll();
        EmbASPManager.getInstance().getHandler().removeAll();

        // INPUT
        try {
            passInputToOracle();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(EmbASPManager.getInstance().getProgram().getPrograms());

        // OUTPUT
        try {
            ArrayList<TransitTroop> moves = getOutputFromOracle();
            System.out.println(moves);
        } catch (ObjectNotValidException | IllegalAnnotationException e) {
            e.printStackTrace();
        }
    }

    private void passInputToOracle() throws Exception {
        String encoding = readEncoding("encodings/encoding.asp");
        EmbASPManager.getInstance().getProgram().addProgram(encoding);

        // TURN
        EmbASPManager.getInstance().getProgram().addProgram("turn(" + turn + ").");
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

        EmbASPManager.getInstance().getHandler().addProgram(EmbASPManager.getInstance().getProgram());
    }

    private ArrayList<TransitTroop> getOutputFromOracle() throws ObjectNotValidException, IllegalAnnotationException {
        // GET OUTPUT
        Output output = EmbASPManager.getInstance().getHandler().startSync();
        ASPMapper.getInstance().registerClass(TransitTroop.class);

        ArrayList<TransitTroop> result = new ArrayList<>();

        AnswerSets answersets = (AnswerSets) output;
        for(AnswerSet a: answersets.getAnswersets()) {

            System.out.println(a.toString());
            try {
                for (Object obj : a.getAtoms()) {
                    if (!(obj instanceof TransitTroop)) continue;
                    TransitTroop transitTroop = (TransitTroop) obj;
                    result.add(transitTroop);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            //Only the first answerSet is needed
            break;
        }

        return result;
    }
}

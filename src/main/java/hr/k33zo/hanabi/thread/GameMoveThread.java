package hr.k33zo.hanabi.thread;

import hr.k33zo.hanabi.model.GameMove;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public abstract class GameMoveThread {

    private static final String GAME_MOVES_FILE_NAME =
            "files/moves.dat";

    private static Boolean fileAccessInProgress = false;

    public synchronized GameMove getLastGameMove() {

        System.out.println("Starting getting the last game move...");

        while(fileAccessInProgress) {
            try {
                System.out.println("Waiting for getting the last game move...");
                wait();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        fileAccessInProgress = true;
        System.out.println("The air is clear for getting the last game move...");

        GameMove lastGameMove = null;
        try{
            lastGameMove = getAllGameMoves().getLast();
            fileAccessInProgress = false;
            System.out.println("The last game move is successfuly fetched...");
        }catch(Exception e){
            fileAccessInProgress = false;
            System.out.println("No moves found yet...");
        }


        notifyAll();

        return lastGameMove;
    }

    public synchronized void saveNewGameMove(GameMove gameMove) {

        System.out.println("Everything is ready to save the new game move...");

        while(fileAccessInProgress) {
            try {
                System.out.println("Waiting to save the new game move...");
                wait();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        fileAccessInProgress = true;
        System.out.println("Saving the new game move in progress...");

        List<GameMove> allGameMoves = getAllGameMoves();
        allGameMoves.add(gameMove);

        try(ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream(GAME_MOVES_FILE_NAME))) {
            oos.writeObject(allGameMoves);
        }
        catch(IOException ex) {
            ex.printStackTrace();
        }

        fileAccessInProgress = false;
        System.out.println("Saving the new game move done...");

        notifyAll();

    }

    private synchronized List<GameMove> getAllGameMoves() {
        List<GameMove> allGameMoves = new ArrayList<>();

        if(Files.exists(Path.of(GAME_MOVES_FILE_NAME))) {
            try (ObjectInputStream ois = new ObjectInputStream(
                    new FileInputStream(GAME_MOVES_FILE_NAME))) {
                allGameMoves.addAll((List<GameMove>) ois.readObject());
            } catch (IOException | ClassNotFoundException ex) {
                ex.printStackTrace();
            }
        }

        return allGameMoves;
    }
}

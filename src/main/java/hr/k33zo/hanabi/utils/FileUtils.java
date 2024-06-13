package hr.k33zo.hanabi.utils;

import hr.k33zo.hanabi.enums.Suit;
import hr.k33zo.hanabi.model.Card;
import hr.k33zo.hanabi.model.Deck;
import hr.k33zo.hanabi.model.GameState;
import hr.k33zo.hanabi.model.Player;

import java.io.*;
import java.util.List;
import java.util.Map;

public class FileUtils {

    public static final String FILE_NAME = "gameState.dat";

    public static final String TARGET_FOLDER_LOCATION = "D:\\Java 2\\Hanabi\\target";

    public static void saveGameState(Deck deck, List<Player> players, int currentPlayerIndex, Map<Suit, Integer> fireworks,
                                     List<Card> discardPile, int fuses, int tips) {
        GameState gameStateToSave = GameStateUtils.createGameState(deck, players,currentPlayerIndex, fireworks,
                discardPile,fuses, tips);
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(
                FileUtils.FILE_NAME))) {
            oos.writeObject(gameStateToSave);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static GameState loadGameState() {
        GameState recoveredGameState;
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(
                FileUtils.FILE_NAME))) {
            recoveredGameState = (GameState) ois.readObject();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return recoveredGameState;
    }

}

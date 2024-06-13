package hr.k33zo.hanabi.utils;

import hr.k33zo.hanabi.enums.Suit;
import hr.k33zo.hanabi.model.Card;
import hr.k33zo.hanabi.model.Deck;
import hr.k33zo.hanabi.model.GameState;
import hr.k33zo.hanabi.model.Player;

import java.util.List;
import java.util.Map;

public class GameStateUtils {


    public static GameState createGameState(Deck deck, List<Player> players, int currentPlayerIndex, Map<Suit, Integer> fireworks,
                                            List<Card> discardPile, int fuses, int tips){



        return new GameState(deck, players, currentPlayerIndex, fireworks, discardPile, fuses, tips);

    }
}

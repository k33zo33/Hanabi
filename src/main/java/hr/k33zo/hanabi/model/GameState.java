package hr.k33zo.hanabi.model;

import hr.k33zo.hanabi.enums.Suit;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class GameState implements Serializable {

    private Deck deck;
    private List<Player> players;
    private int currentPlayerIndex;
    private Map<Suit, Integer> fireworks;


    private List<Card> discardPile;
    private Integer fuses;
    private Integer tips;

    public GameState(Deck deck, List<Player> players, int currentPlayerIndex, Map<Suit, Integer> fireworks,
                     List<Card> discardPile, Integer fuses, Integer tips){
        this.deck = deck;
        this.players = players;
        this.currentPlayerIndex = currentPlayerIndex;
        this.fireworks = fireworks;
        this.discardPile = discardPile;
        this.fuses = fuses;
        this.tips = tips;
    }



    public Deck getDeck() {
        return deck;
    }

    public void setDeck(Deck deck) {
        this.deck = deck;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public void setPlayers(List<Player> players) {
        this.players = players;
    }

    public int getCurrentPlayerIndex() {
        return currentPlayerIndex;
    }

    public void setCurrentPlayerIndex(int currentPlayerIndex) {
        this.currentPlayerIndex = currentPlayerIndex;
    }

    public Map<Suit, Integer> getFireworks() {
        return fireworks;
    }

    public void setFireworks(Map<Suit, Integer> fireworks) {
        this.fireworks = fireworks;
    }

    public List<Card> getDiscardPile() {
        return discardPile;
    }

    public void setDiscardPile(List<Card> discardPile) {
        this.discardPile = discardPile;
    }

    public Integer getFuses() {
        return fuses;
    }

    public void setFuses(int fuses) {
        this.fuses = fuses;
    }

    public Integer getTips() {
        return tips;
    }

    public void setTips(int tips) {
        this.tips = tips;
    }

}

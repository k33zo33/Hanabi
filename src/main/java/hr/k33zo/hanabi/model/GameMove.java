package hr.k33zo.hanabi.model;

import hr.k33zo.hanabi.enums.Suit;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class GameMove implements Serializable {

    private Deck deck;
    private List<Player> players;
    private int currentPlayerIndex;
    private Map<Suit, Integer> fireworks;
    private List<Card> discardPile;
    private Integer fuses;
    private Integer tips;
    private LocalDateTime dateTime;
    private MoveType moveType;

    public GameMove() {
    }



    public GameMove(Deck deck, List<Player> players, int currentPlayerIndex, Map<Suit, Integer> fireworks,
                    List<Card> discardPile, Integer fuses, Integer tips, LocalDateTime dateTime, MoveType moveType) {
        this.deck = deck;
        this.players = players;
        this.currentPlayerIndex = currentPlayerIndex;
        this.fireworks = fireworks;
        this.discardPile = discardPile;
        this.fuses = fuses;
        this.tips = tips;
        this.dateTime = dateTime;
        this.moveType = moveType;
    }

    public GameMove(String actionType, int currentPlayerIndex, int fuses, int tips, LocalDateTime dateTime, List<Player> players, List<Card> discardPile, Map<Suit, Integer> fireworks) {
        this.currentPlayerIndex = currentPlayerIndex;
        this.fuses = fuses;
        this.tips = tips;
        this.dateTime = dateTime;
        this.players = players;
        this.discardPile = discardPile;
        this.fireworks = fireworks;
        this.moveType = MoveType.valueOf(actionType);
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

    public void setFuses(Integer fuses) {
        this.fuses = fuses;
    }

    public Integer getTips() {
        return tips;
    }

    public void setTips(Integer tips) {
        this.tips = tips;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public MoveType getMoveType() {
        return moveType;
    }

    public void setMoveType(MoveType moveType) {
        this.moveType = moveType;
    }

    @Override
    public String toString() {
        return "GameMove{" +
                "deck=" + deck +
                ", players=" + players +
                ", currentPlayerIndex=" + currentPlayerIndex +
                ", fireworks=" + fireworks +
                ", discardPile=" + discardPile +
                ", fuses=" + fuses +
                ", tips=" + tips +
                '}';
    }
}

package hr.k33zo.hanabi.model;

import hr.k33zo.hanabi.enums.Suit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameState {
    private Deck deck;
    private List<Player> players;
    private int currentPlayerIndex;
    private Map<Suit, Integer> fireworks;
    private List<Card> discardPile;
    private int fuses;
    private int tips;

    public GameState() {
        deck = new Deck();
        players = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            players.add(new Player());
        }
        currentPlayerIndex = 0;
        fireworks = new HashMap<>();
        for (Suit suit : Suit.values()) {
            fireworks.put(suit, 0);
        }
        discardPile = new ArrayList<>();
        fuses = 3;
        tips = 8;
        dealInitialCards();
    }
    private void dealInitialCards() {
        for (int i = 0; i < 5; i++) {
            for (Player player : players) {
                Card card = deck.dealCard();
                card.setOwner(player); // Set the owner of the card
                player.drawCard(card);
            }
        }
    }

    public Player getCurrentPlayer() {
        return players.get(currentPlayerIndex);
    }

    public void nextPlayer() {
        currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
    }

    public void playCard(int index) {
        Player player = getCurrentPlayer();
        Card card = player.playCard(index);
        int highestValue = fireworks.get(card.getCardSuit());
        fireworks.put(card.getCardSuit(), Math.max(highestValue, card.getCadNumber()));
    }

    public boolean canPlay(Card card) {
        int highestValue = fireworks.get(card.getCardSuit());
        return card.getCadNumber() == highestValue + 1;
    }

    public void discardCard(int playerIndex, int cardIndex) {
        Player player = getCurrentPlayer();
        Card card = player.playCard(cardIndex);
        discardPile.add(card);
        tips = Math.min(tips + 1, 8);
    }

    public void giveTip() {
        //todo
        tips--;
        nextPlayer();
    }

    public void addTip() {
        if (tips == 8) {
            return;
        }
        tips++;
    }

    public int calculateScore() {
        int score = 0;
        for (int value : fireworks.values()) {
            score += value;
        }
        return score;
    }

    public void playCard(int playerIndex, int cardIndex) {
        Player player = players.get(playerIndex);
        Card card = player.playCard(cardIndex);
        if (canPlay(card)) {
            int highestValue = fireworks.get(card.getCardSuit());
            fireworks.put(card.getCardSuit(), Math.max(highestValue, card.getCadNumber()));
        } else {
            discardPile.add(card);
            fuses--;
        }
    }

    public void updateDiscardPile() {
        //TODO

    }

    public Map<Suit, Integer> getFireworks() {
        return fireworks;
    }

    public List<Card> getDiscardPile() {
        return discardPile;
    }

    public int getFuses() {
        return fuses;
    }


    public int getTips() {
        return tips;
    }

    public Deck getDeck() {
        return deck;
    }

    public List<Player> getPlayers() {
        return players;
    }


    // In GameState.java
    public List<List<Card>> getInitialHands() {
        List<List<Card>> initialHands = new ArrayList<>();
        for (Player player : players) {
            initialHands.add(new ArrayList<>(player.getHand()));
        }
        return initialHands;
    }
}

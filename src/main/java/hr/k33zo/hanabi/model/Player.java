package hr.k33zo.hanabi.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Player implements Serializable {
    private List<Card> hand;

    public Player() {
        this.hand = new ArrayList<>();
    }

    public Player(List<Card> hand) {
        this.hand = new ArrayList<>();
    }

    public List<Card> getHand() {
        return hand;
    }

    public void drawCard(Card card){
        hand.add(card);
    }

    public Card playCard(int index) {
        if (index < 0 || index >= hand.size()) {
            throw new IllegalArgumentException("Index out of bounds");
        }
        return hand.remove(index);
    }


}

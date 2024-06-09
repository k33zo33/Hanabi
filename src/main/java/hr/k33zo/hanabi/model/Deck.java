package hr.k33zo.hanabi.model;

import hr.k33zo.hanabi.enums.Suit;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Deck {

    private List<Card> cards;

    public Deck() {
        cards = new ArrayList<>();
        initializeDeck();
        shuffleDeck();
    }

    private void initializeDeck() {
        for (Suit suit : Suit.values()) {
            for (int number = 1; number <= 5; number++) {
                int copies;
                if (number == 1) {
                    copies = 3;
                } else if (number == 5) {
                    copies = 1;
                } else {
                    copies = 2;
                }
                for (int i = 0; i < copies; i++) {
                    Card card = new Card(suit, number, false);
                    cards.add(card);
                }
            }
        }
    }

    public void shuffleDeck() {
        Collections.shuffle(cards);
    }

    public List<Card> getCards() {
        return cards;
    }

    public Card dealCard() {
        return cards.remove(0);
    }

}

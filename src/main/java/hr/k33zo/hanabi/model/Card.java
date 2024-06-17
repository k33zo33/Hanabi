package hr.k33zo.hanabi.model;

import hr.k33zo.hanabi.enums.Suit;

import java.io.Serializable;

public class Card implements Serializable {

    private Suit cardSuit;
    private Integer cardNumber;

    private Player owner;

    public Card(Suit cardSuit, Integer cardNumber) {
        this.cardSuit = cardSuit;
        this.cardNumber = cardNumber;
    }

    public Suit getCardSuit() {
        return cardSuit;
    }

    public void setCardSuit(Suit cardSuit) {
        this.cardSuit = cardSuit;
    }

    public Integer getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(int cardNumber) {
        this.cardNumber = cardNumber;
    }

    public void setOwner(Player owner) {
        this.owner = owner;
    }

    public Player getOwner() {
        return owner;
    }

    @Override
    public String toString() {
        return cardSuit.name() + " " + cardNumber;
    }

    public static Card fromString(String cardStr) {
        // Remove the <card> and </card> tags and trim any remaining whitespace
        String content = cardStr.replaceAll("<card>|</card>", "").trim();

        String[] parts = content.split(" ");
        if (parts.length != 2) {
            throw new IllegalArgumentException("Invalid card string: " + cardStr);
        }

        String suitStr = parts[0];
        int number = Integer.parseInt(parts[1]);

        Suit suit = Suit.valueOf(suitStr);

        return new Card(suit, number);
    }
}

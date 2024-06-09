package hr.k33zo.hanabi.model;

import hr.k33zo.hanabi.enums.Suit;

public class Card {

    private Suit cardSuit;
    private int cardNumber;
    private Boolean isVisible;

    public Card(Suit cardSuit, int cadNumber, Boolean isVisible) {
        this.cardSuit = cardSuit;
        this.cardNumber = cadNumber;
        this.isVisible = true;
    }

    public Suit getCardSuit() {
        return cardSuit;
    }

    public void setCardSuit(Suit cardSuit) {
        this.cardSuit = cardSuit;
    }

    public int getCadNumber() {
        return cardNumber;
    }

    public void setCadNumber(int cadNumber) {
        this.cardNumber = cadNumber;
    }
    public Boolean getVisible() {
        return isVisible;
    }

    public void setVisible(Boolean visible) {
        isVisible = visible;
    }

    @Override
    public String toString() {
        return cardSuit.name() + " " + cardNumber;
    }
}

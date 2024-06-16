package hr.k33zo.hanabi.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

public class GameMove implements Serializable {

    private Integer player;
    private MoveType moveType;
    private LocalDateTime dateTime;
    private int cardIndex;
    private String hint;
    private List<String> hintedCards;
    private String cardSuit;

    public GameMove(Integer player, MoveType moveType, LocalDateTime dateTime, int cardIndex, String hint, List<String> hintedCardIndices, String cardSuit) {
        this.player = player;
        this.moveType = moveType;
        this.dateTime = dateTime;
        this.cardIndex = cardIndex;
        this.hint = hint;
        this.hintedCards = hintedCardIndices;
        this.cardSuit = cardSuit;
    }

    public Integer getPlayer() {
        return player;
    }

    public void setPlayer(Integer player) {
        this.player = player;
    }

    public MoveType getMoveType() {
        return moveType;
    }

    public void setMoveType(MoveType moveType) {
        this.moveType = moveType;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public int getCardIndex() {
        return cardIndex;
    }

    public void setCardIndex(int cardIndex) {
        this.cardIndex = cardIndex;
    }

    public String getHint() {
        return hint;
    }

    public void setHint(String hint) {
        this.hint = hint;
    }

    public List<String> getHintedCardIndices() {
        return hintedCards;
    }

    public void setHintedCardIndices(List<String> hintedCardIndices) {
        this.hintedCards = hintedCardIndices;
    }

    public String getCardSuit() {
        return cardSuit;
    }

    public void setCardSuit(String cardSuit) {
        this.cardSuit = cardSuit;
    }

    @Override
    public String toString() {
        return "player=" + player.toString() +
                ", moveType=" + moveType +
                ", dateTime=" + dateTime +
                ", cardIndex=" + cardIndex +
                ", hint='" + hint +
                ", hintedCardIndices=" + hintedCards +
                ", cardSuit='" + cardSuit ;
    }

}

package hr.k33zo.hanabi.model;

import hr.k33zo.hanabi.enums.Suit;
import javafx.scene.control.ListCell;
import javafx.scene.text.Text;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;

public class CardListCell extends ListCell<Card> {
    private Text text = new Text();

    @Override
    public void updateItem(Card card, boolean empty) {
        super.updateItem(card, empty);
        if (empty || card == null) {
            setGraphic(null);
        } else {
            text.setText(card.toString()); // assuming Card has a suitable toString method
            text.setFill(getColorForSuit(card.getCardSuit())); // set the color of the text
            HBox hBox = new HBox(text);
            hBox.setStyle("-fx-background-color: lightblue;");
            setGraphic(hBox);
        }
    }

    private Color getColorForSuit(Suit suit) {
        switch (suit) {
            case RED:
                return Color.RED;
            case BLUE:
                return Color.BLUE;
            case GREEN:
                return Color.GREEN;
            case YELLOW:
                return Color.YELLOW;
            case WHITE:
                return Color.WHITE;
            default:
                return Color.BLACK;
        }
    }
}
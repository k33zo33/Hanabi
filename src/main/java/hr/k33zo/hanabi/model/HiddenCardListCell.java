package hr.k33zo.hanabi.model;

import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;

public class HiddenCardListCell extends ListCell<Card> {
    @Override
    public void updateItem(Card card, boolean empty) {
        super.updateItem(card, empty);
        if (empty || card == null) {
            setGraphic(null);
        } else {
            Text text = new Text("Hidden Card");
            HBox hBox = new HBox(text);
            hBox.setStyle("-fx-background-color: grey;");
            setGraphic(hBox);
        }
    }
}

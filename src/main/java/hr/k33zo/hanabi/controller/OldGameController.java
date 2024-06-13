package hr.k33zo.hanabi.controller;

import hr.k33zo.hanabi.enums.Suit;
import hr.k33zo.hanabi.model.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OldGameController {
    @FXML
    private ListView<Card> player1HandListView;
    @FXML
    private ListView<Card> player2HandListView;
    @FXML
    private ListView<Card> discardPileListView;
    @FXML
    private ListView<Card> blueFireworkListView;
    @FXML
    private ListView<Card> greenFireworkListView;
    @FXML
    private ListView<Card> redFireworkListView;
    @FXML
    private ListView<Card> yellowFireworkListView;
    @FXML
    private ListView<Card> whiteFireworkListView;
    @FXML
    private Label remainingFusesLabel;
    @FXML
    private Label remainingTipsLabel;
    @FXML
    private VBox tipsVBox;

    @FXML Button giveTipCheckboxButton;




    private Integer selectedCardIndexPlayer1 = -1;
    private Integer selectedCardIndexPlayer2 = -1;
    private Player currentPlayer;
    private Integer fuseCounter;
    private Integer tipCounter;


    private OldGameState oldGameState;

    public OldGameController() {
        this.oldGameState = new OldGameState();
    }

    public void initialize() {
        player1HandListView.setCellFactory(param -> new HiddenCardListCell());
        player2HandListView.setCellFactory(param -> new CardListCell());
        discardPileListView.setCellFactory(param -> new CardListCell());
        blueFireworkListView.setCellFactory(param -> new CardListCell());
        greenFireworkListView.setCellFactory(param -> new CardListCell());
        redFireworkListView.setCellFactory(param -> new CardListCell());
        yellowFireworkListView.setCellFactory(param -> new CardListCell());
        whiteFireworkListView.setCellFactory(param -> new CardListCell());
        List<List<Card>> initialHands = oldGameState.getInitialHands();
        player1HandListView.getItems().addAll(initialHands.get(0));
        player2HandListView.getItems().addAll(initialHands.get(1));
        player1HandListView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            selectedCardIndexPlayer1 = player1HandListView.getSelectionModel().getSelectedIndex();
        });
        player2HandListView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            selectedCardIndexPlayer2 = player2HandListView.getSelectionModel().getSelectedIndex();
        });


        currentPlayer = oldGameState.getCurrentPlayer();
        fuseCounter = oldGameState.getFuses();
        tipCounter = oldGameState.getTips();
        giveTipCheckboxButton.setVisible(false);

        updateLabels();
        //updatePlayerHandListView(0);
    }
    public void handleDrawCard(ActionEvent event) {
        Player player = oldGameState.getCurrentPlayer();
        Card card = oldGameState.getDeck().dealCard();
        player.drawCard(card);
        if (player == oldGameState.getPlayers().get(0)) {
            player1HandListView.getItems().add(card);
        } else {
            player2HandListView.getItems().add(card);
        }
    }

    @FXML
    public void handlePlayCardButtonAction(ActionEvent event) {
        Player player = oldGameState.getCurrentPlayer();
        int selectedCardIndex = player == oldGameState.getPlayers().get(0) ? selectedCardIndexPlayer1 : selectedCardIndexPlayer2;
        if (player != currentPlayer || selectedCardIndex < 0) {
            return;
        }
        Card card = player.getHand().get(selectedCardIndex);
        if (oldGameState.canPlay(card)) {
            oldGameState.playCard(selectedCardIndex);
            updateFireworksListView(card);
            updatePlayerHandListView(oldGameState.getPlayers().indexOf(player));

            if (player == oldGameState.getPlayers().get(0)) {
                selectedCardIndexPlayer1 = -1;
            } else {
                selectedCardIndexPlayer2 = -1;
            }
        } else {
            oldGameState.discardCard(oldGameState.getPlayers().indexOf(player), selectedCardIndex);
            updatePlayerHandListView(oldGameState.getPlayers().indexOf(player));
            fuseCounter--;
        }
        handleDrawCard(event);
        updateLabels();
        clearCardInfoLabel(oldGameState.getPlayers().indexOf(player), selectedCardIndex);
        oldGameState.nextPlayer();
        currentPlayer = oldGameState.getCurrentPlayer();
        updatePlayerHandListView(oldGameState.getPlayers().indexOf(player));
        updateDiscardPileListView();
        clearCardInfoLabel(oldGameState.getPlayers().indexOf(player), selectedCardIndex);
        checkIfGameWon();
        checkGameOver();
    }

    @FXML
    public void handleDiscardCardButtonAction(ActionEvent event) {
        Player player = oldGameState.getCurrentPlayer();
        int selectedCardIndex = player == oldGameState.getPlayers().get(0) ? selectedCardIndexPlayer1 : selectedCardIndexPlayer2;
        if (player != currentPlayer || selectedCardIndex < 0) {
            return;
        }
        Card card = player.playCard(selectedCardIndex);
        oldGameState.getDiscardPile().add(card);
        updatePlayerHandListView(oldGameState.getPlayers().indexOf(player));
        updateDiscardPileListView();
        handleDrawCard(event);
        if (player == oldGameState.getPlayers().get(0)) {
            selectedCardIndexPlayer1 = -1;
        } else {
            selectedCardIndexPlayer2 = -1;
        }
        if (tipCounter < 8) {
            tipCounter++;
        }
        maxTipsWarning();
        updateLabels();
        clearCardInfoLabel(oldGameState.getPlayers().indexOf(player), selectedCardIndex);
        oldGameState.nextPlayer();
        currentPlayer = oldGameState.getCurrentPlayer();
        updatePlayerHandListView(oldGameState.getPlayers().indexOf(player));

    }

    @FXML
    public void handleGiveTipButtonAction(ActionEvent event) {
        Player otherPlayer = oldGameState.getPlayers().get(0) == currentPlayer ? oldGameState.getPlayers().get(1) : oldGameState.getPlayers().get(0);
        generatePossibleTips(otherPlayer);
        giveTipCheckboxButton.setVisible(true);
        tipCounter--;
        updateLabels();
        checkTips();
    }

    public void  handleGiveTipCheckboxButton(ActionEvent event){
        Player player = oldGameState.getCurrentPlayer();
        String selectedTip = (String) giveTipCheckboxButton.getUserData();
        if (selectedTip == null) {
            return;
        }

        Player otherPlayer = oldGameState.getPlayers().get(0) == currentPlayer ? oldGameState.getPlayers().get(1) : oldGameState.getPlayers().get(0);
        int otherPlayerIndex = oldGameState.getPlayers().indexOf(otherPlayer) + 1;

        String indicesText = selectedTip.substring(selectedTip.lastIndexOf("indices") + 8, selectedTip.length() - 1);
        String[] indicesArray = indicesText.split(", ");
        for (String index : indicesArray) {
            index = index.replace("[", "").replace("]", "");
            int cardIndex = Integer.parseInt(index);
            Label label = (Label) tipsVBox.getScene().lookup("#p" + otherPlayerIndex + "CardInfoLabel" + (cardIndex + 1));
            if (selectedTip.contains("cards with number")) {
                label.setText(label.getText() + " " + "This card is " + otherPlayer.getHand().get(cardIndex).getCadNumber());
            } else {
                label.setText(label.getText() + " " + "This card is " + otherPlayer.getHand().get(cardIndex).getCardSuit());
            }
        }
        giveTipCheckboxButton.setUserData(null);
        giveTipCheckboxButton.setVisible(false);
        tipsVBox.setVisible(false);
        updateLabels();
        oldGameState.nextPlayer();
        currentPlayer = oldGameState.getCurrentPlayer();
        updatePlayerHandListView(oldGameState.getPlayers().indexOf(player));
    }

    private void giveTipAboutSuit(Player player, Suit suit) {
        for (Card card : player.getHand()) {
            if (card.getCardSuit() == suit) {
                card.setVisible(true);
            }
        }
    }

    private void updatePlayerHandListView(int playerIndex) {
        Player player = oldGameState.getPlayers().get(playerIndex);
        if (playerIndex == 0) {
            player1HandListView.setCellFactory(param -> new CardListCell());
            player2HandListView.setCellFactory(param -> new HiddenCardListCell());
            player1HandListView.getItems().setAll(player.getHand());
            player1HandListView.setDisable(player != currentPlayer);
            player2HandListView.setDisable(player == currentPlayer);
        } else {
            player1HandListView.setCellFactory(param -> new HiddenCardListCell());
            player2HandListView.setCellFactory(param -> new CardListCell());
            player2HandListView.getItems().setAll(player.getHand());
            player2HandListView.setDisable(player != currentPlayer);
            player1HandListView.setDisable(player == currentPlayer);
        }
    }

    private void updateDiscardPileListView() {
        discardPileListView.getItems().setAll(oldGameState.getDiscardPile());
    }

    private void updateFireworksListView(Card card) {
        oldGameState.getFireworks().put(card.getCardSuit(), card.getCadNumber());
        switch (card.getCardSuit()) {
            case BLUE:
                blueFireworkListView.getItems().add(card);
                break;
            case GREEN:
                greenFireworkListView.getItems().add(card);
                break;
            case RED:
                redFireworkListView.getItems().add(card);
                break;
            case YELLOW:
                yellowFireworkListView.getItems().add(card);
                break;
            case WHITE:
                whiteFireworkListView.getItems().add(card);
                break;
        }
    }

    private void updateLabels() {
        remainingFusesLabel.setText(fuseCounter.toString());
        remainingTipsLabel.setText(tipCounter.toString());
    }

    public void checkGameOver() {
        if (fuseCounter == 0) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Game Over");
            alert.setHeaderText(null);
            alert.setContentText("Game Over");

            alert.showAndWait();

            resetGame();
        }
    }

    public void checkTips() {
        if (tipCounter == 0) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("No more tips!");
            alert.setHeaderText(null);
            alert.setContentText("No more tips!");
            alert.showAndWait();
        }
    }
    public void maxTipsWarning() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("You have maximum tips!");
        alert.setHeaderText(null);
        alert.setContentText("You have maximum tips!");
        alert.showAndWait();

    }

    public void resetGame() {

        oldGameState = new OldGameState();

        player1HandListView.getItems().setAll(oldGameState.getPlayers().get(0).getHand());
        player2HandListView.getItems().setAll(oldGameState.getPlayers().get(1).getHand());
        discardPileListView.getItems().clear();
        blueFireworkListView.getItems().clear();
        greenFireworkListView.getItems().clear();
        redFireworkListView.getItems().clear();
        yellowFireworkListView.getItems().clear();
        whiteFireworkListView.getItems().clear();

        selectedCardIndexPlayer1 = -1;
        selectedCardIndexPlayer2 = -1;

        currentPlayer = oldGameState.getCurrentPlayer();

        fuseCounter = oldGameState.getFuses();
        tipCounter = oldGameState.getTips();

        updateLabels();
        updatePlayerHandListView(0);
    }

    public void generatePossibleTips(Player player) {
        Map<Suit, List<Integer>> suitIndices = new HashMap<>();
        Map<Integer, List<Integer>> numberIndices = new HashMap<>();

        for (Suit suit : Suit.values()) {
            suitIndices.put(suit, new ArrayList<>());
        }
        for (int number = 1; number <= 5; number++) {
            numberIndices.put(number, new ArrayList<>());
        }

        List<Card> hand = player.getHand();
        for (int i = 0; i < hand.size(); i++) {
            Card card = hand.get(i);
            suitIndices.get(card.getCardSuit()).add(i);
            numberIndices.get(card.getCadNumber()).add(i);
        }

        List<String> possibleTips = new ArrayList<>();
        for (Suit suit : Suit.values()) {
            List<Integer> indices = suitIndices.get(suit);
            if (!indices.isEmpty()) {
                possibleTips.add("There are " + indices.size() + " " + suit + " cards in your hand at indices "
                        + indices + ".");
            }
        }
        for (int number = 1; number <= 5; number++) {
            List<Integer> indices = numberIndices.get(number);
            if (!indices.isEmpty()) {
                possibleTips.add("There are " + indices.size() + " cards with number " + number +
                        " in your hand at indices " + indices + ".");
            }
        }

        tipsVBox.getChildren().clear();
        tipsVBox.setVisible(true);
        for (String tip : possibleTips) {
            CheckBox checkBox = new CheckBox(tip);
            checkBox.setOnAction(event -> {
                if (checkBox.isSelected()) {
                    // Uncheck all other CheckBoxes
                    for (Node node : tipsVBox.getChildren()) {
                        if (node instanceof CheckBox && node != checkBox) {
                            ((CheckBox) node).setSelected(false);
                        }
                    }
                    giveTipCheckboxButton.setUserData(tip);
                }
            });
            tipsVBox.getChildren().add(checkBox);
        }
    }

    private void clearCardInfoLabel(int playerIndex, int cardIndex) {
        Label label = (Label) tipsVBox.getScene().lookup("#p" + (playerIndex + 1) + "CardInfoLabel" + (cardIndex + 1));
        label.setText("");
    }

    private void checkIfGameWon() {
        boolean gameWon = true;
        for (Map.Entry<Suit, Integer> entry : oldGameState.getFireworks().entrySet()) {
            if (entry.getValue() != 5) {
                gameWon = false;
                break;
            }
        }
        if (gameWon) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Game Won");
            alert.setHeaderText(null);
            alert.setContentText("Game Won");
            alert.showAndWait();
            resetGame();
        }
    }
}
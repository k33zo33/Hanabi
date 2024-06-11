package hr.k33zo.hanabi.controller;

import hr.k33zo.hanabi.enums.Suit;
import hr.k33zo.hanabi.model.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameController {
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


    private Integer selectedCardIndexPlayer1 = -1;
    private Integer selectedCardIndexPlayer2 = -1;
    private Player currentPlayer;
    private Integer fuseCounter;
    private Integer tipCounter;


    private GameState gameState;

    public GameController() {
        this.gameState = new GameState();
    }

    public void initialize() {
        player1HandListView.setCellFactory(param -> new CardListCell());
        player2HandListView.setCellFactory(param -> new CardListCell());
        discardPileListView.setCellFactory(param -> new CardListCell());
        blueFireworkListView.setCellFactory(param -> new CardListCell());
        greenFireworkListView.setCellFactory(param -> new CardListCell());
        redFireworkListView.setCellFactory(param -> new CardListCell());
        yellowFireworkListView.setCellFactory(param -> new CardListCell());
        whiteFireworkListView.setCellFactory(param -> new CardListCell());
        List<List<Card>> initialHands = gameState.getInitialHands();
        player1HandListView.getItems().addAll(initialHands.get(0));
        player2HandListView.getItems().addAll(initialHands.get(1));
        player1HandListView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            selectedCardIndexPlayer1 = player1HandListView.getSelectionModel().getSelectedIndex();
        });
        player2HandListView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            selectedCardIndexPlayer2 = player2HandListView.getSelectionModel().getSelectedIndex();
        });

        currentPlayer = gameState.getCurrentPlayer();
        fuseCounter = gameState.getFuses();
        tipCounter = gameState.getTips();

        updateLabels();
        updatePlayerHandListView(0);
    }
    public void handleDrawCard(ActionEvent event) {
        Player player = gameState.getCurrentPlayer();
        Card card = gameState.getDeck().dealCard();
        player.drawCard(card);
        if (player == gameState.getPlayers().get(0)) {
            player1HandListView.getItems().add(card);
        } else {
            player2HandListView.getItems().add(card);
        }
    }

    @FXML
    public void handlePlayCardButtonAction(ActionEvent event) {
        Player player = gameState.getCurrentPlayer();
        int selectedCardIndex = player == gameState.getPlayers().get(0) ? selectedCardIndexPlayer1 : selectedCardIndexPlayer2;
        if (player != currentPlayer || selectedCardIndex < 0) {
            return;
        }
        Card card = player.getHand().get(selectedCardIndex);
        if (gameState.canPlay(card)) {
            gameState.playCard(selectedCardIndex);
            updateFireworksListView(card);
            updatePlayerHandListView(gameState.getPlayers().indexOf(player));

            if (player == gameState.getPlayers().get(0)) {
                selectedCardIndexPlayer1 = -1;
            } else {
                selectedCardIndexPlayer2 = -1;
            }
        } else {
            gameState.discardCard(gameState.getPlayers().indexOf(player), selectedCardIndex);
            updatePlayerHandListView(gameState.getPlayers().indexOf(player));
            fuseCounter--;
        }
        handleDrawCard(event);
        updateLabels();
        gameState.nextPlayer();
        currentPlayer = gameState.getCurrentPlayer();
        updatePlayerHandListView(gameState.getPlayers().indexOf(player));
        updateDiscardPileListView();
        checkGameOver();
    }

    @FXML
    public void handleDiscardCardButtonAction(ActionEvent event) {
        Player player = gameState.getCurrentPlayer();
        int selectedCardIndex = player == gameState.getPlayers().get(0) ? selectedCardIndexPlayer1 : selectedCardIndexPlayer2;
        if (player != currentPlayer || selectedCardIndex < 0) {
            return;
        }
        Card card = player.playCard(selectedCardIndex);
        gameState.getDiscardPile().add(card);
        updatePlayerHandListView(gameState.getPlayers().indexOf(player));
        updateDiscardPileListView();
        handleDrawCard(event);
        if (player == gameState.getPlayers().get(0)) {
            selectedCardIndexPlayer1 = -1;
        } else {
            selectedCardIndexPlayer2 = -1;
        }
        if (tipCounter < 8) {
            tipCounter++;
        }
        maxTipsWarning();
        updateLabels();
        gameState.nextPlayer();
        currentPlayer = gameState.getCurrentPlayer();
        updatePlayerHandListView(gameState.getPlayers().indexOf(player));

    }

    @FXML
    public void handleGiveTipButtonAction(ActionEvent event) {
 /*     Player currentPlayer = gameState.getCurrentPlayer();
        Player otherPlayer = gameState.getPlayers().get(0) == currentPlayer ? gameState.getPlayers().get(1) : gameState.getPlayers().get(0);

        Card cardToGiveTipAbout = otherPlayer.getHand().get(0);

        giveTipAboutSuit(otherPlayer, cardToGiveTipAbout.getCardSuit());

        updatePlayerHandListView(gameState.getPlayers().indexOf(otherPlayer));
        gameState.giveTip();
        updateLabels();*/
        Player otherPlayer = gameState.getPlayers().get(0) == currentPlayer ? gameState.getPlayers().get(1) : gameState.getPlayers().get(0);
        generatePossibleTips(otherPlayer);
        tipCounter--;
        updateLabels();
        checkTips();
    }

    private void giveTipAboutSuit(Player player, Suit suit) {
        for (Card card : player.getHand()) {
            if (card.getCardSuit() == suit) {
                card.setVisible(true);
            }
        }
    }

    private void updatePlayerHandListView(int playerIndex) {
        Player player = gameState.getPlayers().get(playerIndex);
        if (playerIndex == 0) {
            player1HandListView.setCellFactory(param -> new HiddenCardListCell());
            player2HandListView.setCellFactory(param -> new CardListCell());
            player1HandListView.getItems().setAll(player.getHand());
            player1HandListView.setDisable(player != currentPlayer);
            player2HandListView.setDisable(player == currentPlayer);


        } else {
            player1HandListView.setCellFactory(param -> new CardListCell());
            player2HandListView.setCellFactory(param -> new HiddenCardListCell());
            player2HandListView.getItems().setAll(player.getHand());
            player2HandListView.setDisable(player != currentPlayer);
            player1HandListView.setDisable(player == currentPlayer);
        }
    }

    private void updateDiscardPileListView() {
        discardPileListView.getItems().setAll(gameState.getDiscardPile());
    }

    private boolean canPlayCard(Card card) {
        int currentHighestValue = gameState.getFireworks().get(card.getCardSuit());
        return card.getCadNumber() == currentHighestValue + 1;
    }

    private void updateFireworksListView(Card card) {
        gameState.getFireworks().put(card.getCardSuit(), card.getCadNumber());
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

        gameState = new GameState();

        // UI
        player1HandListView.getItems().setAll(gameState.getPlayers().get(0).getHand());
        player2HandListView.getItems().setAll(gameState.getPlayers().get(1).getHand());
        discardPileListView.getItems().clear();
        blueFireworkListView.getItems().clear();
        greenFireworkListView.getItems().clear();
        redFireworkListView.getItems().clear();
        yellowFireworkListView.getItems().clear();
        whiteFireworkListView.getItems().clear();

        selectedCardIndexPlayer1 = -1;
        selectedCardIndexPlayer2 = -1;

        currentPlayer = gameState.getCurrentPlayer();

        fuseCounter = gameState.getFuses();
        tipCounter = gameState.getTips();

        // Update the labels
        updateLabels();
        updatePlayerHandListView(0);
    }

    public void generatePossibleTips(Player player) {
        Map<Suit, List<Integer>> suitIndices = new HashMap<>();
        Map<Integer, List<Integer>> numberIndices = new HashMap<>();

        // Initialize the maps
        for (Suit suit : Suit.values()) {
            suitIndices.put(suit, new ArrayList<>());
        }
        for (int number = 1; number <= 5; number++) {
            numberIndices.put(number, new ArrayList<>());
        }

        //  suit and number
        List<Card> hand = player.getHand();
        for (int i = 0; i < hand.size(); i++) {
            Card card = hand.get(i);
            suitIndices.get(card.getCardSuit()).add(i);
            numberIndices.get(card.getCadNumber()).add(i);
        }

        // Generate the tips
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
                    // Give the tip
                    //giveTip(checkBox.getText());
                }
            });
            tipsVBox.getChildren().add(checkBox);
        }

        // Display the tips
        /*Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Possible Tips");
        alert.setHeaderText(null);
        alert.setContentText(String.join("\n", possibleTips));
        alert.showAndWait();*/
    }

    public  Player getCurrentPlayer() {
        return currentPlayer;
    }


}
package hr.k33zo.hanabi.controller;

import hr.k33zo.hanabi.enums.Suit;
import hr.k33zo.hanabi.model.Card;
import hr.k33zo.hanabi.model.CardListCell;
import hr.k33zo.hanabi.model.GameState;
import hr.k33zo.hanabi.model.Player;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;

import java.util.List;

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


    private int selectedCardIndex = -1;



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
            selectedCardIndex = player1HandListView.getSelectionModel().getSelectedIndex();
        });
        updateLabels();
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
        if (selectedCardIndex >= 0) {
            Player player = gameState.getCurrentPlayer();
            Card card = player.getHand().get(selectedCardIndex);
            if (canPlayCard(card)) {
                gameState.playCard(selectedCardIndex);
                updateFireworksListView(card);
                updatePlayerHandListView(gameState.getPlayers().indexOf(player));
                handleDrawCard(event);
                selectedCardIndex = -1;
            }
        }
        updateLabels();
    }

    @FXML
    public void handleDiscardCardButtonAction(ActionEvent event) {
        if (selectedCardIndex >= 0) {
            Player player = gameState.getCurrentPlayer();
            Card card = player.playCard(selectedCardIndex);
            gameState.getDiscardPile().add(card);
            updatePlayerHandListView(gameState.getPlayers().indexOf(player));
            updateDiscardPileListView();
            handleDrawCard(event);
            selectedCardIndex = -1;
            updateLabels();
        }
    }

    @FXML
    public void handleGiveTipButtonAction(ActionEvent event) {
        Player currentPlayer = gameState.getCurrentPlayer();
        Player otherPlayer = gameState.getPlayers().get(0) == currentPlayer ? gameState.getPlayers().get(1) : gameState.getPlayers().get(0);

        Card cardToGiveTipAbout = otherPlayer.getHand().get(0);

        // Give a tip about the card's suit
        // This is just an example, you might want to implement a different logic
        giveTipAboutSuit(otherPlayer, cardToGiveTipAbout.getCardSuit());

        // Update the UI to reflect the tip given
        // This is just an example, you might want to implement a different logic
        updatePlayerHandListView(gameState.getPlayers().indexOf(otherPlayer));
        gameState.giveTip();
        updateLabels();
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
            player1HandListView.getItems().setAll(player.getHand());
        } else {
            player2HandListView.getItems().setAll(player.getHand());
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
        remainingFusesLabel.setText(String.valueOf(gameState.getFuses()));
        remainingTipsLabel.setText(String.valueOf(gameState.getTips()));
    }

}
package hr.k33zo.hanabi.controller;

import hr.k33zo.hanabi.GameApplication;
import hr.k33zo.hanabi.enums.Suit;
import hr.k33zo.hanabi.model.*;
import hr.k33zo.hanabi.thread.GetLastMoveThread;
import hr.k33zo.hanabi.thread.SaveGameMoveThread;
import hr.k33zo.hanabi.utils.*;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.io.IOException;
import java.rmi.RemoteException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static hr.k33zo.hanabi.GameApplication.remoteChatService;

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
    @FXML
    private Button sendButton;
    @FXML
    private TextArea chatTextArea;
    @FXML
    private TextField messageTextField;
    @FXML
    Button giveTipCheckboxButton;
    @FXML
    Label lastGameMoveLabel;
    private Integer selectedCardIndexPlayer1 = -1;
    private Integer selectedCardIndexPlayer2 = -1;
    private Player currentPlayer;
    private Integer tips;
    private Deck deck;
    private List<Player> players;
    private int currentPlayerIndex;
    private Map<Suit, Integer> fireworks;
    private List<Card> discardPile;
    private Integer fuses;
    private List<String> player1HandTips;
    private List<String> player2HandTips;
    private GameState gameState;
    Integer fuseCounter = 3;
    Integer tipCounter = 8;


    public void initialize() {
        deck = new Deck();
        players = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            players.add(new Player());
        }
        currentPlayerIndex = 0;
        fireworks = new HashMap<>();
        for (Suit suit : Suit.values()) {
            fireworks.put(suit, 0);
        }
        discardPile = new ArrayList<>();
        fuses = 3;
        tips = 8;
        player1HandTips = new ArrayList<>();
        player2HandTips = new ArrayList<>();
        gameState = new GameState(deck, players, currentPlayerIndex, fireworks, discardPile, fuses, tips, player1HandTips, player2HandTips);
        dealInitialCards();
        chatTextArea.setDisable(true);

        if (GameApplication.loggedInRoleName != RoleName.SINGLE_PLAYER){
            final Timeline timeline = new Timeline(
                    new KeyFrame(
                            Duration.millis(1000),
                            event -> {
                                List<String> chatMessages = null;
                                try {
                                    chatMessages = remoteChatService.getAllChatMessages();
                                } catch (RemoteException e) {
                                    throw new RuntimeException(e);
                                }

                                chatTextArea.clear();
                                for (String chatMessage : chatMessages) {
                                    chatTextArea.appendText(chatMessage + "\n");
                                }
                            }
                    )
            );
            timeline.setCycleCount(Animation.INDEFINITE);
            timeline.play();
        }

        if (GameApplication.loggedInRoleName == RoleName.SERVER){
            player1HandListView.setCellFactory(param -> new HiddenCardListCell());
            player2HandListView.setCellFactory(param -> new CardListCell());
            player2HandListView.setDisable(true);
        }
        else if (GameApplication.loggedInRoleName == RoleName.CLIENT) {
            player1HandListView.setCellFactory(param -> new CardListCell());
            player2HandListView.setCellFactory(param -> new HiddenCardListCell());
            player1HandListView.setDisable(true);
        }
        else {
            player1HandListView.setCellFactory(param -> new HiddenCardListCell());
            player2HandListView.setCellFactory(param -> new CardListCell());
            player2HandListView.setDisable(true);
        }

        discardPileListView.setCellFactory(param -> new CardListCell());
        blueFireworkListView.setCellFactory(param -> new CardListCell());
        greenFireworkListView.setCellFactory(param -> new CardListCell());
        redFireworkListView.setCellFactory(param -> new CardListCell());
        yellowFireworkListView.setCellFactory(param -> new CardListCell());
        whiteFireworkListView.setCellFactory(param -> new CardListCell());

        List<List<Card>> initialHands = new ArrayList<>();
        for (Player player : players) {
            initialHands.add(new ArrayList<>(player.getHand()));
        }
        player1HandListView.getItems().addAll(initialHands.get(0));
        player2HandListView.getItems().addAll(initialHands.get(1));
        player1HandListView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            selectedCardIndexPlayer1 = player1HandListView.getSelectionModel().getSelectedIndex();
        });
        player2HandListView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            selectedCardIndexPlayer2 = player2HandListView.getSelectionModel().getSelectedIndex();
        });

        GetLastMoveThread getLastMoveThread = new GetLastMoveThread(lastGameMoveLabel);
        Thread threadStarter = new Thread(getLastMoveThread);
        threadStarter.start();


        currentPlayer = players.get(currentPlayerIndex);
        fuses = gameState.getFuses();
        tips = gameState.getTips();
        giveTipCheckboxButton.setVisible(false);

        updateLabels();

    }

    public void updateGameState(GameState gameState){
          restoreGameState(gameState);
    }

    public void saveGame(){
        FileUtils.saveGameState(deck, players, currentPlayerIndex, fireworks, discardPile, fuses, tips, player1HandTips, player2HandTips);
    }

    public void loadGame(){
        GameState recoveredGameState = FileUtils.loadGameState();
        restoreGameState(recoveredGameState);
        updateLabels();
    }
    private void restoreGameState(GameState restoredGameState) {
        deck = restoredGameState.getDeck();
        players = restoredGameState.getPlayers();
        currentPlayerIndex = restoredGameState.getCurrentPlayerIndex();
        fireworks = restoredGameState.getFireworks();
        discardPile = restoredGameState.getDiscardPile();
        fuses = restoredGameState.getFuses();
        tips = restoredGameState.getTips();
        player1HandTips = restoredGameState.getPlayer1HandTips();
        player2HandTips = restoredGameState.getPlayer2HandTips();

        player1HandListView.getItems().clear();
        player2HandListView.getItems().clear();
        discardPileListView.getItems().clear();
        blueFireworkListView.getItems().clear();
        greenFireworkListView.getItems().clear();
        redFireworkListView.getItems().clear();
        yellowFireworkListView.getItems().clear();
        whiteFireworkListView.getItems().clear();

        gameState = GameStateUtils.createGameState(deck, players, currentPlayerIndex, fireworks, discardPile, fuses, tips, player1HandTips, player2HandTips);

        List<List<Card>> initialHands = new ArrayList<>();
        for (Player player : players) {
            initialHands.add(new ArrayList<>(player.getHand()));
        }
        player1HandListView.getItems().addAll(initialHands.get(0));
        player2HandListView.getItems().addAll(initialHands.get(1));

        discardPileListView.getItems().addAll(discardPile);

        for (Suit suit : Suit.values()) {
            int highestValue = fireworks.getOrDefault(suit, 0);
            for (int i = 1; i <= highestValue; i++) {
                Card card = new Card(suit, i); // assuming the card's visibility should be true for fireworks
                switch (suit) {
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
        }

        player1HandListView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            selectedCardIndexPlayer1 = player1HandListView.getSelectionModel().getSelectedIndex();
        });
        player2HandListView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            selectedCardIndexPlayer2 = player2HandListView.getSelectionModel().getSelectedIndex();
        });

        currentPlayer = players.get(currentPlayerIndex);
        fuses = gameState.getFuses();
        tips = gameState.getTips();
        giveTipCheckboxButton.setVisible(false);

        updatePlayerHandLabels(0, player1HandTips);
        updatePlayerHandLabels(1, player2HandTips);



        updateLabels();
        updatePlayerHandListView(0);
        updatePlayerHandListView(1);


    }

    private void dealInitialCards() {
        for (int i = 0; i < 5; i++) {
            for (Player player : players) {
                Card card = deck.dealCard();
                card.setOwner(player);
                player.drawCard(card);
            }
        }
    }
    public boolean canPlay(Card card) {
        int highestValue = fireworks.get(card.getCardSuit());
        return card.getCardNumber() == highestValue + 1;
    }

    public void handleDrawCard(ActionEvent event) {
        Player player = players.get(currentPlayerIndex);
        Card card = gameState.getDeck().dealCard();
        player.drawCard(card);
        if (player == gameState.getPlayers().get(0)) {
            player1HandListView.getItems().add(card);
        } else {
            player2HandListView.getItems().add(card);
        }
    }
    public void playCard(int index) {
        Player player = players.get(currentPlayerIndex);
        Card card = player.playCard(index);
        int highestValue = fireworks.get(card.getCardSuit());
        fireworks.put(card.getCardSuit(), Math.max(highestValue, card.getCardNumber()));
    }

    public void discardCard(int playerIndex, int cardIndex) {
        Player player = players.get(currentPlayerIndex);
        Card card = player.playCard(cardIndex);
        discardPile.add(card);
    }
    public void nextPlayer() {
        if (currentPlayerIndex == 0) {
            currentPlayerIndex = 1;
        } else {
            currentPlayerIndex = 0;
        }
    }

    @FXML
    public void handlePlayCardButtonAction(ActionEvent event) {
        Player player = players.get(currentPlayerIndex);
        int selectedCardIndex = player == gameState.getPlayers().get(0) ? selectedCardIndexPlayer1 : selectedCardIndexPlayer2;
        if (player != currentPlayer || selectedCardIndex < 0) {
            return;
        }
        Card card = player.getHand().get(selectedCardIndex);
        if (canPlay(card)) {
            playCard(selectedCardIndex);
            updateFireworksListView(card);
            updatePlayerHandListView(gameState.getPlayers().indexOf(player));

            if (player == gameState.getPlayers().get(0)) {
                selectedCardIndexPlayer1 = -1;
            } else {
                selectedCardIndexPlayer2 = -1;
            }
        } else {
            discardCard(gameState.getPlayers().indexOf(player), selectedCardIndex);
            updatePlayerHandListView(gameState.getPlayers().indexOf(player));
            fuses--;
            fuseCounter--;
        }
        handleDrawCard(event);
        updateLabels();
        clearCardInfoLabel(gameState.getPlayers().indexOf(player), selectedCardIndex);


        LocalDateTime dateTime = LocalDateTime.now();

        GameMove gameMove = new GameMove(deck,
                players, currentPlayerIndex, fireworks, discardPile, fuses, tips, dateTime, MoveType.PLAY);

        SaveGameMoveThread saveGameMoveThread = new SaveGameMoveThread(gameMove);

        XmlUtils.saveGameMove(gameMove);

        //save to xml

        Thread threadStarter = new Thread(saveGameMoveThread);
        threadStarter.start();

        nextPlayer();
        currentPlayer = players.get(currentPlayerIndex);
        RoleName roleName = GameApplication.loggedInRoleName;
        updatePlayerHandListView(gameState.getPlayers().indexOf(player));
        updateDiscardPileListView();
        clearCardInfoLabel(gameState.getPlayers().indexOf(player), selectedCardIndex);

        GameState gameStateToSend = GameStateUtils.createGameState(deck, players, currentPlayerIndex, fireworks, discardPile,
                fuses, tips, player1HandTips, player2HandTips);



        if (roleName == RoleName.CLIENT){
            NetworkingUtils.sendGameStateToServer(gameStateToSend);
        }
        else if(roleName == RoleName.SERVER) {
            NetworkingUtils.sendGameStateToClient(gameStateToSend);
        }

        checkIfGameWon();
        checkGameOver();

    }

    @FXML
    public void handleDiscardCardButtonAction(ActionEvent event) {
        Player player = players.get(currentPlayerIndex);
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
        if (tips < 8) {
            tips++;
            tipCounter++;
        }
        maxTipsWarning();
        updateLabels();
        clearCardInfoLabel(gameState.getPlayers().indexOf(player), selectedCardIndex);
        RoleName roleName = GameApplication.loggedInRoleName;
        LocalDateTime dateTime = LocalDateTime.now();
        GameMove gameMove = new GameMove(deck,
                players, currentPlayerIndex, fireworks, discardPile, fuses, tips, dateTime, MoveType.PLAY);
        SaveGameMoveThread saveGameMoveThread = new SaveGameMoveThread(gameMove);
        XmlUtils.saveGameMove(gameMove);
        Thread threadStarter = new Thread(saveGameMoveThread);
        threadStarter.start();



        XmlUtils.saveGameMove(gameMove);
        nextPlayer();
        currentPlayer = players.get(currentPlayerIndex);
        updatePlayerHandListView(gameState.getPlayers().indexOf(player));

        GameState gameStateToSend = GameStateUtils.createGameState(deck, players, currentPlayerIndex, fireworks, discardPile,
                fuses, tips, player1HandTips, player2HandTips);

        if (roleName == RoleName.CLIENT){
            NetworkingUtils.sendGameStateToServer(gameStateToSend);
        }
        else if(roleName == RoleName.SERVER) {
            NetworkingUtils.sendGameStateToClient(gameStateToSend);
        }

    }

    @FXML
    public void handleGiveTipButtonAction(ActionEvent event) {
        Player otherPlayer = gameState.getPlayers().get(0) == currentPlayer ? gameState.getPlayers().get(1) : gameState.getPlayers().get(0);
        generatePossibleTips(otherPlayer);
        giveTipCheckboxButton.setVisible(true);
        tips--;
        tipCounter--;
        updateLabels();
        checkTips();
    }


    public void handleSendButtonAction(ActionEvent event) {
        String message = messageTextField.getText();
        try {
            remoteChatService.sendMessage(GameApplication.loggedInRoleName + ": " + message);
            List<String>  chatMessages = remoteChatService.getAllChatMessages();
            chatTextArea.clear();
            messageTextField.clear();

            for(String chatMessage : chatMessages){
                chatTextArea.appendText(chatMessage + "\n");
            }
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public void handleGiveTipCheckboxButton(ActionEvent event) {
        Player currentPlayer = players.get(currentPlayerIndex);
        String selectedTip = (String) giveTipCheckboxButton.getUserData();
        if (selectedTip == null) {
            return;
        }

        Player otherPlayer = (gameState.getPlayers().get(0) == currentPlayer) ? gameState.getPlayers().get(1) : gameState.getPlayers().get(0);

        String indicesText = selectedTip.substring(selectedTip.lastIndexOf("indices") + 8, selectedTip.length() - 1);
        String[] indicesArray = indicesText.split(", ");
        List<String> tipsList = new ArrayList<>();

        for (String index : indicesArray) {
            index = index.replace("[", "").replace("]", "");
            int cardIndex = Integer.parseInt(index);
            if (selectedTip.contains("cards with number")) {
                tipsList.add("This card is " + otherPlayer.getHand().get(cardIndex).getCardNumber());
            } else {
                tipsList.add("This card is " + otherPlayer.getHand().get(cardIndex).getCardSuit());
            }
        }

        if (currentPlayer == gameState.getPlayers().get(0)) {
            player1HandTips.clear();
            player1HandTips.addAll(tipsList);
        } else {
            player2HandTips.clear();
            player2HandTips.addAll(tipsList);
        }

        String tip = null;
        int cardIndex = 0;
        int otherPlayerIndex = gameState.getPlayers().indexOf(otherPlayer) + 1;
        for (int i = 0; i < tipsList.size(); i++) {
            tip = tipsList.get(i);
            cardIndex = Integer.parseInt(indicesArray[i].replaceAll("[\\[\\]]", ""));
            Label label = (Label) tipsVBox.getScene().lookup("#p" + otherPlayerIndex + "CardInfoLabel" + (cardIndex + 1));
            label.setText(label.getText() + " " + tip);
        }

        giveTipCheckboxButton.setUserData(null);
        giveTipCheckboxButton.setVisible(false);
        tipsVBox.setVisible(false);

        RoleName roleName = GameApplication.loggedInRoleName;
        updateLabels();

        LocalDateTime dateTime = LocalDateTime.now();
        GameMove gameMove = new GameMove(deck,
                players, currentPlayerIndex, fireworks, discardPile, fuses, tips, dateTime, MoveType.PLAY);

        SaveGameMoveThread saveGameMoveThread = new SaveGameMoveThread(gameMove);
        XmlUtils.saveGameMove(gameMove);
        Thread threadStarter = new Thread(saveGameMoveThread);
        threadStarter.start();

        nextPlayer();
        currentPlayer = players.get(currentPlayerIndex);
        updatePlayerHandListView(gameState.getPlayers().indexOf(currentPlayer));


        GameState gameStateToSend = GameStateUtils.createGameState(deck, players, currentPlayerIndex, fireworks, discardPile, fuses, tips, player1HandTips, player2HandTips);

        if (roleName == RoleName.CLIENT){
            NetworkingUtils.sendGameStateToServer(gameStateToSend);
        }
        else if (roleName == RoleName.SERVER) {
            NetworkingUtils.sendGameStateToClient(gameStateToSend);
        }
    }


    private void updatePlayerHandListView(int playerIndex) {
        Player player = gameState.getPlayers().get(playerIndex);
        if (GameApplication.loggedInRoleName == RoleName.SINGLE_PLAYER){
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
        } else {
            if (playerIndex == 0) {
                player1HandListView.getItems().setAll(player.getHand());
            } else {
                player2HandListView.getItems().setAll(player.getHand());
            }
        }
    }



    private void updateDiscardPileListView() {
        discardPileListView.getItems().setAll(gameState.getDiscardPile());
    }

    private void updateFireworksListView(Card card) {
        gameState.getFireworks().put(card.getCardSuit(), card.getCardNumber());
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
//        remainingFusesLabel.setText(gameState.getFuses().toString());
//        remainingTipsLabel.setText(gameState.getTips().toString());
        remainingFusesLabel.setText(fuseCounter.toString());
        remainingTipsLabel.setText(tipCounter.toString());
    }

    public void checkGameOver() {
        if (fuses == 0) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Game Over");
            alert.setHeaderText(null);
            alert.setContentText("Game Over");
            alert.showAndWait();
            resetGame();
        }
    }

    public void checkTips() {
        if (tips == 0) {
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
        deck = new Deck();
        players = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            players.add(new Player());
        }
        currentPlayerIndex = 0;
        fireworks = new HashMap<>();
        for (Suit suit : Suit.values()) {
            fireworks.put(suit, 0);
        }
        discardPile = new ArrayList<>();
        fuses = 3;
        tips = 8;
        remainingFusesLabel.setText(fuses.toString());
        remainingTipsLabel.setText(tips.toString());
        player1HandTips = new ArrayList<>();
        player2HandTips = new ArrayList<>();
        gameState = new GameState(deck, players, currentPlayerIndex, fireworks, discardPile, fuses, tips, player1HandTips, player2HandTips);
        dealInitialCards();
        chatTextArea.setDisable(true);
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
        currentPlayer = players.get(currentPlayerIndex);
        fuses = gameState.getFuses();
        tips = gameState.getTips();
        //updateLabels();
        updatePlayerHandListView(1);
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
            numberIndices.get(card.getCardNumber()).add(i);
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

        if (player == players.get(0)) {
            player1HandTips.clear();
            player1HandTips.addAll(possibleTips);
        } else {
            player2HandTips.clear();
            player2HandTips.addAll(possibleTips);
        }

        tipsVBox.getChildren().clear();
        tipsVBox.setVisible(true);
        for (String tip : possibleTips) {
            CheckBox checkBox = new CheckBox(tip);
            checkBox.setOnAction(event -> {
                if (checkBox.isSelected()) {
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
        for (Map.Entry<Suit, Integer> entry : gameState.getFireworks().entrySet()) {
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

    public void saveDocumentation() throws IOException, ClassNotFoundException {
        DocumentationUtils.generateDocumentation();
    }

    private void updatePlayerHandLabels(int playerIndex, List<String> tipsList) {
        RoleName roleName = GameApplication.loggedInRoleName;

        for (Node node : tipsVBox.getChildren()) {
            if (node instanceof Label) {
                ((Label) node).setText("");
            }
        }

        if (roleName == RoleName.SERVER && playerIndex == 1) {
            for (int i = 0; i < tipsList.size(); i++) {
                String tip = tipsList.get(i);
                String labelId = "#p1CardInfoLabel" + (i + 1);
                Label label = (Label) tipsVBox.getScene().lookup(labelId);
                if (label != null) {
                    label.setText(tip);
                } else {
                    System.out.println("Label not found for index: " + i + " with ID: " + labelId);
                }
            }
        } else if (roleName == RoleName.CLIENT && playerIndex == 0) {
            for (int i = 0; i < tipsList.size(); i++) {
                String tip = tipsList.get(i);
                String labelId = "#p2CardInfoLabel" + (i + 1);
                Label label = (Label) tipsVBox.getScene().lookup(labelId);
                if (label != null) {
                    label.setText(tip);
                } else {
                    System.out.println("Label not found for index: " + i + " with ID: " + labelId);
                }
            }
        }
    }
}

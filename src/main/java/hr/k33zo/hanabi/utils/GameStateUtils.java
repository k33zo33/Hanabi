package hr.k33zo.hanabi.utils;

import hr.k33zo.hanabi.controller.GameController;
import hr.k33zo.hanabi.enums.Suit;
import hr.k33zo.hanabi.model.*;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.control.ListCell;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class GameStateUtils {


    public static GameState createGameState(Deck deck, List<Player> players, int currentPlayerIndex, Map<Suit, Integer> fireworks,
                                            List<Card> discardPile, int fuses, int tips, List<String> player1HandTips, List<String> player2HandTips){



        return new GameState(deck, players, currentPlayerIndex, fireworks, discardPile, fuses, tips, player1HandTips, player2HandTips);

    }

    public static void replayGame(GameController gameController){

        gameController.player1HandListView.getItems().clear();
        gameController.player2HandListView.getItems().clear();
        gameController.discardPileListView.getItems().clear();


        List<GameMove> gameMoveList = XmlUtils.readAllGameMoves();

        if(gameMoveList.isEmpty()) {
            return;
        }

        AtomicInteger counter = new AtomicInteger(0);

        if (counter.get() == 0){
            gameController.blueFireworkListView.getItems().clear();
            gameController.greenFireworkListView.getItems().clear();
            gameController.redFireworkListView.getItems().clear();
            gameController.yellowFireworkListView.getItems().clear();
            gameController.whiteFireworkListView.getItems().clear();
        }

        Timeline timeline = new Timeline(new KeyFrame(Duration.ZERO, e -> {
            GameMove gameMove = gameMoveList.get(counter.get());

            renderMoves(gameController, gameMove);

            counter.set(counter.get() + 1);
        }), new KeyFrame(Duration.seconds(1)));
        timeline.setCycleCount(gameMoveList.size());
        timeline.play();

    }

    private static void renderMoves(GameController gameController, GameMove gameMove) {

        gameController.player1HandListView.getItems().addAll((ArrayList)(gameMove.getCurrentPlayerIndex()== 0 ?
                buildHiddenCardListCellList(gameMove.getPlayers().getFirst().getHand())
                : buildCardListCellList(gameMove.getPlayers().getLast().getHand())));
        gameController.player2HandListView.getItems().addAll((ArrayList)(gameMove.getCurrentPlayerIndex()== 0 ?
                buildCardListCellList(gameMove.getPlayers().getFirst().getHand())
                : buildHiddenCardListCellList(gameMove.getPlayers().getLast().getHand())));

        gameController.discardPileListView.getItems().addAll(buildDiscardPile(gameMove.getDiscardPile()));

        if (gameMove.getFireworks().get(Suit.BLUE) != 0)
            gameController.blueFireworkListView.getItems().add(buildFireworksList(gameMove.getFireworks().get(Suit.BLUE), Suit.BLUE));
        if (gameMove.getFireworks().get(Suit.GREEN) != 0)
            gameController.greenFireworkListView.getItems().add(buildFireworksList(gameMove.getFireworks().get(Suit.GREEN), Suit.GREEN));
        if (gameMove.getFireworks().get(Suit.RED) != 0)
            gameController.redFireworkListView.getItems().add(buildFireworksList(gameMove.getFireworks().get(Suit.RED), Suit.RED));
        if (gameMove.getFireworks().get(Suit.YELLOW) != 0)
            gameController.yellowFireworkListView.getItems().add(buildFireworksList(gameMove.getFireworks().get(Suit.YELLOW), Suit.YELLOW));
        if (gameMove.getFireworks().get(Suit.WHITE) != 0)
            gameController.whiteFireworkListView.getItems().add(buildFireworksList(gameMove.getFireworks().get(Suit.WHITE), Suit.WHITE));

        gameController.remainingFusesLabel.setText(String.valueOf(gameMove.getFuses()));
        gameController.remainingTipsLabel.setText(String.valueOf(gameMove.getTips()));

    }

    private static Card buildFireworksList(Integer integer, Suit suit) {
        return new Card(suit, integer);
    }

    private static List<Card> buildDiscardPile(List<Card> discardPile) {
       return  discardPile.stream()
                .map(card -> {
                    Card listCell = new Card(card.getCardSuit(), card.getCardNumber());
                    return listCell;
                })
                .collect(Collectors.toList());
    }

    private static List<? extends ListCell<Card>> buildCardListCellList(List<Card> cards) {
        return cards.stream()
                .map(card -> {
                 CardListCell cardListCell =  new CardListCell();
                 cardListCell.updateItem(card, false);
                 return cardListCell;})
                .collect(Collectors.toList());
    }

    private static List<? extends ListCell<Card>> buildHiddenCardListCellList(List<Card> cards) {
        return cards.stream()
                .map(card -> {
                    HiddenCardListCell cardListCell =  new HiddenCardListCell();
                    cardListCell.updateItem(card, false);
                    return cardListCell;})
                .collect(Collectors.toList());
    }

}

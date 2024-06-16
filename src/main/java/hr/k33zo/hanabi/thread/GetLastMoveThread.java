package hr.k33zo.hanabi.thread;

import hr.k33zo.hanabi.model.GameMove;
import javafx.application.Platform;
import javafx.scene.control.Label;

public class GetLastMoveThread extends GameMoveThread implements Runnable{

    private Label lastGameMoveLabel;
    public GetLastMoveThread(Label lastGameMoveLabel) {
        this.lastGameMoveLabel = lastGameMoveLabel;
    }

    @Override
    public void run() {

        while (true){
            GameMove lastMove = getLastGameMove();

            Platform.runLater(() -> {
                if(lastMove != null)
                lastGameMoveLabel.setText("Last move: " + lastMove.toString());
                else
                    lastGameMoveLabel.setText("No moves found yet...");
            });

            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }



    }
}

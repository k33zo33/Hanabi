package hr.k33zo.hanabi.thread;

import hr.k33zo.hanabi.model.GameMove;

public class SaveGameMoveThread extends GameMoveThread implements Runnable{

    private GameMove gameMove;
    public SaveGameMoveThread(GameMove newGameMove) {
        this.gameMove = newGameMove;
    }
    @Override
    public void run() {
        saveNewGameMove(gameMove);
    }
}

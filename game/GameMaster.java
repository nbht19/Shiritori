package kobeU.cs.kadaiB.game;

import kobeU.cs.kadaiB.app.ShiritoriGUI;

public class GameMaster {

    private ShiritoriGUI client;

    public static int playerNum;
    int turnPlayerNumber;

    /**
     * コンストラクタ
     * @param client
     */
    public GameMaster(ShiritoriGUI client) {
        this.client = client;
        this.turnPlayerNumber = 0;
        client.board.setTextFieldEditable(client.myNumber == turnPlayerNumber);
    }

    /**
     * 次のプレイヤーにターンを交代するメソッド. テキストの編集権もここで操作する.
     */
    public void changeTurn() {
        System.out.println("ターンを交代します。");
        if(turnPlayerNumber != playerNum - 1) {
            turnPlayerNumber++;
        }else {
            turnPlayerNumber = 0;
        }
        client.board.setTextFieldEditable(client.myNumber == turnPlayerNumber);
        System.out.println("playerNum：" + playerNum);
        System.out.println("turnPlayerNumber：" + turnPlayerNumber);
    }

    public int getPlayerNum() { return playerNum; }

    public int getTurnPlayerNumber() {
        return turnPlayerNumber;
    }

    public void setPlayerNum(int playerNum) {
        this.playerNum = playerNum;
        System.out.println("setPlayerNumを " + playerNum + " にセットしました。");
    }
}

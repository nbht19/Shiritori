package kobeU.cs.kadaiB.app;

import kobeU.cs.kadaiB.comm.Communicator;
import kobeU.cs.kadaiB.game.GameMaster;
import kobeU.cs.kadaiB.game.ShiritoriMaster;
import kobeU.cs.kadaiB.util.ObjMessage2;

import javax.swing.*;
import java.io.IOException;

public class ShiritoriGUI {

	public int myNumber;
	public Board board;
	SetupDialog setupDialog;
	Communicator comm;

	GameMaster gameMaster;
	public static ShiritoriMaster shiritoriMaster = new ShiritoriMaster();

	char preLastChar = 'リ';

	/***************************************************
	 *  GUI, 標準出力表示
	 ***************************************************/
	/**
	 * msg の内容を、GUI および標準出力に表示
	 * @param sender  表示の際のtag
	 * @param msg
	 */
	void displayMsgOnBoard(String sender, ObjMessage2 msg) {
		displayMsgInner(sender + ": " + msg.body); // + "[" + msg.date + "]"
	}
	/**
	 * System からのメッセージを、GUI および標準出力に表示
	 */
	void displaySysMsg(String text) {
		displayMsgInner("Sys: " + text);
	}

	void displayMsgInner(final String text) {
		System.out.println(text);
		if(SwingUtilities.isEventDispatchThread()) {
			board.addMessage(text);
		} else {
			SwingUtilities.invokeLater(()->{
				board.addMessage(text);
			});
		}
	}


	/******************************************
	 *  GUI routines
	 ******************************************/
	/**
	 * GUI 処理を行う。
	 * User 入力は、GUI event としてシステムのGUI 用スレッドが処理。
	 */
	void GUIsetup() {
		assert(SwingUtilities.isEventDispatchThread());
		myNumber = getClientNumber();
		board = new Board(this);
		gameMaster = new GameMaster(this);
		board.setManager(this);
		board.addMessage("Waiting for Connection....");
		board.setVisible(true);
		setupDialog = new SetupDialog(board);
		setupDialog.setModal(true);
	}

	public static int getClientNumber() {
		String result = JOptionPane.showInputDialog("Input your Client Number:", Communicator.getClientNumber());
		return Integer.parseInt(result);
	}

	/**
	 *  接続先入力用の dialog を出し、接続を行う
	 */
	Communicator showDialogAndStartConnection() {
		setupDialog.setEchoText("Please input connection target.");
		setupDialog.setVisible(true);
		if(setupDialog.shouldTerminate()) return null;
		try {
			Communicator c;
			if(setupDialog.asServer()) {
				c = Communicator.startConnectionAsServer(setupDialog.getPort());
			} else {
				c = Communicator.startConnectionAsClient(setupDialog.getHostName(), setupDialog.getPort());
			}
			displaySysMsg("Connection Established");/* 接続成功 */
			return c;
		} catch (IOException e) {
			board.addMessage("Connection failed. Please stop this program."); /* 接続失敗 */
			return null;
		}
	}


	/******************************************
	 *  main routines
	 ******************************************/
	/**
	 * GUI 付きChat プログラムの起動を行う。引数は利用しない。
	 * （接続相手はGUIを介して行う）
	 * @param args
	 */
	public static void main(String[] args) {
		ShiritoriGUI manager = new ShiritoriGUI();
		shiritoriMaster.initialize();
		manager.exec();
	}

	/**
	 *  manager の全処理を順に行う。
	 */
	void exec() {
		try {
			SwingUtilities.invokeAndWait(()-> { /* 依頼を待つ */
				/* GUI Thread に実行依頼 */
				GUIsetup();
				comm = showDialogAndStartConnection();
			});
		} catch (Exception e) {
			System.err.println("Init Error");
			e.printStackTrace();
			return;
		}
		if(comm != null) {
			loopOfMsgRecv();
		}
	}

	/******************************************
	 *  Message 受信
	 ******************************************/
	/**
	 * ネットワーク入力を処理するループ
	 */
	void loopOfMsgRecv() {
		recvAndSetPlayerNum();
		try {
			/*
			  *  メッセージ受信用ルーチン
			 */
			while(true) {
				ObjMessage2 msg = (ObjMessage2)comm.recv();
				if(msg==null) {
					displaySysMsg("Connection closed.");
					return;
				}
				displayMsgOnBoard("MSG", msg);

				// 受信した言葉をしりとり用に加工して保存し,末尾文字を取得する.
				System.out.println(shiritoriMaster.getKana(msg.body));
				String recvKana = shiritoriMaster.process(shiritoriMaster.getKana(msg.body));

				if(recvKana.length() > 0) {
					preLastChar = recvKana.charAt(recvKana.length()-1);
				}
				else {
					board.addMessage("ゲームが終了しました。");
					board.setTextFieldEditable(false);
				}

				// 受け取ったメッセージを使用済みとして保存.
				shiritoriMaster.addUsed(msg.body);

				// メッセージを受け取るとターンを次に進める.
				gameMaster.changeTurn();
			}
		} catch (IOException e) {
			e.printStackTrace();
			board.addMessage("System: IO Exception! check stderr please!");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			board.addMessage("System: ClassNotFound Exception!");
		}
	}

	/**
	 * 参加プレイヤーが揃った際のサーバメッセージから,参加人数を取得する.
	 */
	void recvAndSetPlayerNum() {
		try {
			ObjMessage2 msg = (ObjMessage2)comm.recv();
			if(msg==null) {
				displaySysMsg("Connection closed.");
				return;
			}
			// 既知であるメッセージの文字数から逆算して参加人数を把握する.
			int num = msg.toString().charAt(17) - 48; // charにおける数字は2が50
			displayMsgOnBoard("MSG", msg);
			System.out.println("num = " + num);
			this.gameMaster.setPlayerNum(num);
		} catch (IOException e) {
			e.printStackTrace();
			board.addMessage("System: IO Exception! check stderr please!");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			board.addMessage("System: ClassNotFound Exception!");
		}
	}

	/*************************************************
	 *   Message 送信
	 *************************************************/
	/**
	 * 通信メッセージを送信。GUI 側から呼ばれる前提
	 */
	public void sendMsg(String text)  {
		assert(SwingUtilities.isEventDispatchThread());
		if(comm==null) {
			displaySysMsg("not connected.");
		} else if(text.equals("exit")) {
			comm.startClosing();
			displaySysMsg("Start closing of connections...");
		} else {
			try {
				ObjMessage2 msg =new ObjMessage2(text);
				comm.send(msg);
				displayMsgOnBoard("SELF", msg);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	/**
	 * GUI 側から、終了開始するためのメソッド
	 */
	public synchronized void startTermination() {
		assert(SwingUtilities.isEventDispatchThread());
		try {
			if(comm!=null) comm.startClosing();
		} catch(Exception e) {
			e.printStackTrace();
		}
		board.setVisible(false);
	}
}

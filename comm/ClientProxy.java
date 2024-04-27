package kobeU.cs.kadaiB.comm;

import java.io.IOException;
import java.net.Socket;

public class ClientProxy implements Runnable {
	protected CommunicationHub hub;
	private final String name;
	protected Communicator comm;

	/**
	 * 自身のクライアント番号
	 */
	int number;

	public ClientProxy(int number, Socket socket, CommunicationHub hub) throws IOException {
		this.number = number;
		this.name = "client" + number;
		this.hub= hub;
		this.comm = new Communicator(socket);
		hub.join(this);
	}

	public String getName() { return name; }

	// public int getNumber() { return number; }

	/*************************************************************
	 *  message 送信 (-> Client)
	 *************************************************************/

	public void sendMsg(Object msg) {
		try {
			System.out.println("ClientProxyのsendMsgメソッドです。"); // デバッグ用
			comm.send(msg);
		} catch (IOException e) {
			hub.remove(this);
		}
	}

	/*************************************************************
	 *  message 受信ループ (from Client)
	 *************************************************************/

	public void run() {
		try {
			/**
			 * 受信用ルーチン
			 */
			while(true) {
				Object msg =  comm.recv();
				if(msg==null) {
					hub.remove(this);
					return;
				}
				hub.process(msg, this);
			}
		} catch (ClassNotFoundException | IOException e) {
			e.printStackTrace();
			hub.remove(this);
		}
	}

	/**
	 * 終了作業依頼
	 */
	public synchronized void startTermination() {
		comm.startClosing();
	}
}

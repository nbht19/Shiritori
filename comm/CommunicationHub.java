package kobeU.cs.kadaiB.comm;

import kobeU.cs.kadaiB.util.ObjMessage2;

import javax.swing.*;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;

public class CommunicationHub {
	ArrayList<ClientProxy> clients = new ArrayList<>();
	private ServerSocket ss;
	private final CommunicationHubGUI board;

	private int clientNum;
	CommunicationHub() {
		board = new CommunicationHubGUI();
		board.setManager(this);
		board.setVisible(true);
	}
	
	/**************************************************************
	 *  基本機能群 
	 **************************************************************/
	
	synchronized void broadcastMsg(Object msg, ClientProxy sender) {
		assert(sender!=null);
		for(ClientProxy client: clients) {
			if(sender != client) 
				client.sendMsg(msg);
		}
	}

	synchronized void sendServerMsg(String msg, ClientProxy target) {
		ObjMessage2 serverMsg =new ObjMessage2(msg);
		if(target == null) {
			for(ClientProxy client: clients) {
				client.sendMsg(serverMsg);
			}
		}else {
			target.sendMsg(serverMsg);
		}
	}

	public synchronized void serverTermination() {
		board.addLog("server termination");
		for(ClientProxy client: clients) {
			client.startTermination();
		}
		clients.clear();
		closeServerSocket();
	}
	synchronized void join(ClientProxy proxy) {
		board.addLog("join: "+ proxy.getName());
		clients.add(proxy);
	}
	synchronized void remove(ClientProxy proxy) {
		board.addLog("remove: "+ proxy.getName());
		clients.remove(proxy);
	}
	synchronized void process(Object msg, ClientProxy sender) {
		board.addLog("process msg from " + sender.getName() + ":" +  msg);
		broadcastMsg(msg, sender);
	}

	public void waitClientAndStartGame(int port) throws IOException {
		if(port < 0) {
			board.addLog("Wrong input for Port Number: " + port);
			return;
		}
		ss = new ServerSocket(port);		
		try {	
			int counter = 0;
			// 設定された参加人数まで接続を受け付ける.
			while(counter < clientNum) {
				Socket socket = ss.accept(); // GUIとの接続
				ClientProxy client;
				client = new ClientProxy(counter, socket, this);
				new Thread(client).start();
				counter++;
			}

			String serverMsg = "参加プレイヤー(全" + clientNum + "名)が揃いました。しりとりゲームを始めましょう！しりと『り』";
			board.addLog(serverMsg);
			sendServerMsg(serverMsg, null);

		} catch (SocketException e) {
			synchronized (this) {
				if(ss!=null) { 
					e.printStackTrace();
				} else {
					/* This is the case where other thread closed the server socket, 
					 * and ignore the exception.  */
				}
			}

		} finally {
			closeServerSocket();
		}
	}
	
	/*********************************************************
	 *  connection 関係
	 *********************************************************/
	
	public synchronized void closeServerSocket() {
		if(ss==null) return;
		try { if(!ss.isClosed()) ss.close();} catch (IOException e) {/* ignore closing exception */}
		ss = null;
	}

	public static int getPortNum() {
		String result = JOptionPane.showInputDialog("Input port number:", Communicator.getDefaultPort());
		return Communicator.checkPortNum(result);
	}

	public static int getClientNum() {
		String result = JOptionPane.showInputDialog("Input number of Clients(2~9):", Communicator.getDefaultClientNum());
		System.out.println(Integer.parseInt(result));
		return Integer.parseInt(result);
	}

	public void setClientNum(int clientNum) {
		this.clientNum = clientNum;
	}

	public static void main(String[] args) throws IOException {
		CommunicationHub multiManager = new CommunicationHub();
		int port = getPortNum();
		int clientNum = getClientNum();
		multiManager.setClientNum(clientNum);
		multiManager.waitClientAndStartGame(port);
	}
}

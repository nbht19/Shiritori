package kobeU.cs.kadaiB.comm;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class Communicator {
	static void dbprintln(String str) {
		System.out.println(str);
	}
	public static final int DefaultPort = 50001;
	public static final String DefaultHostName = "localhost";

	/**
	 * デフォルトのプレイヤー数
	 */
	public static final int DefaultClientNum = 2;
	Socket socket;
	final ObjectOutputStream out;
	final ObjectInputStream in;

	private boolean outIsClosed=false,inIsClosed=false;

	static class TerminateMsg implements Serializable {
		private static final long serialVersionUID = -6662704893495880786L;
	}


	/*************************************************
	 *  Connection Setup routines
	 ************************************************/
	/**
	 * Constructor
	 * @param socket 通信用ソケット
	 * @throws IOException
	 */
	public Communicator(Socket socket) throws IOException {
		this.socket = socket;
		out = new ObjectOutputStream(socket.getOutputStream());
		in = new ObjectInputStream(socket.getInputStream());
	}
	/**
	 * Clientとして指定ホスト・ポートに接続を行い、socket生成、初期化を行う
	 * @param hostname
	 * @param portNum
	 * @throws IOException
	 */
	public static Communicator startConnectionAsClient(String hostname, int portNum) throws IOException {
		/* IP address の取得 */
		InetAddress host = InetAddress.getByName(hostname);
		dbprintln("Connecting to " + host + ":" + portNum);
		/* Socket の生成 */
		Socket socket =   new Socket(host, portNum);
		dbprintln("Connection established");	/* 接続完了 */
		return new Communicator(socket);
	}
	/**
	 * Servre として指定ポートで接続待ちを行い、クライアントアクセスを一度待ち、生成socketで初期化を行う
	 * （server portは、接続完了時に閉じる）
	 * @param portNum
	 * @throws IOException
	 */
	public static Communicator startConnectionAsServer(int portNum) throws IOException {
		ServerSocket serverSocket = new ServerSocket(portNum);
		try {
			dbprintln("Server waitting on serverSocket " + serverSocket);
			Socket socket = serverSocket.accept(); /* 接続待ち */
			dbprintln("Connection established");
			return new Communicator(socket);
		} finally {
			serverSocket.close();
		}
	}
	/**
	 * メッセージ送信を行う
	 * @param obj 送信データ。objの実体はSerializableであること。nullの場合はなにもしない。close()後も何もしない。
	 * @throws IOException 例外が起きた場合は、その旨通知するための例外を投げる。実は、close()処理は内部で済ませている。
	 */
	public void send(Object obj) throws IOException {
		if(obj==null) return;
		synchronized(out) {
			if(outIsClosed) return;
			try {
				out.writeObject(obj);
				out.flush();
				out.reset();/* 送信したものを書き換えて再利用しない限り、不要。*/
			} catch (IOException e) {
				outIsClosed=true;
				closeAll();
				throw e;
			}
		}
	}
	/**
	 * @return
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public Object recv() throws IOException, ClassNotFoundException {
		synchronized(in) {
			if(inIsClosed) return null;
			try {
				Object obj = in.readObject();
				if(obj instanceof TerminateMsg) {
					inIsClosed=true;
					if(!outIsClosed) {
						this.startClosing();
					}
					closeAll();
					return null;
				} else {
					return obj;
				}
			} catch (IOException | ClassNotFoundException e) {
				closeAll();
				throw e;
			}
		}
	}

	/**
	 *
	 */
	public void startClosing() {
		synchronized(out) {
			if(outIsClosed) return;
			try {
				outIsClosed=true;
				out.writeObject(new TerminateMsg());
				out.flush();
				//out.close();
			} catch(IOException e) {
				closeAll();
			}
		}
	}
	private void closeAll() {
		try { in.close(); } catch (IOException e) { e.printStackTrace();}
		try { out.close(); } catch (IOException e) {e.printStackTrace();}
		try { socket.close(); } catch (IOException e) {}
	}

	/***************************************************
	 *  Misc for Communications
	 ***************************************************/
	public static int getDefaultPort() {
		return DefaultPort;
	}
	public static String getDefaultHostName() {
		return DefaultHostName;
	}

	/**
	 * 設定したクライアント数を返す
	 * @return
	 */
	public static int getDefaultClientNum() { return DefaultClientNum; }

	/**
	 * 設定したクライアント番号を返す
	 * @return
	 */
	public static int getClientNumber() { return 0; }

	/**
	 * 与えられた文字列を利用ポート番号として正しいか判定
	 * @param text
	 * @return 正しいポートの値を返す。不適当な文字列に対しては、-1を返す
	 */
	public static int checkPortNum(String text) {
		try {
			int v = 	Integer.parseInt(text);
			if((v < 1025 ) ||( v >= (0x1 <<16))) return -1;
			return v;
		} catch (NumberFormatException e) {
			return -1;
		}
	}
}

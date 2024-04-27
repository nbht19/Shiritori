package kobeU.cs.kadaiB.app;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.IOException;

public class Board extends JFrame {
	private static final long serialVersionUID = 1L;
	private JPanel jContentPane = null;
	private JPanel upperPanel = null;
	private JTextField jTextField = null;
	private JScrollPane jScrollPane = null;
	private ShiritoriGUI manager = null;  //  @jve:decl-index=0:
	private JTextArea jTextArea = null;
	private JLabel jLabel = null;
	/**
	 * This is the default constructor
	 */
	public Board(ShiritoriGUI manager) {
		super();
		this.manager = manager;
		initialize();
	}

	/**
	 * This method initializes this
	 */
	private void initialize() {
		this.setSize(300, 300);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setPreferredSize(new Dimension(1000, 2000));
		this.setContentPane(getJContentPane());
		this.setTitle("ShiritoriBoard");
		this.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) {
				manager.startTermination();
			}
		});
	}

	/**
	 * This method initializes jContentPane
	 *
	 * @return javax.swing.JPanel
	 */
	private JPanel getJContentPane() {
		if (jContentPane == null) {
			jContentPane = new JPanel();
			jContentPane.setLayout(new BoxLayout(getJContentPane(), BoxLayout.Y_AXIS));
			jContentPane.add(getUpperPanel(), null);
			jContentPane.add(getJScrollPane(), null);
		}
		return jContentPane;
	}

	/**
	 * This method initializes upperPanel
	 *
	 * @return javax.swing.JPanel
	 */
	private JPanel getUpperPanel() {
		if (upperPanel == null) {
			jLabel = new JLabel();
			jLabel.setText("client" + manager.myNumber + "input: ");
			jLabel.setDisplayedMnemonic(KeyEvent.VK_UNDEFINED);
			upperPanel = new JPanel();
			upperPanel.setLayout(new BoxLayout(getUpperPanel(), BoxLayout.X_AXIS));
			upperPanel.setPreferredSize(new Dimension(300, 40));
			upperPanel.add(jLabel, null);
			upperPanel.add(getJTextField(), null);
		}
		return upperPanel;
	}

	/**
	 * This method initializes jTextField
	 *
	 * @return javax.swing.JTextField
	 */
	private JTextField getJTextField() {
		if (jTextField == null) {
			jTextField = new JTextField();
			jTextField.setEditable(true);
			jTextField.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {

					// テキスト入力にEnterを押した際の動き.入力文字が適切な単語となるまでチェックしやり直させる.

					String text = jTextField.getText();

					if(text.length() > 0) {
						try {
							String result = manager.shiritoriMaster.checkWord(manager.preLastChar, text);
							switch (result) {

								case "used":
									addMessage(manager.shiritoriMaster.used.toString());
									jTextField.setText("");
									break;
								case "noun":
									addMessage("存在する名詞ではありません。やり直し");
									jTextField.setText("");
									break;
								case "follow":
									addMessage("文字が続いていません。やり直し");
									jTextField.setText("");
									break;
								case "already":
									addMessage("使用済みの単語です。やり直し");
									jTextField.setText("");
									break;
								case "ン":
									manager.sendMsg(text);
									jTextField.setText("");
									manager.sendMsg("『ン』がついたので、Client" + manager.gameMaster.getTurnPlayerNumber() + "の負けです。");
									addMessage("ゲームが終了しました。");
									jTextField.setEditable(false);
									break;
								default:
									manager.sendMsg(result);
									jTextField.setText("");
									manager.shiritoriMaster.addUsed(result);

									// 送信するとターンを次に進め,テキストの編集権を切り替える.
									manager.gameMaster.changeTurn();
									jTextField.setEditable(false);
									break;
							}
						} catch (IOException ex) {
							throw new RuntimeException(ex);
						}
					}
				}
			});
		}
		return jTextField;
	}

	/**
	 * jtextFieldの編集権を操作するメソッド.
	 * @param x
	 */
	public void setTextFieldEditable(boolean x) {
		jTextField.setEditable(x);
	}

	/**
	 * This method initializes jScrollPane
	 *
	 * @return javax.swing.JScrollPane
	 */
	private JScrollPane getJScrollPane() {
		if (jScrollPane == null) {
			jScrollPane = new JScrollPane();
			jScrollPane.setPreferredSize(new Dimension(300, 300));
			jScrollPane.setViewportView(getJTextArea());
		}
		return jScrollPane;
	}

	/**
	 * This method initializes jTextArea
	 *
	 * @return javax.swing.JTextArea
	 */
	private JTextArea getJTextArea() {
		if (jTextArea == null) {
			jTextArea = new JTextArea();
			jTextArea.setLineWrap(true);
			jTextArea.setEditable(false);
		}
		return jTextArea;
	}

	/***************************************
	 *  public methods
	 */
	public void setManager(ShiritoriGUI chatManager) {
		this.manager = chatManager;
	}
	public void addMessage(String text) {
		jTextArea.insert(text+"\n", 0);
		jTextArea.setCaretPosition(0);
	}


	@Override
	public void setVisible(boolean flag) {
		if((manager==null)&& flag) {
			System.err.println("Please set manager before setting GUIChatBoard visible.");
		} else {
			super.setVisible(flag);
		}
	}
}  //  @jve:decl-index=0:visual-constraint="69,9"

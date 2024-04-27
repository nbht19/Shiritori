package kobeU.cs.kadaiB.comm;

import javax.swing.*;
import java.awt.*;

public class CommunicationHubGUI extends JFrame {
	private static final long serialVersionUID = 1L;
	private JPanel jContentPane = null;
	private JScrollPane jScrollPane = null;
	private kobeU.cs.kadaiB.comm.CommunicationHub manager = null;  //  @jve:decl-index=0:
	private JTextArea jTextArea = null;
	private JButton jButton = null;
	/**
	 * This is the default constructor
	 */
	public CommunicationHubGUI() {
		super();
		initialize();
	}

	/**
	 * This method initializes this
	 *
	 */
	private void initialize() {
		this.setSize(300, 300);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setPreferredSize(new Dimension(1000, 2000));
		this.setContentPane(getJContentPane());
		this.setTitle("CommunicationHub");
		this.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) {
				if(manager!=null) {
					manager.serverTermination();
					setVisible(false);
				}
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
			jContentPane.add(getJButton(), null);
			jContentPane.add(getJScrollPane(), null);
		}
		return jContentPane;
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
			jTextArea.setBackground(new Color(255, 228, 225));
			jTextArea.setLineWrap(true);
			jTextArea.setEditable(false);
		}
		return jTextArea;
	}

	/***************************************
	 *  public methods
	 */
	public void setManager(CommunicationHub manager) {
		this.manager = manager;
	}
	public void addLog(String text) {
		if(SwingUtilities.isEventDispatchThread()) {
			addLog0(text);
		} else {
			SwingUtilities.invokeLater(()->{
				addLog0(text);
			});
		}
	}
	private void addLog0(String text) {
		jTextArea.insert(text+"\n", 0);
		jTextArea.setCaretPosition(0);
	}

	/**
	 * This method initializes jButton
	 *
	 * @return javax.swing.JButton
	 */
	private JButton getJButton() {
		if (jButton == null) {
			jButton = new JButton();
			jButton.setFont(new Font("MS UI Gothic", Font.BOLD, 14));
			jButton.setText("Stop Server");
			jButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					if(manager!= null)
						manager.serverTermination();
				}
			});
		}
		return jButton;
	}
}  //  @jve:decl-index=0:visual-constraint="69,9"

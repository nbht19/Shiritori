package kobeU.cs.kadaiB.app;

import kobeU.cs.kadaiB.comm.Communicator;

import javax.swing.*;
import java.awt.*;

public class SetupDialog extends JDialog {

	private static final long serialVersionUID = 1L;
	private JPanel jContentPane = null;
	private JPanel jPanel = null;
	private JPanel jPanel1 = null;
	private JPanel jPanel2 = null;
	private JTextField hostnameTextField = null;
	private JTextField portTextField = null;
	private JLabel jLabel1 = null;
	private JLabel jLabel2 = null;
	private JCheckBox jCheckBox = null;
	private JButton jButton = null;
	private JPanel jPanel3 = null;
	private int port = -1;
	private String hostname;
	private boolean asServer;
	private boolean shouldTerminateF = false;
	private JTextField echoArea = null;

	/**
	 * @param frame
	 */
	public SetupDialog(Frame frame) {
		super(frame);
		initialize();
	}

	/**
	 * This method initializes this
	 *
	 */
	private void initialize() {
		this.setSize(300, 200);
		this.setContentPane(getJContentPane());
		this.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) {
				shouldTerminateF = true;
				setVisible(false);
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
			jContentPane.add(getJPanel(), null);
			jContentPane.add(getJPanel1(), null);
			jContentPane.add(getJPanel2(), null);
			jContentPane.add(getEchoArea(), null);
		}
		return jContentPane;
	}

	/**
	 * This method initializes jPanel
	 *
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanel() {
		if (jPanel == null) {
			jPanel = new JPanel();
			jPanel.setLayout(new BoxLayout(getJPanel(), BoxLayout.X_AXIS));
			jPanel.setPreferredSize(new Dimension(200, 40));
			jPanel.add(getJCheckBox(), null);
			jPanel.add(getJPanel3(), null);
			jPanel.add(getJButton(), null);
		}
		return jPanel;
	}

	/**
	 * This method initializes jPanel1
	 *
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanel1() {
		if (jPanel1 == null) {
			jLabel1 = new JLabel();
			jLabel1.setText("hostname");
			jLabel1.setPreferredSize(new Dimension(70, 20));
			jPanel1 = new JPanel();
			jPanel1.setLayout(new BoxLayout(getJPanel1(), BoxLayout.X_AXIS));
			jPanel1.add(jLabel1, null);
			jPanel1.add(getHostnameTextField(), null);
		}
		return jPanel1;
	}

	/**
	 * This method initializes jPanel2
	 *
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanel2() {
		if (jPanel2 == null) {
			jLabel2 = new JLabel();
			jLabel2.setText("port num");
			jLabel2.setVerticalTextPosition(SwingConstants.CENTER);
			jLabel2.setPreferredSize(new Dimension(70, 20));
			jPanel2 = new JPanel();
			jPanel2.setLayout(new BoxLayout(getJPanel2(), BoxLayout.X_AXIS));
			jPanel2.add(jLabel2, null);
			jPanel2.add(getPortTextField(), null);
		}
		return jPanel2;
	}

	/**
	 * This method initializes hostnameTextField
	 *
	 * @return javax.swing.JTextField
	 */
	private JTextField getHostnameTextField() {
		if (hostnameTextField == null) {
			hostnameTextField = new JTextField();
			hostnameTextField.setText(Communicator.getDefaultHostName());
			hostnameTextField.setFont(new Font("Dialog", Font.PLAIN, 18));
		}
		return hostnameTextField;
	}

	/**
	 * This method initializes portTextField
	 *
	 * @return javax.swing.JTextField
	 */
	private JTextField getPortTextField() {
		if (portTextField == null) {
			portTextField = new JTextField();
			portTextField.setFont(new Font("Dialog", Font.PLAIN, 18));
			portTextField.setText("" + Communicator.getDefaultPort());
		}
		return portTextField;
	}

	/**
	 * This method initializes jCheckBox
	 *
	 * @return javax.swing.JCheckBox
	 */
	private JCheckBox getJCheckBox() {
		if (jCheckBox == null) {
			jCheckBox = new JCheckBox();
			jCheckBox.setText("server mode");
			jCheckBox.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					boolean asServer = jCheckBox.isSelected();
					hostnameTextField.setEditable(!asServer);
					hostnameTextField.setForeground(asServer ? Color.lightGray : Color.black);
				}
			});
		}
		return jCheckBox;
	}

	/**
	 * This method initializes jButton
	 *
	 * @return javax.swing.JButton
	 */
	private JButton getJButton() {
		if (jButton == null) {
			jButton = new JButton();
			jButton.setPreferredSize(new Dimension(200, 70));
			jButton.setText("start!");
			jButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					checkinputs();
				}
			});
		}
		return jButton;
	}

	/**
	 * This method initializes jPanel3
	 *
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanel3() {
		if (jPanel3 == null) {
			jPanel3 = new JPanel();
			jPanel3.setLayout(null);
			jPanel3.setPreferredSize(new Dimension(30, 30));
		}
		return jPanel3;
	}

	private void checkinputs() {
		asServer = jCheckBox.isSelected();
		hostname = hostnameTextField.getText().trim();
		if (hostname.equals("")) {
			hostnameTextField.setText("Empty HostName for Client!!!");
			hostname = null;
		}
		port = Communicator.checkPortNum(portTextField.getText());
		if (port < 0) {
			portTextField.setText("Wrong port Numer:" + portTextField.getText());
		}
		if ((hostname != null) && (port >= 0))
			setVisible(false);
	}

	/**
	 * This method initializes echoArea
	 *
	 * @return javax.swing.JTextField
	 */
	private JTextField getEchoArea() {
		if (echoArea == null) {
			echoArea = new JTextField();
			echoArea.setPreferredSize(new Dimension(200, 20));
			echoArea.setEditable(false);
		}
		return echoArea;
	}

	/*
	 *  public methods
	 */
	public boolean asServer() {
		return asServer;
	}

	public String getHostName() {
		return hostname;
	}

	public int getPort() {
		return port;
	}

	public void setEchoText(String str) {
		echoArea.setText(str);
	}

	public boolean shouldTerminate() {
		return shouldTerminateF;
	}

}

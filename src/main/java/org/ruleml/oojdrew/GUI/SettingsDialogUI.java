package org.ruleml.oojdrew.GUI;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JLabel;
import javax.swing.JCheckBox;
import javax.swing.LayoutStyle.ComponentPlacement;
import java.awt.CardLayout;
import javax.swing.SwingConstants;
import java.awt.Component;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class SettingsDialogUI extends JDialog {

	private final JPanel contentPanel = new JPanel();
	private JCheckBox chckbxRuleMLCompatibilityMode;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			SettingsDialogUI dialog = new SettingsDialogUI(null);
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the dialog.
	 */
	public SettingsDialogUI(final SettingsDialog settingsDialog) {
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowOpened(WindowEvent arg0) {
				settingsDialog.syncUI();
			}
		});
		setBounds(100, 100, 313, 137);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		
		chckbxRuleMLCompatibilityMode = new JCheckBox("Use compatibility mode for RuleML");
		chckbxRuleMLCompatibilityMode.setToolTipText("If checked, the RuleML parser assumes that the \"Implies\" clause in a stripe-skipped RuleML document contains the premise before the conclusion.");
		
		JButton btnCancel = new JButton("Cancel");
		btnCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				setVisible(false);
			}
		});
		
		JButton btnOk = new JButton("Ok");
		btnOk.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				settingsDialog.applySettings();
				setVisible(false);
			}
		});
		GroupLayout gl_contentPanel = new GroupLayout(contentPanel);
		gl_contentPanel.setHorizontalGroup(
			gl_contentPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPanel.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_contentPanel.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_contentPanel.createSequentialGroup()
							.addComponent(chckbxRuleMLCompatibilityMode, GroupLayout.DEFAULT_SIZE, 438, Short.MAX_VALUE)
							.addGap(6))
						.addGroup(Alignment.TRAILING, gl_contentPanel.createSequentialGroup()
							.addComponent(btnOk)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(btnCancel))))
		);
		gl_contentPanel.setVerticalGroup(
			gl_contentPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPanel.createSequentialGroup()
					.addContainerGap()
					.addComponent(chckbxRuleMLCompatibilityMode)
					.addPreferredGap(ComponentPlacement.RELATED, 199, Short.MAX_VALUE)
					.addGroup(gl_contentPanel.createParallelGroup(Alignment.BASELINE)
						.addComponent(btnCancel)
						.addComponent(btnOk)))
		);
		gl_contentPanel.linkSize(SwingConstants.HORIZONTAL, new Component[] {btnCancel, btnOk});
		contentPanel.setLayout(gl_contentPanel);
	}
	public boolean getChckbxRuleMLCompatibilityModeSelected() {
		return chckbxRuleMLCompatibilityMode.isSelected();
	}
	public void setChckbxRuleMLCompatibilityModeSelected(boolean selected) {
		chckbxRuleMLCompatibilityMode.setSelected(selected);
	}
}

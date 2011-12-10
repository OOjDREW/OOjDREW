// OO jDREW - An Object Oriented extension of the Java Deductive Reasoning Engine for the Web
// Copyright (C) 2005 Marcel Ball
//
// This library is free software; you can redistribute it and/or
// modify it under the terms of the GNU Lesser General Public
// License as published by the Free Software Foundation; either
// version 2.1 of the License, or (at your option) any later version.
//
// This library is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
// Lesser General Public License for more details.
//
// You should have received a copy of the GNU Lesser General Public
// License along with this library; if not, write to the Free Software
// Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA

package org.ruleml.oojdrew.GUI;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.DefaultComboBoxModel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.JSpinner;
import javax.swing.KeyStroke;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.border.EmptyBorder;

import org.ruleml.oojdrew.parsing.RuleMLParser.RuleMLFormat;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;

public class PreferenceDialogUI extends JDialog {

	private final JPanel contentPanel = new JPanel();
	private JSpinner spinnerTextFontSize;
	private UISettingsController settingsController;
	private JSpinner spinnerUIFontSize;
	private JCheckBox chkBxLinkFontSizes;
	private JComboBox cbBoxLookAndFeel;
	private JComboBox cbBoxRuleMLVersion;
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			PreferenceDialogUI dialog = new PreferenceDialogUI();
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	protected JRootPane createRootPane()
	{
		ActionListener actionListener = new ActionListener()
		{
			public void actionPerformed(ActionEvent actionEvent)
			{
				setVisible(false);
			}
		};

		KeyStroke stroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
		JRootPane rootPane = new JRootPane();
		rootPane.registerKeyboardAction(actionListener, stroke,
				JComponent.WHEN_IN_FOCUSED_WINDOW);
		return rootPane;
	}

	/**
	 * Create the dialog.
	 */
	public PreferenceDialogUI() {
		setTitle("Preferences");
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowOpened(WindowEvent arg0) {
				settingsController.syncUIWithSettings();
			}
		});
		setBounds(100, 100, 300, 235);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		
		JButton btnCancel = new JButton("Cancel");
		btnCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				setVisible(false);
			}
		});
		
		JButton btnOk = new JButton("Ok");
		btnOk.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				settingsController.applySettingsFromUI();
				setVisible(false);
			}
		});
		
		JLabel lblTextFont = new JLabel("Text panel font size");
		spinnerTextFontSize = new JSpinner();
		spinnerTextFontSize.setModel(new SpinnerNumberModel(12, 8, 72, 1));
		
		JLabel lblMainUiFont = new JLabel("Main UI font size");
		spinnerUIFontSize = new JSpinner();
		spinnerUIFontSize.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				if (chkBxLinkFontSizes.isSelected())
				{
					spinnerTextFontSize.setValue(spinnerUIFontSize.getValue());
				}
			}
		});
		spinnerUIFontSize.setModel(new SpinnerNumberModel(12, 8, 72, 1));

		chkBxLinkFontSizes = new JCheckBox("Link font sizes");
		chkBxLinkFontSizes.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				if (chkBxLinkFontSizes.isSelected()) {
					spinnerTextFontSize.setValue(spinnerUIFontSize.getValue());
					spinnerTextFontSize.setEnabled(false);
				}
				else {
					spinnerTextFontSize.setEnabled(true);					
				}
			}
		});
		chkBxLinkFontSizes.setHorizontalAlignment(SwingConstants.RIGHT);
		chkBxLinkFontSizes.setHorizontalTextPosition(SwingConstants.LEFT);
		
		JLabel lblLookAndFeel = new JLabel("Look and feel");
		cbBoxLookAndFeel = new JComboBox();
		
		JLabel lblRuleMLVersion = new JLabel("RuleML version");
		cbBoxRuleMLVersion = new JComboBox();
		
		GroupLayout gl_contentPanel = new GroupLayout(contentPanel);
		gl_contentPanel.setHorizontalGroup(
			gl_contentPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(Alignment.TRAILING, gl_contentPanel.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_contentPanel.createParallelGroup(Alignment.TRAILING)
						.addComponent(chkBxLinkFontSizes, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 247, Short.MAX_VALUE)
						.addGroup(gl_contentPanel.createSequentialGroup()
							.addGroup(gl_contentPanel.createParallelGroup(Alignment.LEADING)
								.addComponent(lblTextFont)
								.addComponent(lblMainUiFont)
								.addComponent(lblLookAndFeel)
								.addComponent(lblRuleMLVersion))
							.addGap(12)
							.addGroup(gl_contentPanel.createParallelGroup(Alignment.TRAILING)
								.addComponent(spinnerUIFontSize, GroupLayout.DEFAULT_SIZE, 140, Short.MAX_VALUE)
								.addComponent(spinnerTextFontSize, GroupLayout.DEFAULT_SIZE, 140, Short.MAX_VALUE)
								.addComponent(cbBoxLookAndFeel, 0, 140, Short.MAX_VALUE)
								.addComponent(cbBoxRuleMLVersion, 0, 140, Short.MAX_VALUE)
								.addGroup(gl_contentPanel.createSequentialGroup()
									.addComponent(btnOk)
									.addPreferredGap(ComponentPlacement.UNRELATED)
									.addComponent(btnCancel)))))
					.addGap(77))
		);
		gl_contentPanel.setVerticalGroup(
			gl_contentPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPanel.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_contentPanel.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblMainUiFont)
						.addComponent(spinnerUIFontSize, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_contentPanel.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblTextFont)
						.addComponent(spinnerTextFontSize, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(chkBxLinkFontSizes)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_contentPanel.createParallelGroup(Alignment.BASELINE)
						.addComponent(cbBoxLookAndFeel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblLookAndFeel))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_contentPanel.createParallelGroup(Alignment.BASELINE)
						.addComponent(cbBoxRuleMLVersion, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblRuleMLVersion))
					.addPreferredGap(ComponentPlacement.RELATED, 23, Short.MAX_VALUE)
					.addGroup(gl_contentPanel.createParallelGroup(Alignment.BASELINE)
						.addComponent(btnOk)
						.addComponent(btnCancel))
					.addContainerGap())
		);
		gl_contentPanel.linkSize(SwingConstants.HORIZONTAL, new Component[] {btnCancel, btnOk});
		contentPanel.setLayout(gl_contentPanel);
		
		populateComboBoxes();
	}
	
	private void populateComboBoxes() {
		for (LookAndFeelInfo lafInfo : UIManager.getInstalledLookAndFeels()) {
			cbBoxLookAndFeel.addItem(lafInfo.getName());
		}
		cbBoxRuleMLVersion.setModel(new DefaultComboBoxModel(RuleMLFormat.getVersionNames()));
	}
		
	private SpinnerModel getSpinnerTextAreaFontSizeModel() {
		return spinnerTextFontSize.getModel();
	}
	
	public int getSpinnerTextAreaFontSizeValue() {
		return (Integer) getSpinnerTextAreaFontSizeModel().getValue();
	}
	
	public void setSpinnerTextAreaFontSizeValue(int newSize) {
		getSpinnerTextAreaFontSizeModel().setValue(newSize);
	}
	
	public void setSettingsController(UISettingsController newController) {
		settingsController = newController;
	}
	
	private SpinnerModel getSpinnerUIFontSizeModel() {
		return spinnerUIFontSize.getModel();
	}
	
	public int getSpinnerUIFontSizeValue() {
		return (Integer) getSpinnerUIFontSizeModel().getValue();
	}
	
	public void setSpinnerUIFontSizeValue(int newSize) {
		getSpinnerUIFontSizeModel().setValue(newSize);
	}
	
	public boolean getLinkFontSizes() {
		return chkBxLinkFontSizes.isSelected();
	}
		
	public void setLinkFontSizes(boolean linkFontSizes) {
		chkBxLinkFontSizes.setSelected(linkFontSizes);
	}
	
	public String getSelectedLookAndFeel() {
		String lafName = cbBoxLookAndFeel.getSelectedItem().toString();
		for (LookAndFeelInfo lafInfo : UIManager.getInstalledLookAndFeels()) {
			if (lafInfo.getName().equals(lafName)) {
				return lafInfo.getClassName();
			}
		}
		return UIManager.getSystemLookAndFeelClassName();
	}
	
	public void setLookAndFeel(String lafClassName) {
		for (LookAndFeelInfo lafInfo : UIManager.getInstalledLookAndFeels()) {
			if (lafInfo.getClassName().equals(lafClassName)) {
				cbBoxLookAndFeel.setSelectedItem(lafInfo.getName());
				break;
			}
		}
	}
	
	public RuleMLFormat getSelectedRuleMLFormat() {
		String selectedRuleMLFormat = cbBoxRuleMLVersion.getSelectedItem().toString();
		return RuleMLFormat.fromString(selectedRuleMLFormat);
	}
	
	public void setSelectedRuleMLFormat(RuleMLFormat rmlFormat)	{
		String ruleMLVersionName = rmlFormat.getVersionName();
		cbBoxRuleMLVersion.setSelectedItem(ruleMLVersionName);
	}
	
	public void updateUI() {
		SwingUtilities.updateComponentTreeUI(this);
		this.pack();
	}
}

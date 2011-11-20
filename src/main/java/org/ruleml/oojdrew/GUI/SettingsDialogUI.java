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
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SpinnerModel;

public class SettingsDialogUI extends JDialog {

	private final JPanel contentPanel = new JPanel();
	private JCheckBox chckbxValidateRuleML;
	private JSpinner spinnerFontSize;

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
		setBounds(100, 100, 273, 147);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		
		chckbxValidateRuleML = new JCheckBox("Validate RuleML documents");
		chckbxValidateRuleML.setToolTipText("If checked, the RuleML parser will validate every document against it's XSD (if the document specifies one)");
		
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
		
		JLabel lblNewLabel = new JLabel("Text panel font size");
		
		spinnerFontSize = new JSpinner();
		spinnerFontSize.setModel(new SpinnerNumberModel(12, 8, 72, 1));
		GroupLayout gl_contentPanel = new GroupLayout(contentPanel);
		gl_contentPanel.setHorizontalGroup(
			gl_contentPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPanel.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_contentPanel.createParallelGroup(Alignment.LEADING)
						.addGroup(Alignment.TRAILING, gl_contentPanel.createSequentialGroup()
							.addComponent(chckbxValidateRuleML, GroupLayout.DEFAULT_SIZE, 277, Short.MAX_VALUE)
							.addGap(6))
						.addGroup(Alignment.TRAILING, gl_contentPanel.createSequentialGroup()
							.addComponent(btnOk)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(btnCancel))
						.addGroup(gl_contentPanel.createSequentialGroup()
							.addComponent(lblNewLabel)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(spinnerFontSize, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
							.addContainerGap(305, Short.MAX_VALUE))))
		);
		gl_contentPanel.setVerticalGroup(
			gl_contentPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPanel.createSequentialGroup()
					.addContainerGap()
					.addComponent(chckbxValidateRuleML)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_contentPanel.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblNewLabel)
						.addComponent(spinnerFontSize, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.RELATED, 163, Short.MAX_VALUE)
					.addGroup(gl_contentPanel.createParallelGroup(Alignment.BASELINE)
						.addComponent(btnCancel)
						.addComponent(btnOk)))
		);
		gl_contentPanel.linkSize(SwingConstants.HORIZONTAL, new Component[] {btnCancel, btnOk});
		contentPanel.setLayout(gl_contentPanel);
	}
	
	public boolean getChckbxValidateRuleMLSelected() {
		return chckbxValidateRuleML.isSelected();
	}
	
	public void setChckbxValidateRuleMLSelected(boolean selected) {
		chckbxValidateRuleML.setSelected(selected);
	}
	
	public int getSpinnerFontSizeValue()
	{
		return (Integer) getSpinnerFontSizeModel().getValue();
	}
	
	public void setSpinnerFontSizeValue(int newSize)
	{
		getSpinnerFontSizeModel().setValue(newSize);
	}
	
	private SpinnerModel getSpinnerFontSizeModel() {
		return spinnerFontSize.getModel();
	}
}

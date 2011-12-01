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
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

public class FontSizeDialogUI extends JDialog {

	private final JPanel contentPanel = new JPanel();
	private JSpinner spinnerTextFontSize;
	private UISettingsController settingsController;
	private JSpinner spinnerUIFontSize;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			FontSizeDialogUI dialog = new FontSizeDialogUI();
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the dialog.
	 */
	public FontSizeDialogUI() {
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowOpened(WindowEvent arg0) {
				settingsController.syncUIWithSettings();
			}
		});
		setBounds(100, 100, 273, 147);
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
		spinnerUIFontSize.setModel(new SpinnerNumberModel(12, 8, 72, 1));
		GroupLayout gl_contentPanel = new GroupLayout(contentPanel);
		gl_contentPanel.setHorizontalGroup(
			gl_contentPanel.createParallelGroup(Alignment.TRAILING)
				.addGroup(gl_contentPanel.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_contentPanel.createParallelGroup(Alignment.LEADING)
						.addComponent(lblTextFont)
						.addComponent(lblMainUiFont))
					.addGap(12)
					.addGroup(gl_contentPanel.createParallelGroup(Alignment.LEADING, false)
						.addComponent(spinnerUIFontSize)
						.addComponent(spinnerTextFontSize))
					.addContainerGap(63, Short.MAX_VALUE))
				.addGroup(gl_contentPanel.createSequentialGroup()
					.addContainerGap()
					.addComponent(btnOk)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(btnCancel)
					.addContainerGap())
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
					.addGap(18, 18, Short.MAX_VALUE)
					.addGroup(gl_contentPanel.createParallelGroup(Alignment.BASELINE)
						.addComponent(btnOk)
						.addComponent(btnCancel))
					.addContainerGap())
		);
		gl_contentPanel.linkSize(SwingConstants.HORIZONTAL, new Component[] {btnCancel, btnOk});
		contentPanel.setLayout(gl_contentPanel);
	}
	
	public int getSpinnerTextAreaFontSizeValue()
	{
		return (Integer) getSpinnerTextAreaFontSizeModel().getValue();
	}
	
	public void setSpinnerTextAreaFontSizeValue(int newSize)
	{
		getSpinnerTextAreaFontSizeModel().setValue(newSize);
	}
	
	private SpinnerModel getSpinnerTextAreaFontSizeModel() {
		return spinnerTextFontSize.getModel();
	}
	
	public void setSettingsController(UISettingsController newController)
	{
		settingsController = newController;
	}
	
	private SpinnerModel getSpinnerUIFontSizeModel() {
		return spinnerUIFontSize.getModel();
	}
	
	public int getSpinnerUIFontSizeValue()
	{
		return (Integer) getSpinnerUIFontSizeModel().getValue();
	}
	
	public void setSpinnerUIFontSizeValue(int newSize)
	{
		getSpinnerUIFontSizeModel().setValue(newSize);
	}
	
	public void updateUI() {
		SwingUtilities.updateComponentTreeUI(this);
		this.pack();
	}
}

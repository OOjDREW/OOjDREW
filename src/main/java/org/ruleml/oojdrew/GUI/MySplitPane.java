package org.ruleml.oojdrew.GUI;

import java.awt.Graphics;
import javax.swing.JSplitPane;

public class MySplitPane extends JSplitPane
{
	public MySplitPane()
	{
		super();
		painted = false;
	}
	
	@Override
	public void paint(Graphics g) {
		if(!painted)
		{
			painted = true;
			this.setDividerLocation(0.5);
		}
		
		super.paint(g);
	}
	
	private boolean painted;
}
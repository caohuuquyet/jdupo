/**
 * jDUPO 1.2
 */
package com.jdupo.editor.action.impl;

import org.eclipse.swt.SWT;

import com.jdupo.editor.action.EditorAction;
import com.jdupo.editor.frame.ScreenManager;
import com.jdupo.sys.EditorConst;

public class VisualizeReturnedData extends EditorAction {
	public static final String LABEL = "&Visualize Returned Data";
	private static final String TOOL_TIP = "Visualize Returned Data";
	private static final int ACCELERATOR = -1;
	private static final String ICON = "";

	public VisualizeReturnedData() {
		super(LABEL, TOOL_TIP, ACCELERATOR, ICON);
		setEnabled(false);
	}

	@Override
	public void run() {
		if (!EditorConst.isDeploy) System.out.println(LABEL);

		//ScreenManager.getApp().closeCurrentTheory();
	}

}

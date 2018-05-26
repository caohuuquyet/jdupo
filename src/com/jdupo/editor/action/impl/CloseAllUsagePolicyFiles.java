/**
 * jDUPO 1.0
 */
package com.jdupo.editor.action.impl;

import org.eclipse.swt.SWT;

import com.jdupo.editor.action.EditorAction;
import com.jdupo.editor.frame.ScreenManager;
import com.jdupo.sys.EditorConst;

public class CloseAllUsagePolicyFiles extends EditorAction {
	public static final String LABEL = "Close &All Usage Policy Files";
	private static final String TOOL_TIP = "Close All Usage Policy Files";
	private static final int ACCELERATOR = SWT.CTRL | 'A';
	private static final String ICON = "";

	public CloseAllUsagePolicyFiles() {
		super(LABEL, TOOL_TIP, ACCELERATOR, ICON);
		setEnabled(false);
	}

	@Override
	public void run() {
		if (!EditorConst.isDeploy) System.out.println(LABEL);

		ScreenManager.getApp().closeAllTheories();
	}

}

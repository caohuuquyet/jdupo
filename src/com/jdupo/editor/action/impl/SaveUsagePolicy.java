/**
 * jDUPO 1.0
 *   
 */
package com.jdupo.editor.action.impl;

import com.jdupo.editor.action.EditorAction;
import com.jdupo.editor.frame.ScreenManager;
import com.jdupo.sys.EditorConst;

public class SaveUsagePolicy extends EditorAction {
	public static final String LABEL = "Save Usage Policy";
	private static final String TOOL_TIP = "Save Usage Policy";
	private static final int ACCELERATOR = -1;
	private static final String ICON = "/com/jdupo/resources/save.gif";

	public SaveUsagePolicy() {
		super(LABEL, TOOL_TIP, ACCELERATOR, ICON);
		setEnabled(false);
	}

	@Override
	public void run() {
		if (!EditorConst.isDeploy) System.out.println(LABEL);

		ScreenManager.getApp().saveTheory();
	}

}

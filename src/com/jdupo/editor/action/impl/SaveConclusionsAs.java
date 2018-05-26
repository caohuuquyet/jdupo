/**
 * jDUPO 1.0
 *   
 */
package com.jdupo.editor.action.impl;

import com.jdupo.editor.action.EditorAction;
import com.jdupo.editor.frame.ScreenManager;
import com.jdupo.sys.EditorConst;

public class SaveConclusionsAs extends EditorAction {
	public static final String LABEL = "Save conclusions as...";
	private static final String TOOL_TIP = "Save conclusions to another file";
	private static final int ACCELERATOR = -1;
	private static final String ICON = "";

	public SaveConclusionsAs() {
		super(LABEL, TOOL_TIP, ACCELERATOR, ICON);
		setEnabled(false);
	}
	@Override
	public void run(){
		if (!EditorConst.isDeploy) System.out.println(LABEL);

		ScreenManager.getApp().saveConclusionsAs();
	}
}

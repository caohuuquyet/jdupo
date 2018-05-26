/**
 * jDUPO
 */
package com.jdupo.editor.action.impl;

import com.jdupo.editor.action.EditorAction;
import com.jdupo.editor.frame.ScreenManager;
import com.jdupo.sys.EditorConst;

import spindle.core.dom.RuleType;

public class CreateRequest extends EditorAction {
	public static final String LABEL = "Consumer's Request";
	private static final String TOOL_TIP = "Consumer's Request";
	private static final int ACCELERATOR = -1;
	private static final String ICON = "";

	public CreateRequest() {
		super(LABEL, TOOL_TIP, ACCELERATOR, ICON);
		setEnabled(false);
	}

	@Override
	public void run() {
		if (!EditorConst.isDeploy) System.out.println(LABEL);

		ScreenManager.getApp().addRequest();
	}

}

/**
 * jDUPO 1.0
 *   
 */
package com.jdupo.editor.action.impl;

import org.eclipse.swt.SWT;

import com.jdupo.editor.action.EditorAction;
import com.jdupo.editor.frame.ScreenManager;
import com.jdupo.sys.EditorConst;

public class LoadUsagePolicy extends EditorAction {
	public static final String LABEL = "&Load Usage Policy";
	private static final String TOOL_TIP = "Load Usage Policy From File";
	private static final int ACCELERATOR = SWT.CTRL | 'L';
	private static final String ICON = "/com/jdupo/resources/folder-open.gif";

	public LoadUsagePolicy() {
		super(LABEL, TOOL_TIP, ACCELERATOR, ICON);
	}

	@Override
	public void run() {
		if (!EditorConst.isDeploy) System.out.println(LABEL);

		ScreenManager.getApp().loadTheory();
	}

}

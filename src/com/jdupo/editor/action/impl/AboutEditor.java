/**
 * jDUPO 1.0
 *   
 */
package com.jdupo.editor.action.impl;

import org.eclipse.swt.widgets.Shell;

import com.jdupo.editor.action.EditorAction;
import com.jdupo.editor.dialog.AboutDUPOEditor;
import com.jdupo.editor.frame.EditorFrame;
import com.jdupo.sys.EditorConf;
import com.jdupo.sys.EditorConst;

public class AboutEditor extends EditorAction {
	public static final String LABEL = "&About " + EditorConst.TITLE;
	private static final String TOOL_TIP = "About " + EditorConst.TITLE;
	private static final int ACCELERATOR = -1;
	private static final String ICON = "";

	public AboutEditor() {
		super(LABEL, TOOL_TIP, ACCELERATOR, ICON);
	}

	@Override
	public void run() {
		if (!EditorConst.isDeploy) System.out.println(LABEL);
		
		Shell parent = EditorFrame.getApp().getShell();
		AboutDUPOEditor dlg = null;
		String title = "About " + EditorConf.getAppTitle() + " (version " + EditorConf.getVersion() + ")";
		String text = "Error";
		try {
			text = EditorConf.getAboutApp();
		} catch (Exception e) {
			e.printStackTrace();
		}
		dlg = new AboutDUPOEditor(parent, title, new String[] { text });
		dlg.open();
	}
}

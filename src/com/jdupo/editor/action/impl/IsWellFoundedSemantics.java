/**
 * SPINdle Defeasible Theory Editor (version 2.2.2)
 * Copyright (C) 2009-2013 NICTA Ltd.
 *
 * This file is part of SPINdle Defeasible Theory Editor project.
 * 
 * SPINdle is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * SPINdle is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with SPINdle.  If not, see <http://www.gnu.org/licenses/>.
 *
 * @author H.-P. Lam (oleklam@gmail.com), National ICT Australia - Queensland Research Laboratory 
 */
package com.jdupo.editor.action.impl;

import org.eclipse.jface.action.IAction;

import com.jdupo.editor.action.EditorAction;
import com.jdupo.editor.frame.ScreenManager;
import com.jdupo.sys.EditorConst;

public class IsWellFoundedSemantics extends EditorAction {
	public static final String LABEL = "Well-founded semantics";
	private static final String TOOL_TIP = "Well-founded semantics";
	private static final int ACCELERATOR = -1;
	private static final String ICON = "";

	public IsWellFoundedSemantics() {
		super(LABEL, TOOL_TIP, ACCELERATOR, ICON, IAction.AS_CHECK_BOX);
		setChecked(false);
	}

	@Override
	public void run() {
		if (!EditorConst.isDeploy) System.out.println(LABEL);

		setChecked(isChecked());
		ScreenManager.getApp().setWellFoundedSemantics(isChecked());
	}

}

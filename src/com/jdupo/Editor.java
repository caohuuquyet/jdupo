/**
 * jDUPO 1.1
 */
package com.jdupo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import com.app.exception.InvalidArgumentException;
import com.app.utils.Utilities;
import com.jdupo.editor.frame.EditorFrame;
import com.jdupo.exception.EditorException;

import spindle.core.ReasonerUtilities;
import spindle.sys.AppConst; 


public class Editor {
	private static Map<String, String> _args = null;
	private static List<String> _nonArgs=null;

	private static void extractArgs(String[] args) throws EditorException, InvalidArgumentException {
		_args = new TreeMap<String, String>();
		_nonArgs=new ArrayList<String>();
		
		Utilities.extractArguments(args, AppConst.ARGUMENT_PREFIX, _args, _nonArgs);		
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// notice: work only on 32 bits
		try {
			extractArgs(args);

			// print application message
			boolean isPrintAppMessage = false;
			try {
				isPrintAppMessage = ReasonerUtilities.printAppMessage(_args);
			} catch (Exception e) {
				e.printStackTrace();
				System.exit(1);
			}

			if (!isPrintAppMessage) {
				Display display = Display.getCurrent();
			      //may be null if outside the UI thread
			      if (display == null)
			         display = Display.getDefault();

				if (null == display) display = Display.getDefault();
				
				Shell shell = new Shell(display);

				EditorFrame editor = new EditorFrame(shell, _args);
				editor.run();
			}
		} catch (Exception e) {
			
			e.printStackTrace();
			
		} finally {
			//System.exit(0);
			
		}
	}

}

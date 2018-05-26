/**
 * jDUPO 1.0
 *   
 */
package com.jdupo.editor.panel;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;

import com.jdupo.sys.EditorConst;

public class EnforcementMessagePanel extends GenericMessagePanel {
	private static final String CAPTION = "Transparency Message";
	protected Text text = null;

	public EnforcementMessagePanel(CTabFolder parent) {
		super(parent, CAPTION);
		Control contents = createContents(parent);
		setControl(contents);
	}

	@Override
	protected Control createContents(Composite parent) {
		Composite contentWrapper = new Composite(parent, SWT.NONE);
		contentWrapper.setLayout(new FillLayout());

		text = new Text(contentWrapper, SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL);
		text.setEditable(false);

		Font font = text.getFont();
		FontData[] fontData = font.getFontData();
		for (int i = 0; i < fontData.length; i++) {
			fontData[i].setHeight(EditorConst.FONT_SIZE);
		}
		text.setFont(new Font(getDisplay(), fontData));

		// text.setText("Text for item " + "\n\none, two, three\n\nabcdefghijklmnop");
		return contentWrapper;
	}

	@Override
	public void clearMessage() {
		setText("");
	}

	@Override
	public void addMessage(final String message) {
		addMessage(null, message);
	}

	@Override
	public void addMessage(String theoryId, String message) {
		if (null == text) {
			System.err.println("text is null");
			return;
		}
		text.append(message + "\n");
	}

}

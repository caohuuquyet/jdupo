/**
 * jDUPO 1.0
 *   
 */
package com.jdupo.editor.panel;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

import com.jdupo.sys.EditorConst;

public class AccountabilityMessagePanel extends GenericMessagePanel {
	private static final String CAPTION = "Accountability Message";
	private Table logTable = null;

	public AccountabilityMessagePanel(CTabFolder parent) {
		super(parent, CAPTION);
		Control contents = createContents(parent);
		setControl(contents);
	}

	@Override
	protected Control createContents(Composite parent) {
		// Text text = new Text(parent, SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL);1
		// text.setText("Text for item " + "\n\none, two, three\n\nabcdefghijklmnop");
		// return text;
		// new Button(contentWrapper, SWT.PUSH).setText("Button in pane2");
		Composite contentWrapper = new Composite(parent, SWT.NONE);
		contentWrapper.setLayout(new GridLayout());

		try {
			GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);
			data.heightHint = 50;
			logTable = new Table(contentWrapper, SWT.MULTI | SWT.BORDER | SWT.FULL_SELECTION);
			logTable.setLinesVisible(true);
			logTable.setHeaderVisible(true);
			logTable.setLayoutData(data);

			Font font = logTable.getFont();
			FontData[] fontData = font.getFontData();
			for (int i = 0; i < fontData.length; i++) {
				fontData[i].setHeight(EditorConst.FONT_SIZE);
			}
			logTable.setFont(new Font(getDisplay(), fontData));

			String[] headers = { "Command", "Message" };
			for (int i = 0; i < headers.length; i++) {
				TableColumn column = new TableColumn(logTable, SWT.NONE);
				column.setText(headers[i]);
				column.setWidth(150);
			}
			for (int i = 1; i < logTable.getColumnCount() - 1; i++) {
				logTable.getColumn(i).pack();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return contentWrapper;
	}

	@Override
	public void clearMessage() {}

	@Override
	public void addMessage(final String message) {
		addMessage("Editor", message);
	}

	@Override
	public void addMessage(String theoryId, String message) {
		TableItem item = new TableItem(logTable, SWT.NONE, 0);
		item.setText(0, theoryId);
		item.setText(1, message);
		logTable.getColumn(logTable.getColumnCount() - 1).pack();
	}
}

import java.awt {
	Component,
	BorderLayout,
	GridLayout
}
import javax.swing {
	JTable,
	JPanel,
	JTextField,
	JButton,
	DefaultCellEditor
}
import lovelace.tartan.model {
	Figure,
	NamedFigure
}
import lovelace.tartan.gui.controls {
	BorderedPanel,
	ImageButton,
	ListenedButton
}
import java.lang {
	Types
}
import java.awt.event {
	ActionEvent,
	KeyEvent,
	KeyAdapter
}
import javax.swing.table {
	TableCellEditor
}
import javax.swing.event {
	CellEditorListener,
	ChangeEvent
}
import java.util {
	EventObject
}
import ceylon.collection {
	ArrayList,
	MutableList
}
// Unfortunately, IIRC, we can't use BorderedPanel and have this be a class
// rather than a function wrapping and _then_ initializing an object.
class FigureEditor(Figure figure, Anything() cancel = noop) extends JPanel(BorderLayout()) {
	void setBars(String bars) {
		if (bars.empty) {
			figure.bars = null;
		} else {
			figure.bars = bars;
		}
	}
	void setText(String text) => figure.description = text;
	JTextField barsField = JTextField(figure.bars else "", 6);
	JTextField descField = JTextField(figure.description, 20);
	add(barsField, Types.nativeString(BorderLayout.lineStart));
	add(descField, Types.nativeString(BorderLayout.center));
	value okButton = ImageButton(loadImage("/lovelace/tartan/gui/Green-Check-Mark-Icon-300px.png"));
	void okListener(ActionEvent _) {
		setBars(barsField.text);
		setText(descField.text);
		cancel();
	}
	okButton.addActionListener(okListener);
	barsField.addActionListener(okListener);
	descField.addActionListener(okListener);
	value cancelButton = ImageButton(loadImage("/lovelace/tartan/gui/Red-X-Icon-300px.png"));
	void cancelListener(Anything _) {
		barsField.text = figure.bars else "";
		descField.text = figure.description;
		cancel();
	}
	object escapeListener extends KeyAdapter() {
		shared actual void keyTyped(KeyEvent event) {
			if (event.keyCode == KeyEvent.vkEscape) {
				cancelListener(event);
			}
		}
		shared actual void keyReleased(KeyEvent event) => keyTyped(event);
	}
	barsField.addKeyListener(escapeListener);
	descField.addKeyListener(escapeListener);
	cancelButton.addActionListener(cancelListener);
	add(BorderedPanel.horizontalLine(okButton, null, cancelButton),
		Types.nativeString(BorderLayout.lineEnd));
}
JPanel danceStringEditor(variable String string, Anything(String) accept, Anything() cancel = noop) {
	JTextField field = JTextField(string, 26);
	void wrapped(ActionEvent _) {
		string = field.text;
		accept(string);
	}
	field.addActionListener(wrapped);
	value okButton = ImageButton(loadImage("/lovelace/tartan/gui/Green-Check-Mark-Icon-300px.png"));
	okButton.addActionListener(wrapped);
	value cancelButton = ImageButton(loadImage("/lovelace/tartan/gui/Red-X-Icon-300px.png"));
	cancelButton.addActionListener((_) {
		field.text = string;
		cancel();
	});
	return BorderedPanel.horizontalLine(null, field, BorderedPanel.horizontalLine(okButton, null, cancelButton));
}
class NamedFigureEditor(NamedFigure nfigure, Anything() stopOperation) extends JPanel(GridLayout(0, 1)) {
	// TODO: Need a way to drag-and-drop within a named figure
	for (num->figure in nfigure.contents.indexed) {
		switch (figure)
		case (is Figure) {
			add(FigureEditor(figure));
		}
		case (is String) {
			add(danceStringEditor(string, (String string) {
				if (string.empty) {
					nfigure.contents.delete(num);
				} else {
					nfigure.contents[num] = string;
				}
			}));
		}
	}
	value addButton = JButton("Add movement");
	value buttonPanel = BorderedPanel.horizontalLine(addButton, null,
		ListenedButton("Done Editing", (_) => stopOperation()));
	addButton.addActionListener((_) {
		Figure newFigure = Figure("description of movement", "bars");
		nfigure.contents.add(newFigure);
		remove(buttonPanel);
		add(FigureEditor(newFigure));
		add(buttonPanel);
	});
	add(buttonPanel);
}
object danceElementEditor satisfies TableCellEditor {
	MutableList<CellEditorListener> listeners = ArrayList<CellEditorListener>();
	shared actual void addCellEditorListener(CellEditorListener listener) => listeners.add(listener);
	shared actual void removeCellEditorListener(CellEditorListener listener) => listeners.remove(listener);
	TableCellEditor default = DefaultCellEditor(JTextField());
	variable Object? current = null;
	shared actual Object? cellEditorValue => current;
	shared actual Component getTableCellEditorComponent(JTable table, Object val, Boolean isSelected,
			Integer rowIndex, Integer columnIndex) {
		current = val;
		switch (val)
		case (is NamedFigure) {
			return NamedFigureEditor(val, stopCellEditing);
		}
		case (is String) {
			// FIXME: This won't actually successfully apply edit
			return danceStringEditor(val, noop, stopCellEditing);
		}
		case (is Figure) {
			return FigureEditor(val, stopCellEditing);
		}
		else {
			return default.getTableCellEditorComponent(table, val, isSelected, rowIndex, columnIndex);
		}
	}
	shared actual void cancelCellEditing() {
		ChangeEvent cancelEvent = ChangeEvent(this);
		for (listener in listeners) {
			listener.editingCanceled(cancelEvent);
		}
	}
	shared actual Boolean isCellEditable(EventObject event) => default.isCellEditable(event);
	shared actual Boolean shouldSelectCell(EventObject event) => default.shouldSelectCell(event);
	shared actual Boolean stopCellEditing() {
		ChangeEvent stopEvent = ChangeEvent(this);
		for (listener in listeners) {
			listener.editingStopped(stopEvent);
		}
		return true;
	}
}
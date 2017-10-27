import ceylon.collection {
	ArrayList
}
import lovelace.tartan.model {
	ProgramElement,
	NamedFigure,
	Figure,
	Dance
}
import javax.swing {
	JList,
	DropMode,
	JComponent,
	JPanel,
	JSplitPane,
	JTable,
	JScrollPane
}
import java.awt {
	Dimension,
	BorderLayout
}
import java.lang {
	Types
}
import lovelace.tartan.gui.model {
	MutableListModel,
	TableModelAdapter,
	MutableSingleColumnTableModel
}
import lovelace.tartan.gui.controls {
	BorderedPanel,
	ListenedButton
}
object elementEditingPanel extends JPanel(BorderLayout()) {
	variable ProgramElement? _current = null;
	shared ProgramElement? current => _current;
	value table = JTable(TableModelAdapter(ArrayList<Figure|NamedFigure|String>()));
	value detailsPanel = DanceDetailsPanel();
	assign current {
		_current = current;
		detailsPanel.current = current;
		if (is Dance current) {
			table.model = TableModelAdapter(current.contents);
			for (row in 0:table.rowCount) {
				value rendered = table.prepareRenderer(table.getCellRenderer(row, 0), row, 0);
				table.rowHeight = rendered.preferredSize.height.integer;
			}
		} else {
			table.model = TableModelAdapter(ArrayList<Figure|NamedFigure|String>());
		}
	}
	table.transferHandler = figureTransferHandler;
	table.dropMode = DropMode.insert;
	table.dragEnabled = true;
	table.setDefaultEditor(`Object`, danceElementEditor);
	table.setDefaultRenderer(Types.classForType<Object>(), danceElementRenderer);
	add(detailsPanel, Types.nativeString(BorderLayout.pageStart));
	add(JScrollPane(table), Types.nativeString(BorderLayout.center));
	add(BorderedPanel.horizontalLine(ListenedButton("Add Figure", (_) {
		if (is MutableSingleColumnTableModel<Figure|NamedFigure|String> model = table.model,
				is Dance temp = current) {
			model.addElement(Figure("Figure To Be Entered", "Bars TBD"));
		}
	}), ListenedButton("Add Multi-Movement Figure", (_) {
		if (is MutableSingleColumnTableModel<Figure|NamedFigure|String> model = table.model,
				is Dance temp = current) {
			model.addElement(NamedFigure(Figure("First Movement of Figure", "Bars TBD")));
		}
	}), ListenedButton("Remove Selected Figure", (_) {
		Integer selection = table.selectedRow;
		if (is MutableSingleColumnTableModel<Figure|NamedFigure|String> model = table.model,
				is Dance temp = current, selection >= 0, selection < table.rowCount) {
			model.removeElement(selection);
		}
	})), Types.nativeString(BorderLayout.pageEnd));
}
JComponent programEditingPanel(MutableListModel<ProgramElement> program) {
	value selectedList = JList<ProgramElement>(program);
	selectedList.minimumSize = Dimension(400, 100);
	selectedList.transferHandler = programElementTransferHandler;
	selectedList.dropMode = DropMode.insert;
	selectedList.dragEnabled = true;
	selectedList.addListSelectionListener((evt) {
		Integer index = selectedList.selectedIndex;
		elementEditingPanel.current = program[index];
	});
	value retval = JSplitPane(JSplitPane.horizontalSplit, true, selectedList, elementEditingPanel);
	retval.resizeWeight = 0.5;
	return retval;
}

import ceylon.collection {
	ArrayList,
	MutableList
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
	ListModel
}
import java.awt {
	Dimension,
	BorderLayout
}
import java.lang {
	Types,
	ArrayIndexOutOfBoundsException
}
import javax.swing.event {
	ListDataListener
}
object emptyDataModel satisfies ListModel<Figure|NamedFigure|String> {
	MutableList<ListDataListener> listeners = ArrayList<ListDataListener>();
	shared actual void addListDataListener(ListDataListener listener) => listeners.add(listener);
	shared actual Figure|NamedFigure|String getElementAt(Integer index) {
		throw ArrayIndexOutOfBoundsException(index);
	}
	shared actual void removeListDataListener(ListDataListener listener) => listeners.remove(listener);
	shared actual Integer size => 0;
}
object elementEditingPanel extends JPanel(BorderLayout()) {
	variable ProgramElement? _current = null;
	shared ProgramElement? current => _current;
	value list = JList(ListModelAdapter(ArrayList<Figure|NamedFigure|String>()));
	assign current {
		_current = current;
		process.writeLine("Emptying list model");
		list.model = emptyDataModel;
		if (is Dance current) {
			list.model = ListModelAdapter(current.contents);
		} else {
			list.model = ListModelAdapter(ArrayList<Figure|NamedFigure|String>());
		}
		process.writeLine("List model now has ``list.model.size`` elements");
	}
	list.transferHandler = figureTransferHandler;
	list.dropMode = DropMode.insert;
	list.dragEnabled = true;
	add(list, Types.nativeString(BorderLayout.center));
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
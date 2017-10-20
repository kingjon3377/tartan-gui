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
	JSplitPane
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
	ListModelAdapter
}
object elementEditingPanel extends JPanel(BorderLayout()) {
	variable ProgramElement? _current = null;
	shared ProgramElement? current => _current;
	value list = JList(ListModelAdapter(ArrayList<Figure|NamedFigure|String>()));
	assign current {
		_current = current;
		if (is Dance current) {
			list.model = ListModelAdapter(current.contents);
		} else {
			list.model = ListModelAdapter(ArrayList<Figure|NamedFigure|String>());
		}
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

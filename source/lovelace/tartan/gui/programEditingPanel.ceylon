import ceylon.collection {
	MutableList,
	ArrayList
}
import lovelace.tartan.model {
	ProgramElement,
	NamedFigure,
	Figure
}
import javax.swing {
	JList,
	DropMode,
	JComponent,
	JPanel
}
import java.awt {
	Dimension
}
object elementEditingPanel extends JPanel() {
	variable ProgramElement? current = null;
	variable ListModelAdapter<Figure|NamedFigure|String> contentsModel =
			ListModelAdapter(ArrayList<Figure|NamedFigure|String>());
	add(JList(contentsModel));
}
//JComponent programEditingPanel(MutableList<ProgramElement> program) {
//	value selectedListModel = ListModelAdapter(program);
//	value selectedList = JList<ProgramElement>(selectedListModel);
//	selectedList.minimumSize = Dimension(400, 100);
//	selectedList.transferHandler = programElementTransferHandler;
//	selectedList.dropMode = DropMode.insert;
//	selectedList.dragEnabled = true;
//}
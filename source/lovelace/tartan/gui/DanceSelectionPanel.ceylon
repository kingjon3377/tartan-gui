import javax.swing {
    JPanel,
    JList,
    BoxLayout,
    Box,
	JSplitPane,
	JComponent,
	JScrollPane,
	JTextField,
	DropMode
}
import java.awt {
    BorderLayout,
    Dimension,
	Image
}
import lovelace.tartan.db {
    DanceRow,
    DanceDatabase,
	convertDance
}
import java.lang {
    Thread,
	Types
}
import javax.imageio {
    ImageIO
}
import lovelace.tartan.model {
	ProgramElement,
	Dance,
	Intermission,
	AuldLangSyne
}
import java.awt.event {
	ActionEvent
}
import lovelace.tartan.gui.model {
	MutableListModel
}
import lovelace.tartan.gui.controls {
	ImageButton,
	ListenedButton
}
"Get an image from the classpath as an icon, in the absence of a Ceylon SDK API to do so."
Image loadImage(String filename) {
	return ImageIO.read(Thread.currentThread().contextClassLoader.getResourceAsStream(filename));
}
JComponent danceSelectionPanel(DanceDatabase db, MutableListModel<ProgramElement> program) {
	JPanel inner = JPanel();
	inner.layout = BoxLayout(inner, BoxLayout.pageAxis);
	value rightButton = ImageButton(loadImage("/lovelace/tartan/gui/arrow-right-300px.png"));
	value leftButton = ImageButton(loadImage("/lovelace/tartan/gui/arrow-left-300px.png"));
	inner.add(Box.createVerticalGlue());
	inner.add(rightButton);
	inner.add(Box.createVerticalStrut(5));
	inner.add(leftButton);
	inner.add(Box.createVerticalGlue());
	inner.maximumSize = Dimension(60, 4096);
	inner.preferredSize = Dimension(40, 480);
	inner.minimumSize = Dimension(20, 45);

	JPanel filterPanel = JPanel(BorderLayout());
	JTextField filterField = JTextField(15);
	filterPanel.add(filterField, Types.nativeString(BorderLayout.center));

	JPanel left = JPanel(BorderLayout());
	value danceListModel = DanceSearchResultsListModel(db);
	value danceList = JList<DanceRow>(danceListModel);

	void filterDanceList(ActionEvent _) {
		String search = filterField.text.trimmed;
		if (search.empty) {
			danceListModel.search(null);
		} else {
			danceListModel.search(search);
		}
		danceList.repaint();
	}

	filterPanel.add(ListenedButton("Search", filterDanceList), Types.nativeString(BorderLayout.lineEnd));

	left.add(JScrollPane(danceList), Types.nativeString(BorderLayout.center));
	left.add(filterPanel, Types.nativeString(BorderLayout.north));

	filterField.addActionListener(filterDanceList);

	value selectedList = JList<ProgramElement>(program);
	selectedList.minimumSize = Dimension(400, 100);
	selectedList.transferHandler = programElementTransferHandler;
	selectedList.dropMode = DropMode.insert;
	selectedList.dragEnabled = true;
	rightButton.addActionListener((evt) {
		if (exists selection = danceList.selectedValue, !program.asIterable.narrow<Dance>().map(Dance.title).equals(selection.name)) {
			program.addElement(convertDance(selection, db.cribText(selection)));
		}
	});
	leftButton.addActionListener((evt) {
		if (exists selection = selectedList.selectedIndex, selection >= 0) {
			program.removeElement(selection);
		}
	});
	JPanel rightPanel = JPanel(BorderLayout());
	rightPanel.add(JScrollPane(selectedList), Types.nativeString(BorderLayout.center));
	JPanel specialPanel = JPanel(BorderLayout());
	// TODO: These should add at (before? after?) the current selection, not always at the end
	specialPanel.add(ListenedButton("Add Break", (_) => program.addElement(Intermission())), Types.nativeString(BorderLayout.lineStart));
	specialPanel.add(ListenedButton("""<html>Add &ldquo;Auld Lang Syne&rdquo;</html>""",
		(_) => program.addElement(AuldLangSyne())), Types.nativeString(BorderLayout.lineEnd));
	rightPanel.add(specialPanel, Types.nativeString(BorderLayout.pageEnd));
	JSplitPane secondSplitPane = JSplitPane(JSplitPane.horizontalSplit, true, inner, rightPanel);
	JSplitPane firstSplitPane = JSplitPane(JSplitPane.horizontalSplit, true, left, secondSplitPane);
	secondSplitPane.resizeWeight = 0.0;
	firstSplitPane.resizeWeight = 0.5;
	return firstSplitPane;
}
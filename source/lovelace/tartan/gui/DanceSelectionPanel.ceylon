import javax.swing {
    JPanel,
    JList,
	JSplitPane,
	JComponent,
	JScrollPane,
	JTextField,
	DropMode
}
import java.awt {
    Dimension,
	Image
}
import lovelace.tartan.db {
    DanceRow,
    DanceDatabase,
	convertDance
}
import java.lang {
    Thread
}
import javax.imageio {
    ImageIO
}
import lovelace.tartan.model {
	ProgramElement,
	Dance,
	Intermission
}
import java.awt.event {
	ActionEvent,
	KeyEvent,
	KeyAdapter,
	MouseAdapter,
	MouseEvent
}
import lovelace.tartan.gui.model {
	MutableListModel
}
import lovelace.tartan.gui.controls {
	ImageButton,
	ListenedButton,
	BoxDirection,
	boxPanel,
	boxGlue,
	BoxStrut,
	BorderedPanel
}
"Get an image from the classpath as an icon, in the absence of a Ceylon SDK API to do so."
Image loadImage(String filename) {
	return ImageIO.read(Thread.currentThread().contextClassLoader.getResourceAsStream(filename));
}
JComponent danceSelectionPanel(DanceDatabase db, MutableListModel<ProgramElement> program) {
	value rightButton = ImageButton(loadImage("/lovelace/tartan/gui/arrow-right-300px.png"));
	value leftButton = ImageButton(loadImage("/lovelace/tartan/gui/arrow-left-300px.png"));
	JPanel inner = boxPanel(BoxDirection.pageAxis, boxGlue, rightButton, BoxStrut(5), leftButton, boxGlue);
	inner.maximumSize = Dimension(60, 4096);
	inner.preferredSize = Dimension(40, 480);
	inner.minimumSize = Dimension(20, 45);

	JTextField filterField = JTextField(15);

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

	value filterPanel = BorderedPanel.horizontalLine(null, filterField, ListenedButton("Search", filterDanceList));
	value left = BorderedPanel.verticalLine(filterPanel, JScrollPane(danceList), null);

	filterField.addActionListener(filterDanceList);

	value selectedList = JList<ProgramElement>(program);
	selectedList.minimumSize = Dimension(400, 100);
	selectedList.transferHandler = programElementTransferHandler;
	selectedList.dropMode = DropMode.insert;
	selectedList.dragEnabled = true;
	void addDance() {
		if (exists selection = danceList.selectedValue, !program.asIterable.narrow<Dance>().map(Dance.title).equals(selection.name)) {
			program.addElement(convertDance(selection, db.cribText(selection)));
		}
	}
	rightButton.addActionListener((evt) => addDance());
	danceList.addKeyListener(object extends KeyAdapter() {
		shared actual void keyPressed(KeyEvent evt) {
			if (evt.keyCode == KeyEvent.vkEnter) {
				addDance();
			}
		}
		shared actual void keyTyped(KeyEvent evt) => keyPressed(evt);
	});
	danceList.addMouseListener(object extends MouseAdapter() {
		shared actual void mouseClicked(MouseEvent evt) {
			if (evt.clickCount == 2 || evt.clickCount == 3) {
				addDance();
			}
		}
	});
	leftButton.addActionListener((evt) {
		value selection = selectedList.selectedIndex;
		if (selection >= 0) {
			program.removeElement(selection);
		}
	});
	// TODO: Breaks should add at (before? after?) the current selection, not always at the end
	value rightPanel = BorderedPanel.verticalLine(null, JScrollPane(selectedList),
		ListenedButton("Add Break", (_) => program.addElement(Intermission())));
	JSplitPane secondSplitPane = JSplitPane(JSplitPane.horizontalSplit, true, inner, rightPanel);
	JSplitPane firstSplitPane = JSplitPane(JSplitPane.horizontalSplit, true, left, secondSplitPane);
	secondSplitPane.resizeWeight = 0.0;
	firstSplitPane.resizeWeight = 0.5;
	return firstSplitPane;
}
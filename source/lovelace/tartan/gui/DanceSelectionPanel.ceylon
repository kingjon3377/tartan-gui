import javax.swing {
    JPanel,
    JList,
    JButton,
    BoxLayout,
    Box,
	ListModel,
	JSplitPane,
	JComponent,
	JScrollPane,
	JTextField,
	DropMode
}
import java.awt {
    BorderLayout,
    Dimension,
	Image,
	Graphics
}
import lovelace.tartan.db {
    DanceRow,
    DanceDatabase,
	convertDance
}
import java.lang {
    Thread,
	ArrayIndexOutOfBoundsException,
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
import ceylon.collection {
	MutableList,
	ArrayList
}
import javax.swing.event {
	ListDataListener,
	ListDataEvent
}
import java.awt.event {
	ActionEvent
}
"Get an image from the classpath as an icon, in the absence of a Ceylon SDK API to do so."
Image loadImage(String filename) {
	return ImageIO.read(Thread.currentThread().contextClassLoader.getResourceAsStream(filename));
}
class ImageButton(Image image) extends JButton() {
	shared actual void paintComponent(Graphics pen) => pen.drawImage(image, 0, 0, width, height, null);
	maximumSize = Dimension(60, 60);
	preferredSize = Dimension(40, 40);
	minimumSize = Dimension(20, 20);
}
interface MutableListModel<Element>
		satisfies ListModel<Element>&Reorderable&Correspondence<Integer,Element>
		given Element satisfies Object {
	shared formal void addElement(Element element);
	shared formal void removeElement(Integer|Element element);
	shared actual Element? get(Integer index) {
		if ((0:size).contains(index)) {
			return getElementAt(index);
		} else {
			return null;
		}
	}
	shared actual Boolean defines(Integer index) => (0:size).contains(index);
	shared formal Iterable<Element> asIterable;
}
class ListModelAdapter<Element>(MutableList<Element> list)
		satisfies MutableListModel<Element> given Element satisfies Object {
	MutableList<ListDataListener> listeners = ArrayList<ListDataListener>();
	shared actual void addListDataListener(ListDataListener listener) => listeners.add(listener);
	shared actual void removeListDataListener(ListDataListener listener) => listeners.remove(listener);
	shared actual Element getElementAt(Integer index) {
		if (exists retval = list[index]) {
			return retval;
		} else {
			throw ArrayIndexOutOfBoundsException(index);
		}
	}
	shared actual Integer size => list.size;
	shared actual void addElement(Element element) {
		list.add(element);
		for (listener in listeners) {
			listener.intervalAdded(ListDataEvent(this, ListDataEvent.intervalAdded, list.size - 1, list.size - 1));
		}
	}
	shared actual void removeElement(Integer|Element element) {
		if (is Integer element) {
			list.delete(element);
			for (listener in listeners) {
				listener.intervalRemoved(ListDataEvent(this, ListDataEvent.intervalRemoved, element, element));
			}
		} else {
			if (exists index = list.firstIndexWhere(element.equals)) {
				removeElement(index);
			}
		}
	}
	shared actual void reorder(Integer fromIndex, Integer toIndex) {
		if (fromIndex != toIndex, exists item = list.delete(fromIndex)) {
			if (fromIndex > toIndex) {
				list.insert(toIndex, item);
			} else {
				list.insert(toIndex - 1, item);
			}
		}
		value addedEvent = ListDataEvent(this, ListDataEvent.intervalAdded, toIndex, toIndex);
		value removedEvent = ListDataEvent(this, ListDataEvent.intervalRemoved, fromIndex, fromIndex);
		for (listener in listeners) {
			listener.intervalRemoved(removedEvent);
			listener.intervalAdded(addedEvent);
		}
	}
	shared actual Iterable<Element> asIterable => list;
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
	JButton filterButton = JButton("Search");
	filterPanel.add(filterButton, Types.nativeString(BorderLayout.lineEnd));

	JPanel left = JPanel(BorderLayout());
	value danceListModel = DanceSearchResultsListModel(db);
	value danceList = JList<DanceRow>(danceListModel);
	left.add(JScrollPane(danceList), Types.nativeString(BorderLayout.center));
	left.add(filterPanel, Types.nativeString(BorderLayout.north));

	void filterDanceList(ActionEvent _) {
		String search = filterField.text.trimmed;
		if (search.empty) {
			danceListModel.search(null);
		} else {
			danceListModel.search(search);
		}
		danceList.repaint();
	}

	filterField.addActionListener(filterDanceList);
	filterButton.addActionListener(filterDanceList);

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
	JButton breakButton = JButton("Add Break");
	breakButton.addActionListener((evt) {
		program.addElement(Intermission());
	});
	specialPanel.add(breakButton, Types.nativeString(BorderLayout.lineStart));
	JButton alsButton = JButton("""<html>Add &ldquo;Auld Lang Syne&rdquo;</html>""");
	alsButton.addActionListener((evt) {
		program.addElement(AuldLangSyne());
	});
	specialPanel.add(alsButton, Types.nativeString(BorderLayout.lineEnd));
	rightPanel.add(specialPanel, Types.nativeString(BorderLayout.pageEnd));
	JSplitPane secondSplitPane = JSplitPane(JSplitPane.horizontalSplit, true, inner, rightPanel);
	JSplitPane firstSplitPane = JSplitPane(JSplitPane.horizontalSplit, true, left, secondSplitPane);
	secondSplitPane.resizeWeight = 0.0;
	firstSplitPane.resizeWeight = 0.5;
	return firstSplitPane;
}
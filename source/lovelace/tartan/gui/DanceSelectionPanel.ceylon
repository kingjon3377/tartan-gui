import javax.swing {
    JPanel,
    JList,
    JButton,
    JFrame,
    WindowConstants,
    BoxLayout,
    Box,
	ListModel
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
import ceylon.interop.java {
    javaString
}
import java.lang {
    Thread,
	ArrayIndexOutOfBoundsException
}
import javax.imageio {
    ImageIO
}
import lovelace.tartan.model {
	ProgramElement
}
import ceylon.collection {
	MutableList,
	ArrayList
}
import javax.swing.event {
	ListDataListener,
	ListDataEvent
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
class ListModelAdapter<Element>(MutableList<Element> list)
		satisfies ListModel<Element> given Element satisfies Object {
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
	shared void addElement(Element element) {
		list.add(element);
		for (listener in listeners) {
			listener.intervalAdded(ListDataEvent(this, ListDataEvent.intervalAdded, list.size - 1, list.size - 1));
		}
	}
	shared void removeElement(Integer|Element element) {
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
}
JPanel danceSelectionPanel(DanceDatabase db, MutableList<ProgramElement> program) {
	JPanel retval = JPanel(BorderLayout());
	value danceList = JList<DanceRow>(DanceSearchResultsListModel(db));
	danceList.maximumSize = Dimension(620, 480);
	danceList.preferredSize = Dimension(310, 480);
	retval.add(danceList,
		javaString(BorderLayout.lineStart));
	JPanel inner = JPanel();
	inner.layout = BoxLayout(inner, BoxLayout.pageAxis);
	value rightButton = ImageButton(loadImage("/lovelace/tartan/gui/arrow-right-300px.png"));
	value leftButton = ImageButton(loadImage("/lovelace/tartan/gui/arrow-left-300px.png"));
	leftButton.addActionListener((evt) => process.writeLine("leftButton pressed"));
	inner.add(Box.createVerticalGlue());
	inner.add(rightButton);
	inner.add(Box.createVerticalStrut(5));
	inner.add(leftButton);
	inner.add(Box.createVerticalGlue());
	retval.add(inner, javaString(BorderLayout.center));
	value selectedListModel = ListModelAdapter(program);
	value selectedList = JList<ProgramElement>(selectedListModel);
	retval.add(selectedList, javaString(BorderLayout.lineEnd));
	selectedList.maximumSize = Dimension(620, 480);
	selectedList.preferredSize = Dimension(310, 480);
	rightButton.addActionListener((evt) => selectedListModel.addElement(convertDance(danceList.selectedValue)));
	return retval;
}
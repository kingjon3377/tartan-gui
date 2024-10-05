package lovelace.tartan.gui.model;

import java.util.List;
import javax.swing.ListModel;

/**
 * An interface for a list model to support drag-and-drop within the GUI list. (Those
 * features are provided by the interfaces we extend.)
 *
 * @author Jonathan Lovelace
 */
public interface ReorderableListModel<Element> extends ListModel<Element>, Reorderable,
		                                                       List<Element> {
}

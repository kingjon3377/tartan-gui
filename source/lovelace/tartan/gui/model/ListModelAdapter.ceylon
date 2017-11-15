import ceylon.collection {
	ArrayList,
	MutableList
}
import java.lang {
	ArrayIndexOutOfBoundsException
}
import javax.swing.event {
	ListDataListener,
	ListDataEvent
}
shared class ListModelAdapter<Element>(MutableList<Element> list)
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
	shared actual void addElements({Element*} elements) {
		if (!elements.empty) {
			Integer oldSize = size;
			list.addAll(elements);
			for (listener in listeners) {
				listener.intervalAdded(ListDataEvent(this, ListDataEvent.intervalAdded, oldSize, list.size - 1));
			}
		}
	}
	shared actual void clear() {
		Integer oldSize = size;
		list.clear();
		for (listener in listeners) {
			listener.intervalRemoved(ListDataEvent(this, ListDataEvent.intervalRemoved, 0, oldSize));
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
			value addedEvent = ListDataEvent(this, ListDataEvent.intervalAdded, toIndex, toIndex);
			value removedEvent = ListDataEvent(this, ListDataEvent.intervalRemoved, fromIndex, fromIndex);
			for (listener in listeners) {
				listener.intervalRemoved(removedEvent);
				listener.intervalAdded(addedEvent);
			}
		}
	}
	shared actual Iterable<Element> asIterable => list;
}

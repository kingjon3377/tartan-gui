package lovelace.tartan.gui.model;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;
import javax.swing.ListModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

/**
 * A list model to support drag-and-drop within the GUI list.
 *
 * @author Jonathan Lovelace
 */
public class ReorderableListModel<Element> extends AbstractList<Element>
		implements ListModel<Element>, Reorderable {
	private final List<ListDataListener> listeners = new ArrayList<>();
	private final List<Element> wrapped;

	public ReorderableListModel(List<Element> list) {
		wrapped = list;
	}

	@Override
	public int getSize() {
		return wrapped.size();
	}

	@Override
	public Element getElementAt(final int index) {
		return wrapped.get(index);
	}

	@Override
	public void addListDataListener(final ListDataListener listener) {
		listeners.add(listener);
	}

	@Override
	public void removeListDataListener(final ListDataListener listener) {
		listeners.remove(listener);
	}

	/**
	 * Move a row of a list or table from one position to another.
	 *
	 * @param fromIndex the index to remove from
	 * @param toIndex   the index (*before* removing the item!) to move it to
	 */
	@Override
	public void reorder(final int fromIndex, final int toIndex) {
		if (fromIndex != toIndex) {
			final Element item = wrapped.remove(fromIndex);
			if (fromIndex > toIndex) {
				wrapped.add(toIndex, item);
			} else {
				wrapped.add(toIndex - 1, item);
			}
			final ListDataEvent removedEvent =
					new ListDataEvent(this, ListDataEvent.INTERVAL_REMOVED, fromIndex,
							fromIndex);
			final ListDataEvent addedEvent =
					new ListDataEvent(this, ListDataEvent.INTERVAL_ADDED, toIndex,
							toIndex);
			for (final ListDataListener listener : listeners) {
				listener.intervalRemoved(removedEvent);
				listener.intervalAdded(addedEvent);
			}
		}
	}

	@Override
	public int size() {
		return wrapped.size();
	}

	@Override
	public Element get(final int index) {
		return wrapped.get(index);
	}

	@Override
	public Element remove(int index) {
		Element retval = wrapped.remove(index);
		final ListDataEvent event =
				new ListDataEvent(this, ListDataEvent.INTERVAL_REMOVED, index, index);
		for (final ListDataListener listener : listeners) {
			listener.intervalRemoved(event);
		}
		return retval;
	}

	public void clear() {
		final ListDataEvent event = new ListDataEvent(this,
				ListDataEvent.INTERVAL_REMOVED, 0, wrapped.size() - 1);
		wrapped.clear();
		for (final ListDataListener listener : listeners) {
			listener.intervalRemoved(event);
		}
	}

	@Override
	public void add(final int index, final Element element) {
		wrapped.add(index, element);
		final ListDataEvent event = new ListDataEvent(this, ListDataEvent.INTERVAL_ADDED, index, index);
		for (final ListDataListener listener : listeners) {
			listener.intervalAdded(event);
		}
	}

	@Override
	public Element set(final int index, final Element element) {
		final Element retval = wrapped.set(index, element);
		final ListDataEvent event = new ListDataEvent(this, ListDataEvent.CONTENTS_CHANGED, index, index);
		for (final ListDataListener listener : listeners) {
			listener.contentsChanged(event);
		}
		return retval;
	}
}

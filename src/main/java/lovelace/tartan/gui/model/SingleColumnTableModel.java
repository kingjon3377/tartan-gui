package lovelace.tartan.gui.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;
import org.jetbrains.annotations.NotNull;

/**
 * A model for a single-column table.
 *
 * @param <Element> The type of things stored in the table.
 * @author Jonathan Lovelace
 */
public final class SingleColumnTableModel<Element>
		implements TableModel, Reorderable, List<Element> {
	private final List<TableModelListener> listeners = new ArrayList<>();

	private final List<Element> wrapped;
	private final String columnName;
	private final Class<Element> cls;

	private void fireEvents(final TableModelEvent... events) {
		for (final TableModelListener listener : listeners) {
			Arrays.stream(events).forEach(listener::tableChanged);
		}
	}

	private void fireRemovalEvent(final int index) {
		fireEvents(new TableModelEvent(this, index, index, TableModelEvent.ALL_COLUMNS,
				TableModelEvent.DELETE));
	}

	private void fireInsertionEvent(final int index) {
		fireEvents(new TableModelEvent(this, index, index, TableModelEvent.ALL_COLUMNS,
				TableModelEvent.INSERT));
	}

	public SingleColumnTableModel(final List<Element> wrapped, final Class<Element> cls,
			final String columnName) {
		this.wrapped = wrapped;
		this.cls = cls;
		this.columnName = columnName;
	}

	@Deprecated
	public SingleColumnTableModel(final List<Element> wrapped, final Class<Element> cls) {
		this(wrapped, cls, "Directions");
	}

	@Override
	public int getRowCount() {
		return wrapped.size();
	}

	@Override
	public int getColumnCount() {
		return 1;
	}

	/**
	 * Returns the name of the column at <code>columnIndex</code>.  This is used to
	 * initialize the table's column header name.  Note: this name does not need to be
	 * unique; two columns in a table can have the same name.
	 *
	 * @param columnIndex the index of the column
	 * @return the name of the column
	 */
	@Override
	public String getColumnName(final int columnIndex) {
		return columnName;
	}

	/**
	 * Returns the most specific superclass for all the cell values in the column.  This
	 * is used by the <code>JTable</code> to set up a default renderer and editor for the
	 * column.
	 *
	 * @param columnIndex the index of the column
	 * @return the common ancestor class of the object values in the model.
	 */
	@Override
	public Class<?> getColumnClass(final int columnIndex) {
		return Object.class;
	}

	@Override
	public boolean isCellEditable(final int rowIndex, final int columnIndex) {
		return columnIndex == 0 && rowIndex >= 0 && rowIndex < wrapped.size();
	}

	@Override
	public Object getValueAt(final int rowIndex, final int columnIndex) {
		if (columnIndex == 0) {
			return wrapped.get(rowIndex);
		} else {
			throw new IndexOutOfBoundsException("Column must be 0");
		}
	}

	@Override
	public void setValueAt(final Object val, final int rowIndex,
						   final int columnIndex) {
		if (columnIndex == 0) {
			if (rowIndex >= 0 && rowIndex <= wrapped.size()) {
				if (cls.isInstance(val)) {
					if (rowIndex == wrapped.size()) {
						wrapped.add((Element) val);
					} else {
						wrapped.set(rowIndex, (Element) val);
					}
				} else {
					throw new IllegalArgumentException(
							"Unexpected type of list item: " + val.getClass().getName());
				}
			} else {
				throw new IndexOutOfBoundsException(String.format(
						"Row must be between 0 and %d", wrapped.size()));
			}
		} else {
			throw new IndexOutOfBoundsException("Column must be 0");
		}
		fireEvents(
				new TableModelEvent(this, rowIndex, columnIndex,
						TableModelEvent.UPDATE));
	}

	@Override
	public void addTableModelListener(final TableModelListener listener) {
		listeners.add(listener);
	}

	@Override
	public void removeTableModelListener(final TableModelListener listener) {
		listeners.remove(listener);
	}

	@Override
	public void reorder(final int fromIndex, final int toIndex) {
		if (fromIndex != toIndex) {
			final Element item = wrapped.remove(fromIndex);
			if (fromIndex > toIndex) {
				wrapped.add(toIndex, item);
			} else {
				wrapped.add(toIndex - 1, item);
			}
			final TableModelEvent removalEvent =
					new TableModelEvent(this, fromIndex, fromIndex,
							TableModelEvent.ALL_COLUMNS, TableModelEvent.DELETE);
			// TODO: Should toIndex in addEvent be adjusted if fromIndex < toIndex?
			final TableModelEvent addEvent = new TableModelEvent(this, toIndex, toIndex,
					TableModelEvent.ALL_COLUMNS, TableModelEvent.INSERT);
			fireEvents(removalEvent, addEvent);
		}
	}

	@Override
	public int size() {
		return wrapped.size();
	}

	@Override
	public boolean isEmpty() {
		return wrapped.isEmpty();
	}

	@Override
	public boolean contains(final Object obj) {
		return wrapped.contains(obj);
	}

	@Override
	public @NotNull Iterator<Element> iterator() {
		return wrapped.iterator();
	}

	@Override
	public @NotNull Object[] toArray() {
		return wrapped.toArray();
	}

	@Override
	public @NotNull <T> T[] toArray(final @NotNull T[] array) {
		return wrapped.toArray(array);
	}

	@Override
	public boolean add(final Element element) {
		wrapped.add(element);
		fireInsertionEvent(wrapped.size() - 1);
		return true;
	}

	@Override
	public boolean remove(final Object obj) {
		final int index = wrapped.indexOf(obj);
		if (index >= 0) {
			wrapped.remove(index);
			fireRemovalEvent(index);
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean containsAll(final @NotNull Collection<?> collection) {
		return wrapped.containsAll(collection);
	}

	@Override
	public boolean addAll(final @NotNull Collection<? extends Element> collection) {
		boolean retval = false;
		for (final Element item : collection) {
			add(item);
			retval = true;
		}
		return retval;
	}

	@Override
	public boolean addAll(final int index,
						  final @NotNull Collection<? extends Element> collection) {
		if (wrapped.addAll(index, collection)) {
			fireEvents(new TableModelEvent(this, index, index + collection.size() - 1,
					TableModelEvent.ALL_COLUMNS, TableModelEvent.INSERT));
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean removeAll(final @NotNull Collection<?> collection) {
		boolean retval = false;
		for (final Object item : collection) {
			if (remove(item)) {
				retval = true;
			}
		}
		return retval;
	}

	@Override
	public boolean retainAll(final @NotNull Collection<?> collection) {
		final int oldSize = wrapped.size();
		// TODO: Implement more granularly
		if (wrapped.retainAll(collection)) {
			fireEvents(
					new TableModelEvent(this, 0, oldSize - 1,
							TableModelEvent.ALL_COLUMNS,
							TableModelEvent.UPDATE));
			return true;
		} else {
			return false;
		}
	}

	@Override
	public void clear() {
		final TableModelEvent event = new TableModelEvent(this, 0, wrapped.size() - 1,
				TableModelEvent.ALL_COLUMNS, TableModelEvent.DELETE);
		wrapped.clear();
		fireEvents(event);
	}

	@Override
	public Element get(final int index) {
		return wrapped.get(index);
	}

	@Override
	public Element set(final int index, final Element element) {
		final Element retval = wrapped.set(index, element);
		fireEvents(new TableModelEvent(this, index, index, TableModelEvent.ALL_COLUMNS,
				TableModelEvent.UPDATE));
		return retval;
	}

	@Override
	public void add(final int index, final Element element) {
		wrapped.add(index, element);
		fireInsertionEvent(index);
	}

	@Override
	public Element remove(final int index) {
		final Element retval = wrapped.remove(index);
		fireRemovalEvent(index);
		return retval;
	}

	@Override
	public int indexOf(final Object obj) {
		return wrapped.indexOf(obj);
	}

	@Override
	public int lastIndexOf(final Object obj) {
		return wrapped.lastIndexOf(obj);
	}

	@Override
	public @NotNull ListIterator<Element> listIterator() {
		return wrapped.listIterator();
	}

	@Override
	public @NotNull ListIterator<Element> listIterator(final int index) {
		return wrapped.listIterator(index);
	}

	@Override
	public @NotNull List<Element> subList(final int fromIndex, final int toIndex) {
		return wrapped.subList(fromIndex, toIndex);
	}
}

import ceylon.collection {
	MutableList,
	ArrayList
}
import javax.swing.table {
	TableModel
}
import java.lang {
	Types,
	ArrayIndexOutOfBoundsException,
	IllegalArgumentException,
	Class,
	JString=String
}
import javax.swing.event {
	TableModelListener,
	TableModelEvent
}
import ceylon.language.meta {
	type
}
// TODO: add an interface to let callers add and remove elements, notifying listeners when they do
shared class TableModelAdapter<Element>(MutableList<Element> list, String title)
		satisfies Reorderable&TableModel given Element satisfies Object {
	MutableList<TableModelListener> listeners = ArrayList<TableModelListener>();
	shared actual void addTableModelListener(TableModelListener listener) => listeners.add(listener);
	shared actual void removeTableModelListener(TableModelListener listener) => listeners.remove(listener);
	shared actual Element getValueAt(Integer rowIndex, Integer columnIndex) {
		if (columnIndex == 0) {
			if (exists item = list[rowIndex]) {
				return item;
			} else {
				throw ArrayIndexOutOfBoundsException(rowIndex);
			}
		} else {
			throw ArrayIndexOutOfBoundsException(columnIndex);
		}
	}
	shared actual Boolean isCellEditable(Integer rowIndex, Integer columnIndex) =>
			columnIndex == 0 && list.defines(rowIndex);
	shared actual void setValueAt(Object val, Integer rowIndex, Integer columnIndex) {
		if (columnIndex == 0) {
			if (rowIndex >= 0, rowIndex <= list.size) {
				if (is Element val) {
					list[rowIndex] = val;
				} else if (is JString val) {
					setValueAt(val.string, rowIndex, columnIndex);
				} else {
					throw IllegalArgumentException("Unexpected type of list item: ``type(val)``");
				}
			} else {
				throw ArrayIndexOutOfBoundsException(rowIndex);
			}
		} else {
			throw ArrayIndexOutOfBoundsException(columnIndex);
		}
		TableModelEvent event = TableModelEvent(this, rowIndex, rowIndex, columnIndex, TableModelEvent.update);
		for (listener in listeners) {
			listener.tableChanged(event);
		}
	}
	shared actual void reorder(Integer fromIndex, Integer toIndex) {
		if (fromIndex != toIndex, exists item = list.delete(fromIndex)) {
			if (fromIndex > toIndex) {
				list.insert(toIndex, item);
			} else {
				list.insert(toIndex - 1, item);
			}
			TableModelEvent removalEvent = TableModelEvent(this, fromIndex, fromIndex,
				TableModelEvent.allColumns, TableModelEvent.delete);
			TableModelEvent addEvent = TableModelEvent(this, toIndex, toIndex,
				TableModelEvent.allColumns, TableModelEvent.insert);
			for (listener in listeners) {
				listener.tableChanged(removalEvent);
				listener.tableChanged(addEvent);
			}
		}
	}

	shared actual Integer columnCount => 1;
	shared actual Class<out Object> getColumnClass(Integer columnIndex) => Types.classForType<Object>();
	shared actual String getColumnName(Integer columnIndex) => title;
	shared actual Integer rowCount => list.size;
}
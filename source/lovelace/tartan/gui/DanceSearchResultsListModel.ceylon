import lovelace.tartan.db {
    DanceDatabase,
    DanceRow
}
import javax.swing {
	SwingListModel=ListModel
}
import java.lang {
    ArrayIndexOutOfBoundsException
}
import javax.swing.event {
    ListDataListener,
	ListDataEvent
}
import ceylon.collection {
    MutableList,
    ArrayList
}
class DanceSearchResultsListModel(DanceDatabase db) satisfies SwingListModel<DanceRow> {
	MutableList<ListDataListener> listeners = ArrayList<ListDataListener>();
	MutableList<DanceRow> backing = ArrayList {
		*db.dances
	};
    shared actual void addListDataListener(ListDataListener listener) =>
		    listeners.add(listener);
    shared actual DanceRow getElementAt(Integer index) {
	    if (exists retval = backing[index]) {
		    return retval;
	    } else {
		    throw ArrayIndexOutOfBoundsException(index);
	    }
    }
    shared actual void removeListDataListener(ListDataListener listener) =>
		    listeners.remove(listener);
    shared actual Integer size => backing.size;
	variable String? currentSearch = null;
	shared void search(String? term) {
		if (exists term) {
			if (exists temp = currentSearch, term == temp) {
				return;
			}
			currentSearch = term;
			value removeEvent = ListDataEvent(this, ListDataEvent.intervalRemoved, 0, backing.size - 1);
			backing.clear();
			for (listener in listeners) {
				listener.intervalRemoved(removeEvent);
			}
			backing.addAll(db.dances.filter(
						(dance) => dance.name.lowercased.contains(term.lowercased)));
			value addEvent = ListDataEvent(this, ListDataEvent.intervalAdded, 0, backing.size - 1);
			for (listener in listeners) {
				listener.intervalRemoved(addEvent);
			}
		} else if (currentSearch exists) {
			currentSearch = null;
			value removeEvent = ListDataEvent(this, ListDataEvent.intervalRemoved, 0, backing.size - 1);
			backing.clear();
			for (listener in listeners) {
				listener.intervalRemoved(removeEvent);
			}
			backing.addAll(db.dances);
			value addEvent = ListDataEvent(this, ListDataEvent.intervalAdded, 0, backing.size - 1);
			for (listener in listeners) {
				listener.intervalRemoved(addEvent);
			}
		}
	}
}
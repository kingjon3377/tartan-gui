package lovelace.tartan.gui;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.swing.ListModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import lovelace.tartan.db.DanceDatabase;
import lovelace.tartan.db.DanceRow;
import org.jetbrains.annotations.Nullable;

/**
 * The list model for the list of dance search results.
 *
 * @author Jonathan Lovelace
 */
public final class DanceSearchResultsListModel implements ListModel<DanceRow> {
	private final DanceDatabase db;
	private final List<ListDataListener> listeners = new ArrayList<>();
	private final List<DanceRow> backing = new ArrayList<>();
	@Nullable
	private String currentSearch = null;

	public DanceSearchResultsListModel(final DanceDatabase db) {
		this.db = db;
		backing.addAll(db.getDances());
	}

	public void search(@Nullable final String term) {
		if (Objects.equals(term, currentSearch)) {
			return;
		}
		if (term == null) {
			currentSearch = null;
			final ListDataEvent removeEvent =
					new ListDataEvent(this, ListDataEvent.INTERVAL_REMOVED, 0,
							backing.size() - 1);
			backing.clear();
			fireRemovalEvent(removeEvent);
			backing.addAll(db.getDances());
			fireInsertionEvent(new ListDataEvent(this, ListDataEvent.INTERVAL_ADDED, 0,
					backing.size() - 1));
		} else {
			currentSearch = term;
			final ListDataEvent removeEvent =
					new ListDataEvent(this, ListDataEvent.INTERVAL_REMOVED, 0,
							Integer.max(0, backing.size() - 1));
			backing.clear();
			fireRemovalEvent(removeEvent);
			final String lowered = term.toLowerCase();
			db.getDances().stream()
					.filter((dance) -> dance.getName().toLowerCase().contains(lowered))
					.forEach(backing::add);
			fireInsertionEvent(new ListDataEvent(this, ListDataEvent.INTERVAL_ADDED, 0,
					Integer.max(0, backing.size() - 1)));
		}
	}

	@Override
	public int getSize() {
		return backing.size();
	}

	@Override
	public DanceRow getElementAt(final int index) {
		return backing.get(index);
	}

	@Override
	public void addListDataListener(final ListDataListener listener) {
		listeners.add(listener);
	}

	@Override
	public void removeListDataListener(final ListDataListener listener) {
		listeners.remove(listener);
	}

	private void fireRemovalEvent(final ListDataEvent event) {
		for (final ListDataListener listener : listeners) {
			listener.intervalRemoved(event);
		}
	}

	private void fireInsertionEvent(final ListDataEvent event) {
		for (final ListDataListener listener : listeners) {
			listener.intervalAdded(event);
		}
	}
}

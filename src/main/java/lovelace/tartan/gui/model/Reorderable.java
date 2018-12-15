package lovelace.tartan.gui.model;

/**
 * An interface for list-like things that can have elements moved within them.
 *
 * @author Jonathan Lovelace
 */
public interface Reorderable {
	/**
	 * Move a row of a list or table from one position to another.
	 * @param fromIndex the index to remove from
	 * @param toIndex the index (*before* removing the item!) to move it to
	 */
	void reorder(final int fromIndex, final int toIndex);
}

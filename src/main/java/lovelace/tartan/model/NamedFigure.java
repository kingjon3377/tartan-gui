package lovelace.tartan.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A "named figure" in a dance: a series of figures that should be grouped together and
 * further indented from the rest of the dance.
 *
 * @author Jonathan Lovelace
 */
public final class NamedFigure implements DanceMember, FigureParent {
	/**
	 * The list of sub-figures.
	 */
	private final List<NamedFigureMember> contents;

	/**
	 * Constructor.
	 */
	public NamedFigure(final NamedFigureMember... initialContents) {
		contents = new ArrayList<>(initialContents.length);
		Collections.addAll(contents, initialContents);
	}

	/**
	 * @return the list of sub-figures
	 */
	public List<NamedFigureMember> getContents() {
		return contents;
	}

	/**
	 * TODO: Check this: I suspect Java's ArrayList's toString() is a lot less nice than
	 * Ceylon's.
	 *
	 * @return a String representation
	 */
	@Override
	public String toString() {
		return "Named figure " + contents;
	}

	/**
	 * @param other an object
	 * @return whether it is an identical named-figure or not
	 */
	@Override
	public boolean equals(final Object other) {
		return other instanceof NamedFigure &&
			contents.equals(((NamedFigure) other).contents);
	}

	/**
	 * @return a hash value for this object
	 */
	@Override
	public int hashCode() {
		return contents.hashCode();
	}
}

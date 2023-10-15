package lovelace.tartan.model;

import java.util.Objects;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A movement in a dance.
 *
 * @author Jonathan Lovelace
 */
public final class Figure implements NamedFigureMember, DanceMember {
	/**
	 * The description of the figure.
	 */
	private @NotNull String description;

	/**
	 * The bars on which this figure is danced, if any is specified.
	 */
	private @Nullable String bars;

	/**
	 * @return The description of the figure.
	 */
	public @NotNull String getDescription() {
		return description;
	}

	/**
	 * @param description The new description of the figure.
	 */
	public void setDescription(final @NotNull String description) {
		this.description = description;
	}

	/**
	 * @return The bars on which this figure is danced, if any were specified, or
	 * otherwise null.
	 */
	public @Nullable String getBars() {
		return bars;
	}

	/**
	 * @param bars The bars on which this figure is to be danced, or null if not
	 *             specified.
	 */
	public void setBars(final @Nullable String bars) {
		this.bars = bars;
	}

	/**
	 * Constructor.
	 *
	 * @param description The description of the figure.
	 * @param bars        The bars on which this figure is danced.
	 */
	public Figure(final @NotNull String description, final @Nullable String bars) {
		this.description = description;
		this.bars = bars;
	}

	/**
	 * Constructor not specifying bars.
	 *
	 * @param description The description of the figure.
	 */
	public Figure(final @NotNull String description) {
		this.description = description;
		this.bars = null;
	}

	/**
	 * A String representation of the figure for use in the GUI.
	 */
	@Override
	public String toString() {
		final @Nullable String localBars = bars;
		if (localBars == null) {
			return description;
		} else {
			return String.format("%s: %s", bars, description);
		}
	}

	/**
	 * @param other another object
	 * @return whether it is the same as this
	 */
	@Override
	public boolean equals(final Object other) {
		if (other instanceof Figure that) {
			return Objects.equals(bars, that.bars) && description.equals(that.description);
		} else {
			return false;
		}
	}

	/**
	 * @return a hash value to use for this object
	 */
	@Override
	public int hashCode() {
		return description.hashCode() + 31 * Objects.hashCode(bars);
	}
}

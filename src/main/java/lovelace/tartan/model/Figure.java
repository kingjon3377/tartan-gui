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
	 * @return The description of the figure.
	 */
	@NotNull
	public String getDescription() {
		return description;
	}

	/**
	 * @param description The new description of the figure.
	 */
	public void setDescription(@NotNull final String description) {
		this.description = description;
	}

	/**
	 * @return The bars on which this figure is danced, if any were specified, or
	 * otherwise null.
	 */
	@Nullable
	public String getBars() {
		return bars;
	}

	/**
	 * @param bars The bars on which this figure is to be danced, or null if not
	 *             specified.
	 */
	public void setBars(@Nullable final String bars) {
		this.bars = bars;
	}

	/**
	 * The description of the figure.
	 */
	@NotNull
	private String description;

	/**
	 * The bars on which this figure is danced, if any is specified.
	 */
	@Nullable
	private String bars;

	/**
	 * Constructor.
	 *
	 * @param description The description of the figure.
	 * @param bars        The bars on which this figure is danced.
	 */
	public Figure(@NotNull final String description, @Nullable final String bars) {
		this.description = description;
		this.bars = bars;
	}

	/**
	 * Constructor not specifying bars.
	 *
	 * @param description The description of the figure.
	 */
	public Figure(@NotNull final String description) {
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
		if (other instanceof Figure) {
			final Figure that = (Figure) other;
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

package lovelace.tartan.model;

import java.util.Optional;
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
		return bars.orElse(null);
	}

	/**
	 * @param bars The bars on which this figure is to be danced, or null if not
	 *             specified.
	 */
	public void setBars(@Nullable String bars) {
		this.bars = Optional.ofNullable(bars);
	}

	/**
	 * The description of the figure.
	 */
	@NotNull
	private String description;

	/**
	 * The bars on which this figure is danced, if any is specified.
	 */
	@NotNull
	private Optional<String> bars;

	/**
	 * Constructor.
	 *
	 * @param description The description of the figure.
	 * @param bars        The bars on which this figure is danced.
	 */
	public Figure(String description, String bars) {
		this.description = description;
		this.bars = Optional.ofNullable(bars);
	}

	/**
	 * Constructor not specifying bars.
	 *
	 * @param description The description of the figure.
	 */
	public Figure(String description) {
		this.description = description;
		this.bars = Optional.empty();
	}

	/**
	 * A String representation of the figure for use in the GUI.
	 */
	@Override
	public String toString() {
		final Optional<String> localBars = bars;
		if (localBars.isPresent()) {
			return String.format("%s: %s", bars.get(), description);
		} else {
			return description;
		}
	}

	/**
	 * @param other another object
	 * @return whether it is the same as this
	 */
	@Override
	public boolean equals(Object other) {
		if (other instanceof Figure) {
			final Figure that = (Figure) other;
			return bars.equals(that.bars) && description.equals(that.description);
		} else {
			return false;
		}
	}

	/**
	 * @return a hash value to use for this object
	 */
	@Override
	public int hashCode() {
		return description.hashCode() + 31 * bars.hashCode();
	}
}

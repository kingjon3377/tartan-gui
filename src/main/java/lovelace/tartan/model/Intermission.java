package lovelace.tartan.model;

import org.jetbrains.annotations.NotNull;

/**
 * A break between dances.
 *
 * @author Jonathan Lovelace
 */
public class Intermission implements ProgramElement {
	/**
	 * How to describe the break in the program.
	 */
	private @NotNull String description;

	/**
	 * @return how to describe the break in the program
	 */
	public @NotNull String getDescription() {
		return description;
	}

	/**
	 * @param description how to describe the break in the program
	 */
	public void setDescription(final @NotNull String description) {
		this.description = description;
	}

	/**
	 * Constructor.
	 *
	 * @param description how to describe the break in the program
	 */
	public Intermission(final @NotNull String description) {
		this.description = description;
	}

	/**
	 * Constructor setting description to "Intermission".
	 */
	public Intermission() {
		this("Intermission");
	}

	/**
	 * @return the description of this break
	 */
	@Override
	public String toString() {
		return description;
	}

	/**
	 * @param other another object
	 * @return whether it is identical to this oe
	 */
	@Override
	public boolean equals(final Object other) {
		return other instanceof Intermission &&
					   description.equals(((Intermission) other).description);
	}

	/**
	 * @return a hash value for this object
	 */
	@Override
	public int hashCode() {
		return description.hashCode();
	}
}

package lovelace.tartan.model;

/**
 * A break between dances.
 *
 * @author Jonathan Lovelace
 */
public class Intermission implements ProgramElement {
	/**
	 * How to describe the break in the program.
	 */
	private String description;

	/**
	 * @return how to describe the break in the program
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description how to describe the break in the program
	 */
	public void setDescription(final String description) {
		this.description = description;
	}

	/**
	 * Constructor.
	 *
	 * @param description how to describe the break in the program
	 */
	public Intermission(final String description) {
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
	 * @param obj another object
	 * @return whether it is identical to this oe
	 */
	@Override
	public boolean equals(final Object obj) {
		return obj instanceof Intermission &&
			description.equals(((Intermission) obj).description);
	}

	/**
	 * @return a hash value for this object
	 */
	@Override
	public int hashCode() {
		return description.hashCode();
	}
}

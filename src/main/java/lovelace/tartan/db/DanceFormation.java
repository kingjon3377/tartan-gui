package lovelace.tartan.db;

/**
 * A formation that a dance can be danced in.
 *
 * @param id           The number that identifies this formation in the database.
 * @param name         The unabbreviated "name" of this formation.
 * @param abbreviation The "name" of this formation, in abbreviated form.
 * @author Jonathan Lovelace
 */
public record DanceFormation(Integer id, String name, String abbreviation) {
	/**
	 * A singleton "unknown" formation.
	 */
	public static final DanceFormation UNKNOWN =
			new DanceFormation(-1, "Unknown", "?");

	/**
	 * @return the unabbreviated "name" of this formation
	 */
	@Override
	public String name() {
		return name;
	}

	/**
	 * The "name" of this formation, in abbreviated form
	 */
	@Override
	public String abbreviation() {
		return abbreviation;
	}

	@Override
	public String toString() {
		return "%s (%s)".formatted(name, abbreviation);
	}
}

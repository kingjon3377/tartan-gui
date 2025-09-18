package lovelace.tartan.db;

/**
 * An implementation of {@link DanceProgression}.
 *
 * @param id   The number identifying this progression in the database.
 * @param name A brief textual description of this progression.
 * @author Jonathan Lovelace
 */
public record DanceProgressionImpl(int id, String name)
	implements DanceProgression {
	/**
	 * A singleton for cases where the database is missing progression information.
	 */
	public static final DanceProgression UNKNOWN =
		new DanceProgressionImpl(-1, "Unknown");

	/**
	 * @return the number identifying this progression in the database
	 */
	@Override
	public int id() {
		return id;
	}

	/**
	 * @return a brief textual description of this progression
	 */
	@Override
	public String name() {
		return name;
	}

}

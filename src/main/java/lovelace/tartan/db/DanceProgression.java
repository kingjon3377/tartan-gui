package lovelace.tartan.db;

/**
 * How couples (or dancers) "progress" from one round of the dance to the next.
 *
 * @param id   The number identifying this progression in the database.
 * @param name A brief textual description of this progression.
 * @author Jonathan Lovelace
 */
public record DanceProgression(int id, String name) {
	/**
	 * A singleton for cases where the database is missing progression information.
	 */
	public static final DanceProgression UNKNOWN =
		new DanceProgression(-1, "Unknown");
	/**
	 * @return whether this dance is danced only once through
	 */
	public boolean isOnlyOnce() {
		return "OnceOnly".equals(name);
	}
}

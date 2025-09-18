package lovelace.tartan.db;

/**
 * A formation that a dance can be danced in.
 *
 * @author Jonathan Lovelace
 */
public interface DanceFormation {
	/**
	 * @return the number that identifies this formation in the database.
	 */
	int getId();

	/**
	 * @return the unabbreviated "name" of this formation
	 */
	String getName();

	/**
	 * The "name" of this formation, in abbreviated form
	 */
	String getAbbreviation();
}

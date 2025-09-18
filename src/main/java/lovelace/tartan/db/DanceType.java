package lovelace.tartan.db;

/**
 * A type of dance. With vanishingly-rare exceptions, all dances in the database are jigs,
 * reels, strathspeys, or medleys, but it's conceivable that the database's IDs for these
 * would change.
 *
 * @author Jonathan Lovelace
 */
public interface DanceType {
	/**
	 * @return the number identifying this type in the database
	 */
	int id();

	/**
	 * @return the name of this type of dance
	 */
	String name();
	/**
	 * All dance types in the database as of this writing are single characters, but the
	 * database schema allows up to four.
	 * @return the short abbreviation of this type.
	 */
	String abbreviation();
}

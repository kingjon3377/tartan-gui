package lovelace.tartan.db;

/**
 * A type of dance. With vanishingly-rare exceptions, all dances in the database are jigs,
 * reels, strathspeys, or medleys, but it's conceivable that the database's IDs for these
 * would change.
 *
 * @param id           The number identifying this type in the database.
 * @param name         The name of this type of dance.
 * @param abbreviation The short abbreviation of this type. All dance types in the
 *                     database as of this writing are single characters, but the database
 *                     schema allows up to four.
 * @author Jonathan Lovelace
 */
public record DanceType(int id, String name, String abbreviation) {
	/**
	 * A singleton for cases where the database doesn't specify a type, if such exist.
	 */
	public static final DanceType UNKNOWN = new DanceType(-1, "Unknown", "?");
}

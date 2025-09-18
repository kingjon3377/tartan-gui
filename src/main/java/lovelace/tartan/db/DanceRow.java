package lovelace.tartan.db;

/**
 * A dance in the database.
 *
 * @author Jonathan Lovelace
 */
public interface DanceRow {
	/**
	 * This dance's ID in the database.
	 */
	int id();
	/**
	 * The name of this dance.
	 */
	String name();
	/**
	 * How many bars per time through the dance.
	 */
	int length();
	/**
	 * What shape of set the dance is danced in.
	 */
	DanceFormation shape();
	/**
	 * What type of dance this is.
	 */
	DanceType type();
	/**
	 * How many couples dance in this dance.
	 */
	int couples();
	/**
	 * The source for this dance.
	 */
	String source();
	/**
	 * The progression used between times through the dance.
	 */
	DanceProgression progression();
}

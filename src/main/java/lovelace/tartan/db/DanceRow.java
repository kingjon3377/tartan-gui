package lovelace.tartan.db;

import org.jetbrains.annotations.NotNull;

/**
 * A dance in the database.
 *
 * @author Jonathan Lovelace
 */
public interface DanceRow {
	/**
	 * This dance's ID in the database.
	 */
	int getId();
	/**
	 * The name of this dance.
	 */
	@NotNull
	String getName();
	/**
	 * How many bars per time through the dance.
	 */
	int getLength();
	/**
	 * What shape of set the dance is danced in.
	 */
	@NotNull
	DanceFormation getShape();
	/**
	 * What type of dance this is.
	 */
	@NotNull
	DanceType getType();
	/**
	 * How many couples dance in this dance.
	 */
	int getCouples();
	/**
	 * The source for this dance.
	 */
	@NotNull
	String getSource();
	/**
	 * The progression used between times through the dance.
	 */
	@NotNull
	DanceProgression getProgression();
}

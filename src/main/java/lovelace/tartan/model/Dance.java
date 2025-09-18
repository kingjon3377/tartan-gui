package lovelace.tartan.model;

import java.util.List;

/**
 * A dance on the program. (Interface.) TODO: split out "MutableDance"?
 *
 * @author Jonathan Lovelace
 */
public interface Dance extends ProgramElement, FigureParent {
	/**
	 * @return The title or name of the dance.
	 */
	String getTitle();

	/**
	 * @param title The new title or name of the dance.
	 */
	void setTitle(String title);

	/**
	 * @return The source from which the dance is taken, or the name of its deviser.
	 */
	String getSource();

	/**
	 * @param source The source from which the dance is now said to be taken, or the name
	 *               of its deviser.
	 */
	void setSource(String source);

	/**
	 * @return The tempo of the dance: jig, reel, strathspey, or medley.
	 */
	String getTempo();

	/**
	 * @param tempo The tempo of the dance: jig, reel, strathspey, or medley.
	 */
	void setTempo(String tempo);

	/**
	 * @return How many times through the dance is danced.
	 */
	int getTimes();

	/**
	 * @param times How many times through the dance is danced.
	 */
	void setTimes(int times);

	/**
	 * @return How many bars of music long each time through the dance is.
	 */
	int getLength();

	/**
	 * @param length How many bars of music long each time through the dance is.
	 */
	void setLength(int length);

	/**
	 * @return The formation in which the dance is danced: "2C (4C set)", "Sq. Set", "3C
	 * set", etc.
	 */
	String getFormation();

	/**
	 * @param formation the formation in which the dance is to be danced
	 */
	void setFormation(String formation);

	/**
	 * @return the list of figures etc. that make up the dance
	 */
	List<DanceMember> getContents();
}

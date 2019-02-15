package lovelace.tartan.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import org.jetbrains.annotations.NotNull;

/**
 * A dance on the program.
 *
 * @author Jonathan Lovelace
 */
public class Dance implements ProgramElement, FigureParent {
	/**
	 * @return The title or name of the dance.
	 */
	@NotNull
	public String getTitle() {
		return title;
	}

	/**
	 * @param title The new title or name of the dance.
	 */
	public void setTitle(final @NotNull String title) {
		this.title = title;
	}

	/**
	 * @return The source from which the dance is taken, or the name of its deviser.
	 */
	@NotNull
	public String getSource() {
		return source;
	}

	/**
	 * @param source The source from which the dance is now said to be taken, or the name
	 *               of its deviser.
	 */
	public void setSource(@NotNull final String source) {
		this.source = source;
	}

	/**
	 * @return The tempo of the dance: jig, reel, strathspey, or medley.
	 */
	@NotNull
	public String getTempo() {
		return tempo;
	}

	/**
	 * @param tempo The tempo of the dance: jig, reel, strathspey, or medley.
	 */
	public void setTempo(@NotNull final String tempo) {
		this.tempo = tempo;
	}

	/**
	 * @return How many times through the dance is danced.
	 */
	public int getTimes() {
		return times;
	}

	/**
	 * @param times How many times through the dance is danced.
	 */
	public void setTimes(final int times) {
		this.times = times;
	}

	/**
	 * @return How many bars of music long each time through the dance is.
	 */
	public int getLength() {
		return length;
	}

	/**
	 * @param length How many bars of music long each time through the dance is.
	 */
	public void setLength(final int length) {
		this.length = length;
	}

	/**
	 * @return The formation in which the dance is danced: "2C (4C set)", "Sq. Set", "3C
	 * set", etc.
	 */
	@NotNull
	public String getFormation() {
		return formation;
	}

	/**
	 * @param formation the formation in which the dance is to be danced
	 */
	public void setFormation(@NotNull final String formation) {
		this.formation = formation;
	}

	/**
	 * @return the list of figures etc. that make up the dance
	 */
	@NotNull
	public List<@NotNull DanceMember> getContents() {
		return contents;
	}

	/**
	 * The title or name of the dance.
	 */
	@NotNull
	private String title;
	/**
	 * The source from which the dance is taken, or the name of its deviser.
	 */
	@NotNull
	private String source;
	/**
	 * The tempo of the dance: jig, reel, strathspey, or medley.
	 *
	 * TODO: make an Enum of cases instead of allowing arbitrary text.
	 */
	@NotNull
	private String tempo;
	/**
	 * How many times through the dance is danced.
	 */
	private int times;
	/**
	 * How many bars of music long each time through the dance is.
	 */
	private int length;
	/**
	 * The formation in which the dance is danced: "2C (4C set)", "Sq. Set", "3C set",
	 * etc.
	 */
	@NotNull
	private String formation;
	/**
	 * The figures (some of which may be "named figures") that make up the dance, and any
	 * other text that needs to be printed inside the "scdance" environment in the
	 * output.
	 */
	private final List<@NotNull DanceMember> contents = new ArrayList<>();

	/**
	 * Constructor.
	 */
	public Dance(@NotNull final String title, @NotNull final String source,
				 @NotNull final String tempo, final int times, final int length,
				 @NotNull final String formation,
				 @NotNull final DanceMember... initialContents) {
		this.title = title;
		this.source = source;
		this.tempo = tempo;
		this.times = times;
		this.length = length;
		this.formation = formation;
		contents.addAll(Arrays.asList(initialContents));
	}

	/**
	 * @return a (very simple) String representation of the dance (for use in lists).
	 */
	@Override
	@NotNull
	public String toString() {
		return String.format("%s (%dx%d%s) (%s)", title, times, length, tempo, source);
	}

	/**
	 * @param other an object
	 * @return whether it is an identical dance.
	 */
	@Override
	public boolean equals(final Object other) {
		if (other instanceof Dance) {
			final Dance that = (Dance) other;
			return title.equals(that.title) && source.equals(that.source) &&
						   tempo.equals(that.tempo) && times == that.times &&
						   length == that.length && formation.equals(that.formation) &&
						   contents.equals(that.contents);
		} else {
			return false;
		}
	}

	/**
	 * @return a hash value for this object
	 */
	@Override
	public int hashCode() {
		return Objects.hash(title, source, tempo, times, length, formation, contents);
	}
}

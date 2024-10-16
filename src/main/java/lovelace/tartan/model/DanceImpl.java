package lovelace.tartan.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import org.jetbrains.annotations.NotNull;

/**
 * A dance on the program.
 *
 * @author Jonathan Lovelace
 */
public class DanceImpl implements Dance {
	/**
	 * The title or name of the dance.
	 */
	private @NotNull String title;
	/**
	 * The source from which the dance is taken, or the name of its deviser.
	 */
	private @NotNull String source;
	/**
	 * The tempo of the dance: jig, reel, strathspey, or medley.
	 *
	 * TODO: make an Enum of cases instead of allowing arbitrary text.
	 */
	private @NotNull String tempo;
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
	private @NotNull String formation;
	/**
	 * The figures (some of which may be "named figures") that make up the dance, and any
	 * other text that needs to be printed inside the "scdance" environment in the
	 * output.
	 */
	private final List<@NotNull DanceMember> contents;

	/**
	 * @return The title or name of the dance.
	 */
	@Override
	public @NotNull String getTitle() {
		return title;
	}

	/**
	 * @param title The new title or name of the dance.
	 */
	@Override
	public void setTitle(final @NotNull String title) {
		this.title = title;
	}

	/**
	 * @return The source from which the dance is taken, or the name of its deviser.
	 */
	@Override
	public @NotNull String getSource() {
		return source;
	}

	/**
	 * @param source The source from which the dance is now said to be taken, or the name
	 *               of its deviser.
	 */
	@Override
	public void setSource(final @NotNull String source) {
		this.source = source;
	}

	/**
	 * @return The tempo of the dance: jig, reel, strathspey, or medley.
	 */
	@Override
	public @NotNull String getTempo() {
		return tempo;
	}

	/**
	 * @param tempo The tempo of the dance: jig, reel, strathspey, or medley.
	 */
	@Override
	public void setTempo(final @NotNull String tempo) {
		this.tempo = tempo;
	}

	/**
	 * @return How many times through the dance is danced.
	 */
	@Override
	public int getTimes() {
		return times;
	}

	/**
	 * @param times How many times through the dance is danced.
	 */
	@Override
	public void setTimes(final int times) {
		this.times = times;
	}

	/**
	 * @return How many bars of music long each time through the dance is.
	 */
	@Override
	public int getLength() {
		return length;
	}

	/**
	 * @param length How many bars of music long each time through the dance is.
	 */
	@Override
	public void setLength(final int length) {
		this.length = length;
	}

	/**
	 * @return The formation in which the dance is danced: "2C (4C set)", "Sq. Set", "3C
	 * set", etc.
	 */
	@Override
	public @NotNull String getFormation() {
		return formation;
	}

	/**
	 * @param formation the formation in which the dance is to be danced
	 */
	@Override
	public void setFormation(final @NotNull String formation) {
		this.formation = formation;
	}

	/**
	 * @return the list of figures etc. that make up the dance
	 */
	@Override
	public @NotNull List<@NotNull DanceMember> getContents() {
		return contents;
	}

	/**
	 * Constructor.
	 */
	public DanceImpl(final @NotNull String title, final @NotNull String source,
	                 final @NotNull String tempo, final int times, final int length,
	                 final @NotNull String formation,
	                 final @NotNull DanceMember... initialContents) {
		this.title = title;
		this.source = source;
		this.tempo = tempo;
		this.times = times;
		this.length = length;
		this.formation = formation;
		contents = new ArrayList<>(initialContents.length);
		Collections.addAll(contents, initialContents);
	}

	/**
	 * @return a (very simple) String representation of the dance (for use in lists).
	 */
	@Override
	public @NotNull String toString() {
		return String.format("%s (%dx%d%s) (%s)", title, times, length, tempo, source);
	}

	/**
	 * @param other an object
	 * @return whether it is an identical dance.
	 */
	@Override
	public boolean equals(final Object other) {
		if (other instanceof final Dance that) {
			return title.equals(that.getTitle()) && source.equals(that.getSource()) &&
				tempo.equals(that.getTempo()) && times == that.getTimes() &&
				length == that.getLength() && formation.equals(that.getFormation()) &&
				contents.equals(that.getContents());
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

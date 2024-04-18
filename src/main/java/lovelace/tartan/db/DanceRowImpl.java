package lovelace.tartan.db;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * An implementation of {@link DanceRow}.
 *
 * @param id          This dance's ID in the database.
 * @param name        The name of this dance.
 * @param length      How many bars per time through the dance.
 * @param shape       What shape of set the dance is danced in.
 * @param type        What type of dance this is.
 * @param couples     How many couples dance in this dance.
 * @param source      The source for this dance.
 * @param progression The progression used between times through the dance.
 * @author Jonathan Lovelace
 */
public record DanceRowImpl(int id, @NotNull String name, int length,
						   @NotNull DanceFormation shape, @NotNull DanceType type,
						   int couples, @NotNull String source,
						   @NotNull DanceProgression progression) implements DanceRow {
	/**
	 * This dance's ID in the database.
	 */
	@Override
	public int id() {
		return id;
	}

	/**
	 * The name of this dance.
	 */
	@Override
	public @NotNull String name() {
		return name;
	}

	/**
	 * How many bars per time through the dance.
	 */
	@Override
	public int length() {
		return length;
	}

	/**
	 * What shape of set the dance is danced in.
	 */
	@Override
	public @NotNull DanceFormation shape() {
		return shape;
	}

	/**
	 * What type of dance this is.
	 */
	@Override
	public @NotNull DanceType type() {
		return type;
	}

	/**
	 * How many couples dance in this dance.
	 */
	@Override
	public int couples() {
		return couples;
	}

	/**
	 * The source for this dance.
	 */
	@Override
	public @NotNull String source() {
		return source;
	}

	/**
	 * The progression used between times through the dance.
	 */
	@Override
	public @NotNull DanceProgression progression() {
		return progression;
	}

	/**
	 * Constructor.
	 *
	 * @param id          This dance's ID in the database
	 * @param name        The name of this dance
	 * @param length      How many bars per time through the dance
	 * @param shape       What shape of set the dance is danced in
	 * @param type        What type of dance this is
	 * @param couples     How many couples dance in this dance
	 * @param source      Who devised the dance, or what source it is taken from
	 * @param progression The progression used between times through the dance
	 */
	public DanceRowImpl {
	}

	@Override
	public String toString() {
		return String.format("%s is a %d-bar %s for %d couples in a %s, from \"%s\".",
			name, length, type.name(), couples, shape.getName(), source);
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj instanceof DanceRow that) {
			return id == that.id() && name.equals(that.name()) &&
				length == that.length() && shape.equals(that.shape()) &&
				type.equals(that.type()) && couples == that.couples() &&
				source.equals(that.source()) &&
				progression.equals(that.progression());
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, name, length, shape, type, couples, source, progression);
	}
}

package lovelace.tartan.db;

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
public record DanceRowImpl(int id, String name, int length,
                           DanceFormation shape, DanceType type,
                           int couples, String source,
                           DanceProgression progression) implements DanceRow {
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
	public String name() {
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
	public DanceFormation shape() {
		return shape;
	}

	/**
	 * What type of dance this is.
	 */
	@Override
	public DanceType type() {
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
	public String source() {
		return source;
	}

	/**
	 * The progression used between times through the dance.
	 */
	@Override
	public DanceProgression progression() {
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
		return "%s is a %d-bar %s for %d couples in a %s, from \"%s\".".formatted(
			name, length, type.name(), couples, shape.name(), source);
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj instanceof final DanceRow row) {
			//noinspection OverlyComplexBooleanExpression
			return id == row.id() && name.equals(row.name()) &&
				length == row.length() && shape.equals(row.shape()) &&
				type.equals(row.type()) && couples == row.couples() &&
				source.equals(row.source()) &&
				progression.equals(row.progression());
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, name, length, shape, type, couples, source, progression);
	}
}

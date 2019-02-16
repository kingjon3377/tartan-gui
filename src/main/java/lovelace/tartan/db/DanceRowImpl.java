package lovelace.tartan.db;

import java.util.Objects;
import org.jetbrains.annotations.NotNull;

/**
 * An implementation of {@link DanceRow}.
 *
 * @author Jonathan Lovelace
 */
public final class DanceRowImpl implements DanceRow {
	/**
	 * This dance's ID in the database.
	 */
	private final int id;
	/**
	 * The name of this dance.
	 */
	private final @NotNull String name;
	/**
	 * How many bars per time through the dance.
	 */
	private final int length;
	/**
	 * What shape of set the dance is danced in.
	 */
	private final @NotNull DanceFormation shape;
	/**
	 * What type of dance this is.
	 */
	private final @NotNull DanceType type;
	/**
	 * How many couples dance in this dance.
	 */
	private final int couples;
	/**
	 * The source for this dance.
	 */
	private final @NotNull String source;
	/**
	 * The progression used between times through the dance.
	 */
	private final @NotNull DanceProgression progression;

	/**
	 * This dance's ID in the database.
	 */
	@Override
	public int getId() {
		return id;
	}

	/**
	 * The name of this dance.
	 */
	@Override
	public @NotNull String getName() {
		return name;
	}

	/**
	 * How many bars per time through the dance.
	 */
	@Override
	public int getLength() {
		return length;
	}

	/**
	 * What shape of set the dance is danced in.
	 */
	@Override
	public @NotNull DanceFormation getShape() {
		return shape;
	}

	/**
	 * What type of dance this is.
	 */
	@Override
	public @NotNull DanceType getType() {
		return type;
	}

	/**
	 * How many couples dance in this dance.
	 */
	@Override
	public int getCouples() {
		return couples;
	}

	/**
	 * The source for this dance.
	 */
	@Override
	public @NotNull String getSource() {
		return source;
	}

	/**
	 * The progression used between times through the dance.
	 */
	@Override
	public @NotNull DanceProgression getProgression() {
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
	public DanceRowImpl(final int id, final @NotNull String name, final int length,
						final @NotNull DanceFormation shape,
						final @NotNull DanceType type, final int couples,
						final @NotNull String source,
						final @NotNull DanceProgression progression) {
		this.id = id;
		this.name = name;
		this.length = length;
		this.shape = shape;
		this.type = type;
		this.couples = couples;
		this.source = source;
		this.progression = progression;
	}

	@Override
	public String toString() {
		return String.format("%s is a %d-bar %s for %d couples in a %s, from \"%s\".",
				name, length, type.getName(), couples, shape.getName(), source);
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj instanceof DanceRow) {
			final DanceRow that = (DanceRow) obj;
			return id == that.getId() && name.equals(that.getName()) &&
						   length == that.getLength() && shape.equals(that.getShape()) &&
						   type.equals(that.getType()) && couples == that.getCouples() &&
						   source.equals(that.getSource()) &&
						   progression.equals(that.getProgression());
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, name, length, shape, type, couples, source, progression);
	}
}

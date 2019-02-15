package lovelace.tartan.db;

import java.util.Objects;
import org.jetbrains.annotations.NotNull;

/**
 * An implementation of {@link DanceFormation}
 *
 * @author Jonathan Lovelace
 */
public class DanceFormationImpl implements DanceFormation {
	/**
	 * The number that identifies this formation in the database.
	 */
	private final Integer id;
	/**
	 * The unabbreviated "name" of this formation.
	 */
	@NotNull
	private final String name;
	/**
	 * The "name" of this formation, in abbreviated form.
	 */
	@NotNull
	private final String abbreviation;

	/**
	 * @return the number that identifies this formation in the database.
	 */
	@Override
	public int getId() {
		return id;
	}

	/**
	 * @return the unabbreviated "name" of this formation
	 */
	@Override
	public @NotNull String getName() {
		return name;
	}

	/**
	 * The "name" of this formation, in abbreviated form
	 */
	@Override
	public @NotNull String getAbbreviation() {
		return abbreviation;
	}

	/**
	 * Constructor.
	 *
	 * @param id           the number identifying this formation in the database
	 * @param name         the unabbreviated "name" of this formation
	 * @param abbreviation the "name" of this formation, in abbreviated form
	 */
	public DanceFormationImpl(final int id, @NotNull final String name,
							  @NotNull final String abbreviation) {
		this.id = id;
		this.name = name;
		this.abbreviation = abbreviation;
	}

	/**
	 * A singleton "unknown" formation.
	 */
	public static final DanceFormation UNKNOWN =
			new DanceFormationImpl(-1, "Unknown", "?");

	@Override
	public boolean equals(final Object obj) {
		if (obj instanceof DanceFormation) {
			final DanceFormation formation = (DanceFormation) obj;
			return id == formation.getId() && name.equals(formation.getName()) &&
						   abbreviation.equals(formation.getAbbreviation());
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, name, abbreviation);
	}
}

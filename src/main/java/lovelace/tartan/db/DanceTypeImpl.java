package lovelace.tartan.db;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * An implementation of {@link DanceType}.
 *
 * @param id           The number identifying this type in the database.
 * @param name         The name of this type of dance.
 * @param abbreviation The short abbreviation of this type. All dance types in the
 *                     database as of this writing are single characters, but the database
 *                     schema allows up to four.
 * @author Jonathan Lovelace
 */
public record DanceTypeImpl(int id, @NotNull String name, @NotNull String abbreviation)
		implements DanceType {
	/**
	 * A singleton for cases where the database doesn't specify a type, if such exist.
	 */
	public static final DanceType UNKNOWN = new DanceTypeImpl(-1, "Unknown", "?");

	/**
	 * @return the number identifying this type in the database
	 */
	@Override
	public int id() {
		return id;
	}

	/**
	 * @return the name of this type of dance
	 */
	@Override
	public @NotNull String name() {
		return name;
	}

	/**
	 * All dance types in the database as of this writing are single characters, but the
	 * database schema allows up to four.
	 *
	 * @return the short abbreviation of this type.
	 */
	@Override
	public @NotNull String abbreviation() {
		return abbreviation;
	}

	/**
	 * Constructor.
	 *
	 * @param id           The number identifying this type in the database.
	 * @param name         The name of this type of dance.
	 * @param abbreviation The short abbreviation of this type.
	 */
	public DanceTypeImpl {
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj instanceof final DanceType type) {
			return id == type.id() && name.equals(type.name()) &&
					abbreviation.equals(type.abbreviation());
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, name, abbreviation);
	}
}

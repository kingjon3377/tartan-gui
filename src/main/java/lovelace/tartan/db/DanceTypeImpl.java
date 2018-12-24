package lovelace.tartan.db;

import java.util.Objects;
import org.jetbrains.annotations.NotNull;

/**
 * An implementation of {@link DanceType}.
 *
 * @author Jonathan Lovelace
 */
public class DanceTypeImpl implements DanceType {
	/**
	 * The number identifying this type in the database.
	 */
	private final int id;

	/**
	 * The name of this type of dance.
	 */
	@NotNull
	private final String name;

	/**
	 * The short abbreviation of this type. All dance types in the database as of this
	 * writing are single characters, but the database schema allows up to four.
	 */
	@NotNull
	private final String abbreviation;

	/**
	 * @return the number identifying this type in the database
	 */
	@Override
	public int getId() {
		return id;
	}

	/**
	 * @return the name of this type of dance
	 */
	@Override
	public @NotNull String getName() {
		return name;
	}

	/**
	 * All dance types in the database as of this writing are single characters, but the
	 * database schema allows up to four.
	 *
	 * @return the short abbreviation of this type.
	 */
	@Override
	public @NotNull String getAbbreviation() {
		return abbreviation;
	}

	/**
	 * Constructor.
	 *
	 * @param id           The number identifying this type in the database.
	 * @param name         The name of this type of dance.
	 * @param abbreviation The short abbreviation of this type.
	 */
	public DanceTypeImpl(final int id, @NotNull final String name,
						 @NotNull final String abbreviation) {
		this.id = id;
		this.name = name;
		this.abbreviation = abbreviation;
	}

	/**
	 * A singleton for cases where the database doesn't specify a type, if such exist.
	 */
	public static final DanceType UNKNOWN = new DanceTypeImpl(-1, "Unknown", "?");

	@Override
	public boolean equals(final Object obj) {
		if (obj instanceof DanceType) {
			final DanceType type = (DanceType) obj;
			return id == type.getId() && name.equals(type.getName()) &&
						   abbreviation.equals(((DanceType) obj).getAbbreviation());
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		return id + 31 * Objects.hash(name, abbreviation);
	}
}

package lovelace.tartan.db;

import org.jetbrains.annotations.NotNull;

/**
 * An implementation of {@link DanceProgression}.
 *
 * @param id   The number identifying this progression in the database.
 * @param name A brief textual description of this progression.
 * @author Jonathan Lovelace
 */
public record DanceProgressionImpl(int id, @NotNull String name)
	implements DanceProgression {
	/**
	 * A singleton for cases where the database is missing progression information.
	 */
	public static final DanceProgression UNKNOWN =
		new DanceProgressionImpl(-1, "Unknown");

	/**
	 * @return the number identifying this progression in the database
	 */
	@Override
	public int id() {
		return id;
	}

	/**
	 * @return a brief textual description of this progression
	 */
	@Override
	public @NotNull String name() {
		return name;
	}

}

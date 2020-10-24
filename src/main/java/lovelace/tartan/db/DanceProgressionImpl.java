package lovelace.tartan.db;

import org.jetbrains.annotations.NotNull;

/**
 * An implementation of {@link DanceProgression}.
 *
 * @author Jonathan Lovelace
 */
public class DanceProgressionImpl implements DanceProgression {
	/**
	 * The number identifying this progression in the database.
	 */
	private final int id;
	/**
	 * A brief textual description of this progression.
	 */
	private final @NotNull String name;

	/**
	 * A singleton for cases where the database is missing progression information.
	 */
	public static final DanceProgression UNKNOWN =
			new DanceProgressionImpl(-1, "Unknown");

	/**
	 * @return the number identifying this progression in the database
	 */
	@Override
	public int getId() {
		return id;
	}

	/**
	 * @return a brief textual description of this progression
	 */
	@Override
	public @NotNull String getName() {
		return name;
	}

	public DanceProgressionImpl(final int id, final @NotNull String name) {
		this.id = id;
		this.name = name;
	}
}

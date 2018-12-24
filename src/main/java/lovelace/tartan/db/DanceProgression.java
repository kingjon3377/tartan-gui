package lovelace.tartan.db;

import org.jetbrains.annotations.NotNull;

/**
 * How couples (or dancers) "progress" from one round of the dance to the next.
 *
 * @author Jonathan Lovelace
 */
public interface DanceProgression {
	/**
	 * @return the number identifying this progression in the database
	 */
	int getId();
	/**
	 * @return a brief textual description of this progression
	 */
	@NotNull String getName();
	/**
	 * @return whether this dance is danced only once through
	 */
	default boolean isOnlyOnce() {
		return "OnceOnly".equals(getName());
	}
}

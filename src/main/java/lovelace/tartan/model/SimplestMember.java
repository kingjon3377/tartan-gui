package lovelace.tartan.model;

import org.jetbrains.annotations.NotNull;

/**
 * A trivial wrapper around a String to make it a possible member of a named figure or a
 * dance.
 *
 * @author Jonathan Lovelace
 */
public final class SimplestMember implements NamedFigureMember, DanceMember {
	/**
	 * The string we wrap.
	 */
	private @NotNull String string;

	/**
	 * @return The string we wrap
	 */
	public @NotNull String getString() {
		return string;
	}

	/**
	 * @param string the string to now wrap
	 */
	public void setString(final @NotNull String string) {
		this.string = string;
	}

	/**
	 * @param string The string to wrap
	 */
	public SimplestMember(final @NotNull String string) {
		this.string = string;
	}

	/**
	 * @return The string we wrap.
	 */
	@Override
	public @NotNull String toString() {
		return string;
	}

	/**
	 * @param other an object
	 * @return whether it is also a NamedFigureMember and wraps an identical string.
	 */
	@Override
	public boolean equals(final Object other) {
		return other instanceof SimplestMember &&
					   string.equals(((SimplestMember) other).string);
	}

	/**
	 * @return a hash value for this object
	 */
	@Override
	public int hashCode() {
		return string.hashCode();
	}
}

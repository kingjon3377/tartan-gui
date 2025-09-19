package lovelace.util;

/**
 * A simple Pair implementation.
 *
 * @author Jonathan Lovelace
 */
@SuppressWarnings("ClassCanBeRecord")
public final class Pair<First, Second> {
	/**
	 * The first item in this pair.
	 */
	private final First first;
	/**
	 * The second item in this pair.
	 */
	private final Second second;

	/**
	 * @return the first item in this pair.
	 */
	public First getFirst() {
		return first;
	}

	/**
	 * @return the second item in this pair.
	 */
	public Second getSecond() {
		return second;
	}

	/**
	 * Constructor, private because callers will want to use the factory method.
	 */
	private Pair(final First first, final Second second) {
		this.first = first;
		this.second = second;
	}

	/**
	 * Factory method.
	 */
	public static <First, Second> Pair<First, Second> of(final First first,
														 final Second second) {
		return new Pair<>(first, second);
	}

	@Override
	public String toString() {
		return "(%s, %s)".formatted(first, second);
	}
}

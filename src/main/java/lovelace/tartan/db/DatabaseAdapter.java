package lovelace.tartan.db;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;
import lovelace.tartan.model.Dance;
import lovelace.tartan.model.Figure;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A collection of adapters from this package's database-model types and the
 * closer-to-LaTeX types of {@link lovelace.tartan.model}.
 *
 * @author Jonathan Lovelace
 */
public final class DatabaseAdapter {
	private static final Logger LOGGER =
			Logger.getLogger(DatabaseAdapter.class.getName());

	/**
	 * Do not instantiate.
	 */
	private DatabaseAdapter() {
	}

	/**
	 * Convert a dance in the database to a dance suitable to be written to LaTeX.
	 *
	 * @param dbRow the database row
	 * @param crib  the crib associated with the dance in the database, or null if none
	 *              available
	 * @return the {@link Dance} object based on the row and the crib
	 */
	public static Dance convertDance(final @NotNull DanceRow dbRow,
									 final @Nullable String crib) {
		final Dance retval =
				new Dance(dbRow.name(), dbRow.source(), dbRow.type().name(),
						timesThrough(dbRow.shape(), dbRow.progression(),
								dbRow.couples()), dbRow.length(),
						dbRow.shape().getAbbreviation());
		if (crib != null) {
			if (crib.startsWith("<table>")) {
				retval.getContents().addAll(convertHtmlCrib(crib));
				if (retval.getContents().stream().filter(Figure.class::isInstance)
							.map(Figure.class::cast).map(Figure::getDescription)
							.anyMatch((str) -> str.contains("><"))) {
					LOGGER.warning(() -> "Looks like HTML leaked in while parsing " +
						dbRow.name());
				}
			} else {
				retval.getContents().addAll(convertAceCrib(crib));
			}
		}
		return retval;
	}

	/**
	 * Deduce the number of times through based on the set size and shape, the
	 * progression, and the number of couples. TODO: Is this really right in all cases?
	 *
	 * @param shape       the shape of the set
	 * @param progression the progression type (only used to check against "OnceOnly")
	 * @param couples     the number of couples
	 * @return 1 if "OnceOnly", twice the number of couples if a longwise set longer than
	 * the number of couples, and otherwise the number of couples.
	 */
	private static int timesThrough(final DanceFormation shape,
									final DanceProgression progression,
									final Integer couples) {
		if (progression.isOnlyOnce()) {
			return 1;
		} else if (shape.getName().startsWith("Longwise -")) {
			final String abbreviation = shape.getAbbreviation();
			final char lastChar = abbreviation.charAt(abbreviation.length() - 1);
			if (Character.isDigit(lastChar)) {
				final int setSize = Character.getNumericValue(lastChar);
				if (setSize > couples) {
					return couples * 2;
				}
			}
		}
		return couples;
	}

	/**
	 * Parse a structured-text crib.
	 *
	 * @param crib the crib to parse
	 * @return the figures it represents
	 */
	private static @NotNull List<@NotNull Figure> convertAceCrib(final @NotNull String crib) {
		if (crib.startsWith("<table>")) {
			throw new IllegalArgumentException("Can't handle HTML cribs here");
		}
		@Nullable String currentBars = null;
		final List<String> split = crib.lines().map(String::trim).toList();
		final List<@NotNull Figure> retval = new ArrayList<>(split.size());
		for (final String line : split) {
			if (line.endsWith("::")) {
				currentBars = line.substring(0, line.length() - 2);
			} else {
				retval.add(new Figure(line, currentBars));
				currentBars = null;
			}
		}
		return Collections.unmodifiableList(retval);
	}

	private static @NotNull String stripFromStart(final @NotNull String string,
												  final @NotNull String pattern) {
		if (string.startsWith(pattern)) {
			return string.substring(pattern.length());
		} else {
			LOGGER.info(
					() -> String.format("'%s' didn't start with '%s'", string, pattern));
			return string;
		}
	}

	private static @NotNull String stripFromEnd(final @NotNull String string,
												final @NotNull String pattern) {
		if (string.endsWith(pattern)) {
			return string.substring(0, string.length() - pattern.length());
		} else {
			LOGGER.info(
					() -> String.format("'%s' didn't end with '%s'", string, pattern));
			return string;
		}
	}

	private static @NotNull List<@NotNull Figure> convertHtmlCrib(final @NotNull String crib) {
		final String base =
				stripFromEnd(stripFromStart(crib, "<table>"), "</table>").trim();
		final List<String> split = base.lines().map(String::trim).toList();
		final List<@NotNull Figure> retval = new ArrayList<>(split.length);
		for (final String line : split) {
			if (line.startsWith("<tr><td class=\"expl\" colspan=\"2\">")) {
				final String current = stripFromEnd(
						stripFromStart(line, "<tr><td class=\"expl\" colspan=\"2\">"),
						"</td></tr>");
				retval.add(new Figure(current, null));
			} else {
				final String current =
						stripFromEnd(stripFromStart(line, "<tr><td class=\"bars\">"),
								"</td>");
				final int firstAngleBracket = current.indexOf('<');
				final String bars = current.substring(0, firstAngleBracket);
				final String rest = current.substring(firstAngleBracket);
				retval.add(new Figure(stripFromStart(rest, "</td><td class=\"desc\">"),
						bars));
			}
		}
		return retval;
	}
}

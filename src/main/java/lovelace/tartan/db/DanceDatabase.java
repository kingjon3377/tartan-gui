package lovelace.tartan.db;

import java.nio.file.Path;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jspecify.annotations.Nullable;
import org.sqlite.SQLiteDataSource;

/**
 * A connection to (a downloaded snapshot of) the dance database.
 *
 * @author Jonathan Lovelace
 */
public class DanceDatabase implements AutoCloseable {

	private static final Logger LOGGER = Logger.getLogger(DanceDatabase.class.getName());

	private final Connection sql;

	private final List<DanceRow> dances;

	private final PreparedStatement cribStatement;

	public Collection<DanceRow> getDances() {
		return Collections.unmodifiableList(dances);
	}

	@FunctionalInterface
	private interface ResultsFunction<T> {
		T apply(ResultSet results) throws SQLException;
	}

	private static <T> Map<Integer, T> resultsToMap(final PreparedStatement statement,
	                                                final ResultsFunction<T> reader)
			throws SQLException {
		try (final ResultSet results = statement.executeQuery()) {
			final Map<Integer, T> retval = new HashMap<>(results.getFetchSize());
			while (results.next()) {
				retval.put(results.getInt("id"), reader.apply(results));
			}
			return retval;
		}
	}

	private static <T> List<T> resultsToList(final PreparedStatement statement,
	                                         final ResultsFunction<T> reader)
			throws SQLException {
		try (final ResultSet results = statement.executeQuery()) {
			final List<T> retval = new ArrayList<>(results.getFetchSize());
			while (results.next()) {
				retval.add(reader.apply(results));
			}
			return retval;
		}
	}

	@SuppressWarnings("SpellCheckingInspection")
	public DanceDatabase(final Path filename) throws SQLException {
		final SQLiteDataSource ds = new SQLiteDataSource();
		ds.setUrl("jdbc:sqlite:" + filename);
		sql = ds.getConnection();
		final Map<Integer, DanceType> typesMap =
				resultsToMap(sql.prepareStatement(
						"SELECT id, name, short_name FROM dancetype"),
						results -> new DanceTypeImpl(results.getInt("id"),
								results.getString("name"),
								results.getString("short_name")));
		final Map<Integer, DanceFormation> shapesMap =
				resultsToMap(sql.prepareStatement(
						"SELECT id, name, shortname FROM shape"),
						results -> new DanceFormation(results.getInt("id"),
								results.getString("name"),
								results.getString("shortname")));
		final Map<Integer, DanceProgression> progressionsMap =
				resultsToMap(sql.prepareStatement("SELECT id, name FROM progression"),
						results -> new DanceProgressionImpl(results.getInt("id"),
								results.getString("name")));
		dances = resultsToList(sql.prepareStatement("""
						SELECT dance.id, dance.name, dance.barsperrepeat, \
						dance.shape_id, dance.type_id, dance.couples_id, \
						publication.name AS publicationName, dance.progression_id \
						FROM dance \
						JOIN dancepublicationsmap dpm ON dance.id = dpm.dance_id \
						JOIN publication ON publication.id = dpm.publication_id"""),
				results -> parseDance(results, shapesMap, typesMap, progressionsMap));
		cribStatement = sql.prepareStatement(
				"""
						SELECT text FROM dancecrib WHERE dance_id = ? \
						ORDER BY format ASC LIMIT 1""");
	}

	private static <T> T getFromMap(final Map<Integer, T> map, final int num,
	                                final String desc, final String context,
	                                final T defaultValue) {
		if (map.containsKey(num)) {
			return map.get(num);
		} else {
			LOGGER.fine(() -> "%s for %s was SQL null or an unknown ID".formatted(desc,
					context));
			return defaultValue;
		}
	}

	@SuppressWarnings("SpellCheckingInspection")
	private static DanceRowImpl parseDance(final ResultSet danceResults,
	                                       final Map<Integer, DanceFormation> shapesMap,
	                                       final Map<Integer, DanceType> typesMap,
	                                       final Map<Integer, DanceProgression> progressionsMap)
			throws SQLException {
		final int id = danceResults.getInt("id");
		final String name = danceResults.getString("name");
		final int length = danceResults.getInt("barsperrepeat");
		final DanceFormation shape = getFromMap(shapesMap,
				danceResults.getInt("shape_id"), "Shape", name,
				DanceFormation.UNKNOWN);
		final DanceType type = getFromMap(typesMap, danceResults.getInt("type_id"),
				"Type", name, DanceTypeImpl.UNKNOWN);
		final int couplesRaw = danceResults.getInt("couples_id");
		final int couples;
		// The database helpfully uses the number of couples for the ID in that
		// table for dances that actually use couples, and then IDs in the 50s
		// for dances with one to five individuals or 2 trios, in the 90s for
		// other or unknown, and in the 100s for more complicated arrangements.
		// For our purposes, any of the cases more complicated than a simple
		// number of couples is filed under "other". This field can also be
		// NULL (returned by getInt() as 0), in which case we also use "other".
		if (couplesRaw > 0 && couplesRaw < 10) {
			couples = couplesRaw;
		} else if (couplesRaw == 0) {
			LOGGER.fine(() -> "Number of couples for %s was SQL null".formatted(name));
			couples = -1;
		} else {
			couples = -1;
		}
		final String source = danceResults.getString("publicationname");
		final DanceProgression progression = getFromMap(progressionsMap,
				danceResults.getInt("progression_id"), "Progression", name,
				DanceProgressionImpl.UNKNOWN);
		return new DanceRowImpl(id, name, length, shape, type, couples, source,
				progression);
	}

	public @Nullable String cribText(final DanceRow dance) {
		try {
			cribStatement.setInt(1, dance.id());
		} catch (final SQLException except) {
			LOGGER.log(Level.WARNING, "SQL error preparing to get crib text", except);
			return null;
		}
		try (final ResultSet result = cribStatement.executeQuery()) {
			if (result.next()) {
				return result.getString("text");
			} else {
				return null;
			}
		} catch (final SQLException except) {
			LOGGER.log(Level.WARNING, "SQL error getting crib text", except);
			return null;
		}
	}

	@Override
	public String toString() {
		return "DanceDatabase with %d dances".formatted(dances.size());
	}

	@Override
	public void close() throws SQLException {
		sql.close();
	}
}

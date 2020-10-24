package lovelace.tartan.db;

import java.nio.file.Path;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.sqlite.SQLiteDataSource;

/**
 * A connection to (a downloaded snapshot of) the dance database.
 *
 * @author Jonathan Lovelace
 */
public class DanceDatabase {

	private static final Logger LOGGER = Logger.getLogger(DanceDatabase.class.getName());

	final @NotNull Connection sql;

	final @NotNull List<DanceRow> dances = new ArrayList<>();

	final @NotNull PreparedStatement cribStatement;

	public Collection<DanceRow> getDances() {
		return dances;
	}

	public DanceDatabase(final @NotNull Path filename) throws SQLException {
		final SQLiteDataSource ds = new SQLiteDataSource();
		ds.setUrl("jdbc:sqlite:" + filename);
		sql = ds.getConnection();
		final Map<Integer, DanceType> typesMap = new HashMap<>();
		try (final PreparedStatement typesStatement = sql.prepareStatement(
				"SELECT id, name, short_name FROM dancetype");
			 final ResultSet typesResults = typesStatement.executeQuery()) {
			while (typesResults.next()) {
				final int id = typesResults.getInt("id");
				final String name = typesResults.getString("name");
				final String abbreviation = typesResults.getString("short_name");
				typesMap.put(id, new DanceTypeImpl(id, name, abbreviation));
			}
		}
		final Map<Integer, DanceFormation> shapesMap = new HashMap<>();
		try (final PreparedStatement shapesStatement = sql.prepareStatement(
				"SELECT id, name, shortname FROM shape");
			 final ResultSet shapesResults = shapesStatement.executeQuery()) {
			while (shapesResults.next()) {
				final int id = shapesResults.getInt("id");
				final String name = shapesResults.getString("name");
				final String abbreviation = shapesResults.getString("shortname");
				shapesMap.put(id, new DanceFormationImpl(id, name, abbreviation));
			}
		}
		final Map<Integer, DanceProgression> progressionsMap = new HashMap<>();
		try (final PreparedStatement progressionsStatement = sql.prepareStatement(
				"SELECT id, name FROM progression");
			 final ResultSet progressionsResults = progressionsStatement.executeQuery()) {
			while (progressionsResults.next()) {
				final int id = progressionsResults.getInt("id");
				final String name = progressionsResults.getString("name");
				progressionsMap.put(id, new DanceProgressionImpl(id, name));
			}
		}
		try (final PreparedStatement danceStatement = sql.prepareStatement(
				"SELECT dance.id, dance.name, dance.barsperrepeat, dance.shape_id, " +
						"dance" +
						".type_id, dance.couples_id, publication.name AS " +
						"publicationName, dance.progression_id FROM dance, " +
						"dancespublicationsmap, publication WHERE dance.id = " +
						"dancespublicationsmap.dance_id AND publication.id = " +
						"dancespublicationsmap.publication_id");
			 final ResultSet danceResults = danceStatement.executeQuery()) {
			while (danceResults.next()) {
				final int id = danceResults.getInt("id");
				final String name = danceResults.getString("name");
				final int length = danceResults.getInt("barsperrepeat");
				final DanceFormation shape;
				final int shapeNum = danceResults.getInt("shape_id");
				if (shapesMap.containsKey(shapeNum)) {
					shape = shapesMap.get(shapeNum);
				} else {
					LOGGER.fine(() -> String.format(
							"Shape for %s was SQL null or an unknown ID", name));
					shape = DanceFormationImpl.UNKNOWN;
				}
				final DanceType type;
				final int typeNum = danceResults.getInt("type_id");
				if (typesMap.containsKey(typeNum)) {
					type = typesMap.get(typeNum);
				} else {
					LOGGER.info(() -> String.format(
							"Type for %s was SQL null or an unknown ID", name));
					// TODO: Ceylon just asserted instead of providing a default
					type = DanceTypeImpl.UNKNOWN;
				}
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
					LOGGER.fine(
							() -> String.format("Number of couples for %s was SQL null",
									name));
					couples = -1;
				} else {
					couples = -1;
				}
				final String source = danceResults.getString("publicationname");
				final int progressionNum = danceResults.getInt("progression_id");
				final DanceProgression progression;
				if (progressionsMap.containsKey(progressionNum)) {
					progression = progressionsMap.get(progressionNum);
				} else {
					LOGGER.fine(() -> String.format(
							"Progression for %s was SQL null or an unknown ID", name));
					progression = DanceProgressionImpl.UNKNOWN;
				}
				dances.add(
						new DanceRowImpl(id, name, length, shape, type, couples, source,
								progression));
			}
		}
		cribStatement = sql.prepareStatement(
				"SELECT text FROM dancecrib WHERE dance_id = ? ORDER BY format ASC LIMIT" +
						" 1");
	}

	public @Nullable String cribText(final DanceRow dance) {
		try {
			cribStatement.setInt(1, dance.getId());
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
}

package lovelace.tartan.db;

/**
 * A dance in the database.
 *
 * @param id          This dance's ID in the database.
 * @param name        The name of this dance.
 * @param length      How many bars per time through the dance.
 * @param shape       What shape of set the dance is danced in.
 * @param type        What type of dance this is.
 * @param couples     How many couples dance in this dance.
 * @param source      The source for this dance.
 * @param progression The progression used between times through the dance.
 * @author Jonathan Lovelace
 */
public record DanceRow(int id, String name, int length,
                       DanceFormation shape, DanceType type,
                       int couples, String source,
                       DanceProgression progression) {
	@Override
	public String toString() {
		return "%s is a %d-bar %s for %d couples in a %s, from \"%s\".".formatted(
			name, length, type.name(), couples, shape.name(), source);
	}
}

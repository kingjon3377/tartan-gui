import ceylon.dbc {
    newConnectionFromDataSource,
    Sql,
    SqlNull
}
import org.sqlite {
    SQLiteDataSource
}
import java.lang {
    IllegalArgumentException
}
import ceylon.language.meta {
    type
}
"Connection to the dance database."
shared class DanceDatabase(String filename) {
    SQLiteDataSource ds = SQLiteDataSource();
    ds.url = "jdbc:sqlite:``filename``";
    Sql sql = Sql(newConnectionFromDataSource(ds));
    Map<Integer, DanceType> types = map {
        *sql.Select("select id, name, short_name from dancetype").execute()
            .map(DanceTypeImpl.fromSql).map((type) => type.id->type)
    };
    Map<Integer, DanceFormation> shapes = map {
        *sql.Select("select id, name, shortname from shape").execute()
            .map(DanceFormationImpl.fromSql).map((shape) => shape.id->shape)
    };
    Map<Integer, DanceProgression> progressions = map {
        *sql.Select("select id, name from progression").execute()
                .map(DanceProgressionImpl.fromSql).map((type) => type.id->type)
    };
    value danceStatement =
            sql.Select("SELECT dance.id, dance.name, dance.barsperrepeat, dance.shape_id,
                            dance.type_id, dance.couples_id,
                            publication.name AS publicationName,
                            dance.progression_id
                        FROM dance, dancespublicationsmap, publication
                        WHERE dance.id = dancespublicationsmap.dance_id AND
                            publication.id = dancespublicationsmap.publication_id");
    DanceRow danceRowBuilder(Map<String, Object> row) {
        assert (exists idStr = row["id"]);
        Integer id = parseSqlInt(idStr);
        assert (is String name = row["name"]);
        assert (exists lengthStr = row["barsperrepeat"]);
        Integer length = parseSqlInt(lengthStr);
        assert (exists shapeStr = row["shape_id"]);
        DanceFormation shape;
        switch (shapeNum = parseSqlNullableInt(shapeStr))
        case (is SqlNull) {
            process.writeLine("Formation for ``name`` was SQL null");
            shape = DanceFormationImpl.unknown;
        }
        case (is Integer) {
            assert (exists temp = shapes[shapeNum]);
            shape = temp;
        }
        assert (exists typeStr = row["type_id"],
            exists type = types[parseSqlInt(typeStr)]);
        assert (exists couplesStr = row["couples_id"]);
        Integer|SqlNull couplesRaw = parseSqlNullableInt(couplesStr);
        Integer couples;
        // The database helpfully uses the number of couples for the ID in that table
        // for dances that actually use couples, and then IDs in the 50s for dances with
        // one to five individuals or 2 trios, in the 90s for other or unknown, and in the
        // 100s for more complicated arrangements. For our purposes, any of the cases more
        // complicated than a simple number of couples is filed under "other". This field
        // can also be NULL, in shich case we also use "other".
        if (is SqlNull couplesRaw) {
            process.writeLine("Number of couples for ``name`` was SQL null");
            couples = -1;
        } else if (couplesRaw > 10) {
            couples = -1;
        } else {
            couples = couplesRaw;
        }
        assert (is String source = row["publicationname"]);
        assert (exists progressionStr = row["progression_id"]);
        DanceProgression progression;
        switch (progressionNum = parseSqlNullableInt(progressionStr))
        case (is SqlNull) {
            process.writeLine("Progression for ``name`` was SQL null");
            progression = DanceProgressionImpl.unknown;
        }
        case (is Integer) {
            assert (exists temp = progressions[progressionNum]);
            progression = temp;
        }
        return DanceRowImpl(id, name, length, shape, type, couples, source, progression);
    }
    value cribStatement = sql.Select("SELECT text FROM dancecrib
                                      WHERE dance_id = ? ORDER BY format ASC LIMIT 1");
    "The dances in the database."
    shared {DanceRow*} dances = danceStatement.execute().map(danceRowBuilder).sequence();
    """The "crib" text (directions) for the given dance in the database."""
    shared String? cribText(DanceRow dance) {
        if (exists row = cribStatement.execute(dance.id).first, is String retval = row["text"]) {
                return retval;
            } else {
                return null;
            }
    }
}
"A dance in the database."
shared interface DanceRow {
    "This dance's ID in the database"
    shared formal Integer id;
    "The name of this dance"
    shared formal String name;
    "How many bars per time through the dance"
    shared formal Integer length;
    "What shape of set the dance is danced in."
    shared formal DanceFormation shape;
    "What type of dance this is"
    shared formal DanceType type;
    "How many couples dance in this dance"
    shared formal Integer couples;
    "The source for the dance."
    shared formal String source;
    "The progression used between times through the dance."
    shared formal DanceProgression progression;
}
class DanceRowImpl(id, name, length, shape, type, couples, source, progression) satisfies DanceRow {
    "This dance's ID in the database"
    shared actual Integer id;
    "The name of this dance"
    shared actual String name;
    "How many bars per time through the dance"
    shared actual Integer length;
    "What shape of set the dance is danced in."
    shared actual DanceFormation shape;
    "What type of dance this is"
    shared actual DanceType type;
    "How many couples dance in this dance"
    shared actual Integer couples;
    "Who devised the dance."
    shared actual String source;
    "The progression used between times through the dance."
    shared actual DanceProgression progression;
    shared actual String string =>
            "\"``name``\" is a ``length``-bar ``type.name`` for ``couples
                `` couples in a ``shape.name``, from \"``source``\".";
}
"A formation that a dance can be danced in."
shared interface DanceFormation {
    "The number that identifies this formation in the database"
    shared formal Integer id;
    """The unabbreviated "name" of this formation"""
    shared formal String name;
    """The "name" of this formation, in abbreviated form"""
    shared formal String abbreviation;
}
"An implementation of DanceFormation."
class DanceFormationImpl satisfies DanceFormation {
    shared actual Integer id;
    shared actual String name;
    shared actual String abbreviation;
    shared new (Integer id, String name, String abbreviation) {
        this.id = id;
        this.name = name;
        this.abbreviation = abbreviation;
    }
    shared new fromSql(Map<String, Object> row) {
        assert (exists idStr = row["id"]);
        id = parseSqlInt(idStr);
        assert (is String nameStr = row["name"]);
        name = nameStr;
        assert (is String shortName = row["shortname"]);
        abbreviation = shortName;
    }
    shared new unknown extends DanceFormationImpl(-1, "Unknown", "?") {}
}
"A type of dance---with perhaps vanishingly rare exceptions, all are jigs, reels,
 strathspeys, or medleys, but it's conceivable that the database's IDs for these would
 change."
shared interface DanceType {
    "The number that identifies this type in the database"
    shared formal Integer id;
    "The name of this type of dance"
    shared formal String name;
    "The short abbreviation of this type. All in the database as of this writing are
     single characters, but the database schema allows up to four."
    shared formal String abbreviation;
}
"An implementation of [[DanceType]]."
class DanceTypeImpl satisfies DanceType {
    shared actual Integer id;
    shared actual String name;
    shared actual String abbreviation;
    shared new (Integer id, String name, String abbreviation) {
        this.id = id;
        this.name = name;
        this.abbreviation = abbreviation;
    }
    shared new fromSql(Map<String, Object> row) {
        assert (exists idStr = row["id"]);
        id = parseSqlInt(idStr);
        assert (is String nameStr = row["name"]);
        name = nameStr;
        assert (is String shortName = row["short_name"]);
        abbreviation = shortName;
    }
}
"""How couples (or dancers) "progress" from one round of the dance to the next."""
shared interface DanceProgression {
	"The number that identifies this progression in the database."
	shared formal Integer id;
	"A textual description of this progression."
	shared formal String name;
	"Whether this dance is danced only once through."
	shared Boolean onlyOnce => name == "OnceOnly";
}
"An implementation of [[DanceProgression]]"
class DanceProgressionImpl satisfies DanceProgression {
	shared actual Integer id;
	shared actual String name;
	shared new (Integer id, String name) {
		this.id = id;
		this.name = name;
	}
	shared new fromSql(Map<String, Object> row) {
		assert (exists idStr = row["id"]);
		id = parseSqlInt(idStr);
		assert (is String nameStr = row["name"]);
		name = nameStr;
	}
	shared new unknown extends DanceProgressionImpl(-1, "Unknown") {}
}
Integer parseSqlInt(Object obj) {
    if (is Integer obj) {
        return obj;
    } else if (is String obj) {
        assert (is Integer retval = Integer.parse(obj));
        return retval;
    } else {
        throw IllegalArgumentException("Got non-numeric value, a ``type(obj)``, when we expected a number");
    }
}
Integer|SqlNull parseSqlNullableInt(Object obj) {
    if (is SqlNull obj) {
        return obj;
    } else {
        return parseSqlInt(obj);
    }
}

import lovelace.tartan.model {
	Dance
}
// FIXME: Needs to get its directions too!
shared Dance convertDance(DanceRow dbRow) {
	return Dance(dbRow.name, dbRow.source, dbRow.type.name,
		timesThrough(dbRow.shape, dbRow.progression, dbRow.couples),
		dbRow.length, dbRow.shape.abbreviation);
}
// TODO: Is this really right in all cases?
Integer timesThrough(DanceFormation shape, DanceProgression progression, Integer couples) {
	if (progression.onlyOnce) {
		return 1;
	} else if (shape.name.startsWith("Longwise -"), exists lastChar = shape.abbreviation.last,
			is Integer num = Integer.parse(lastChar.string), num > couples) {
		return couples * 2;
	} else {
		return couples;
	}
}
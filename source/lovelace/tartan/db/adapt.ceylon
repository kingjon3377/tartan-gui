import lovelace.tartan.model {
	Dance,
	Figure
}
import java.lang {
	IllegalArgumentException
}
import ceylon.collection {
	ArrayList,
	MutableList
}
// FIXME: Needs to get its directions too!
shared Dance convertDance(DanceRow dbRow, String? crib) {
	Dance retval = Dance(dbRow.name, dbRow.source, dbRow.type.name,
		timesThrough(dbRow.shape, dbRow.progression, dbRow.couples),
		dbRow.length, dbRow.shape.abbreviation);
	if (exists crib, !crib.startsWith("<table>")) {
		retval.contents.addAll(convertAceCrib(crib));
	}
	return retval;
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

{Figure*} convertAceCrib(String crib) {
	if (crib.startsWith("<table>")) {
		throw IllegalArgumentException("Can't handle HTML cribs yet");
	}
	variable String? currentBars = null;
	MutableList<Figure> retval = ArrayList<Figure>();
	for (line in crib.split('\n'.equals).map(String.trimmed)) {
		if (line.endsWith("::")) {
			currentBars = line.substring(0, line.size - 2);
		} else {
			retval.add(Figure(line, currentBars));
			currentBars = null;
		}
	}
	return {*retval};
}
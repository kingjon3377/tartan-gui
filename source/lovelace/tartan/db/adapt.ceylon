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
import ceylon.logging {
	Logger,
	logger
}
Logger log = logger(`module lovelace.tartan.db`);
shared Dance convertDance(DanceRow dbRow, String? crib) {
	Dance retval = Dance(dbRow.name, dbRow.source, dbRow.type.name,
		timesThrough(dbRow.shape, dbRow.progression, dbRow.couples),
		dbRow.length, dbRow.shape.abbreviation);
	if (exists crib) {
		if (crib.startsWith("<table>")) {
			retval.contents.addAll(convertHtmlCrib(crib));
		} else {
			retval.contents.addAll(convertAceCrib(crib));
		}
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
		throw IllegalArgumentException("Can't handle HTML cribs here");
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
{Figure*} convertHtmlCrib(String crib) {
	MutableList<Figure> retval = ArrayList<Figure>();
	String stripFromStart(String string, String pattern) {
		if (!string.startsWith(pattern)) {
			log.debug("'``string``' didn't start with '``pattern``'");
		}
		return string.removeInitial(pattern);
	}
	String stripFromEnd(String string, String pattern) {
		if (!string.endsWith(pattern)) {
			log.debug("'``string``' didn't end with '``pattern``'");
		}
		return string.removeTerminal(pattern);
	}
	String base = stripFromEnd(stripFromStart(crib, "<table>"), "</table>").trimmed;
	for (line in base.split('\n'.equals).map(String.trimmed)) {
		String current = stripFromEnd(stripFromStart(line, """<tr><td class=""bars"">"""), "</td>");
		value splitted = current.split('<'.equals, true, false, 1);
		String bars = splitted.first;
		assert (exists rest = splitted.rest.first);
		retval.add(Figure(stripFromStart(rest, """/td><td class=""desc"">"""), bars));
	}
	return {*retval};
}
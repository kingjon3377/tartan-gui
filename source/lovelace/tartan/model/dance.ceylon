import ceylon.collection {
    MutableList,
    ArrayList
}
"A movement in a dance."
shared class Figure(description, bars = null) {
	"The description of the figure."
	shared variable String description;
	"The bars on which this figure is danced, if any is specified."
	shared variable String? bars;
	shared actual String string {
		if (exists temp = bars) {
			return "``temp``: ``description``";
		} else {
			return description;
		}
	}
	shared actual Boolean equals(Object that) {
		if (is Figure that) {
			if (exists ourBars = bars) {
				if (exists theirBars = that.bars) {
					return ourBars == theirBars && description == that.description;
				} else {
					return false;
				}
			} else if (!that.bars exists) {
				return description == that.description;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}
	shared actual Integer hash {
		if (exists temp = bars) {
			return description.hash + 31 * temp.hash;
		} else {
			return description.hash;
		}
	}
}

"""A "named figure" in a dance: a series of figures that should be grouped together and
   further indented from the rest of the dance."""
// TODO: Should this have an (optional) "name" and/or "bars" field?
shared class NamedFigure(<Figure|String>* initialContents) {
	"The sub-figures in this named figure, and any other text that needs to be printed."
	shared MutableList<Figure|String> contents = ArrayList { *initialContents };
	shared actual Boolean equals(Object that) {
		if (is NamedFigure that) {
			return that.contents == contents;
		} else {
			return false;
		}
	}
	shared actual Integer hash => contents.hash;
	shared actual String string => "Named figure ``contents``";
}
"A dance."
shared class Dance(title, source, tempo, times, length, formation, <Figure|NamedFigure|String>* initialContents) {
	"The title or name of the dance."
	shared variable String title;
	"The source from which the dance is taken. (Or the name of its deviser.)"
	shared variable String source;
	"The tempo of the dance: jig, reel, strathspey, or medley."
	// TODO: Make an enumerated set of cases instead of allowing arbitrary text
	shared variable String tempo;
	"How many times through the dance is danced."
	shared variable Integer times;
	"How many bars of music long each time through the dance is."
	shared variable Integer length;
	"""The formation in which the dance is danced: "2C (4C set)", "Sq. Set", "3C set",
	   etc."""
	shared variable String formation;
	"""The figures (some of which may be "named figures") that make up the dance, and any
	   other text that needs to be printed inside the "scdance" environment in the
	   output."""
	shared MutableList<Figure|NamedFigure|String> contents =
			ArrayList<Figure|NamedFigure|String> { *initialContents };
	"A (very simple) String representation of the dance (for use in lists)"
	shared actual String string => "``title`` (``times``x``length````tempo``) (``source``)";
	shared actual Boolean equals(Object that) {
		if (is Dance that) {
			return title == that.title && source == that.source && tempo == that.tempo &&
				times == that.times && length == that.length && formation == that.formation &&
				contents==that.contents;
		} else {
			return false;
		}
	}
	shared actual Integer hash {
		variable value hash = 1;
		hash = 31*hash + title.hash;
		hash = 31*hash + source.hash;
		hash = 31*hash + tempo.hash;
		hash = 31*hash + times;
		hash = 31*hash + length;
		hash = 31*hash + formation.hash;
		hash = 31*hash + contents.hash;
		return hash;
	}
}


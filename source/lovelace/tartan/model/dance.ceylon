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
}

"""A "named figure" in a dance: a series of figures that should be grouped together and
   further indented from the rest of the dance."""
shared class NamedFigure(<Figure|String>* initialContents) {
	"The sub-figures in this named figure, and any other text that needs to be printed."
	shared MutableList<Figure|String> contents = ArrayList { *initialContents };
}
"A dance."
shared class Dance(title, source, tempo, times, length, formation) {
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
			ArrayList<Figure|NamedFigure|String>();
	"A (very simple) String representation of the dance (for use in lists)"
	shared actual String string => "``title`` (``times``x``length````tempo``) (``source``)";
}


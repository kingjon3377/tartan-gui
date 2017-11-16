import lovelace.tartan.model {
	ProgramElement,
	Dance,
	Figure,
	NamedFigure,
	Intermission,
	ProgramMetadata
}
import ceylon.test {
	test,
	parameters,
	assertEquals
}
import ceylon.collection {
	ArrayList
}
[[Boolean, Boolean]+] bools = [[true, true], [true, false], [false, true], [false, false]];
test
parameters(`value bools`)
void noImageTest(Boolean titleOnCover, Boolean printALS) {
	List<ProgramElement> startingProgram = ArrayList {
		Dance("Fiddler's Choice", "M. Morgan", "Jig", 8, 32, "2C (4C set)",
			Figure("1s & 2s advance and retire and dance back to back", "1-8"),
			Figure("1s and 2s dance Right Hands across and Left Hands back", "9-16"),
			Figure("1s lead down the middle & up to 2d place (2s step up on 19-20", "17-24"),
			Figure("2s & 1s circle 4H around to the left and back", "25-32")),
		Dance("Random Reel", "A. Mouse", "Reel", 6, 40, "2C (3C set)",
			Figure("1s & 2s dance the Targe:", "1-8"),
			NamedFigure(Figure("1L & 2L turn RH 3/4 <b>while</b> Men dance 1/4 round anticlockwise", "1-2"),
				Figure("1M with 2L & 1L with 2M full turn", "3-4"),
				Figure("1L & 2L turn RH 3/4 <b>while</b> Men dance 1/4 way round anticlockwise", "5-6"),
				Figure("1M with 2L & 1L with 2M full turn", "7-8")),
			Figure("remainder of dance description here")),
		Intermission(),
		Dance("Odd Example", "Bk -1", "Strathspey", 3, 32, "3C Triangle", Figure("dance description here")),
		Dance("Unsourced Medley", "", "Medley", 1, 64, "Sq. Set", Figure("dance description here"), Figure("xyzzy")),
		Intermission("Break")
	};
	ProgramMetadata startingMetadata = ProgramMetadata();
	startingMetadata.groupCoverName = "groupCoverName";
	startingMetadata.groupTitleName = "groupTitleName";
	startingMetadata.eventCoverName = "eventCoverName";
	startingMetadata.eventTitleName = "eventTitleName";
	startingMetadata.coverDate = "coverDate";
	startingMetadata.titleDate = "titleDate";
	startingMetadata.coverLocation = "coverLocation";
	startingMetadata.titleLocation = "titleLocation";
	startingMetadata.locationAddress = "locationAddress";
	startingMetadata.titleTimes = "titleTimes
	                               secondLine";
	startingMetadata.musicians = "musicians";
	startingMetadata.titleOnCover = titleOnCover;
	startingMetadata.printAuldLangSyne = printALS;
	StringBuilder builder = StringBuilder();
	writeLaTeXProgram(builder.append, startingProgram, startingMetadata);
	String serialized = builder.string;
	assert (is [ProgramMetadata, ProgramElement*] deserializationResults = LaTeXReader().readLaTeXProgram(serialized));
	ProgramMetadata readMetadata = deserializationResults.first;
	List<ProgramElement> readProgram = ArrayList { *deserializationResults.rest };
	assertEquals(readMetadata, startingMetadata, "Metadata should be (de)serialized correctly");
	assertEquals(readProgram, startingProgram, "Dances should be (de)serialized correctly");
}
test
parameters(`value bools`)
void withImageTest(Boolean titleOnCover, Boolean printALS) {
	List<ProgramElement> startingProgram = ArrayList {
		Dance("Fiddler's Choice", "M. Morgan", "Jig", 8, 32, "2C (4C set)",
			Figure("1s & 2s advance and retire and dance back to back", "1-8"),
			Figure("1s and 2s dance Right Hands across and Left Hands back", "9-16"),
			Figure("1s lead down the middle & up to 2d place (2s step up on 19-20", "17-24"),
			Figure("2s & 1s circle 4H around to the left and back", "25-32")),
		Dance("Random Reel", "A. Mouse", "Reel", 6, 40, "2C (3C set)",
			Figure("1s & 2s dance the Targe:", "1-8"),
			NamedFigure(Figure("1L & 2L turn RH 3/4 <b>while</b> Men dance 1/4 round anticlockwise", "1-2"),
				Figure("1M with 2L & 1L with 2M full turn", "3-4"),
				Figure("1L & 2L turn RH 3/4 <b>while</b> Men dance 1/4 way round anticlockwise", "5-6"),
				Figure("1M with 2L & 1L with 2M full turn", "7-8")),
			Figure("remainder of dance description here")),
		Intermission(),
		Dance("Odd Example", "Bk -1", "Strathspey", 3, 32, "3C Triangle", Figure("dance description here")),
		Dance("Unsourced Medley", "", "Medley", 1, 64, "Sq. Set", Figure("dance description here"), Figure("xyzzy")),
		Intermission("Break")
	};
	ProgramMetadata startingMetadata = ProgramMetadata();
	startingMetadata.groupCoverName = "groupCoverName";
	startingMetadata.groupTitleName = "groupTitleName";
	startingMetadata.eventCoverName = "eventCoverName";
	startingMetadata.eventTitleName = "eventTitleName";
	startingMetadata.coverDate = "coverDate";
	startingMetadata.titleDate = "titleDate";
	startingMetadata.coverLocation = "coverLocation";
	startingMetadata.titleLocation = "titleLocation";
	startingMetadata.locationAddress = "locationAddress";
	startingMetadata.titleTimes = "titleTimes
	                               secondLine";
	startingMetadata.musicians = "musicians";
	startingMetadata.coverImage = "path/to/coverImage";
	startingMetadata.backCoverImage = "path/to/backCover";
	startingMetadata.insidePostDanceImages.addAll({"firstExtra", "secondExtra"});
	startingMetadata.titleOnCover = titleOnCover;
	startingMetadata.printAuldLangSyne = printALS;
	StringBuilder builder = StringBuilder();
	writeLaTeXProgram(builder.append, startingProgram, startingMetadata);
	String serialized = builder.string;
	assert (is [ProgramMetadata, ProgramElement*] deserializationResults = LaTeXReader().readLaTeXProgram(serialized));
	ProgramMetadata readMetadata = deserializationResults.first;
	List<ProgramElement> readProgram = ArrayList { *deserializationResults.rest };
	assertEquals(readMetadata, startingMetadata, "Metadata should be (de)serialized correctly");
	assertEquals(readProgram, startingProgram, "Dances should be (de)serialized correctly");
}
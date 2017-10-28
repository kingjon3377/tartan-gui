import ceylon.collection {
	MutableList,
	ArrayList
}
import ceylon.logging {
	Logger,
	logger
}
Logger log = logger(`module lovelace.tartan.model`);
"Information having to do with the ball program as exported or imported, beyond the list of dances and
 their directions."
shared class ProgramMetadata() {
	"The filename this program was loaded from or should be saved to."
	// TODO: Use ceylon.file type instead of String here?
	shared variable String? filename = null;
	"The name of the group putting on the event, as you would like it to appear on the cover."
	shared variable String groupCoverName = "";
	"The name of the group putting on the event, as you would like it to appear on the title page."
	shared variable String groupTitleName = "";
	"The name of the event as you would like it to appear on the cover."
	shared variable String eventCoverName = "";
	"The name of the event as you would like it to appear on the title page."
	shared variable String eventTitleName = "";
	"The date of the event, as you would like it to appear on the cover."
	shared variable String coverDate = "";
	"The date of the event, as you would like it to appear on the title page."
	shared variable String titleDate = "";
	"The location of the event, as you would like it to appear on the cover."
	shared variable String coverLocation = "";
	"The location of the event, as you would like it to appear on the title page."
	shared variable String titleLocation = "";
	"The address of the event, if you would like it to appear on the title page."
	shared variable String locationAddress = "";
	"""The time(s) of the event (e.g. "Gather 6 p.m., Dinner 6:30 p.m., Dance 7:30 p.m.")
	   Newlines will be replaced with "\\*" for LaTeX"""
	shared variable String titleTimes = "";
	"The name(s) of musician(s) providing music for the event. If provided, this will be
	 typeset on the title page."
	shared variable String musicians = "";
	"The filename of an image to put on the cover."
	shared variable String? coverImage = null;
	"Whether to put the title page on the back of the cover."
	shared variable Boolean titleOnCover = false;
	"Whether to print the text of Auld Lang Syne after the last dance's crib."
	shared variable Boolean printAuldLangSyne = false;
	"The filename of an image to put on the back cover (or, rather, the last page)."
	shared variable String? backCoverImage = null;
	"Filenames of images to put after the last dance's crib, before Auld Lang Syne."
	shared MutableList<String> insidePostDanceImages = ArrayList<String>();
}
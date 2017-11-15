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
shared class ProgramMetadata {
	static Boolean equalOrNull<Type>(Type? first, Type? second) given Type satisfies Object {
		if (exists first) {
			if (exists second) {
				return first == second;
			} else {
				return false;
			}
		} else {
			return !second exists;
		}
	}
	shared new () {}
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
	shared actual Boolean equals(Object other) {
		if (is ProgramMetadata other) {
			// We deliberately omit filename here
			return groupCoverName == other.groupCoverName && groupTitleName == other.groupTitleName &&
					eventCoverName == other.eventCoverName && eventTitleName == other.eventTitleName &&
					coverDate == other.coverDate && titleDate == other.titleDate &&
					coverLocation == other.coverLocation && titleLocation == other.titleLocation &&
					locationAddress == other.locationAddress && titleTimes == other.titleTimes &&
					musicians == other.musicians && equalOrNull(coverImage, other.coverImage) &&
					titleOnCover == other.titleOnCover && printAuldLangSyne == other.printAuldLangSyne &&
					equalOrNull(backCoverImage, other.backCoverImage) &&
					insidePostDanceImages == other.insidePostDanceImages;
		} else {
			return false;
		}
	}
	shared actual Integer hash {
		variable value hash = 1;
		hash = 31*hash + groupCoverName.hash;
		hash = 31*hash + groupTitleName.hash;
		hash = 31*hash + eventCoverName.hash;
		hash = 31*hash + eventTitleName.hash;
		hash = 31*hash + coverDate.hash;
		hash = 31*hash + titleDate.hash;
		hash = 31*hash + coverLocation.hash;
		hash = 31*hash + titleLocation.hash;
		hash = 31*hash + locationAddress.hash;
		hash = 31*hash + titleTimes.hash;
		hash = 31*hash + musicians.hash;
		hash = 31*hash + titleOnCover.hash;
		hash = 31*hash + printAuldLangSyne.hash;
		hash = 31*hash + insidePostDanceImages.hash;
		return hash;
	}
}
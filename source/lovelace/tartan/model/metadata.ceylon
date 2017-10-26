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
	"""The text to put on the cover in large print. Usually includes the name of the
	   group; the title, location, date, and time(s) of the ball; and the name(s) of the
	   musician(s) providing the music. It will be centered and typeset in "large" text."""
	shared variable String coverText = "";
	"Any extra TeX packages to import. If Auld Lang Syne is to be printed in the program,
	 the `verse` class will be added even if not present here (and not double-included if
	 present here), but any other needed packages must be listed here."
	MutableList<String> extraPackagesList = ArrayList<String>();
	"Add a LaTeX package to the list of packages imported in the program's preamble."
	shared void addExtraPackage(
		"The name of the package. May only contain alphanumeric characters, hyphens, and underscores."
		String pkg) {
		"A (portable) LaTeX package name may only contain alphanumeric characters, hyphens, and underscores."
		assert (pkg.every((char) => char.letter || char.digit || "-_".contains(char)));
		if (!extraPackagesList.contains(pkg)) {
			extraPackagesList.add(pkg);
		} else {
			log.info("Double inclusion of LaTeX package ``pkg``");
		}
	}
	"Remove a LaTeX package from the list of packages imported in the program's preamble."
	shared void removeExtraPackage(String pkg) => extraPackagesList.remove(pkg);
	"LaTeX packages to import in the preamble. If Auld Lang Syne is to be printed in the program,
	 the LaTeX export routine should import the `verse` package exactly once whether it is in this
	 list or not."
	shared {String*} extraPackages => {*extraPackagesList};
	"Any text (LaTeX code) to write in the prologue after the documentclass and package imports."
	shared variable String extraPrologue = "";
	"Any text (LaTeX code) to write before the cover."
	shared variable String preCoverText = "";
	// TODO: Allow formatting of teh cover to be customized? In documentclass or here?
	"Any text (LaTeX code) to write after the cover before the list of dances."
	shared variable String postCoverText = "";
	"Any text (LaTeX code) to write after the list of dances and before the first dance."
	shared variable String preDancesText = "";
	"Any text (LaTeX code) to write after the dance cribs before the end of the document."
	shared variable String postDancesText = "";
}
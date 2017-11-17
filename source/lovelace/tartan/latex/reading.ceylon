import lovelace.tartan.model {
	ProgramMetadata,
	ProgramElement,
	Dance,
	Figure,
	NamedFigure,
	Intermission
}
import ceylon.logging {
	Logger,
	logger,
	Priority,
	addLogWriter
}
import ceylon.language.meta.declaration {
	Module,
	Package
}
import ceylon.collection {
	ArrayList,
	MutableList,
	Stack
}
shared class LaTeXReader {
	static void logWriter(Priority priority, Module|Package mod,
		String message, Throwable? except) {
		process.writeErrorLine("``priority`` (``mod``): ``message``");
		if (exists except) {
			process.writeErrorLine(except.message);
			except.printStackTrace();
		}
	}
	static Logger log = logger(`module lovelace.tartan.latex`);
	static void skipComment(Stack<Character> localInput) {
		// This will be called after the '%' has been popped off.
		while (exists top = localInput.pop(), '\n' != top) {}
	}
	variable static Boolean loggerInitialized = false;
	if (!loggerInitialized) {
		loggerInitialized = true;
		addLogWriter(logWriter);
	}
	shared new () {}
	variable Boolean haveHadCover = false;
	variable Boolean haveHadTitle = false;
	variable Boolean nextIsBackCover = false;
	// TODO: Can we get back to a single input stack and a single StringBuilder?
	"Parse a LaTeX command, minus its initial backslash. In other words, assuming the previous character
	 was a backslash, we return a concatenation of every character at the top of the stack until we run
	 into one that is not a letter or an asterisk; we do not consume that character, unless it is whitespace,
	 in which case we do."
	String parseCommand(Stack<Character> localInput) {
		// This will be called after the '\' has been popped off, I think.
		StringBuilder builder = StringBuilder();
		while (exists top = localInput.pop()) {
			if (top.whitespace) {
				break;
			} else if (top.letter || top == '*') { // Assuming no macro with '@' makes it into a document
				// TODO: Should numerals count here too?
				builder.appendCharacter(top);
			} else {
				localInput.push(top);
				break;
			}
		}
		return builder.string;
	}
	void defaultFraction(String numerator, String denominator, StringBuilder buffer) {
		buffer.append(numerator);
		buffer.append("/");
		buffer.append(denominator);
	}
	"""Writes a fraction to the buffer. If a "nice" Unicode glyph is available, this routine uses it;
	   otherwise it simply separate the numerator from the denominator with a slash."""
	void parseFraction(String numerator, String denominator, StringBuilder buffer) {
		switch (numerator)
		case ("1") {
			switch (denominator)
			case ("2") {
				buffer.append("½");
			}
			case ("3") {
				buffer.append("⅓");
			}
			case ("4") {
				buffer.append("¼");
			}
			case ("5") {
				buffer.append("⅕");
			}
			case ("6") {
				buffer.append("⅙");
			}
			case ("7") {
				buffer.append("⅐");
			}
			case ("8") {
				buffer.append("⅛");
			}
			case ("9") {
				buffer.append("⅑");
			}
			case ("10") {
				buffer.append("⅒");
			}
			else {
				defaultFraction(numerator, denominator, buffer);
			}
		}
		case ("2") {
			switch (denominator)
			case ("3") {
				buffer.append("⅔");
			}
			case ("5") {
				buffer.append("⅖");
			}
			else {
				defaultFraction(numerator, denominator, buffer);
			}
		}
		case ("3") {
			switch (denominator)
			case ("4") {
				buffer.append("¾");
			}
			case ("5") {
				buffer.append("⅗");
			}
			case ("8") {
				buffer.append("⅜");
			}
			else {
				defaultFraction(numerator, denominator, buffer);
			}
		}
		case ("4") {
			if (denominator == "5") {
				buffer.append("⅘");
			} else {
				defaultFraction(numerator, denominator, buffer);
			}
		}
		case ("5") {
			switch (denominator)
			case ("6") {
				buffer.append("⅚");
			}
			case ("8") {
				buffer.append("⅝");
			}
			else {
				defaultFraction(numerator, denominator, buffer);
			}
		}
		case ("7") {
			if (denominator == "8") {
				buffer.append("⅞");
			} else {
				defaultFraction(numerator, denominator, buffer);
			}
		}
		else {
			defaultFraction(numerator, denominator, buffer);
		}
	}
	"If the cursor is at the beginning of a { ... } block, we return its contents, replacing some LaTeX idioms
	 with HTML equivalents (`\textbf{}` with HTML bold tags, for example)."
	throws(`class ParseException`, "if there are fewer } than { in the input")
	String blockContents(Stack<Character> localInput) {
		// TODO: Pop off any whitespace first.
		StringBuilder buffer = StringBuilder();
		if (exists first = localInput.top, first != '{') {
			return "";
		} else if (!localInput.top exists) { // TODO: Throw here instead?
			return "";
		}
		variable Integer braceLevel = 0;
		while (exists top = localInput.pop()) {
			if (top == '}') {
				braceLevel--;
				if (braceLevel == 0) {
					return buffer.string;
				} else {
					buffer.appendCharacter('}');
				}
			} else if (top == '{') {
				braceLevel++;
				if (braceLevel > 1) {
					buffer.appendCharacter('{');
				}
			} else if (top == '\\') {
				if (exists next = localInput.top, next == '\\') {
					localInput.pop();
					if (exists yetNext = localInput.top, yetNext == '*') {
						localInput.pop();
					}
					if (exists yetNext = localInput.top, yetNext == '\n') {
						localInput.pop();
					}
					buffer.appendNewline();
					continue;
				} else if (exists next = localInput.top, next == '&') {
					localInput.pop();
					buffer.appendCharacter(next);
					continue;
				}
				switch (nextCommand = parseCommand(localInput))
				case ("textbf") {
					buffer.append("<b>");
					buffer.append(blockContents(localInput));
					buffer.append("</b>");
				}
				case ("nicefrac") {
					String numerator = blockContents(localInput).trimmed;
					String denominator = blockContents(localInput).trimmed;
					parseFraction(numerator, denominator, buffer);
				}
				else {
					buffer.appendCharacter(top);
					buffer.append(nextCommand);
				}
			} else if (top == '`') {
				if (exists next = localInput.top, next == '`') {
					localInput.pop();
					buffer.appendCharacter('“');
				} else {
					buffer.appendCharacter(top);
				}
			} else if (top == '\'') {
				if (exists next = localInput.top, next == '\'') {
					localInput.pop();
					buffer.appendCharacter('”');
				} else {
					buffer.appendCharacter(top);
				}
			} else {
				buffer.appendCharacter(top);
			}
		}
		throw ParseException("Unbalanced curly braces in block");
	}
	"If the cursor is at the beginning of a [ ... ] block, returns its contents. Unlike [[blockContents]], this
	 does not (currently) do any additional parsing."
	throws(`class ParseException`, "if there are fewer ] than [ in the input")
	String parseOptionalBlock(Stack<Character> localInput) {
		StringBuilder buffer = StringBuilder();
		if (exists first = localInput.pop()) {
			if (first != '[') {
				localInput.push(first);
				return "";
			}
			variable Integer braceLevel = 1;
			while (exists top = localInput.pop()) {
				if (top == ']') {
					braceLevel--;
					if (braceLevel == 0) {
						return buffer.string;
					}
				} else if (top == '[') {
					braceLevel++;
				}
				buffer.appendCharacter(top);
			}
			throw ParseException("Unbalanced square braces in optional argument");
		} else {
			return "";
		}
	}
	"Parse the contents of a LaTeX environment. This is separated out so it can object to constructs that the LaTeX
	 compiler might not refuse to compile but that don't make sense, like a dance inside another dance. However,
	 while we log an error if the dance-length parameter of a dance can't be parsed (it's supposed to be TxB, where T
	 is the number of times through and B is the length of each time through the dance in bars), that will not cause
	 parsing of the document to fail."
	throws(`class ParseException`,
		"if a dance is inside another dance, the environment name is the empty string, or we're given an
		 environment we don't know how to handle.")
	void handleEnvironment(String environment, ProgramMetadata mRetval, MutableList<ProgramElement> pRetval,
			Stack<Character> innerStack, Dance|NamedFigure? currentDance = null) {
		switch (environment)
		case ("") {
			throw ParseException("Empty environment name");
		}
		case ("document") {
			parseTokens(innerStack, mRetval, pRetval, currentDance);
		}
		case ("scdance") {
			if (currentDance exists) {
				throw ParseException("Dance nested inside another dance");
			}
			String danceTitle = blockContents(innerStack);
			String danceSource = blockContents(innerStack);
			String danceTempo = blockContents(innerStack);
			String complexLength = blockContents(innerStack);
			String formation = blockContents(innerStack);
			value lengthParsed = complexLength.split('x'.equals, true, true, 1);
			Dance temp;
			if (is Integer timesThrough = Integer.parse(lengthParsed.first)) {
				if (exists barLengthTemp = lengthParsed.rest.first,
					is Integer barLength = Integer.parse(barLengthTemp)) {
					temp = Dance(danceTitle, danceSource, danceTempo, timesThrough,
						barLength, formation);
				} else {
					log.error("Length-in-bars couldn't be extracted from dance-length parameter for dance '``danceTitle``'");
					temp = Dance(danceTitle, danceSource, danceTempo, timesThrough, 0, formation);
				}
			} else {
				log.error("Times through couldn't be extracted from dance-length parameter for dance '``danceTitle``'");
				temp = Dance(danceTitle, danceSource, danceTempo, 0, 0, formation);
			}
			pRetval.add(temp);
			parseTokens(innerStack, mRetval, pRetval, temp);
		}
		else {
			throw ParseException("Unhandled LaTeX environment ``environment``");
		}
	}
	"""Parses the arguments to an `\scfigure` command, which has already been removed from the input stack,
	   and returns a [[Figure]] based on those arguments."""
	Figure parseFigure(Stack<Character> ourStack) {
		// The `\scfigure` has already been parsed at this point.
		String bars = parseOptionalBlock(ourStack);
		String desc = blockContents(ourStack);
		String? barsTemp;
		if (bars.empty) {
			barsTemp = null;
		} else {
			barsTemp = bars;
		}
		return Figure(desc, barsTemp);
	}
	"""Handles any LaTeX (backslash-prefixed) command. For "metadata" commands specified by the `tartan`
	   documentclass, changes the provided [[ProgramMetadata]] instance to match. For the `\tartanimage`
	   command, we do some heuristics to figure out whether this is the cover image, back cover image,
	   or other end-of-program-filler image. `\begin{}` delegates to [[handleEnvironment]].
	   This returns true if this is an `\end{}`, so [[handleEnvironment]] can exit cleanly. `\scfigure`,
	   `\namedfigure`, and `\intermission` are parsed into the model classes they represent, so long as it's
	   legal for them to appear here. Other `tartan`-provided commands (and other commands used in our exporter)
	   are mostly essentially no-ops (except for helping the which-kind-of-image-is-this heuristics)."""
	throws(`class ParseException`,
		"if command name is empty, a documentclass other than tartan is specified, or a
		 legal-nesting invariant is violated")
	Boolean handleCommand(String command, ProgramMetadata mRetval, MutableList<ProgramElement> pRetval,
			Dance|NamedFigure? currentContext, Stack<Character> ourStack) {
		switch (command)
		case ("") {
			throw ParseException("Unhandled backslash-quoted character");
		}
		case ("documentclass") {
			if (blockContents(ourStack) != "tartan") {
				throw ParseException("We only support the tartan documentclass");
			}
			return false;
		}
		case ("tartangroupname") {
			mRetval.groupCoverName = blockContents(ourStack);
		}
		case ("tartangroupname*") {
			mRetval.groupTitleName = blockContents(ourStack);
		}
		case ("tartanballname") {
			mRetval.eventCoverName = blockContents(ourStack);
		}
		case ("tartanballname*") {
			mRetval.eventTitleName = blockContents(ourStack);
		}
		case ("tartanballdate") {
			mRetval.coverDate = blockContents(ourStack);
		}
		case ("tartanballdate*") {
			mRetval.titleDate = blockContents(ourStack);
		}
		case ("tartanhall") {
			mRetval.coverLocation = blockContents(ourStack);
		}
		case ("tartanhall*") {
			mRetval.titleLocation = blockContents(ourStack);
		}
		case ("tartanhalladdress") {
			mRetval.locationAddress = blockContents(ourStack);
		}
		case ("tartantimes") {
			mRetval.titleTimes = blockContents(ourStack);
		}
		case ("tartanmusicians") {
			mRetval.musicians = blockContents(ourStack);
		}
		case ("tartancover") {
			mRetval.coverImage = null;
			haveHadCover = true;
		}
		case ("listofdances") {
			haveHadCover = true;
			haveHadTitle = true;
		}
		case ("tartanimage") {
			if (nextIsBackCover) {
				if (exists oldBackCover = mRetval.backCoverImage) {
					mRetval.insidePostDanceImages.add(oldBackCover);
				}
				mRetval.backCoverImage = blockContents(ourStack);
			} else if (haveHadCover) {
				mRetval.insidePostDanceImages.add(blockContents(ourStack));
			} else {
				mRetval.coverImage = blockContents(ourStack);
				haveHadCover = true;
			}
		}
		case ("cleartoverso") {
			nextIsBackCover = true;
		}
		case ("tartanimagecover") {
			mRetval.coverImage = blockContents(ourStack);
			haveHadCover = true;
		}
		case ("clearpage") {
			if (haveHadCover, !haveHadTitle) {
				mRetval.titleOnCover = true;
				haveHadTitle = true;
			}
		}
		case ("cleardoublepage") {
			if (haveHadCover, !haveHadTitle) {
				mRetval.titleOnCover = false;
				haveHadTitle = true;
			}
		}
		case ("maketartantitle") {
			haveHadCover = true;
			haveHadTitle = true;
		}
		case ("begin") {
			handleEnvironment(blockContents(ourStack), mRetval, pRetval, ourStack, currentContext);
		}
		case ("end") {
			switch (environmentName = blockContents(ourStack))
			case ("document") { }
			case ("scdance") {
				if (!currentContext exists) {
					throw ParseException("\\end{scdance} without \\begin{scdance}");
				}
			}
			else {
				throw ParseException("Unhandled end-environment ``environmentName``");
			}
			return true;
		}
		case ("scfigure") {
			if (is Dance dance = currentContext) {
				dance.contents.add(parseFigure(ourStack));
			} else if (is NamedFigure currentContext) {
				currentContext.contents.add(parseFigure(ourStack));
			} else {
				throw ParseException("Figure outside any dance");
			}
		}
		case ("namedfigure") {
			if (is Dance dance = currentContext) {
				NamedFigure nfigure = NamedFigure();
				String contents = blockContents(ourStack);
				parseTokens(ArrayList { *contents.reversed }, mRetval, pRetval, nfigure);
				dance.contents.add(nfigure);
			} else if (is NamedFigure currentContext) {
				throw ParseException("Named figure nested inside named figure");
			} else {
				throw ParseException("Named figure outside any dance");
			}
		}
		case ("intermission") {
			// TODO: Object if we're in the middle of a dance or named figure. And do the same for most other commands.
			String argument = parseOptionalBlock(ourStack);
			if (!argument.empty) {
				pRetval.add(Intermission(argument));
			} else {
				pRetval.add(Intermission());
			}
		}
		case ("auldlangsyne") {
			mRetval.printAuldLangSyne = true;
		}
		else {
			throw ParseException("Unhandled command \\``command``");
		}
		return false;
	}
	"The main loop of the parser, which is a separate method because it simplifies things for [[handleCommand]] and
	 [[handleEnvironment]] to be able to call it."
	void parseTokens(Stack<Character> inputStack, ProgramMetadata mRetval, MutableList<ProgramElement> pRetval,
			Dance|NamedFigure? currentContext = null) {
		while (exists top = inputStack.pop()) {
			if (top == '%') {
				skipComment(inputStack);
			} else if (top == '\\') {
				if (handleCommand(parseCommand(inputStack), mRetval, pRetval, currentContext, inputStack)) {
					break;
				}
			} else if (top.whitespace) {
				continue;
			} else {
				throw ParseException("Unhandled character '``top``'");
			}
		}
	}
	"Parses a LaTeX representation of a Ball program, provided as a String, and returns the
	 [[ProgramMetadata]] and program elements it contains if parsing succeeds; if parsing fails,
	 a [[ParseException]] explaining where and why it failed is returned."
	shared [ProgramMetadata, ProgramElement*]|ParseException readLaTeXProgram(String input) {
		ProgramMetadata mRetval = ProgramMetadata();
		MutableList<ProgramElement> pRetval = ArrayList<ProgramElement>();
		Stack<Character> inputStack = ArrayList { *input.reversed };
		try {
			parseTokens(inputStack, mRetval, pRetval);
		} catch (ParseException except) {
			log.error(except.message, except);
			return except;
		}
		return [mRetval, *pRetval];
	}
}
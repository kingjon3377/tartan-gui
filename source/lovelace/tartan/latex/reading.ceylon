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
	String parseCommand(Stack<Character> localInput) {
		// This will be called after the '\' has been popped off, I think.
		StringBuilder builder = StringBuilder();
		while (exists top = localInput.pop()) {
			if (top.whitespace) {
				break;
			} else if (top.letter || top == '*') { // Assuming no macro with '@' makes it into a document
				builder.appendCharacter(top);
			} else {
				localInput.push(top);
				break;
			}
		}
		return builder.string;
	}
	String blockContents(Stack<Character> localInput) {
		// If the cursor is at the beginning of a { ... } block, return its contents, which
		// may need to be further parsed. (TODO: is that the best way?)
		// TODO: Pop off any whitespace first.
		StringBuilder buffer = StringBuilder();
		if (exists first = localInput.pop()) {
			if (first != '{') {
				localInput.push(first);
				return "";
			}
			variable Integer braceLevel = 1;
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
					buffer.appendCharacter('{');
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
					else {
						buffer.appendCharacter(top);
						buffer.append(nextCommand);
					}
				} else {
					buffer.appendCharacter(top);
				}
			}
			throw ParseException("Unbalanced curly braces in block");
		} else {
			return "";
		}
	}
	String parseOptionalBlock(Stack<Character> localInput) {
		// If the cursor is at the beginning of a [ ... ] block, return its contents, which
		// may need to be further parsed. (TODO: is that the best way?)
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
	"""Returns true if this is an \end{}"""
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
package lovelace.tartan.latex;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import lovelace.tartan.model.Dance;
import lovelace.tartan.model.DanceImpl;
import lovelace.tartan.model.Figure;
import lovelace.tartan.model.FigureParent;
import lovelace.tartan.model.Intermission;
import lovelace.tartan.model.NamedFigure;
import lovelace.tartan.model.ProgramElement;
import lovelace.tartan.model.ProgramMetadata;
import lovelace.util.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A class to read a dance program from LaTeX.
 *
 * @author Jonathan Lovelace
 */
public final class LaTeXReader {
	private boolean haveHadCover = false;
	private boolean haveHadTitle = false;
	private boolean nextIsBackCover = false;

	/**
	 * Logger.
	 */
	private static final Logger LOGGER = Logger.getLogger(LaTeXReader.class.getName());

	private static boolean isLinebreak(final Character character) {
		return character != null && ('\n' == character || '\r' == character);
	}

	/**
	 * Skip a newline, represented by either a carriage return, a line feed, or the two
	 * in either order, but <em>not</em> multiple consecutive newlines.
	 */
	private static void skipNewline(final @NotNull Deque<Character> localInput) {
		if (localInput.isEmpty()) {
			return;
		}
		final char first = localInput.peek();
		if (!isLinebreak(first)) {
			return;
		}
		localInput.pop();
		if (localInput.isEmpty()) {
			return;
		}
		final char second = localInput.peek();
		if (isLinebreak(second) && first != second) {
			localInput.pop();
		}
	}

	/**
	 * Skip a comment, not including the initial '%' character.
	 *
	 * @param localInput the queue from which to read
	 */
	private static void skipComment(final @NotNull Deque<Character> localInput) {
		while (!localInput.isEmpty()) {
			if (isLinebreak(localInput.peek())) {
				skipNewline(localInput);
				break;
			} else {
				localInput.pop();
			}
		}
	}

	// TODO: Can we get back to a single input stack and a single StringBuilder?

	/**
	 * Parse a LaTeX command, minus its initial backslash. In other words, assuming the
	 * previous character was a backslash, we return a concatenation of every
	 * character at
	 * the head of the queue until we reach one that is neither a letter nor an asterisk;
	 * if that character is whitespace, we also consume it, but otherwise we do not.
	 *
	 * @param localInput the queue from which to read
	 */
	private static String parseCommand(final @NotNull Deque<Character> localInput) {
		final StringBuilder builder = new StringBuilder();
		while (!localInput.isEmpty()) {
			final char top = localInput.peekFirst();
			if (Character.isWhitespace(top)) {
				localInput.pop();
				break;
			} else if (Character.isLetterOrDigit(top) || top == '*') {
				// We assume no macro with '@' in its name will ever make it into a
				// document.
				localInput.pop();
				builder.append(top);
			} else {
				break;
			}
		}
		return builder.toString();
	}

	/**
	 * Append the default representation of a fraction to the given buffer.
	 *
	 * @param numerator   the numerator of the fraction
	 * @param denominator the denominator of the fraction
	 * @param buffer      the buffer to write to
	 */
	@SuppressWarnings("HardcodedFileSeparator") // Not a file separator
	private static void defaultFraction(final @NotNull String numerator,
										final @NotNull String denominator,
										final @NotNull StringBuilder buffer) {
		buffer.append(numerator);
		buffer.append('/');
		buffer.append(denominator);
	}

	/**
	 * Write a fraction with numerator 1 and the given denominator to the given buffer,
	 * using a "nice" Unicode glyph if one is available.
	 */
	private static void parseFractionNumeratorOne(final @NotNull String denominator,
	                                              final @NotNull StringBuilder buffer) {
		switch (denominator) {
			case "2" -> buffer.append("½");
			case "3" -> buffer.append("⅓");
			case "4" -> buffer.append("¼");
			case "5" -> buffer.append("⅕");
			case "6" -> buffer.append("⅙");
			case "7" -> buffer.append("⅐");
			case "8" -> buffer.append("⅛");
			case "9" -> buffer.append("⅑");
			case "10" -> buffer.append("⅒");
			default -> defaultFraction("1", denominator, buffer);
		}
	}

	/**
	 * Write a fraction with numerator 2 and the given denominator to the given buffer,
	 * using a "nice" Unicode glyph if one is available.
	 */
	private static void parseFractionNumeratorTwo(final @NotNull String denominator,
	                                              final @NotNull StringBuilder buffer) {
		switch (denominator) {
			case "3" -> buffer.append("⅔");
			case "5" -> buffer.append("⅖");
			default -> defaultFraction("2", denominator, buffer);
		}
	}

	/**
	 * Write a fraction with numerator 3 and the given denominator to the given buffer,
	 * using a "nice" Unicode glyph if one is available.
	 */
	private static void parseFractionNumeratorThree(final @NotNull String denominator,
	                                                final @NotNull StringBuilder buffer) {
		switch (denominator) {
		case "4" -> buffer.append("¾");
		case "5" -> buffer.append("⅗");
		case "8" -> buffer.append("⅜");
		default -> defaultFraction("3", denominator, buffer);
		}
	}


	/**
	 * Write a fraction with numerator 5 and the given denominator to the given buffer,
	 * using a "nice" Unicode glyph if one is available.
	 */
	private static void parseFractionNumeratorFive(final @NotNull String denominator,
	                                               final @NotNull StringBuilder buffer) {
		switch (denominator) {
		case "6":
			buffer.append("⅚");
			break;
		case "8":
			buffer.append("⅝");
			break;
		default:
			defaultFraction("5", denominator, buffer);
			break;
		}
	}
	/**
	 * Write the given fraction to the given buffer. If a "nice" Unicode glyph is
	 * available, use it; otherwise, simply separate the numerator from the denominator
	 * with a slash.
	 *
	 * @param numerator   the numerator of the fraction
	 * @param denominator the denominator of the fraction
	 * @param buffer      the buffer to write to
	 */
	private static void parseFraction(final @NotNull String numerator,
									  final @NotNull String denominator,
									  final @NotNull StringBuilder buffer) {
		switch (numerator) {
			case "1" -> parseFractionNumeratorOne(denominator, buffer);
			case "2" -> parseFractionNumeratorTwo(denominator, buffer);
			case "3" -> parseFractionNumeratorThree(denominator, buffer);
			case "4" -> {
				if ("5".equals(denominator)) {
					buffer.append("⅘");
				} else {
					defaultFraction(numerator, denominator, buffer);
				}
			}
			case "5" -> parseFractionNumeratorFive(denominator, buffer);
			case "7" -> {
				if ("8".equals(denominator)) {
					buffer.append("⅞");
				} else {
					defaultFraction(numerator, denominator, buffer);
				}
			}
			default -> defaultFraction(numerator, denominator, buffer);
		}
	}

	/**
	 * If the cursor is at the beginning of a curly-brace block, return its contents,
	 * replacing some LaTeX idioms with HTML equivalents (e.g. <pre>\\textbf{}</pre> with
	 * HTML bold tags).
	 *
	 * @param localInput the queue from which to read
	 * @throws ParseException if there are fewer <pre>}</pre> than <pre>{</pre> in the
	 *                        input
	 */
	@SuppressWarnings({"ContinueStatement", "HardcodedFileSeparator"}) // '/' is cross-platform in Java!
	static String blockContents(final @NotNull Deque<Character> localInput)
			throws ParseException {
		// TODO: Keep track of cursor position so we can give accurate data in thrown
		//  error
		while (!localInput.isEmpty() && Character.isWhitespace(localInput.peekFirst())) {
			localInput.pop();
		}
		if (localInput.isEmpty()) { // TODO: Throw here instead?
			return "";
		} else if ('{' != localInput.peekFirst()) {
			return "";
		}
		final StringBuilder buffer = new StringBuilder();
		int braceLevel = 0;
		while (!localInput.isEmpty()) {
			final char top = localInput.pop();
			switch (top) {
				case '}' -> {
					braceLevel--;
					final int temp = braceLevel;
					if (braceLevel == 0) { // TODO: Use <= instead of ==?
						return buffer.toString();
					} else {
						buffer.append(top);
					}
				}
				case '{' -> {
					braceLevel++;
					if (braceLevel > 1) {
						buffer.append(top);
					}
				}
				case '\\' -> {
					if (localInput.isEmpty()) {
						throw new ParseException("EOF after backslash", -1);
					}
					if ('\\' == localInput.peekFirst()) {
						localInput.pop();
						if (Character.valueOf('*').equals(localInput.peekFirst())) {
							localInput.pop();
						}
						skipNewline(localInput);
						buffer.append(System.lineSeparator());
						continue;
					}
					final char next = localInput.peekFirst();
					if ('&' == next || '{' == next || '}' == next) {
						localInput.pop();
						buffer.append(next);
						continue;
					}
					final String nextCommand = parseCommand(localInput);
					handleSingleCommand(localInput, nextCommand, buffer, top);
				}
				case '`' -> {
					if (!localInput.isEmpty() && '`' == localInput.peekFirst()) {
						localInput.pop();
						buffer.append('"');
					} else {
						buffer.append(top);
					}
				}
				case '\'' -> {
					if (!localInput.isEmpty() && '\'' == localInput.peekFirst()) {
						localInput.pop();
						buffer.append('"');
					} else {
						buffer.append(top);
					}
				}
				default -> buffer.append(top);
			}
		}
		throw new ParseException("Unbalanced curly braces in block", -1);
	}

	private static void handleSingleCommand(final @NotNull Deque<Character> localInput,
	                              final String nextCommand, final StringBuilder buffer,
	                              final char top) throws ParseException {
		switch (nextCommand) {
			case "textbf":
				buffer.append("<b>");
				buffer.append(blockContents(localInput));
				buffer.append("</b>");
				break;
			case "nicefrac":
				final String numerator = blockContents(localInput).trim();
				final String denominator = blockContents(localInput).trim();
				parseFraction(numerator, denominator, buffer);
				break;
			case "textit": // TODO: Handle \emph as well
				buffer.append("<i>");
				buffer.append(blockContents(localInput));
				buffer.append("</i>");
				break;
			case "textsuperscript":
				buffer.append("<sup>");
				buffer.append(blockContents(localInput));
				buffer.append("</sup>");
				break;
			default:
				buffer.append(top);
				buffer.append(nextCommand);
				break;
		}
	}

	/**
	 * If the cursor is at the beginning of a square-bracket block, return its contents.
	 * Unlike {@link #blockContents}, this does not (currently) do any additional
	 * parsing.
	 *
	 * @param localInput the queue from which to read
	 * @throws ParseException if there are fewer <pre>]</pre> than <pre>[</pre> in the
	 *                        input
	 */
	private static String parseOptionalBlock(final @NotNull Deque<Character> localInput)
			throws ParseException {
		if (localInput.isEmpty()) {
			return "";
		}
		if (localInput.peekFirst() != '[') {
			return "";
		}
		localInput.pop();
		final StringBuilder buffer = new StringBuilder();
		int braceLevel = 1;
		while (!localInput.isEmpty()) {
			final char top = localInput.pop();
			if (top == ']') {
				braceLevel--;
				if (braceLevel == 0) {
					return buffer.toString();
				}
			} else if (top == '[') {
				braceLevel++;
			}
			buffer.append(top);
		}
		throw new ParseException("Unbalanced square braces in optional argument", -1);
	}

	/**
	 * From what should be a length-2 array, try to parse an integer from the first cell.
	 * On failure, log an error and return 0.
	 *
	 * @param array the array.
	 * @param dance the dance we're in, for use in the error message if parsing fails
	 */
	private static int parseTimesThrough(final String[] array, final String dance) {
		if (array.length > 0) {
			try {
				return Integer.parseInt(array[0]);
			} catch (final NumberFormatException except) {
				LOGGER.severe(() -> String.format(
						"Times through couldn't be extracted from dance-length " +
								"parameter for dance '%s'",
						dance));
				return 0;
			}
		} else {
			LOGGER.severe(() -> String.format(
					"No dance-length parameter provided for dance '%s'", dance));
			return 0;
		}
	}

	/**
	 * From what should be a length-2 array, try to parse an integer from the second
	 * cell.
	 * On failure, log an error and return 0.
	 *
	 * @param array the array.
	 * @param dance the dance we're in, for use in the error message if parsing fails
	 */
	private static int parseBars(final String[] array, final String dance) {
		if (array.length > 1) {
			try {
				return Integer.parseInt(array[1]);
			} catch (final NumberFormatException except) {
				LOGGER.severe(() -> String.format(
						"Length-in-bars couldn't be extracted from dance-length " +
								"parameter for dance '%s'",
						dance));
				return 0;
			}
		} else {
			LOGGER.severe(() -> String.format(
					"No length-in-bars provided in dance-length parameter for dance '%s'",
					dance));
			return 0;
		}
	}

	/**
	 * Parse the contents of a LaTeX environment when not in a dance or named figure.
	 *
	 * @param environment the environment we're being asked to parse the contents of
	 * @param mRetval     the document metadata object to put metadata into
	 * @param pRetval     the list of dances (etc.) to put dances and intermissions into
	 * @param innerQueue  the queue from which to read
	 * @throws ParseException if a dance is inside another dance, the environment name is
	 *                        the empty string, or we're given an environment this parser
	 *                        doesn't know how to handle
	 */
	private void handleEnvironment(final @NotNull String environment,
								   final @NotNull ProgramMetadata mRetval,
								   final @NotNull List<@NotNull ProgramElement> pRetval,
								   final @NotNull Deque<Character> innerQueue)
			throws ParseException {
		handleEnvironment(environment, mRetval, pRetval, innerQueue, null);
	}

	/**
	 * Parse the contents of a LaTeX environment. This is separated out in order to be
	 * able to object to constructs that the LaTeX compiler might not refuse to compile
	 * but that don't make sense, like a dance inside another dance. However, while we
	 * log an error if the dance-length parameter of a dance can't be parsed (it's
	 * supposed to be TxB, where T is the number of times through and B is the length of
	 * each time through the dance in bars), that will not cause parsing of the document
	 * to fail.
	 *
	 * @param environment  the environment we're being asked to parse the contents of
	 * @param mRetval      the document metadata object to put metadata into
	 * @param pRetval      the list of dances (etc.) to put dances and intermissions into
	 * @param innerQueue   the queue from which to read
	 * @param currentDance the dance or named figure we are currently inside
	 * @throws ParseException if a dance is inside another dance, the environment name is
	 *                        the empty string, or we're given an environment this parser
	 *                        doesn't know how to handle
	 */
	private void handleEnvironment(final @NotNull String environment,
								   final @NotNull ProgramMetadata mRetval,
								   final @NotNull List<@NotNull ProgramElement> pRetval,
								   final @NotNull Deque<Character> innerQueue,
								   final @Nullable FigureParent currentDance)
			throws ParseException {
		switch (environment) {
		case "":
			throw new ParseException("Empty environment name", -1);
		case "document":
			parseTokens(innerQueue, mRetval, pRetval, currentDance);
			break;
		case "scdance":
			if (currentDance != null) {
				throw new ParseException("Dance nested inside another dance", -1);
			}
			final String danceTitle = blockContents(innerQueue);
			final String danceSource = blockContents(innerQueue);
			final String danceTempo = blockContents(innerQueue);
			final String complexLength = blockContents(innerQueue);
			final String formation = blockContents(innerQueue);
			final String[] lengthParsed = complexLength.split("x", 2);
			final Dance temp = new DanceImpl(danceTitle, danceSource, danceTempo,
					parseTimesThrough(lengthParsed, danceTitle),
					parseBars(lengthParsed, danceTitle), formation);
			pRetval.add(temp);
			parseTokens(innerQueue, mRetval, pRetval, temp);
			break;
		default:
			throw new ParseException("Unhandled LaTeX environment " + environment, -1);
		}
	}

	/**
	 * Parses the arguments to an <pre>\\scfigure</pre> command, which has already been
	 * removed from the input queue, and produces a {@link Figure} based on those
	 * arguments.
	 *
	 * @param ourQueue the queue to read from
	 * @throws ParseException when thrown by {@link #parseOptionalBlock(Deque)} or {@link
	 *                        #blockContents(Deque)}
	 */
	private static Figure parseFigure(final @NotNull Deque<Character> ourQueue)
			throws ParseException {
		final String bars = parseOptionalBlock(ourQueue);
		final String desc = blockContents(ourQueue);
		if (bars.isEmpty()) {
			return new Figure(desc, null);
		} else {
			return new Figure(desc, bars);
		}
	}

	/**
	 * Handle any LaTeX (backslash-prefixed) command. For "metadata" commands
	 * specified by
	 * the <code>tartan</code> documentclass, changes the provided
	 * {@link ProgramMetadata}
	 * instance to match. For the <pre>\tartanimage</pre> command, uses some
	 * heuristics to
	 * figure out whether this is the cover image, back cover image, or other
	 * end-of-program-filler image. <pre>\begin{}</pre> delegates to {@link
	 * #handleEnvironment(String, ProgramMetadata, List, Deque, FigureParent)} . This
	 * method returns true if this is an <pre>\end{}</pre>, so {@link
	 * #handleEnvironment(String, ProgramMetadata, List, Deque, FigureParent)} can exit
	 * cleanly. <pre>\scfigure{}</pre>, <pre>\namedfigure</pre>, and
	 * <pre>\intermission</pre> are parsed into the model classes they represent, so
	 * long as it's legal for them to appear here. Other <code>tartan</code>-provided
	 * commands (and other commands used in our exporter) are mostly essentially no-ops
	 * (except for helping the which-kind-of-image-is-this heuristics).
	 *
	 * @param command        the command to handle
	 * @param mRetval        the metadata object to update if this is a metadata-storage
	 *                       command
	 * @param pRetval        the list of dances to add new dances to
	 * @param currentContext the current dance or named figure, if any
	 * @param ourQueue       the input queue
	 * @throws ParseException if command name is empty, a documentclass other than tartan
	 *                        is specified, or a legal-nesting invariant is violated
	 */
	private boolean handleCommand(final @NotNull String command,
								  final @NotNull ProgramMetadata mRetval,
								  final @NotNull List<@NotNull ProgramElement> pRetval,
								  final @Nullable FigureParent currentContext,
								  final @NotNull Deque<Character> ourQueue)
			throws ParseException {
		switch (command) {
		case "":
			throw new ParseException("Unhandled backslash-quoted character", -1);
		case "documentclass":
			if (currentContext != null) {
				throw new ParseException("documentclass in the middle of a dance", -1);
			}
			if (!"tartan".equals(blockContents(ourQueue))) {
				throw new ParseException("We only support the tartan documentclass", -1);
			}
			return false;
		case "tartangroupname":
			if (currentContext != null) {
				throw new ParseException("\\tartangroupname in the middle of a dance",
						-1);
			}
			mRetval.setGroupCoverName(blockContents(ourQueue));
			break;
		case "tartangroupname*":
			if (currentContext != null) {
				throw new ParseException("\\tartangroupname* in the middle of a dance",
						-1);
			}
			mRetval.setGroupTitleName(blockContents(ourQueue));
			break;
		case "tartanballname":
			if (currentContext != null) {
				throw new ParseException("\\tartanballname in the middle of a dance",
						-1);
			}
			mRetval.setEventCoverName(blockContents(ourQueue));
			break;
		case "tartanballname*":
			if (currentContext != null) {
				throw new ParseException("\\tartanballname* in the middle of a dance",
						-1);
			}
			mRetval.setEventTitleName(blockContents(ourQueue));
			break;
		case "tartanballdate":
			if (currentContext != null) {
				throw new ParseException("\\tartanballdate in the middle of a dance",
						-1);
			}
			mRetval.setCoverDate(blockContents(ourQueue));
			break;
		case "tartanballdate*":
			if (currentContext != null) {
				throw new ParseException("\\tartanballdate* in the middle of a dance",
						-1);
			}
			mRetval.setTitleDate(blockContents(ourQueue));
			break;
		case "tartanhall":
			if (currentContext != null) {
				throw new ParseException("\\tartanhall in the middle of a dance", -1);
			}
			mRetval.setCoverLocation(blockContents(ourQueue));
			break;
		case "tartanhall*":
			if (currentContext != null) {
				throw new ParseException("\\tartanhall* in the middle of a dance", -1);
			}
			mRetval.setTitleLocation(blockContents(ourQueue));
			break;
		case "tartanhalladdress":
			if (currentContext != null) {
				throw new ParseException("\\tartanhalladdress in the middle of a dance",
						-1);
			}
			mRetval.setLocationAddress(blockContents(ourQueue));
			break;
		case "tartantimes":
			if (currentContext != null) {
				throw new ParseException("\\tartantimes in the middle of a dance", -1);
			}
			mRetval.setTitleTimes(blockContents(ourQueue).trim());
			break;
		case "tartanmusicians":
			if (currentContext != null) {
				throw new ParseException("\\tartanmusicians in the middle of a dance",
						-1);
			}
			mRetval.setMusicians(blockContents(ourQueue).trim());
			break;
		case "tartancover":
			if (currentContext != null) {
				throw new ParseException("\\tartancover in the middle of a dance", -1);
			}
			mRetval.setCoverImage(null);
			haveHadCover = true;
			break;
		case "listofdances":
			if (currentContext != null) {
				throw new ParseException("\\listofdances in the middle of a dance", -1);
			}
			haveHadCover = true;
			haveHadTitle = true;
			break;
		case "tartanimage":
			if (currentContext != null) {
				throw new ParseException("\\tartanimage in the middle of a dance", -1);
			}
			if (nextIsBackCover) {
				final @Nullable Path oldBackCover = mRetval.getBackCoverImage();
				if (oldBackCover != null) {
					mRetval.getInsidePostDanceImages().add(oldBackCover);
				}
				mRetval.setBackCoverImage(Paths.get(blockContents(ourQueue)));
			} else if (haveHadCover) {
				mRetval.getInsidePostDanceImages()
						.add(Paths.get(blockContents(ourQueue)));
			} else {
				mRetval.setCoverImage(Paths.get(blockContents(ourQueue)));
				haveHadCover = true;
			}
			break;
		case "cleartoverso":
			if (currentContext != null) {
				throw new ParseException("\\cleartoverso in the middle of a dance", -1);
			}
			nextIsBackCover = true;
			break;
		case "tartanimagecover":
			if (currentContext != null) {
				throw new ParseException("\\tartanimagecover in the middle of a dance",
						-1);
			}
			mRetval.setCoverImage(Paths.get(blockContents(ourQueue)));
			haveHadCover = true;
			break;
		case "clearpage":
			if (currentContext != null) {
				// we're clearly not delimiting the title page!
				return false;
			}
			if (haveHadCover && !haveHadTitle) {
				mRetval.setTitleOnCover(true);
				haveHadTitle = true;
			}
			break;
		case "cleardoublepage":
			if (currentContext != null) {
				// we're clearly not delimiting the title page!
				return false;
			}
			if (haveHadCover && !haveHadTitle) {
				mRetval.setTitleOnCover(false);
				haveHadTitle = true;
			}
			break;
		case "maketartantitle":
			if (currentContext != null) {
				throw new ParseException("\\maketartantitle in the middle of a dance",
						-1);
			}
			haveHadCover = true;
			haveHadTitle = true;
			break;
		case "begin":
			handleEnvironment(blockContents(ourQueue), mRetval, pRetval, ourQueue,
					currentContext);
			break;
		case "end":
			final String environmentName = blockContents(ourQueue);
			if ("scdance".equals(environmentName)) {
				if (currentContext == null) {
					throw new ParseException("\\end{scdance} without \\begin{scdance}",
							-1);
				}
			} else if (!"document".equals(environmentName)) {
				throw new ParseException("Unhandled end-environment " + environmentName,
						-1);
			}
			return true;
		case "scfigure":
			switch (currentContext) {
				case final Dance dance -> dance.getContents().add(parseFigure(ourQueue));
				case final NamedFigure namedFigure ->
						namedFigure.getContents().add(parseFigure(ourQueue));
				case null, default ->
						throw new ParseException("Figure outside any dance", -1);
			}
			break;
		case "namedfigure":
			switch (currentContext) {
				case final Dance dance -> {
					final NamedFigure namedFigure = new NamedFigure();
					final String contents = blockContents(ourQueue);
					parseTokens(contents.chars().mapToObj(i -> (char) i).collect(
									Collectors.toCollection(LinkedList::new)),
							mRetval, pRetval, namedFigure);
					dance.getContents().add(namedFigure);
				}
				case final NamedFigure ignored -> throw new ParseException(
						"Named figure nested inside named figure", -1);
				case null, default ->
						throw new ParseException("Named figure outside any dance", -1);
			}
			break;
		case "intermission":
			if (currentContext != null) {
				throw new ParseException("Intermission in the middle of a dance", -1);
			}
			final String argument = parseOptionalBlock(ourQueue);
			if (argument.isEmpty()) {
				pRetval.add(new Intermission());
			} else {
				pRetval.add(new Intermission(argument));
			}
			break;
		case "auldlangsyne":
			if (currentContext != null) {
				throw new ParseException("Auld Lang Syne in the middle of a dance", -1);
			}
			mRetval.setPrintAuldLangSyne(true);
			break;
		case "vspace":
		case "vspace*":
			blockContents(ourQueue); // ignore argument
			break;
		default:
			throw new ParseException("Unhandled command \\" + command, -1);
		}
		return false;
	}

	/**
	 * The main loop of the parser. A separate method because it simplifies things for
	 * {@link #handleCommand(String, ProgramMetadata, List, FigureParent, Deque)} and
	 * {@link #handleEnvironment(String, ProgramMetadata, List, Deque, FigureParent)} to
	 * be able to call it.
	 *
	 * @param inputQueue the input stream to read from
	 * @param mRetval    the metadata object to update from metadata commands
	 * @param pRetval    the list of dances etc. to add dances to
	 * @throws ParseException if one of our invariants is violated
	 */
	private void parseTokens(final @NotNull Deque<Character> inputQueue,
							 final @NotNull ProgramMetadata mRetval,
							 final @NotNull List<ProgramElement> pRetval)
			throws ParseException {
		parseTokens(inputQueue, mRetval, pRetval, null);
	}

	/**
	 * The main loop of the parser. A separate method because it simplifies things for
	 * {@link #handleCommand(String, ProgramMetadata, List, FigureParent, Deque)} and
	 * {@link #handleEnvironment(String, ProgramMetadata, List, Deque, FigureParent)} to
	 * be able to call it.
	 *
	 * @param inputQueue     the input stream to read from
	 * @param mRetval        the metadata object to update from metadata commands
	 * @param pRetval        the list of dances etc. to add dances to
	 * @param currentContext the current dance or named figure, if any
	 * @throws ParseException if one of our invariants is violated
	 */
	private void parseTokens(final @NotNull Deque<Character> inputQueue,
							 final @NotNull ProgramMetadata mRetval,
							 final @NotNull List<ProgramElement> pRetval,
							 final @Nullable FigureParent currentContext)
			throws ParseException {
		while (!inputQueue.isEmpty()) {
			final char top = inputQueue.pop();
			if (top == '%') {
				skipComment(inputQueue);
			} else if (top == '\\') {
				if (handleCommand(parseCommand(inputQueue), mRetval, pRetval,
						currentContext, inputQueue)) {
					break;
				}
			} else if (!Character.isWhitespace(top)) {
				throw new ParseException(String.format("Unhandled character '%c'", top),
						-1);
			}
		}
	}

	/**
	 * Parse a LaTeX representation of a Ball program, provided as a String, and return
	 * the {@link ProgramMetadata} and the list of program elements it contains if
	 * parsing
	 * succeeds; if parsing fails, a {@link ParseException} explaining where (in the
	 * parsing code) and why it failed is thrown.
	 *
	 * @param input the LaTeX to parse
	 * @throws ParseException on parsing failure
	 */
	public Pair<@NotNull ProgramMetadata,
				@NotNull List<@NotNull ProgramElement>> readLaTeXProgram(
			final @NotNull String input) throws ParseException {
		final ProgramMetadata mRetval = new ProgramMetadata();
		final List<@NotNull ProgramElement> pRetval = new ArrayList<>();
		final Deque<Character> inputQueue =
				input.chars().mapToObj(i -> (char) i).collect(
						Collectors.toCollection(LinkedList::new));
		parseTokens(inputQueue, mRetval, pRetval);
		return Pair.of(mRetval, pRetval);
	}

	@Override
	public String toString() {
		return "LaTeXReader (cover: %s, title: %s, inside back cover: %s)"
				       .formatted(haveHadCover, haveHadTitle, nextIsBackCover);
	}
}

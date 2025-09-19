package lovelace.tartan.latex;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;
import lovelace.tartan.model.Dance;
import lovelace.tartan.model.DanceMember;
import lovelace.tartan.model.Figure;
import lovelace.tartan.model.Intermission;
import lovelace.tartan.model.NamedFigure;
import lovelace.tartan.model.NamedFigureMember;
import lovelace.tartan.model.ProgramElement;
import lovelace.tartan.model.ProgramMetadata;
import lovelace.tartan.model.SimplestMember;
import org.jspecify.annotations.Nullable;

/**
 * Code to write a Ball program to LaTeX.
 *
 * @author Jonathan Lovelace
 */
@SuppressWarnings("ClassNamePrefixedWithPackageName")
public final class LaTeXWriter {
	/**
	 * The extensions the graphicx package supports.
	 */
	@SuppressWarnings("StaticCollection") // unmodifiable
	private static final List<String> SUPPORTED_IMAGE_EXTENSIONS =
			List.of(".png", ".jpg", ".pdf");

	@SuppressWarnings("HardcodedFileSeparator") // Not a file separator
	private static String quoted(final String string) {
		return string.replace("&", "\\&").replace("{", "\\{").replace("}", "\\}")
			.replace("<b>", "\\textbf{").replace("</b>", "}")
			.replace("<i>", "\\textit{").replace("</i>", "}")
			.replace("½", "\\nicefrac{1}{2}")
			.replace("<sup>", "\\textsuperscript").replace("</sup>", "}")
			.replace("¾", "\\nicefrac{3}{4}").replace("“", "``")
			.replace("”", "''").replace("–", "---").replace("’", "'")
			.replace("„", "``").replace("‟", "''").replace("‘", "'")
			.replace("‗", "`").replace("\f", "")
			.replace("¼", "\\nicefrac{1}{4}").replace("‑", "--")
			.replace("─", "---").replace("—", "---").replace(";", ";")
			.replace("⅔", "\\nicefrac{2}{3}").replace("⅜", "\\nicefrac{3}{8}")
			.replace("⅞", "\\nicefrac{7}{8}");
	}

	private static void writePrologueLine(final Appendable ostream,
										  final String command,
										  final String arg)
			throws IOException {
		if (!arg.trim().isEmpty()) {
			// not a file separator
			//noinspection HardcodedFileSeparator
			ostream.append("\\");
			ostream.append(command);
			ostream.append("{");
			ostream.append(quoted(arg).trim());
			writeLine(ostream, "}");
		}
	}

	@SuppressWarnings("HardcodedLineSeparator") // unavoidable
	private static void writePrologue(final Appendable ostream,
									  final ProgramMetadata metadata)
			throws IOException {
		// TODO: Do't write starred forms when the same as unstarred
		writePrologueLine(ostream, "tartangroupname", metadata.getGroupCoverName());
		writePrologueLine(ostream, "tartangroupname*", metadata.getGroupTitleName());
		writePrologueLine(ostream, "tartanballname", metadata.getEventCoverName());
		writePrologueLine(ostream, "tartanballname*", metadata.getEventTitleName());
		writePrologueLine(ostream, "tartanballdate", metadata.getCoverDate());
		writePrologueLine(ostream, "tartanballdate*", metadata.getTitleDate());
		writePrologueLine(ostream, "tartanhall", metadata.getCoverLocation());
		writePrologueLine(ostream, "tartanhall*", metadata.getTitleLocation());
		writePrologueLine(ostream, "tartanhalladdress", metadata.getLocationAddress());
		writePrologueLine(ostream, "tartantimes", metadata.getTitleTimes()
			.replace("\n", "\\\\*\n"));
		writePrologueLine(ostream, "tartanmusicians", metadata.getMusicians());
	}

	@SuppressWarnings("HardcodedLineSeparator") // unavoidable
	private static void writeLine(final Appendable ostream,
								  final String string) throws IOException {
		ostream.append(string);
		ostream.append('\n');
	}

	private static void writeSimpleCommand(final Appendable ostream,
										   final String command)
			throws IOException {
		writeSimpleCommand(ostream, command, null);
	}

	private static void writeSimpleCommand(final Appendable ostream,
										   final String command,
										   final @Nullable String arg)
			throws IOException {
		// Not a file separator
		//noinspection HardcodedFileSeparator
		ostream.append('\\');
		ostream.append(command);
		if (arg != null) {
			ostream.append('{');
			ostream.append(arg);
			writeLine(ostream, "}");
		}
	}

	private static String latexImage(final Path imageFilename) {
		final String asString = imageFilename.toString();
		for (final String extension : SUPPORTED_IMAGE_EXTENSIONS) {
			if (asString.endsWith(extension)) {
				return asString.substring(0, asString.length() - extension.length());
			}
		}
		return asString;
	}

	private static void writeSimpleFigure(final Appendable ostream,
										  final Figure figure)
			throws IOException {
		// Not a file separator
		//noinspection HardcodedFileSeparator
		ostream.append("\\scfigure");
		final String bars = figure.getBars();
		if (bars != null) {
			ostream.append('[');
			ostream.append(bars);
			ostream.append(']');
		}
		ostream.append('{');
		ostream.append(quoted(figure.getDescription()));
		writeLine(ostream, "}");
	}

	private static void writeProgramElement(final Appendable out,
	                                        final ProgramElement item)
			throws IOException {
		switch (item) {
			case final Dance dance -> writeDance(out, dance);
			case final Intermission intermission ->
					writeIntermission(out, intermission);
			default -> {
				// TODO: Log?
			}
		}
	}

	/**
	 * Write LaTeX code representing the given Ball program to the given stream.
	 *
	 * @param out      the stream to write to
	 * @param program  the dances etc. that make up the program
	 * @param metadata the metadata to write in the prologue
	 * @throws IOException on I/O error while writing
	 */
	@SuppressWarnings("HardcodedFileSeparator") // double-backslash isn't a file separator
	public static void writeLaTeXProgram(final Appendable out,
	                                     final Iterable<ProgramElement> program,
	                                     final ProgramMetadata metadata)
			throws IOException {
		writeLine(out,
				"% This LaTeX file was produced by the tartan-gui graphical editor;");
		writeLine(out,
				"""
						% if you edit it by hand, that editor may not be able to read it \
						again.""");
		writeSimpleCommand(out, "documentclass", "tartan");
		writePrologue(out, metadata);
		writeSimpleCommand(out, "begin", "document");
		final Path coverImage = metadata.getCoverImage();
		if (Objects.nonNull(coverImage)) {
			if (metadata.hasCoverContent()) {
				writeSimpleCommand(out, "tartanimagecover", latexImage(coverImage));
			} else {
				writeSimpleCommand(out, "tartanimage", latexImage(coverImage));
			}
		} else if (metadata.hasCoverContent()) {
			writeSimpleCommand(out, "tartancover");
		}
		if (metadata.hasTitlePageContent()) {
			if (metadata.getTitleOnCover()) {
				writeSimpleCommand(out, "clearpage");
			} else {
				writeSimpleCommand(out, "cleardoublepage");
			}
			writeSimpleCommand(out, "maketartantitle");
		}
		writeSimpleCommand(out, "clearpage");
		writeSimpleCommand(out, "vspace*", "\\fill");
		writeSimpleCommand(out, "listofdances");
		writeSimpleCommand(out, "vspace", "\\fill");
		writeSimpleCommand(out, "clearpage");
		for (final ProgramElement item : program) {
			writeProgramElement(out, item);
		}
		for (final Path image : metadata.getInsidePostDanceImages()) {
			writeSimpleCommand(out, "clearpage");
			writeSimpleCommand(out, "tartanimage", latexImage(image));
		}
		if (metadata.getPrintAuldLangSyne()) {
			writeSimpleCommand(out, "clearpage");
			writeSimpleCommand(out, "auldlangsyne");
		}
		final Path image = metadata.getBackCoverImage();
		if (image != null) {
			writeSimpleCommand(out, "cleartoverso");
			writeSimpleCommand(out, "tartanimage", latexImage(image));
		}
		writeLine(out, "\\end{document}");
	}

	private static void writeIntermission(final Appendable out,
	                              final Intermission intermission) throws IOException {
		final String text = intermission.getDescription();
		if ("Intermission".equals(text) || text.isEmpty()) {
			writeSimpleCommand(out, "intermission");
		} else {
			writeSimpleCommand(out, "intermission");
			out.append('[');
			out.append(text);
			out.append(']');
		}
	}

	@SuppressWarnings("HardcodedFileSeparator") // \\ is not a file separator here
	private static void writeDance(final Appendable out, final Dance dance)
			throws IOException {
		out.append(String.format("\\begin{scdance}{%s}{%s}{%s}{%dx%d}{%s}%n",
				dance.getTitle(), dance.getSource(),
				dance.getTempo(), dance.getTimes(),
				dance.getLength(), dance.getFormation()));
		for (final DanceMember figure : dance.getContents()) {
			switch (figure) {
				case final Figure fig -> writeSimpleFigure(out, fig);
				case final NamedFigure named -> writeNamedFigure(out, named);
				case final SimplestMember simplestMember ->
						out.append(simplestMember.getString());
				default -> throw new IllegalStateException(
						"Impossible DanceMember");
			}
		}
		writeLine(out, "\\end{scdance}");
	}

	@SuppressWarnings("HardcodedFileSeparator")
	private static void writeNamedFigure(final Appendable out,
										 final NamedFigure named) throws IOException {
		out.append("\\namedfigure{");
		for (final NamedFigureMember subfigure : named
				.getContents()) {
			switch (subfigure) {
				case final Figure fig ->
						writeSimpleFigure(out, fig);
				case final SimplestMember simplestMember ->
						out.append(simplestMember.getString());
				default -> throw new IllegalStateException(
						"Impossible NamedFigureMember");
			}
		}
		writeLine(out, "}");
	}

	public static int estimateSize(final List<ProgramElement> program,
								   final ProgramMetadata metadata) {
		int retval = 328;
		retval += estimatePrologueSize(metadata);
		retval += Optional.ofNullable(metadata.getCoverImage()).map(Path::toString)
						  .stream().mapToInt(String::length).sum();
		for (final ProgramElement item : program) {
			retval += switch (item) {
			case final Dance dance -> estimateDanceSize(dance);
			case final Intermission intermission ->
					estimateIntermissionSize(intermission);
			default -> 0;
			};
		}
		for (final Path image : metadata.getInsidePostDanceImages()) {
			retval += 26;
			retval += image.toString().length();
		}
		if (metadata.getPrintAuldLangSyne()) {
			retval += 11 + 14;
		}
		final Path image = metadata.getBackCoverImage();
		if (image != null) {
			retval += 29;
			retval += image.toString().length();
		}
		return retval;
	}

	private static int estimateIntermissionSize(final Intermission intermission) {
		final String text = intermission.getDescription();
		if ("Intermission".equals(text) || text.isEmpty()) {
			return 14;
		} else {
			return 16 + text.length();
		}
	}

	private static int estimateDanceSize(final Dance dance) {
		int retval = 41;
		retval += Stream.of(dance.getTitle(), dance.getSource(), dance.getTempo(),
				Integer.toString(dance.getTimes()), Integer.toString(dance.getLength()),
				dance.getFormation()).mapToInt(String::length).sum();
		for (final DanceMember figure : dance.getContents()) {
			retval += switch (figure) {
			case final Figure fig -> estimateSimpleFigureSize(fig);
			case final NamedFigure named -> estimateNamedFigureSize(named);
			case final SimplestMember simplestMember ->
					simplestMember.getString().length();
			default -> throw new IllegalStateException(
					"Impossible DanceMember");
			};
		}
		return retval;
	}

	private static int estimateNamedFigureSize(final NamedFigure named) {
		int retval = 15;
		for (final NamedFigureMember subfigure : named.getContents()) {
			retval += switch (subfigure) {
			case final Figure fig ->
					estimateSimpleFigureSize(fig);
			case final SimplestMember simplestMember ->
					simplestMember.getString().length();
			default -> throw new IllegalStateException(
					"Impossible NamedFigureMember");
			};
		}
		return retval;
	}

	private static int estimateSimpleFigureSize(final Figure fig) {
		return 12 + Optional.ofNullable(fig.getBars()).stream().mapToInt(String::length)
							.map(i -> i + 2).sum() + fig.getDescription().length();
	}

	@SuppressWarnings("HardcodedLineSeparator")
	private static int estimatePrologueSize(final ProgramMetadata metadata) {
		// TODO: keep in sync with writePrologue()
		return 197 + Stream.of(metadata.getGroupCoverName(),
						metadata.getGroupTitleName(), metadata.getEventCoverName(),
						metadata.getEventTitleName(), metadata.getCoverDate(),
						metadata.getTitleDate(), metadata.getCoverLocation(),
						metadata.getTitleLocation(), metadata.getLocationAddress(),
						metadata.getTitleTimes(),
						metadata.getMusicians()).filter(Objects::nonNull)
							 .map(s -> s.replace("\n", "\\\\*\n"))
							 .mapToInt(String::length).sum();
	}
}
